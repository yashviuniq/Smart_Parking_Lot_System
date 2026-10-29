package com.parkinglot.dto.response;

import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.SlotStatus;
import com.parkinglot.entity.SlotType;

import java.time.LocalDateTime;

public class ParkingSlotResponse {

    private Long id;
    private String slotNumber;
    private SlotType slotType;
    private SlotStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ParkingSlotResponse() {
    }

    public static ParkingSlotResponse fromEntity(ParkingSlot slot) {
        if (slot == null) return null;
        ParkingSlotResponse response = new ParkingSlotResponse();
        response.setId(slot.getId());
        response.setSlotNumber(slot.getSlotNumber());
        response.setSlotType(slot.getSlotType());
        response.setStatus(slot.getStatus());
        response.setCreatedAt(slot.getCreatedAt());
        response.setUpdatedAt(slot.getUpdatedAt());
        return response;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(String slotNumber) {
        this.slotNumber = slotNumber;
    }

    public SlotType getSlotType() {
        return slotType;
    }

    public void setSlotType(SlotType slotType) {
        this.slotType = slotType;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
