package com.parkinglot.dto.request;

import com.parkinglot.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Request payload for parking lot checkout and billing")
public class BookingCheckoutRequest {

    @Schema(description = "Booking ID to checkout (if known)", example = "1")
    @Positive(message = "Booking ID must be a positive integer")
    private Long bookingId;

    @Schema(description = "Vehicle registration number to checkout", example = "MH12AB1234")
    @Pattern(regexp = "^[A-Z0-9 -]{4,15}$", message = "Vehicle number must be valid alphanumeric (4-15 characters)")
    private String vehicleNumber;

    @Schema(description = "Parking slot ID to checkout", example = "1")
    @Positive(message = "Slot ID must be a positive integer")
    private Long slotId;

    @Schema(description = "Optional checkout / exit timestamp. Defaults to current system time.", example = "2026-10-29T13:15:00")
    private LocalDateTime exitTime;

    @Schema(description = "Payment status upon checkout (defaults to PAID)", example = "PAID")
    private PaymentStatus paymentStatus = PaymentStatus.PAID;

    public BookingCheckoutRequest() {
    }

    public BookingCheckoutRequest(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BookingCheckoutRequest(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
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
        this.vehicleNumber = vehicleNumber != null ? vehicleNumber.trim().toUpperCase() : null;
    }

    public Long getSlotId() {
        return slotId;
    }

    public void setSlotId(Long slotId) {
        this.slotId = slotId;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
}
