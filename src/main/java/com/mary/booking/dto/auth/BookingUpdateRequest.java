package com.mary.booking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BookingUpdateRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
