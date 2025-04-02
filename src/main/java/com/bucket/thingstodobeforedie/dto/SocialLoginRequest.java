package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.NotEmpty;

public record SocialLoginRequest(
    @NotEmpty(message = "Access token is required")
    String accessToken,
    
    @NotEmpty(message = "Provider is required")
    String provider // "google" or "facebook"
) {} 