package com.mytrainer.backend.controllers;

import com.mytrainer.backend.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        Map<String,Object> result = authService.register(req.name(), req.email());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String token = authService.login(req.code());
        return ResponseEntity.ok(Map.of("token", token));
    }

    public static record RegisterRequest(String name, String email) {}
    public static record LoginRequest(String code) {}
}
