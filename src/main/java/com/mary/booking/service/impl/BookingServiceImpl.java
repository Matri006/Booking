package com.mary.booking.service.impl;

import com.mary.booking.dto.auth.BookingRequest;
import com.mary.booking.dto.auth.BookingResponse;
import com.mary.booking.dto.auth.BookingUpdateRequest;
import com.mary.booking.entity.Booking;
import com.mary.booking.entity.Room;
import com.mary.booking.entity.User;
import com.mary.booking.enums.BookingStatus;
import com.mary.booking.repository.BookingRepository;
import com.mary.booking.repository.RoomRepository;
import com.mary.booking.repository.UserRepository;
import com.mary.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        if (request.getEndTime().isBefore(request.getStartTime()) ||
                request.getEndTime().isEqual(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        boolean exists = bookingRepository.existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                room.getId(),
                request.getEndTime(),
                request.getStartTime()
        );

        if (exists) {
            throw new RuntimeException("Room already booked for this time");
        }

        Booking booking = Booking.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .room(room)
                .user(user)
                .status(BookingStatus.ACTIVE)
                .build();

        return mapToResponse(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResponse> getMyBookings(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByUser(user)
                .stream()
                .filter(booking -> booking.getStatus() != BookingStatus.CANCELLED)
                .filter(booking -> booking.getStartTime().isAfter(LocalDateTime.now()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelBooking(Long bookingId, String email) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getEmail().equals(email)) {
            throw new RuntimeException("You cannot cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    public List<BookingResponse> getMyBookingHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return bookingRepository.findByUser(user)
                .stream()
                .filter(booking -> booking.getStartTime().isBefore(LocalDateTime.now())
                        || booking.getStatus() == BookingStatus.CANCELLED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<LocalTime> getAvailableTimeSlots(Long roomId, LocalDate date) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);
        List<Booking> bookings = bookingRepository.findAll()
                .stream()
                .filter(b -> b.getRoom().getId().equals(roomId))
                .filter(b -> b.getStartTime().isAfter(startOfDay) && b.getStartTime().isBefore(endOfDay))
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .collect(Collectors.toList());
        List<LocalTime> availableSlots = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(20, 0);

        while (start.isBefore(end)) {
            final LocalTime slot = start;
            LocalDateTime slotStart = date.atTime(slot);
            LocalDateTime slotEnd = date.atTime(slot.plusHours(1));

            boolean isBooked = bookings.stream().anyMatch(booking ->
                    (slotStart.isBefore(booking.getEndTime()) && slotEnd.isAfter(booking.getStartTime()))
            );

            if (!isBooked) {
                availableSlots.add(slot);
            }
            start = start.plusHours(1);
        }
        return availableSlots;
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
    }

    @Override
    public Booking updateBooking(Long id, BookingUpdateRequest request) {
        Booking existing = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        existing.setTitle(request.getTitle());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        return bookingRepository.save(existing);
    }

    private BookingResponse mapToResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .roomName(booking.getRoom().getName())
                .title(booking.getTitle())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .username(booking.getUser().getUsername())
                .build();
    }
}