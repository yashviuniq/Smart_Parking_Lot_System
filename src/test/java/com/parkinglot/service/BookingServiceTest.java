package com.parkinglot.service;

import com.parkinglot.dto.request.BookingCheckoutRequest;
import com.parkinglot.dto.request.BookingEntryRequest;
import com.parkinglot.dto.response.BookingResponse;
import com.parkinglot.entity.BookingStatus;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.PaymentStatus;
import com.parkinglot.entity.SlotStatus;
import com.parkinglot.entity.SlotType;
import com.parkinglot.entity.VehicleBooking;
import com.parkinglot.exception.DuplicateActiveBookingException;
import com.parkinglot.exception.SlotUnavailableException;
import com.parkinglot.repository.ParkingSlotRepository;
import com.parkinglot.repository.VehicleBookingRepository;
import com.parkinglot.service.impl.BookingServiceImpl;
import com.parkinglot.service.impl.FeeCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking Service Unit Tests")
class BookingServiceTest {

    @Mock
    private VehicleBookingRepository bookingRepository;

    @Mock
    private ParkingSlotRepository slotRepository;

    @Spy
    private FeeCalculationService feeCalculationService = new FeeCalculationServiceImpl();

    @InjectMocks
    private BookingServiceImpl bookingService;

    private ParkingSlot availableSlot;

    @BeforeEach
    void setUp() {
        availableSlot = new ParkingSlot("A1", SlotType.STANDARD);
        availableSlot.setId(1L);
        availableSlot.setStatus(SlotStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Successfully allocate available slot to entering vehicle")
    void testSuccessfulEntryAllocation() {
        BookingEntryRequest request = new BookingEntryRequest("MH12AB1234");

        when(bookingRepository.existsByVehicleNumberIgnoreCaseAndBookingStatus("MH12AB1234", BookingStatus.ACTIVE))
                .thenReturn(false);
        when(slotRepository.findFirstByStatusOrderByIdAsc(SlotStatus.AVAILABLE))
                .thenReturn(Optional.of(availableSlot));
        when(slotRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(availableSlot));
        when(bookingRepository.save(any(VehicleBooking.class)))
                .thenAnswer(invocation -> {
                    VehicleBooking b = invocation.getArgument(0);
                    b.setId(10L);
                    return b;
                });

        BookingResponse response = bookingService.bookEntry(request);

        assertNotNull(response);
        assertEquals("MH12AB1234", response.getVehicleNumber());
        assertEquals("A1", response.getSlotNumber());
        assertEquals(BookingStatus.ACTIVE, response.getBookingStatus());
        assertEquals(PaymentStatus.PENDING, response.getPaymentStatus());
        assertEquals(SlotStatus.OCCUPIED, availableSlot.getStatus());

        verify(slotRepository).save(availableSlot);
        verify(bookingRepository).save(any(VehicleBooking.class));
    }

    @Test
    @DisplayName("Reject booking when vehicle already has active booking (409)")
    void testRejectDuplicateActiveBooking() {
        BookingEntryRequest request = new BookingEntryRequest("MH12AB1234");

        when(bookingRepository.existsByVehicleNumberIgnoreCaseAndBookingStatus("MH12AB1234", BookingStatus.ACTIVE))
                .thenReturn(true);

        assertThrows(DuplicateActiveBookingException.class, () -> bookingService.bookEntry(request));
        verify(slotRepository, never()).save(any());
    }

    @Test
    @DisplayName("Reject booking when no slots available (409)")
    void testRejectWhenNoSlotsAvailable() {
        BookingEntryRequest request = new BookingEntryRequest("KA01CD5678");

        when(bookingRepository.existsByVehicleNumberIgnoreCaseAndBookingStatus("KA01CD5678", BookingStatus.ACTIVE))
                .thenReturn(false);
        when(slotRepository.findFirstByStatusOrderByIdAsc(SlotStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        assertThrows(SlotUnavailableException.class, () -> bookingService.bookEntry(request));
    }

    @Test
    @DisplayName("Checkout vehicle, calculate fee and free up slot")
    void testCheckoutAndBilling() {
        LocalDateTime entry = LocalDateTime.of(2026, 10, 29, 10, 0, 0);
        LocalDateTime exit = entry.plusHours(3).plusMinutes(5); // 3h 5m -> 2 slabs -> ₹100

        availableSlot.setStatus(SlotStatus.OCCUPIED);
        VehicleBooking activeBooking = new VehicleBooking("MH12AB1234", availableSlot, entry);
        activeBooking.setId(50L);
        activeBooking.setBookingStatus(BookingStatus.ACTIVE);

        BookingCheckoutRequest request = new BookingCheckoutRequest();
        request.setBookingId(50L);
        request.setExitTime(exit);

        when(bookingRepository.findById(50L)).thenReturn(Optional.of(activeBooking));
        when(bookingRepository.save(any(VehicleBooking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        BookingResponse response = bookingService.checkout(request);

        assertNotNull(response);
        assertEquals(BookingStatus.COMPLETED, response.getBookingStatus());
        assertEquals(PaymentStatus.PAID, response.getPaymentStatus());
        assertEquals(new BigDecimal("100.00"), response.getFeeAmount());
        assertEquals(SlotStatus.AVAILABLE, availableSlot.getStatus());

        verify(slotRepository).save(availableSlot);
        verify(bookingRepository).save(activeBooking);
    }
}
