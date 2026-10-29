package com.parkinglot.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parkinglot.dto.request.BookingCheckoutRequest;
import com.parkinglot.dto.request.BookingEntryRequest;
import com.parkinglot.dto.response.BookingResponse;
import com.parkinglot.entity.BookingStatus;
import com.parkinglot.entity.PaymentStatus;
import com.parkinglot.exception.DuplicateActiveBookingException;
import com.parkinglot.exception.ResourceNotFoundException;
import com.parkinglot.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
@ActiveProfiles("test")
@DisplayName("Booking Controller REST Endpoint Tests")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    @DisplayName("POST /api/v1/bookings/entry returns 201 Created on valid request")
    void testBookEntrySuccess() throws Exception {
        BookingEntryRequest request = new BookingEntryRequest("MH12AB1234");

        BookingResponse response = new BookingResponse();
        response.setBookingId(1L);
        response.setVehicleNumber("MH12AB1234");
        response.setSlotId(1L);
        response.setSlotNumber("A1");
        response.setBookingStatus(BookingStatus.ACTIVE);
        response.setPaymentStatus(PaymentStatus.PENDING);
        response.setEntryTime(LocalDateTime.now());

        when(bookingService.bookEntry(any(BookingEntryRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bookings/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.vehicleNumber").value("MH12AB1234"))
                .andExpect(jsonPath("$.data.slotNumber").value("A1"));
    }

    @Test
    @DisplayName("POST /api/v1/bookings/entry returns 400 Bad Request on blank vehicle number")
    void testBookEntryValidationFailure() throws Exception {
        BookingEntryRequest request = new BookingEntryRequest("");

        mockMvc.perform(post("/api/v1/bookings/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("POST /api/v1/bookings/entry returns 409 Conflict on duplicate active booking")
    void testBookEntryDuplicateConflict() throws Exception {
        BookingEntryRequest request = new BookingEntryRequest("MH12AB1234");

        when(bookingService.bookEntry(any(BookingEntryRequest.class)))
                .thenThrow(new DuplicateActiveBookingException("Vehicle already has active booking"));

        mockMvc.perform(post("/api/v1/bookings/entry")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Vehicle already has active booking"));
    }

    @Test
    @DisplayName("POST /api/v1/bookings/checkout returns 200 OK on valid checkout")
    void testCheckoutSuccess() throws Exception {
        BookingCheckoutRequest request = new BookingCheckoutRequest(1L);

        BookingResponse response = new BookingResponse();
        response.setBookingId(1L);
        response.setVehicleNumber("MH12AB1234");
        response.setSlotNumber("A1");
        response.setFeeAmount(new BigDecimal("100.00"));
        response.setBookingStatus(BookingStatus.COMPLETED);
        response.setPaymentStatus(PaymentStatus.PAID);

        when(bookingService.checkout(any(BookingCheckoutRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/bookings/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.feeAmount").value(100.00))
                .andExpect(jsonPath("$.data.bookingStatus").value("COMPLETED"));
    }

    @Test
    @DisplayName("GET /api/v1/bookings/{id} returns 404 Not Found when ID does not exist")
    void testGetBookingNotFound() throws Exception {
        when(bookingService.getBookingById(999L))
                .thenThrow(new ResourceNotFoundException("Booking not found with ID: 999"));

        mockMvc.perform(get("/api/v1/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Booking not found with ID: 999"));
    }
}
