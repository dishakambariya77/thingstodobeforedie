package com.bucket.thingstodobeforedie.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Response with user profile information
 */
@Builder
public record UserProfileResponse(
    // Basic user information
    Long id,
    String username,
    String email,
    String fullName,
    String bio,
    String profileImage,
    String role,
    
    // Stats
    int totalBucketLists,
    int completedBucketLists,
    int activeBucketLists,
    int totalBlogPosts,
    
    // Timestamps
    LocalDateTime createdAt,
    LocalDateTime lastActive,
    
    // Additional user details (can be null)
    String location,
    String website,
    String socialLinks, // Could be JSON or comma-separated values
    List<String> interests, // User's interests or hobbies
    
    // Recent activity
    List<ActivityResponse> recentActivities,
    
    // Achievements
    List<AchievementResponse> achievements,
    
    // Profile completion percentage (calculated based on filled fields)
    int profileCompletionPercentage
) {} 