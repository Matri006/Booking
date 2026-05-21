package com.mary.booking.dto.auth;

import com.mary.booking.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long id;

    private String username;

    private String email;

    private Role role;
}
