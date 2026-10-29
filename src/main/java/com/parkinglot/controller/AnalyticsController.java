package com.parkinglot.controller;

import com.parkinglot.dto.response.ApiResponse;
import com.parkinglot.dto.response.RevenueReportResponse;
import com.parkinglot.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics & Reports API", description = "Endpoints for aggregate metrics and financial revenue reporting")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/revenue")
    @Operation(summary = "Daily Revenue Reporting", description = "Calculates and displays aggregate revenue generated on a specified date (defaults to current date).")
    public ResponseEntity<ApiResponse<RevenueReportResponse>> getDailyRevenue(
            @Parameter(description = "Date in ISO format (YYYY-MM-DD). Defaults to current date if omitted.", example = "2026-10-29")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        RevenueReportResponse report = analyticsService.getDailyRevenueReport(date);
        return ResponseEntity.ok(ApiResponse.ok("Revenue report generated successfully", report));
    }
}
