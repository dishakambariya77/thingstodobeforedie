package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.*;
import com.bucket.thingstodobeforedie.entity.PasswordResetToken;
import com.bucket.thingstodobeforedie.entity.Role;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.AuthenticationException;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.PasswordResetTokenRepository;
import com.bucket.thingstodobeforedie.repository.UserRepository;
import com.bucket.thingstodobeforedie.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    /**
     * Authenticate a user and generate JWT token
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.username(),
                            loginRequest.password()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(loginRequest.username())
                    .orElseThrow(() -> new ResourceNotFoundException("User", "username", loginRequest.username()));

            String token = tokenProvider.createToken(user);

            return new AuthResponse(
                    token,
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name()
            );
        } catch (org.springframework.security.core.AuthenticationException e) {
            throw new AuthenticationException("Invalid username/password supplied", e);
        }
    }

    /**
     * Register a new user
     */
    @Transactional
    public AuthResponse registerUser(RegistrationRequest registrationRequest) {

        // Check if email is already in use
        if (userRepository.existsByEmail(registrationRequest.email())) {
            throw new AuthenticationException("Email is already in use");
        }

        // Create new user
        User user = User.builder()
                .username(registrationRequest.email())
                .email(registrationRequest.email())
                .password(passwordEncoder.encode(registrationRequest.password()))
                .fullName(registrationRequest.fullName())
                .bio(registrationRequest.bio())
                .profileImage(registrationRequest.profileImage())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        String token = tokenProvider.createToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole().name()
        );
    }

    /**
     * Process a forgot password request by creating a token and sending an email
     *
     * @param request The forgot password request
     * @return true if the email was sent, false otherwise
     */
    @Transactional
    public boolean processForgotPasswordRequest(ForgotPasswordRequest request) {
        String email = request.getEmail();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.info("Forgot password request for non-existent email: {}", email);
            return false;
        }

        User user = userOptional.get();

        // Delete any existing tokens for this user
        tokenRepository.deleteByUser(user);

        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDateTime(LocalDateTime.now().plusMinutes(30)) // 30 minutes expiry
                .used(false)
                .build();

        tokenRepository.save(resetToken);

        // Send email
        emailService.sendPasswordResetEmail(email, token);

        return true;
    }

    /**
     * Validate a reset token
     *
     * @param token The token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean validateResetToken(String token) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(token);

        if (tokenOptional.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOptional.get();

        // Check if token is expired or used
        return !resetToken.isExpired() && !resetToken.isUsed();
    }

    /**
     * Reset a user's password using a token
     *
     * @param request The reset password request
     * @return true if the password was reset, false otherwise
     */
    @Transactional
    public boolean resetPassword(ResetPasswordRequest request) {
        Optional<PasswordResetToken> tokenOptional = tokenRepository.findByToken(request.getToken());

        if (tokenOptional.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOptional.get();

        // Check if token is expired or used
        if (resetToken.isExpired() || resetToken.isUsed()) {
            return false;
        }

        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // Send confirmation email
        emailService.sendPasswordChangeConfirmationEmail(user.getEmail());

        return true;
    }
} 