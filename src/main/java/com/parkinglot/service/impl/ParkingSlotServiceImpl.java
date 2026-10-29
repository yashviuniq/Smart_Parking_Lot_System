package com.parkinglot.service.impl;

import com.parkinglot.dto.response.ParkingSlotResponse;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.entity.SlotStatus;
import com.parkinglot.entity.SlotType;
import com.parkinglot.exception.BusinessException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.repository.ParkingSlotRepository;
import com.parkinglot.service.ParkingSlotService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ParkingSlotServiceImpl implements ParkingSlotService {

    private final ParkingSlotRepository slotRepository;

    public ParkingSlotServiceImpl(ParkingSlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    @Override
    public List<ParkingSlotResponse> getAvailableSlots(SlotType slotType) {
        List<ParkingSlot> slots;
        if (slotType != null) {
            slots = slotRepository.findByStatusAndSlotType(SlotStatus.AVAILABLE, slotType);
        } else {
            slots = slotRepository.findByStatus(SlotStatus.AVAILABLE);
        }
        return slots.stream()
                .map(ParkingSlotResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParkingSlotResponse> getAllSlots() {
        return slotRepository.findAll().stream()
                .map(ParkingSlotResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ParkingSlotResponse getSlotById(Long slotId) {
        ParkingSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with id: " + slotId));
        return ParkingSlotResponse.fromEntity(slot);
    }

    @Override
    public ParkingSlotResponse getSlotByNumber(String slotNumber) {
        ParkingSlot slot = slotRepository.findBySlotNumberIgnoreCase(slotNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Parking slot not found with number: " + slotNumber));
        return ParkingSlotResponse.fromEntity(slot);
    }

    @Override
    @Transactional
    public ParkingSlotResponse createSlot(String slotNumber, SlotType slotType) {
        if (slotRepository.findBySlotNumberIgnoreCase(slotNumber).isPresent()) {
            throw new BusinessException("Slot with number " + slotNumber + " already exists", HttpStatus.CONFLICT);
        }
        ParkingSlot slot = new ParkingSlot(slotNumber.toUpperCase().trim(), slotType != null ? slotType : SlotType.STANDARD);
        ParkingSlot saved = slotRepository.save(slot);
        return ParkingSlotResponse.fromEntity(saved);
    }
}
