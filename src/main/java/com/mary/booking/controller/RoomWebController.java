package com.mary.booking.controller;

import com.mary.booking.dto.auth.RoomResponse;
import com.mary.booking.entity.Room;
import com.mary.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/rooms")
@RequiredArgsConstructor
public class RoomWebController {
    private final RoomService roomService;

    @GetMapping
    public String listRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        if (date == null) {
            date = LocalDate.now();
        }
        Page<RoomResponse> roomsWithStatus = roomService.getRoomsStatusPage(
                PageRequest.of(page, 6),
                date,
                search
        );
//        Page<Room> rooms;
//        if (search != null && !search.isEmpty()) {
//            rooms = roomService.searchRooms(search, PageRequest.of(page, 6));
//        } else {
//            rooms = roomService.getAllRooms(PageRequest.of(page, 6));
//        }

        model.addAttribute("rooms", roomsWithStatus);
        model.addAttribute("search", search);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomsWithStatus.getTotalPages());
        model.addAttribute("selectedDate", date);
        model.addAttribute("title", "Список переговорных комнат");

        return "list";
    }
}
