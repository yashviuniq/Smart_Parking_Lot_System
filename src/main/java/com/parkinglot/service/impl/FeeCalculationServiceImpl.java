package com.parkinglot.service.impl;

import com.parkinglot.dto.response.FareCalculationResponse;
import com.parkinglot.entity.VehicleBooking;
import com.parkinglot.exception.InvalidTimestampException;
import com.parkinglot.service.FeeCalculationService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class FeeCalculationServiceImpl implements FeeCalculationService {

    public static final BigDecimal RATE_PER_SLAB = new BigDecimal("50.00");
    public static final long SLAB_MINUTES = 180L; // 3 hours = 180 minutes
    public static final String CURRENCY = "INR";

    @Override
    public BigDecimal calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long slabs = calculateBilledSlabs(entryTime, exitTime);
        return RATE_PER_SLAB.multiply(BigDecimal.valueOf(slabs)).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public long calculateBilledSlabs(LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null) {
            throw new InvalidTimestampException("Entry time cannot be null");
        }
        if (exitTime == null) {
            throw new InvalidTimestampException("Exit time cannot be null");
        }
        if (exitTime.isBefore(entryTime)) {
            throw new InvalidTimestampException(String.format(
                    "Exit time (%s) cannot be before entry time (%s)", exitTime, entryTime));
        }

        Duration duration = Duration.between(entryTime, exitTime);
        long totalMinutes = duration.toMinutes();

        // Any fraction of time (even < 1 minute) counts as at least 1 slab
        if (totalMinutes <= 0) {
            return 1L;
        }

        return (long) Math.ceil((double) totalMinutes / SLAB_MINUTES);
    }

    @Override
    public FareCalculationResponse calculateAccruedFare(VehicleBooking booking, LocalDateTime asOfTime) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }

        LocalDateTime calculationTime = asOfTime != null ? asOfTime : LocalDateTime.now();
        LocalDateTime entryTime = booking.getEntryTime();

        if (calculationTime.isBefore(entryTime)) {
            throw new InvalidTimestampException(String.format(
                    "Calculation time (%s) cannot be before vehicle entry time (%s)", calculationTime, entryTime));
        }

        Duration duration = Duration.between(entryTime, calculationTime);
        long totalMinutes = duration.toMinutes();
        long hours = duration.toHours();
        long minutesPart = duration.toMinutesPart();
        String durationFormatted = String.format("%d hours %d minutes", hours, minutesPart);

        long slabs = calculateBilledSlabs(entryTime, calculationTime);
        BigDecimal totalFare = RATE_PER_SLAB.multiply(BigDecimal.valueOf(slabs)).setScale(2, RoundingMode.HALF_UP);

        String breakdown = String.format(
                "Duration: %s (%d min). Billed as %d three-hour slab(s) @ ₹%.2f per slab = ₹%.2f",
                durationFormatted, totalMinutes, slabs, RATE_PER_SLAB, totalFare);

        String slotNumber = booking.getParkingSlot() != null ? booking.getParkingSlot().getSlotNumber() : "N/A";

        return new FareCalculationResponse(
                booking.getId(),
                booking.getVehicleNumber(),
                slotNumber,
                entryTime,
                calculationTime,
                totalMinutes,
                durationFormatted,
                slabs,
                RATE_PER_SLAB,
                totalFare,
                CURRENCY,
                breakdown
        );
    }
}
