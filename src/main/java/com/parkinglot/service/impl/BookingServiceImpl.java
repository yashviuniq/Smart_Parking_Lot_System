package com.parkinglot.service.impl;

import com.parkinglot.dto.request.BookingCheckoutRequest;
import com.parkinglot.dto.request.BookingEntryRequest;
import com.parkinglot.dto.response.BookingResponse;
import com.parkinglot.dto.response.FareCalculationResponse;
import com.parkinglot.entity.BookingStatus;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.PaymentStatus;
import com.parkinglot.entity.SlotStatus;
import com.parkinglot.entity.VehicleBooking;
import com.parkinglot.exception.DuplicateActiveBookingException;
import com.parkinglot.exception.InvalidBookingStateException;
import com.parkinglot.exception.InvalidTimestampException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.exception.SlotUnavailableException;
import com.parkinglot.repository.ParkingSlotRepository;
import com.parkinglot.repository.VehicleBookingRepository;
import com.parkinglot.service.BookingService;
import com.parkinglot.service.FeeCalculationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final VehicleBookingRepository bookingRepository;
    private final ParkingSlotRepository slotRepository;
    private final FeeCalculationService feeCalculationService;

    public BookingServiceImpl(VehicleBookingRepository bookingRepository,
                              ParkingSlotRepository slotRepository,
                              FeeCalculationService feeCalculationService) {
        this.bookingRepository = bookingRepository;
        this.slotRepository = slotRepository;
        this.feeCalculationService = feeCalculationService;
    }

    @Override
    public BookingResponse bookEntry(BookingEntryRequest request) {
        String vehicleNumber = request.getVehicleNumber().trim().toUpperCase();

        // 1. Check if vehicle already has an active parking session
        if (bookingRepository.existsByVehicleNumberIgnoreCaseAndBookingStatus(vehicleNumber, BookingStatus.ACTIVE)) {
            throw new DuplicateActiveBookingException(String.format(
                    "Vehicle '%s' already has an active parking booking. Please checkout first.", vehicleNumber));
        }

        // 2. Determine slot to allocate
        ParkingSlot slot;
        if (request.getSlotId() != null) {
            slot = slotRepository.findByIdForUpdate(request.getSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Slot not found with ID: " + request.getSlotId()));

            if (slot.getStatus() != SlotStatus.AVAILABLE) {
                throw new SlotUnavailableException(String.format(
                        "Slot '%s' (ID: %d) is not available. Current status: %s",
                        slot.getSlotNumber(), slot.getId(), slot.getStatus()));
            }
        } else {
            // Automatic allocation: pick first available slot
            if (request.getSlotType() != null) {
                slot = slotRepository.findFirstByStatusAndSlotTypeOrderByIdAsc(SlotStatus.AVAILABLE, request.getSlotType())
                        .orElseThrow(() -> new SlotUnavailableException(
                                "No available parking slots of type: " + request.getSlotType()));
            } else {
                slot = slotRepository.findFirstByStatusOrderByIdAsc(SlotStatus.AVAILABLE)
                        .orElseThrow(() -> new SlotUnavailableException("Parking lot is fully occupied. No slots available."));
            }
            // Acquire lock on the selected slot
            slot = slotRepository.findByIdForUpdate(slot.getId())
                    .orElseThrow(() -> new SlotUnavailableException("Slot allocation race condition detected."));
            if (slot.getStatus() != SlotStatus.AVAILABLE) {
                throw new SlotUnavailableException("Selected slot is no longer available. Please retry.");
            }
        }

        // 3. Mark slot as occupied
        slot.setStatus(SlotStatus.OCCUPIED);
        slotRepository.save(slot);

        // 4. Create and persist booking record
        LocalDateTime entryTime = request.getEntryTime() != null ? request.getEntryTime() : LocalDateTime.now();
        if (entryTime.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw new InvalidTimestampException("Entry timestamp cannot be in the future.");
        }

        VehicleBooking booking = new VehicleBooking(vehicleNumber, slot, entryTime);
        booking.setPaymentStatus(PaymentStatus.PENDING);
        booking.setBookingStatus(BookingStatus.ACTIVE);

        VehicleBooking savedBooking = bookingRepository.save(booking);
        return BookingResponse.fromEntity(savedBooking);
    }

    @Override
    public BookingResponse checkout(BookingCheckoutRequest request) {
        VehicleBooking booking = findActiveBooking(request.getBookingId(), request.getVehicleNumber(), request.getSlotId());

        if (booking.getBookingStatus() != BookingStatus.ACTIVE) {
            throw new InvalidBookingStateException(String.format(
                    "Booking ID %d is not in ACTIVE state. Current status: %s",
                    booking.getId(), booking.getBookingStatus()));
        }

        LocalDateTime exitTime = request.getExitTime() != null ? request.getExitTime() : LocalDateTime.now();
        if (exitTime.isBefore(booking.getEntryTime())) {
            throw new InvalidTimestampException(String.format(
                    "Exit time (%s) cannot be before entry time (%s).", exitTime, booking.getEntryTime()));
        }

        Duration duration = Duration.between(booking.getEntryTime(), exitTime);
        long durationMinutes = Math.max(1, duration.toMinutes());
        BigDecimal fee = feeCalculationService.calculateFee(booking.getEntryTime(), exitTime);

        // Update booking
        booking.setExitTime(exitTime);
        booking.setDurationMinutes(durationMinutes);
        booking.setFeeAmount(fee);
        booking.setBookingStatus(BookingStatus.COMPLETED);
        booking.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : PaymentStatus.PAID);

        // Free up slot
        ParkingSlot slot = booking.getParkingSlot();
        if (slot != null) {
            slot.setStatus(SlotStatus.AVAILABLE);
            slotRepository.save(slot);
        }

        VehicleBooking updatedBooking = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updatedBooking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getActiveBookings() {
        return bookingRepository.findByBookingStatus(BookingStatus.ACTIVE).stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long bookingId) {
        VehicleBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        return BookingResponse.fromEntity(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public FareCalculationResponse calculateCurrentFare(Long bookingId, String vehicleNumber, LocalDateTime asOfTime) {
        VehicleBooking booking = findActiveBooking(bookingId, vehicleNumber, null);
        return feeCalculationService.calculateAccruedFare(booking, asOfTime);
    }

    private VehicleBooking findActiveBooking(Long bookingId, String vehicleNumber, Long slotId) {
        if (bookingId != null) {
            return bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));
        }

        if (vehicleNumber != null && !vehicleNumber.trim().isEmpty()) {
            return bookingRepository.findFirstByVehicleNumberIgnoreCaseAndBookingStatus(
                    vehicleNumber.trim(), BookingStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No active booking found for vehicle: " + vehicleNumber.trim().toUpperCase()));
        }

        if (slotId != null) {
            return bookingRepository.findFirstByParkingSlotIdAndBookingStatus(slotId, BookingStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "No active booking found for slot ID: " + slotId));
        }

        throw new IllegalArgumentException("At least one search criterion (bookingId, vehicleNumber, or slotId) must be provided.");
    }
}
