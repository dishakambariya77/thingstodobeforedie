package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

/**
 * Request for initiating a forgot password flow
 */
public record ForgotPasswordRequest(
    @NotEmpty(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    String email
) {} 