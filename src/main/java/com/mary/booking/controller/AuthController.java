package com.mary.booking.controller;

import com.mary.booking.dto.auth.AuthResponse;
import com.mary.booking.dto.auth.LoginRequest;
import com.mary.booking.dto.auth.RegisterRequest;
import com.mary.booking.dto.auth.UserResponse;
import com.mary.booking.entity.User;
import com.mary.booking.repository.UserRepository;
import com.mary.booking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public AuthResponse register(
            @RequestBody @Valid RegisterRequest request
    ) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @RequestBody LoginRequest request
    ) {

        return authService.login(request);
    }

    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow();

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
