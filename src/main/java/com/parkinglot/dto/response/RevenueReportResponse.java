package com.parkinglot.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RevenueReportResponse {

    private LocalDate reportDate;
    private long totalCompletedBookings;
    private BigDecimal totalRevenue;
    private String currency;
    private List<BookingResponse> bookings;

    public RevenueReportResponse() {
    }

    public RevenueReportResponse(LocalDate reportDate, long totalCompletedBookings,
                                 BigDecimal totalRevenue, String currency,
                                 List<BookingResponse> bookings) {
        this.reportDate = reportDate;
        this.totalCompletedBookings = totalCompletedBookings;
        this.totalRevenue = totalRevenue;
        this.currency = currency;
        this.bookings = bookings;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public long getTotalCompletedBookings() {
        return totalCompletedBookings;
    }

    public void setTotalCompletedBookings(long totalCompletedBookings) {
        this.totalCompletedBookings = totalCompletedBookings;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<BookingResponse> getBookings() {
        return bookings;
    }

    public void setBookings(List<BookingResponse> bookings) {
        this.bookings = bookings;
    }
}
