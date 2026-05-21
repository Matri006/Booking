package com.mary.booking.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomResponse {
    private Long id;

    private String name;

    private Integer capacity;

    private String description;

    private Boolean hasProjector;

    private Boolean hasWhiteboard;

    private String location;

    private Boolean isActive;
}
