package com.ksana.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/sales")
@Tag(name = "Sales APIs")
@SecurityRequirement(name = "bearerAuth")
public class SalesController {

    // ✅ SALES DASHBOARD
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard(Authentication authentication) {

        return Map.of(
                "message", "Welcome Sales User",
                "username", authentication.getName(),
                "roles", authentication.getAuthorities()
        );
    }

    // ✅ SALES PROFILE (basic)
    @PreAuthorize("hasAnyRole('ADMIN','SALES')")
    @GetMapping("/profile")
    public Map<String, Object> profile(Authentication authentication) {

        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities()
        );
    }
}
