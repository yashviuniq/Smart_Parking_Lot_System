package com.parkinglot.service;

import com.parkinglot.dto.response.ParkingSlotResponse;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.SlotType;

import java.util.List;

public interface ParkingSlotService {

    List<ParkingSlotResponse> getAvailableSlots(SlotType slotType);

    List<ParkingSlotResponse> getAllSlots();

    ParkingSlotResponse getSlotById(Long slotId);

    ParkingSlotResponse getSlotByNumber(String slotNumber);

    ParkingSlotResponse createSlot(String slotNumber, SlotType slotType);
}
