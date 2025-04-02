package com.bucket.thingstodobeforedie.controller;

import com.bucket.thingstodobeforedie.dto.AuthResponse;
import com.bucket.thingstodobeforedie.dto.SocialLoginRequest;
import com.bucket.thingstodobeforedie.service.SocialLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication API")
public class SocialAuthController {

    private final SocialLoginService socialLoginService;

    @PostMapping("/social/login")
    @Operation(summary = "Login with a social provider", description = "Login with a social provider like Google or Facebook")
    public ResponseEntity<AuthResponse> socialLogin(@Valid @RequestBody SocialLoginRequest request) {
        log.debug("Processing social login request for provider: {}", request.provider());
        AuthResponse response = socialLoginService.loginWithSocialProvider(request);
        return ResponseEntity.ok(response);
    }
} 