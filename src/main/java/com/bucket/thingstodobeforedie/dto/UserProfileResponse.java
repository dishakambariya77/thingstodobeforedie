package com.bucket.thingstodobeforedie.dto;

import java.time.LocalDateTime;

/**
 * DTO for user profile responses
 */
public record UserProfileResponse(
    Long id,
    String username,
    String email,
    String fullName,
    String bio,
    String profileImage,
    String role,
    int bucketListsCount,
    int blogPostsCount,
    LocalDateTime createdAt
) {} 