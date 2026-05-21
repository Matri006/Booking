package com.mary.booking.service;

import com.mary.booking.entity.Room;

import java.util.List;

public interface RoomService {
    List<Room> getAllRooms();

    Room getRoomById(Long id);

    Room createRoom(Room room);

    Room updateRoom(Long id, Room room);

    void deleteRoom(Long id);
}
