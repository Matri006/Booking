package com.mary.booking.controller;

import com.mary.booking.dto.auth.BookingRequest;
import com.mary.booking.entity.Booking;
import com.mary.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(
            @RequestBody BookingRequest request
    ) {

        return bookingService.createBooking(request);
    }

    @GetMapping
    public List<Booking> getAllBookings() {

        return bookingService.getAllBookings();
    }

}
