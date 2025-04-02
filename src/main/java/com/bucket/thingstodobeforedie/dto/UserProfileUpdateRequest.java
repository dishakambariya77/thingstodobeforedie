package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import java.util.List;

/**
 * Request for updating a user's profile
 */
@Builder
public record UserProfileUpdateRequest(
    @Size(max = 50, message = "Full name must be less than 50 characters")
    String fullName,
    
    @Size(max = 500, message = "Bio must be less than 500 characters")
    String bio,
    
    String profileImage,
    
    @Size(max = 100, message = "Location must be less than 100 characters")
    String location,
    
    @Size(max = 100, message = "Website URL must be less than 100 characters")
    String website,
    
    @Size(max = 255, message = "Social links must be less than 255 characters")
    String socialLinks,
    
    List<String> interests
) {} 