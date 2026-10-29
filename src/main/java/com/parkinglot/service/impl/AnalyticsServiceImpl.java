package com.parkinglot.service.impl;

import com.parkinglot.dto.response.BookingResponse;
import com.parkinglot.dto.response.RevenueReportResponse;
import com.parkinglot.entity.BookingStatus;
import com.parkinglot.entity.VehicleBooking;
import com.parkinglot.repository.VehicleBookingRepository;
import com.parkinglot.service.AnalyticsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final VehicleBookingRepository bookingRepository;

    public AnalyticsServiceImpl(VehicleBookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public RevenueReportResponse getDailyRevenueReport(LocalDate date) {
        LocalDate reportDate = date != null ? date : LocalDate.now();
        LocalDateTime startOfDay = reportDate.atStartOfDay();
        LocalDateTime endOfDay = reportDate.plusDays(1).atStartOfDay();

        BigDecimal totalRevenue = bookingRepository.calculateTotalRevenueBetween(
                startOfDay, endOfDay, BookingStatus.COMPLETED);
        if (totalRevenue == null) {
            totalRevenue = BigDecimal.ZERO;
        }

        List<VehicleBooking> bookings = bookingRepository.findByExitTimeBetweenAndBookingStatus(
                startOfDay, endOfDay, BookingStatus.COMPLETED);

        List<BookingResponse> bookingResponses = bookings.stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());

        return new RevenueReportResponse(
                reportDate,
                bookings.size(),
                totalRevenue,
                "INR",
                bookingResponses
        );
    }
}
