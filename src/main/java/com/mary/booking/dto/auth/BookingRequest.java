package com.mary.booking.dto.auth;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long roomId;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
