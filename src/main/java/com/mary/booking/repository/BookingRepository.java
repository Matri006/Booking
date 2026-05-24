package com.mary.booking.repository;

import com.mary.booking.entity.Booking;
import com.mary.booking.entity.Room;
import com.mary.booking.entity.User;
import com.mary.booking.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    List<Booking> findByUser(User user);



    List<Booking> findByUserAndStartTimeAfterOrderByStartTimeAsc(User user, LocalDateTime now);


    List<Booking> findByUserAndStartTimeBeforeOrderByStartTimeDesc(User user, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.room = :room AND DATE(b.startTime) = DATE(:dateTime)")
    List<Booking> findByRoomAndStartTimeDate(@Param("room") Room room, @Param("dateTime") LocalDateTime dateTime);

    List<Booking> findByRoomAndStartTimeBetween(Room room, LocalDateTime startOfDay, LocalDateTime endOfDay);
}
