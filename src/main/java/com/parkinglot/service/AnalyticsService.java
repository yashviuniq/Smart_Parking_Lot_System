package com.parkinglot.service;

import com.parkinglot.dto.response.RevenueReportResponse;

import java.time.LocalDate;

public interface AnalyticsService {

    RevenueReportResponse getDailyRevenueReport(LocalDate date);
}
