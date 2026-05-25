package com.mary.booking.controller;

import com.mary.booking.dto.auth.UserResponse;
import com.mary.booking.entity.User;
import com.mary.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;

    @GetMapping("/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }

    @GetMapping("/users/create")
    public String createUserPage(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-create";
    }

    @PostMapping("/users/create")
    public String createUser(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/admin/users";
    }

//    @PostMapping("/users")
//    public UserResponse createUser(@RequestBody User user) {
//        User created = userService.createUser(user);
//        return new UserResponse(
//                created.getId(),
//                created.getUsername(),
//                created.getEmail(),
//                created.getRole()
//        );
//    }

    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PutMapping("/users/{id}/role")
    public UserResponse changeUserRole(@PathVariable Long id, @RequestParam String role) {
        User updated = userService.changeUserRole(id, role);
        return new UserResponse(
                updated.getId(),
                updated.getUsername(),
                updated.getEmail(),
                updated.getRole()
        );
    }

}
