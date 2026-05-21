package com.mary.booking.repository;

import com.mary.booking.entity.Booking;
import com.mary.booking.entity.Room;
import com.mary.booking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    boolean existsByRoomAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Room room,
            BookingStatus status,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
    boolean existsByRoomIdAndStartTimeLessThanAndEndTimeGreaterThan(
            Long roomId,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}
