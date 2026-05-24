package com.mary.booking.service;

import com.mary.booking.dto.auth.BookingRequest;
import com.mary.booking.dto.auth.BookingResponse;
import com.mary.booking.entity.Booking;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface BookingService {

    BookingResponse createBooking(BookingRequest request);

    List<BookingResponse> getAllBookings();

    List<BookingResponse> getMyBookings(String email);

    void cancelBooking(Long bookingId, String email);

    List<BookingResponse> getMyBookingHistory(String email);

    List<LocalTime> getAvailableTimeSlots(Long roomId, LocalDate date);
}
