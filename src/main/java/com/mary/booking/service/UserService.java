package com.mary.booking.service;

import com.mary.booking.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    User changeUserRole(Long id, String role);
    User save(User user);
}
