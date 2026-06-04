package com.mary.booking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private String name;
    private Integer capacity;
    private String location;
    private Boolean hasProjector;
    private Boolean hasWhiteboard;
    private Boolean isActive;
    private RoomAvailabilityStatus availabilityStatus;
    private LocalDateTime currentBookingEndTime;
    private List<TimeSlotDto> timeSlots;

    public enum RoomAvailabilityStatus {
        FREE, BUSY, UNAVAILABLE
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TimeSlotDto {
        private LocalDateTime start;
        private LocalDateTime end;
        private Boolean isAvailable;
    }
}
