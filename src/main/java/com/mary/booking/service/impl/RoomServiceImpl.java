package com.mary.booking.service.impl;

import com.mary.booking.dto.auth.RoomResponse;
import com.mary.booking.entity.Booking;
import com.mary.booking.entity.Room;
import com.mary.booking.enums.BookingStatus;
import com.mary.booking.repository.BookingRepository;
import com.mary.booking.repository.RoomRepository;
import com.mary.booking.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

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

    @Override
    public RoomResponse getRoomStatus(Long roomId, LocalDate date) {
        Room room = getRoomById(roomId);

        if (!room.getIsActive()) {
            return RoomResponse.builder()
                    .id(room.getId())
                    .name(room.getName())
                    .capacity(room.getCapacity())
                    .location(room.getLocation())
                    .hasProjector(room.getHasProjector())
                    .hasWhiteboard(room.getHasWhiteboard())
                    .isActive(false)
                    .availabilityStatus(RoomResponse.RoomAvailabilityStatus.UNAVAILABLE)
                    .timeSlots(new ArrayList<>())
                    .build();
        }

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        List<Booking> todayBookings =
                bookingRepository.findActiveBookingsByRoomAndDate(
                        roomId,
                        startOfDay,
                        endOfDay
                );

        RoomResponse.RoomAvailabilityStatus currentStatus;
        LocalDateTime currentBookingEndTime = null;

        if (date.equals(LocalDate.now())) {
            Booking currentBooking = todayBookings.stream()
                    .filter(b -> b.getStartTime().isBefore(now) && b.getEndTime().isAfter(now))
                    .findFirst()
                    .orElse(null);

            if (currentBooking != null) {
                currentStatus = RoomResponse.RoomAvailabilityStatus.BUSY;
                currentBookingEndTime = currentBooking.getEndTime();
            } else {
                currentStatus = RoomResponse.RoomAvailabilityStatus.FREE;
            }
        } else {
            currentStatus = RoomResponse.RoomAvailabilityStatus.FREE;
        }

        List<RoomResponse.TimeSlotDto> timeSlots = generateTimeSlots(date, todayBookings);

        return RoomResponse.builder()
                .id(room.getId())
                .name(room.getName())
                .capacity(room.getCapacity())
                .location(room.getLocation())
                .hasProjector(room.getHasProjector())
                .hasWhiteboard(room.getHasWhiteboard())
                .isActive(true)
                .availabilityStatus(currentStatus)
                .currentBookingEndTime(currentBookingEndTime)
                .timeSlots(timeSlots)
                .build();
    }

    @Override
    public List<RoomResponse> getAllRoomsStatus(LocalDate date) {
        return getAllRooms().stream()
                .filter(Room::getIsActive)
                .map(room -> getRoomStatus(room.getId(), date))
                .collect(Collectors.toList());
    }

    @Override
    public Page<RoomResponse> getRoomsStatusPage(Pageable pageable, LocalDate date, String search) {
        Page<Room> roomsPage;

        if (search != null && !search.isEmpty()) {
            roomsPage = roomRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(search, search, pageable);
        } else {
            roomsPage = roomRepository.findAll(pageable);
        }

        List<RoomResponse> statusResponses = roomsPage.getContent().stream()
                .map(room -> getRoomStatus(room.getId(), date))
                .collect(Collectors.toList());

        return new PageImpl<>(statusResponses, pageable, roomsPage.getTotalElements());
    }

    private List<RoomResponse.TimeSlotDto> generateTimeSlots(
            LocalDate date,
            List<Booking> bookings
    ) {
        List<RoomResponse.TimeSlotDto> slots = new ArrayList<>();

        for (int minutes = 0; minutes < 24 * 60; minutes += 1) {
            LocalDateTime slotStart = date.atTime(minutes / 60, minutes % 60);
            LocalDateTime slotEnd = slotStart.plusMinutes(1);

            boolean isAvailable = bookings.stream()
                    .noneMatch(b ->
                            slotStart.isBefore(b.getEndTime()) &&
                                    slotEnd.isAfter(b.getStartTime())
                    );

            slots.add(
                    RoomResponse.TimeSlotDto.builder()
                            .start(slotStart)
                            .end(slotEnd)
                            .isAvailable(isAvailable)
                            .build()
            );
        }

        return slots;
    }
}