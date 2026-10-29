package com.parkinglot.service;

import com.parkinglot.dto.request.BookingCheckoutRequest;
import com.parkinglot.dto.request.BookingEntryRequest;
import com.parkinglot.dto.response.BookingResponse;
import com.parkinglot.dto.response.FareCalculationResponse;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    BookingResponse bookEntry(BookingEntryRequest request);

    BookingResponse checkout(BookingCheckoutRequest request);

    List<BookingResponse> getActiveBookings();

    BookingResponse getBookingById(Long bookingId);

    FareCalculationResponse calculateCurrentFare(Long bookingId, String vehicleNumber, LocalDateTime asOfTime);
}
