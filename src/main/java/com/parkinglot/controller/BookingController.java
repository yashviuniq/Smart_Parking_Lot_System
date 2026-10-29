package com.parkinglot.controller;

import com.parkinglot.dto.request.BookingCheckoutRequest;
import com.parkinglot.dto.request.BookingEntryRequest;
import com.parkinglot.dto.response.ApiResponse;
import com.parkinglot.dto.response.BookingResponse;
import com.parkinglot.dto.response.FareCalculationResponse;
import com.parkinglot.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@Tag(name = "Vehicle Booking API", description = "Endpoints for vehicle entry, checkout, active records, and real-time fare calculation")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/entry")
    @Operation(summary = "Slot Booking (Vehicle Entry)", description = "Allocates an available slot to a vehicle upon entry and persists booking details.")
    public ResponseEntity<ApiResponse<BookingResponse>> bookEntry(
            @Valid @RequestBody BookingEntryRequest request) {
        BookingResponse response = bookingService.bookEntry(request);
        return new ResponseEntity<>(
                ApiResponse.ok("Vehicle successfully booked into slot " + response.getSlotNumber(), response),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout & Billing", description = "Processes vehicle exit, calculates total fee, records payment, and frees up the allocated slot.")
    public ResponseEntity<ApiResponse<BookingResponse>> checkout(
            @Valid @RequestBody BookingCheckoutRequest request) {
        BookingResponse response = bookingService.checkout(request);
        return ResponseEntity.ok(ApiResponse.ok("Vehicle checked out successfully. Allocated slot is now free.", response));
    }

    @GetMapping("/active")
    @Operation(summary = "Active Records", description = "Fetches details for all currently occupied parking slots and active vehicle sessions.")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getActiveBookings() {
        List<BookingResponse> activeBookings = bookingService.getActiveBookings();
        return ResponseEntity.ok(ApiResponse.ok(
                String.format("Found %d active parking session(s)", activeBookings.size()), activeBookings));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Booking by ID", description = "Retrieves full details of a specific vehicle booking record.")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        BookingResponse response = bookingService.getBookingById(id);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{id}/fare")
    @Operation(summary = "Real-time Fare Calculation by ID", description = "Calculates the total charge accrued at any given time for an active booking without checking out.")
    public ResponseEntity<ApiResponse<FareCalculationResponse>> calculateFareById(
            @PathVariable Long id,
            @Parameter(description = "Optional as-of calculation timestamp (defaults to current time)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime asOf) {
        FareCalculationResponse response = bookingService.calculateCurrentFare(id, null, asOf);
        return ResponseEntity.ok(ApiResponse.ok("Real-time fare calculated successfully", response));
    }

    @GetMapping("/fare")
    @Operation(summary = "Real-time Fare Calculation by Vehicle Number", description = "Calculates the total charge accrued at any given time for an active vehicle number without checking out.")
    public ResponseEntity<ApiResponse<FareCalculationResponse>> calculateFareByVehicle(
            @RequestParam String vehicleNumber,
            @Parameter(description = "Optional as-of calculation timestamp (defaults to current time)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime asOf) {
        FareCalculationResponse response = bookingService.calculateCurrentFare(null, vehicleNumber, asOf);
        return ResponseEntity.ok(ApiResponse.ok("Real-time fare calculated successfully", response));
    }
}
