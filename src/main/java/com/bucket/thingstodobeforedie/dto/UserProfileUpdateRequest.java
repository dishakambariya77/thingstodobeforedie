package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {
    
    @Size(max = 50, message = "Full name must be less than 50 characters")
    private String fullName;
    
    @Size(max = 500, message = "Bio must be less than 500 characters")
    private String bio;
    
    private String profileImage;
    
    @Size(max = 100, message = "Location must be less than 100 characters")
    private String location;
    
    @Size(max = 100, message = "Website URL must be less than 100 characters")
    private String website;
    
    @Size(max = 255, message = "Social links must be less than 255 characters")
    private String socialLinks;
    
    private List<String> interests;
} 