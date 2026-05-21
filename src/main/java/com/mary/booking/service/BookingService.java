package com.mary.booking.service;

import com.mary.booking.dto.auth.BookingRequest;
import com.mary.booking.entity.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(BookingRequest request);

    List<Booking> getAllBookings();
}
