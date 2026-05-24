package com.mary.booking.controller;

import com.mary.booking.dto.auth.BookingRequest;
import com.mary.booking.entity.Room;
import com.mary.booking.service.BookingService;
import com.mary.booking.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingWebController {
    private final BookingService bookingService;
    private final RoomService roomService;

    @GetMapping("/new")
    public String showBookingForm(
            @RequestParam(required = false) Long roomId,
            Model model
    ) {
        List<Room> rooms = roomService.getAllRooms();

        BookingRequest bookingRequest = new BookingRequest();
        if (roomId != null) {
            bookingRequest.setRoomId(roomId);
            Room selectedRoom = roomService.getRoomById(roomId);
            model.addAttribute("selectedRoom", selectedRoom);
        }

        model.addAttribute("rooms", rooms);
        model.addAttribute("booking", bookingRequest);
        model.addAttribute("title", "Новая встреча");

        return "form";
    }

    @PostMapping("/create")
    public String createBooking(
            @Valid @ModelAttribute("booking") BookingRequest request,
            BindingResult result,
            Authentication authentication,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("title", "Новая встреча");
            return "form";
        }

        try {
            bookingService.createBooking(request);
            return "redirect:/user/dashboard?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("booking", request);
            model.addAttribute("title", "Новая встреча");
            return "form";
        }
    }

    @GetMapping("/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, Authentication authentication) {
        bookingService.cancelBooking(id, authentication.getName());
        return "redirect:/user/dashboard?cancelled=true";
    }

    @GetMapping("/available-times")
    @ResponseBody
    public List<LocalTime> getAvailableTimes(
            @RequestParam Long roomId,
            @RequestParam String date
    ) {
        LocalDate bookingDate = LocalDate.parse(date);
        return bookingService.getAvailableTimeSlots(roomId, bookingDate);
    }
}
