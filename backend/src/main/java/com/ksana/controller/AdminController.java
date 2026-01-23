package com.ksana.controller;

import com.ksana.repository.UserRepository;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin APIs")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ✅ BASIC ADMIN CHECK
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of(
                "message", "Welcome Admin",
                "status", "UP"
        );
    }

    // ✅ SIMPLE STATISTICS
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/stats")
    public Map<String, Object> stats() {
        return Map.of(
                "totalUsers", userRepository.count(),
                "timestamp", System.currentTimeMillis()
        );
    }
}
