package com.bucket.thingstodobeforedie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for user profile responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    // Basic user information
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String bio;
    private String profileImage;
    private String role;
    
    // Stats
    private int totalBucketLists;
    private int completedBucketLists;
    private int activeBucketLists;
    private int totalBlogPosts;
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime lastActive;
    
    // Additional user details (can be null)
    private String location;
    private String website;
    private String socialLinks; // Could be JSON or comma-separated values
    private List<String> interests; // User's interests or hobbies
    
    // Profile completion percentage (calculated based on filled fields)
    private int profileCompletionPercentage;
} 