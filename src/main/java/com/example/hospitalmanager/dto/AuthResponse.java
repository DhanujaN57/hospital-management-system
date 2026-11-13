package com.example.hospitalmanager.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        UserResponse user
) {}
