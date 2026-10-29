package com.parkinglot.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FareCalculationResponse {

    private Long bookingId;
    private String vehicleNumber;
    private String slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime asOfTime;
    private long durationMinutes;
    private String durationFormatted;
    private long billedSlabs;
    private BigDecimal slabRate;
    private BigDecimal totalFare;
    private String currency;
    private String feeBreakdown;

    public FareCalculationResponse() {
    }

    public FareCalculationResponse(Long bookingId, String vehicleNumber, String slotNumber,
                                   LocalDateTime entryTime, LocalDateTime asOfTime,
                                   long durationMinutes, String durationFormatted,
                                   long billedSlabs, BigDecimal slabRate,
                                   BigDecimal totalFare, String currency, String feeBreakdown) {
        this.bookingId = bookingId;
        this.vehicleNumber = vehicleNumber;
        this.slotNumber = slotNumber;
        this.entryTime = entryTime;
        this.asOfTime = asOfTime;
        this.durationMinutes = durationMinutes;
        this.durationFormatted = durationFormatted;
        this.billedSlabs = billedSlabs;
        this.slabRate = slabRate;
        this.totalFare = totalFare;
        this.currency = currency;
        this.feeBreakdown = feeBreakdown;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getAsOfTime() {
        return asOfTime;
    }

    public void setAsOfTime(LocalDateTime asOfTime) {
        this.asOfTime = asOfTime;
    }

    public long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDurationFormatted() {
        return durationFormatted;
    }

    public void setDurationFormatted(String durationFormatted) {
        this.durationFormatted = durationFormatted;
    }

    public long getBilledSlabs() {
        return billedSlabs;
    }

    public void setBilledSlabs(long billedSlabs) {
        this.billedSlabs = billedSlabs;
    }

    public BigDecimal getSlabRate() {
        return slabRate;
    }

    public void setSlabRate(BigDecimal slabRate) {
        this.slabRate = slabRate;
    }

    public BigDecimal getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(BigDecimal totalFare) {
        this.totalFare = totalFare;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFeeBreakdown() {
        return feeBreakdown;
    }

    public void setFeeBreakdown(String feeBreakdown) {
        this.feeBreakdown = feeBreakdown;
    }
}
