package com.bucket.thingstodobeforedie.dto;

import lombok.Builder;

/**
 * DTO for authentication responses
 */
@Builder
public record AuthResponse(
    String token,
    String tokenType,
    Long userId,
    String username,
    String email,
    String role,
    String fullName,
    String profileImage
) {
}