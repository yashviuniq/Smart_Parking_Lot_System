package com.parkinglot.service;

import com.parkinglot.dto.response.FareCalculationResponse;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.SlotType;
import com.parkinglot.entity.VehicleBooking;
import com.parkinglot.exception.InvalidTimestampException;
import com.parkinglot.service.impl.FeeCalculationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Fee Calculation Engine Unit Tests")
class FeeCalculationServiceTest {

    private FeeCalculationService feeCalculationService;

    @BeforeEach
    void setUp() {
        feeCalculationService = new FeeCalculationServiceImpl();
    }

    @Nested
    @DisplayName("Slab and Fee Calculation Tests")
    class SlabAndFeeTests {

        @ParameterizedTest(name = "Duration: {0} minutes should cost ₹{1} ({2} slabs)")
        @CsvSource({
                "0,    50.00, 1",    // Immediate checkout / fraction of first hour
                "1,    50.00, 1",    // 1 minute
                "60,   50.00, 1",    // 1 hour
                "135,  50.00, 1",    // 2 hours 15 mins (Specification Example 1)
                "179,  50.00, 1",    // 2 hours 59 mins (just under 3 hours)
                "180,  50.00, 1",    // Exactly 3 hours
                "181,  100.00, 2",   // 3 hours 1 min (boundary over 3 hours)
                "185,  100.00, 2",   // 3 hours 5 mins (Specification Example 2)
                "359,  100.00, 2",   // 5 hours 59 mins
                "360,  100.00, 2",   // Exactly 6 hours
                "361,  150.00, 3",   // 6 hours 1 min
                "540,  150.00, 3",   // Exactly 9 hours
                "541,  200.00, 4",   // 9 hours 1 min
                "1440, 400.00, 8"    // Exactly 24 hours (8 slabs * 50 = 400)
        })
        void testDurationToFeeAndSlabs(long minutes, String expectedFeeStr, long expectedSlabs) {
            LocalDateTime entryTime = LocalDateTime.of(2026, 10, 29, 10, 0, 0);
            LocalDateTime exitTime = entryTime.plusMinutes(minutes);

            long slabs = feeCalculationService.calculateBilledSlabs(entryTime, exitTime);
            BigDecimal fee = feeCalculationService.calculateFee(entryTime, exitTime);

            assertEquals(expectedSlabs, slabs, "Billed slabs mismatch for " + minutes + " minutes");
            assertEquals(new BigDecimal(expectedFeeStr), fee, "Fee mismatch for " + minutes + " minutes");
        }
    }

    @Nested
    @DisplayName("Defensive Edge Case and Validation Tests")
    class DefensiveValidationTests {

        @Test
        @DisplayName("Throw InvalidTimestampException when exit time is before entry time")
        void testExitBeforeEntryThrowsException() {
            LocalDateTime entry = LocalDateTime.of(2026, 10, 29, 12, 0, 0);
            LocalDateTime exit = entry.minusMinutes(1);

            InvalidTimestampException ex = assertThrows(InvalidTimestampException.class, () ->
                    feeCalculationService.calculateFee(entry, exit));

            assertTrue(ex.getMessage().contains("cannot be before entry time"));
        }

        @Test
        @DisplayName("Throw InvalidTimestampException when entry time is null")
        void testNullEntryThrowsException() {
            LocalDateTime exit = LocalDateTime.now();
            assertThrows(InvalidTimestampException.class, () ->
                    feeCalculationService.calculateFee(null, exit));
        }

        @Test
        @DisplayName("Throw InvalidTimestampException when exit time is null")
        void testNullExitThrowsException() {
            LocalDateTime entry = LocalDateTime.now();
            assertThrows(InvalidTimestampException.class, () ->
                    feeCalculationService.calculateFee(entry, null));
        }
    }

    @Nested
    @DisplayName("Real-time Accrued Fare Calculation Tests")
    class AccruedFareTests {

        @Test
        @DisplayName("Accrued fare for active booking with duration 2h 15m")
        void testAccruedFareCalculation() {
            ParkingSlot slot = new ParkingSlot("A1", SlotType.STANDARD);
            slot.setId(1L);

            LocalDateTime entryTime = LocalDateTime.of(2026, 10, 29, 9, 0, 0);
            VehicleBooking booking = new VehicleBooking("DL01AB9999", slot, entryTime);
            booking.setId(101L);

            // 2 hours 15 minutes later
            LocalDateTime asOfTime = entryTime.plusHours(2).plusMinutes(15);

            FareCalculationResponse response = feeCalculationService.calculateAccruedFare(booking, asOfTime);

            assertNotNull(response);
            assertEquals(101L, response.getBookingId());
            assertEquals("DL01AB9999", response.getVehicleNumber());
            assertEquals("A1", response.getSlotNumber());
            assertEquals(135L, response.getDurationMinutes());
            assertEquals(1L, response.getBilledSlabs());
            assertEquals(new BigDecimal("50.00"), response.getTotalFare());
            assertEquals("INR", response.getCurrency());
            assertTrue(response.getFeeBreakdown().contains("₹50.00"));
        }
    }
}
