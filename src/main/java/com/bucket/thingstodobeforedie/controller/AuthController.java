package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.*;
import com.bucket.thingstodobeforedie.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegistrationRequest registrationRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.registerUser(registrationRequest));
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request password reset", description = "Send a password reset link to the user's email")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.processForgotPasswordRequest(request);

        // For security reasons, we always return success, even if the email doesn't exist
        return ResponseEntity.ok().body(
                Map.of("message", "If your email is registered with us, you will receive a password reset link shortly.")
        );
    }

    @GetMapping("/validate-token")
    @Operation(summary = "Validate reset token", description = "Check if a password reset token is valid")
    public ResponseEntity<?> validateResetToken(@RequestParam String token) {
        boolean isValid = authService.validateResetToken(token);

        if (isValid) {
            return ResponseEntity.ok().body(
                    Map.of("valid", true)
            );
        } else {
            return ResponseEntity.badRequest().body(
                    Map.of("valid", false, "message", "Invalid or expired token.")
            );
        }
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password", description = "Reset user password with a valid token")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        boolean result = authService.resetPassword(request);

        if (result) {
            return ResponseEntity.ok().body(
                    Map.of("message", "Password has been reset successfully. You can now login with your new password.")
            );
        } else {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Failed to reset password. The token may be invalid or expired.")
            );
        }
    }
} 