package com.krushna.fileserver.controller;

import com.krushna.fileserver.entity.Activity;
import com.krushna.fileserver.entity.User;
import com.krushna.fileserver.repository.ActivityRepository;
import com.krushna.fileserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ActivityRepository activityRepository;

    @GetMapping("/all-users")
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @GetMapping("/all-activities")
    public List<Activity> getAllActivities() {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));


        if (!isAdmin(user)) {
            throw new RuntimeException("Access Denied");
        }

        return activityRepository.findAll();
    }

    @DeleteMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable Long id) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!isAdmin(admin)) {
            throw new RuntimeException("Access Denied");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Target user not found"));

        userRepository.delete(user);

        return "User deleted successfully";
    }

    @PutMapping("/create-admin/{id}")
    public String makeAdmin(@PathVariable Long id) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User admin = userRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        if (!isAdmin(admin)) {
            throw new RuntimeException("Access Denied");
        }

        User targetUser = userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Target user not found"));

        targetUser.setRole("ADMIN");

        userRepository.save(targetUser);

        return "User promoted to ADMIN";
    }

    private boolean isAdmin(User user) {
        if (user == null) {
            return false;
        }
        String role = user.getRole();
        if (role != null) {
            String r = role.trim();
            if ("ADMIN".equalsIgnoreCase(r) || "ROLE_ADMIN".equalsIgnoreCase(r)) {
                return true;
            }
        }
        return user.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equalsIgnoreCase(a.getAuthority()));
    }
}
