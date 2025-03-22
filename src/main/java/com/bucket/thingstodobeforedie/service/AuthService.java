package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.AuthResponse;
import com.bucket.thingstodobeforedie.dto.LoginRequest;
import com.bucket.thingstodobeforedie.dto.RegistrationRequest;
import com.bucket.thingstodobeforedie.entity.Role;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.AuthenticationException;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

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
} 