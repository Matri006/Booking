package com.mary.booking.controller;

import com.mary.booking.dto.auth.BookingRequest;
import com.mary.booking.dto.auth.BookingResponse;
import com.mary.booking.entity.Booking;
import com.mary.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingResponse createBooking(
           @Valid @RequestBody BookingRequest request
    ) {

        return bookingService.createBooking(request);
    }

    @GetMapping
    public List<BookingResponse> getAllBookings() {

        return bookingService.getAllBookings();
    }

    @GetMapping("/my")
    public List<BookingResponse> getMyBookings(
            Authentication authentication
    ) {

        return bookingService.getMyBookings(
                authentication.getName()
        );
    }

    @PutMapping("/{id}/cancel")
    public void cancelBooking(
            @PathVariable Long id,
            Authentication authentication
    ) {

        bookingService.cancelBooking(
                id,
                authentication.getName()
        );
    }
}
