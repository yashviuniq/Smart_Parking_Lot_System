package com.parkinglot.controller;

import com.parkinglot.dto.response.ApiResponse;
import com.parkinglot.dto.response.ParkingSlotResponse;
import com.parkinglot.entity.SlotType;
import com.parkinglot.service.ParkingSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/slots")
@Tag(name = "Parking Slots API", description = "Endpoints for managing parking slots and checking real-time availability")
public class ParkingSlotController {

    private final ParkingSlotService slotService;

    public ParkingSlotController(ParkingSlotService slotService) {
        this.slotService = slotService;
    }

    @GetMapping("/available")
    @Operation(summary = "Get available parking slots", description = "Retrieves all currently available parking slots, optionally filtered by slot type.")
    public ResponseEntity<ApiResponse<List<ParkingSlotResponse>>> getAvailableSlots(
            @Parameter(description = "Optional filter by slot type: STANDARD, COMPACT, LARGE, EV")
            @RequestParam(required = false) SlotType slotType) {
        List<ParkingSlotResponse> available = slotService.getAvailableSlots(slotType);
        return ResponseEntity.ok(ApiResponse.ok(
                String.format("Found %d available parking slot(s)", available.size()), available));
    }

    @GetMapping
    @Operation(summary = "List all parking slots", description = "Retrieves all parking slots regardless of availability status.")
    public ResponseEntity<ApiResponse<List<ParkingSlotResponse>>> getAllSlots() {
        List<ParkingSlotResponse> allSlots = slotService.getAllSlots();
        return ResponseEntity.ok(ApiResponse.ok(allSlots));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get parking slot by ID", description = "Retrieves details of a specific parking slot by its database ID.")
    public ResponseEntity<ApiResponse<ParkingSlotResponse>> getSlotById(@PathVariable Long id) {
        ParkingSlotResponse slot = slotService.getSlotById(id);
        return ResponseEntity.ok(ApiResponse.ok(slot));
    }

    @PostMapping
    @Operation(summary = "Create a parking slot", description = "Admin endpoint to register a new parking slot.")
    public ResponseEntity<ApiResponse<ParkingSlotResponse>> createSlot(
            @RequestParam @NotBlank String slotNumber,
            @RequestParam(defaultValue = "STANDARD") SlotType slotType) {
        ParkingSlotResponse created = slotService.createSlot(slotNumber, slotType);
        return new ResponseEntity<>(ApiResponse.ok("Parking slot created successfully", created), HttpStatus.CREATED);
    }
}
