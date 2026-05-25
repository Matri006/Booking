package com.mary.booking.controller;

import com.mary.booking.dto.auth.BookingUpdateRequest;
import com.mary.booking.entity.Booking;
import com.mary.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingEditController {
    private final BookingService bookingService;

    @GetMapping("/edit/{id}")
    public String editBooking(@PathVariable Long id,
                              Authentication auth,
                              Model model) {
        Booking booking = bookingService.getBookingById(id);
        if (!booking.getUser().getEmail().equals(auth.getName())) {
            return "redirect:/access-denied";
        }
        model.addAttribute("booking", booking);
        return "bookings/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateBooking(@PathVariable Long id,
                                @ModelAttribute BookingUpdateRequest booking,
                                Authentication auth) {

        Booking existing = bookingService.getBookingById(id);
        if (!existing.getUser().getEmail().equals(auth.getName())) {
            return "redirect:/access-denied";
        }
        bookingService.updateBooking(id, booking);
        return "redirect:/bookings/my";
    }

    @GetMapping("/my")
    public String myBookings(Authentication auth, Model model) {
        model.addAttribute("bookings",
                bookingService.getMyBookings(auth.getName()));
        return "redirect:/user/dashboard";
    }

    @PostMapping("/cancel/{id}")
    public String cancelBookingUi(@PathVariable Long id,
                                  Authentication auth) {

        bookingService.cancelBooking(id, auth.getName());
        return "redirect:/user/dashboard";
    }
}
