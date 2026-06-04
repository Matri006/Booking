package com.mary.booking.service;

import com.mary.booking.dto.auth.RoomResponse;
import com.mary.booking.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();

    Room getRoomById(Long id);

    Room createRoom(Room room);

    Room updateRoom(Long id, Room room);

    void deleteRoom(Long id);

    Page<Room> getAllRooms(Pageable pageable);

    public Page<Room> searchRooms(String search, Pageable pageable);

    RoomResponse getRoomStatus(Long roomId, LocalDate date);

    List<RoomResponse> getAllRoomsStatus(LocalDate date);

    Page<RoomResponse> getRoomsStatusPage(Pageable pageable, LocalDate date, String search);
}
