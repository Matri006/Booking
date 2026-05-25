package com.mary.booking.controller;

import com.mary.booking.entity.Room;
import com.mary.booking.entity.User;
import com.mary.booking.service.RoomService;
import com.mary.booking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminWebController {
    private final UserService userService;
    private final RoomService roomService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("title", "Admin Panel");
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("title", "Manage Users");
        return "admin/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserPage(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getUserById(id));
        return "admin/user-edit";
    }
    @PostMapping("/users/edit/{id}")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User formUser,
                             @RequestParam(required = false) String newPassword) {

        User existing = userService.getUserById(id);

        existing.setUsername(formUser.getUsername());
        existing.setEmail(formUser.getEmail());
        existing.setRole(formUser.getRole());
        existing.setEnabled(formUser.getEnabled());

        if (newPassword != null && !newPassword.isBlank()) {
            existing.setPassword(passwordEncoder.encode(newPassword));
        }

        userService.save(existing);

        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/rooms")
    public String manageRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        model.addAttribute("title", "Manage Rooms");
        return "admin/rooms";
    }

    @PostMapping("/rooms/delete/{id}")
    public String deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return "redirect:/admin/rooms?deleted=true";
    }

    @GetMapping("/users/create")
    public String createUserPage(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-create";
    }
    @PostMapping("/users/create")
    public String create(@ModelAttribute User user) {
        userService.createUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/rooms/create")
    public String createRoomPage(Model model) {
        model.addAttribute("room", new Room());
        return "admin/room-create";
    }

    @PostMapping("/rooms/create")
    public String createRoom(@ModelAttribute Room room) {
        roomService.createRoom(room);
        return "redirect:/admin/rooms";
    }

    @GetMapping("/rooms/edit/{id}")
    public String editRoomPage(@PathVariable Long id, Model model) {
        model.addAttribute("room", roomService.getRoomById(id));
        return "admin/room-edit";
    }

    @PostMapping("/rooms/edit/{id}")
    public String updateRoom(@PathVariable Long id,
                             @ModelAttribute Room room) {
        roomService.updateRoom(id, room);
        return "redirect:/admin/rooms";
    }

}
