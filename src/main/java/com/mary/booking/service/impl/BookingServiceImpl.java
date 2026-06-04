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
import com.mary.booking.service.NotificationService;
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
    private final NotificationService notificationService;

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
                .filter(b -> b.getStatus() != BookingStatus.CANCELLED)
                .filter(b -> b.getStartTime().isAfter(LocalDateTime.now()))
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

        notificationService.createCancellationNotification(booking);
    }

    @Override
    public List<BookingResponse> getMyBookingHistory(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return bookingRepository.findByUser(user)
                .stream()
                .filter(b -> b.getStartTime().isBefore(LocalDateTime.now())
                        || b.getStatus() == BookingStatus.CANCELLED)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Booking getBookingById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + id));
    }

    @Override
    public Booking updateBooking(Long id, BookingUpdateRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        booking.setTitle(request.getTitle());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());

        return bookingRepository.save(booking);
    }

    @Override
    public BookingResponse createBooking(BookingRequest request) {

        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        boolean hasConflict = bookingRepository.existsConflictingBooking(
                room.getId(),
                request.getStartTime(),
                request.getEndTime()
        );

        if (hasConflict) {
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

        Booking saved = bookingRepository.save(booking);

        notificationService.createBookingNotification(saved);

        return mapToResponse(saved);
    }

    private BookingResponse mapToResponse(Booking booking) {
        String displayStatus;
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            displayStatus = "CANCELLED";
        } else if (booking.getEndTime().isBefore(LocalDateTime.now())) {
            displayStatus = "COMPLETED";
        } else {
            displayStatus = "ACTIVE";
        }

        return BookingResponse.builder()
                .id(booking.getId())
                .roomName(booking.getRoom().getName())
                .title(booking.getTitle())
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .status(booking.getStatus())
                .displayStatus(displayStatus)
                .username(booking.getUser().getUsername())
                .build();
    }
    @Override
    public List<LocalTime> getAvailableTimeSlots(Long roomId, LocalDate date) { //todo удалить из интерфейса
        return new ArrayList<>();
    }
}