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

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId AND b.status != 'CANCELLED' AND ((b.startTime < :endTime AND b.endTime > :startTime))")
    List<Booking> findConflictingBookings(@Param("roomId") Long roomId,
                                          @Param("startTime") LocalDateTime startTime,
                                          @Param("endTime") LocalDateTime endTime);

    @Query("""
SELECT b FROM Booking b
WHERE b.room.id = :roomId
AND b.status <> 'CANCELLED'
AND b.startTime < :end
AND b.endTime > :start
""")
    List<Booking> findBookingsByRoomAndRange(
            @Param("roomId") Long roomId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<Booking> findByUserId(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status = com.mary.booking.enums.BookingStatus.ACTIVE " +
            "AND b.startTime < :endOfDay " +
            "AND b.endTime > :startOfDay")
    List<Booking> findActiveBookingsByRoomAndDate(
            @Param("roomId") Long roomId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.room.id = :roomId " +
            "AND b.status = com.mary.booking.enums.BookingStatus.ACTIVE " +
            "AND b.startTime < :endTime " +
            "AND b.endTime > :startTime")
    boolean existsConflictingBooking(
            @Param("roomId") Long roomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

}
