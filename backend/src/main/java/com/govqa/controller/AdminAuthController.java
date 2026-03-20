package com.govqa.controller;

import com.govqa.common.ApiResponse;
import com.govqa.dto.LoginRequest;
import com.govqa.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<Map<String, String>> login(@Valid @RequestBody LoginRequest req) {
        String token = authService.login(req.getUsername(), req.getPassword());
        return ApiResponse.ok(Map.of("token", token));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ApiResponse.ok(null);
    }
}
