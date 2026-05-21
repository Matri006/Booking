package com.mary.booking.service.impl;

import com.mary.booking.dto.auth.BookingRequest;
import com.mary.booking.entity.Booking;
import com.mary.booking.entity.Room;
import com.mary.booking.entity.User;
import com.mary.booking.repository.BookingRepository;
import com.mary.booking.repository.RoomRepository;
import com.mary.booking.repository.UserRepository;
import com.mary.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Override
    public Booking createBooking(BookingRequest request) {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow();

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow();

        boolean exists = bookingRepository
                .existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        room.getId(),
                        request.getEndTime(),
                        request.getStartTime()
                );

        if (exists) {
            throw new RuntimeException(
                    "Room already booked for this time"
            );
        }

        Booking booking = Booking.builder()
                .title(request.getTitle())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .room(room)
                .user(user)
                .build();

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
