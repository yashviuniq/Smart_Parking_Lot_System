package com.parkinglot.service;

import com.parkinglot.dto.response.FareCalculationResponse;
import com.parkinglot.entity.VehicleBooking;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface FeeCalculationService {

    BigDecimal calculateFee(LocalDateTime entryTime, LocalDateTime exitTime);

    long calculateBilledSlabs(LocalDateTime entryTime, LocalDateTime exitTime);

    FareCalculationResponse calculateAccruedFare(VehicleBooking booking, LocalDateTime asOfTime);
}
