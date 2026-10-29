package com.parkinglot.dto.request;

import com.parkinglot.entity.SlotType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

@Schema(description = "Request payload for parking lot entry / slot booking")
public class BookingEntryRequest {

    @Schema(description = "Vehicle registration number", example = "MH12AB1234")
    @NotBlank(message = "Vehicle number is required")
    @Pattern(regexp = "^[A-Z0-9 -]{4,15}$", message = "Vehicle number must be valid alphanumeric (4-15 characters)")
    private String vehicleNumber;

    @Schema(description = "Optional specific slot ID to allocate. If omitted, first available slot is allocated automatically.", example = "1")
    @Positive(message = "Slot ID must be a positive integer")
    private Long slotId;

    @Schema(description = "Preferred slot type if automatic allocation is used", example = "STANDARD")
    private SlotType slotType;

    @Schema(description = "Optional entry timestamp. Defaults to current system time if omitted.", example = "2026-10-29T10:00:00")
    private LocalDateTime entryTime;

    public BookingEntryRequest() {
    }

    public BookingEntryRequest(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public BookingEntryRequest(String vehicleNumber, Long slotId, SlotType slotType, LocalDateTime entryTime) {
        this.vehicleNumber = vehicleNumber;
        this.slotId = slotId;
        this.slotType = slotType;
        this.entryTime = entryTime;
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

    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }
}
