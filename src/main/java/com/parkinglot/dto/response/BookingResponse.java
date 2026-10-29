package com.parkinglot.dto.response;

import com.parkinglot.entity.BookingStatus;
import com.parkinglot.entity.PaymentStatus;
import com.parkinglot.entity.VehicleBooking;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

public class BookingResponse {

    private Long bookingId;
    private String vehicleNumber;
    private Long slotId;
    private String slotNumber;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private Long durationMinutes;
    private String durationFormatted;
    private BigDecimal feeAmount;
    private PaymentStatus paymentStatus;
    private BookingStatus bookingStatus;

    public BookingResponse() {
    }

    public static BookingResponse fromEntity(VehicleBooking booking) {
        if (booking == null) return null;
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setVehicleNumber(booking.getVehicleNumber());
        if (booking.getParkingSlot() != null) {
            response.setSlotId(booking.getParkingSlot().getId());
            response.setSlotNumber(booking.getParkingSlot().getSlotNumber());
        }
        response.setEntryTime(booking.getEntryTime());
        response.setExitTime(booking.getExitTime());
        response.setDurationMinutes(booking.getDurationMinutes());
        response.setFeeAmount(booking.getFeeAmount());
        response.setPaymentStatus(booking.getPaymentStatus());
        response.setBookingStatus(booking.getBookingStatus());

        if (booking.getEntryTime() != null) {
            LocalDateTime end = booking.getExitTime() != null ? booking.getExitTime() : LocalDateTime.now();
            Duration duration = Duration.between(booking.getEntryTime(), end);
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            response.setDurationFormatted(String.format("%d hours %d minutes", hours, minutes));
            if (response.getDurationMinutes() == null) {
                response.setDurationMinutes(duration.toMinutes());
            }
        }

        return response;
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

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
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

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDurationFormatted() {
        return durationFormatted;
    }

    public void setDurationFormatted(String durationFormatted) {
        this.durationFormatted = durationFormatted;
    }

    public BigDecimal getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(BigDecimal feeAmount) {
        this.feeAmount = feeAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BookingStatus getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(BookingStatus bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
