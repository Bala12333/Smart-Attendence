package com.smartattendance.controller;

import com.smartattendance.model.Role;
import com.smartattendance.model.User;
import com.smartattendance.service.AuthService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    private record RegisterRequest(@NotBlank String username, @NotBlank String password, @NotBlank String role, String fullName) {}

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        return authService.login(body.username(), body.password())
                .<ResponseEntity<?>>map(t -> ResponseEntity.ok(Map.of("token", t)))
                .orElseGet(() -> ResponseEntity.status(401).body(Map.of("error", "Invalid credentials")));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest body) {
        Role role = Role.valueOf(body.role().toUpperCase());
        User user = authService.register(body.username(), body.password(), role, body.fullName());
        return ResponseEntity.ok(Map.of("id", user.getId()));
    }
}


