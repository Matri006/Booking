package com.mary.booking.dto.auth;

import com.mary.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponse {
    private Long id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BookingStatus status;

    private String roomName;

    private String username;

    private String displayStatus;
}
