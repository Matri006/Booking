package com.mary.booking.service;

import com.mary.booking.entity.Booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking);

    List<Booking> getAllBookings();
}
