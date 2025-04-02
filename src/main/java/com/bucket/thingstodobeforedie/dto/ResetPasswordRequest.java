package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

/**
 * Request for resetting a user's password
 */
public record ResetPasswordRequest(
    @NotEmpty(message = "Token is required")
    String token,
    
    @NotEmpty(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    String password,
    
    @NotEmpty(message = "Password confirmation is required")
    String confirmPassword
) {} 