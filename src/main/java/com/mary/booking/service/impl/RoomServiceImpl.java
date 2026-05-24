package com.mary.booking.service.impl;

import com.mary.booking.entity.Room;
import com.mary.booking.repository.RoomRepository;
import com.mary.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));
    }

    @Override
    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    @Override
    public Room updateRoom(Long id, Room updatedRoom) {

        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        room.setName(updatedRoom.getName());
        room.setCapacity(updatedRoom.getCapacity());
        room.setDescription(updatedRoom.getDescription());
        room.setHasProjector(updatedRoom.getHasProjector());
        room.setHasWhiteboard(updatedRoom.getHasWhiteboard());
        room.setLocation(updatedRoom.getLocation());
        room.setIsActive(updatedRoom.getIsActive());

        return roomRepository.save(room);
    }

    @Override
    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    @Override
    public Page<Room> getAllRooms(Pageable pageable) {
        return roomRepository.findAll(pageable);
    }

    @Override
    public Page<Room> searchRooms(String search, Pageable pageable) {
        return roomRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(search, search, pageable);
    }
}
