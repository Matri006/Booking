package com.mary.booking.service.impl;

import com.mary.booking.entity.Booking;
import com.mary.booking.repository.BookingRepository;
import com.mary.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    @Override
    public Booking createBooking(Booking booking) {

        boolean exists = bookingRepository
                .existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
                        booking.getRoom().getId(),
                        booking.getEndTime(),
                        booking.getStartTime()
                );

        if (exists) {
            throw new RuntimeException(
                    "Room already booked for this time"
            );
        }

        return bookingRepository.save(booking);
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
}
