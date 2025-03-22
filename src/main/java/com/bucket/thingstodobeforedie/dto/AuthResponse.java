package com.bucket.thingstodobeforedie.dto;

/**
 * DTO for authentication responses
 */
public record AuthResponse(
    String token,
    String tokenType,
    Long userId,
    String username,
    String email,
    String role
) {
    public AuthResponse(String token, Long userId, String username, String email, String role) {
        this(token, "Bearer", userId, username, email, role);
    }
} 