package com.mary.booking.controller;


import com.mary.booking.dto.auth.BookingResponse;
import com.mary.booking.entity.User;
import com.mary.booking.repository.UserRepository;
import com.mary.booking.service.BookingService;
import com.mary.booking.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserWebController {
    private final BookingService bookingService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        List<BookingResponse> activeBookings = bookingService.getMyBookings(email);
        List<BookingResponse> historyBookings = bookingService.getMyBookingHistory(email);
        System.out.println("History bookings count: " + historyBookings.size());
        historyBookings.forEach(b -> System.out.println("Booking: " + b.getTitle() + ", Status: " + b.getStatus()));
        long unreadCount = notificationService.getUnreadCount(email);
        model.addAttribute("user", user);
        model.addAttribute("activeBookings", activeBookings);
        model.addAttribute("historyBookings", historyBookings);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("title", "Личный кабинет");
        return "dashboard";
    }
}
