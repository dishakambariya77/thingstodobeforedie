package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.AuthResponse;
import com.bucket.thingstodobeforedie.dto.SocialLoginRequest;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.enums.Role;
import com.bucket.thingstodobeforedie.enums.SocialProvider;
import com.bucket.thingstodobeforedie.repository.UserRepository;
import com.bucket.thingstodobeforedie.security.JwtTokenProvider;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialLoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    // Create HttpClient as a singleton - it's thread-safe and reusable
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Login or register a user using a social provider token
     */
    @Transactional
    public AuthResponse loginWithSocialProvider(SocialLoginRequest request) {
        SocialProvider provider = SocialProvider.fromString(request.provider());
        
        if (provider == SocialProvider.LOCAL) {
            throw new BadCredentialsException("Invalid social provider: " + request.provider());
        }

        // Verify the token with the provider and get user info
        Map<String, Object> userAttributes = verifyTokenAndGetUserInfo(provider, request.accessToken());
        
        // Extract necessary info from userAttributes
        String email = getEmail(userAttributes, provider);
        String providerId = getProviderId(userAttributes, provider);
        
        if (!StringUtils.hasText(email)) {
            throw new BadCredentialsException("Email not found from OAuth2 provider");
        }

        // Check if user exists
        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update user with provider info if needed
            if (user.getProvider() == SocialProvider.LOCAL || 
                (user.getProvider() != provider && !StringUtils.hasText(user.getProviderId()))) {
                user.setProvider(provider);
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        } else {
            // Register new user
            user = createNewUser(provider, providerId, email, userAttributes);
        }

        // Create JWT token
        String token = tokenProvider.createToken(user);
        
        // Return auth response
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(user.getFullName())
                .profileImage(user.getProfileImage())
                .build();
    }
    
    private Map<String, Object> verifyTokenAndGetUserInfo(SocialProvider provider, String accessToken) {
        try {
            String userInfoEndpoint = getUserInfoEndpoint(provider, accessToken);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(userInfoEndpoint))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
                    
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                log.error("Error response from provider {}: Status={}, Body={}", 
                    provider, response.statusCode(), response.body());
                throw new BadCredentialsException("Invalid token or error from provider: " + provider);
            }
            
            // Parse JSON response to Map
            return objectMapper.readValue(response.body(), new TypeReference<>() {
            });
            
        } catch (IOException | InterruptedException e) {
            log.error("Error verifying token with provider {}: {}", provider, e.getMessage());
            // Restore interrupted state
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new BadCredentialsException("Invalid access token for provider: " + provider);
        }
    }
    
    private String getUserInfoEndpoint(SocialProvider provider, String accessToken) {
        return switch (provider) {
            case GOOGLE -> "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
            case FACEBOOK -> "https://graph.facebook.com/me?fields=id,name,email,picture&access_token=" + accessToken;
            default -> throw new BadCredentialsException("Unsupported provider: " + provider);
        };
    }
    
    private String getProviderId(Map<String, Object> attributes, SocialProvider provider) {
        return switch (provider) {
            case GOOGLE -> (String) attributes.get("sub");
            case FACEBOOK -> (String) attributes.get("id");
            default -> null;
        };
    }

    private String getEmail(Map<String, Object> attributes, SocialProvider provider) {
        return switch (provider) {
            case GOOGLE, FACEBOOK -> (String) attributes.get("email");
            default -> null;
        };
    }

    private String getName(Map<String, Object> attributes, SocialProvider provider) {
        return switch (provider) {
            case GOOGLE, FACEBOOK -> (String) attributes.get("name");
            default -> null;
        };
    }

    private String getProfileImage(Map<String, Object> attributes, SocialProvider provider) {
        switch (provider) {
            case GOOGLE:
                return (String) attributes.get("picture");
            case FACEBOOK:
                Object pictureObj = attributes.get("picture");
                if (pictureObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> picture = (Map<String, Object>) pictureObj;
                    Object dataObj = picture.get("data");
                    if (dataObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> data = (Map<String, Object>) dataObj;
                        return (String) data.get("url");
                    }
                }
                return null;
            default:
                return null;
        }
    }
    
    private User createNewUser(SocialProvider provider, String providerId, String email, Map<String, Object> attributes) {
        User user = new User();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setEmail(email);
        user.setUsername(generateUniqueUsername(email));
        user.setPassword(UUID.randomUUID().toString()); // Generate a random secure password
        user.setFullName(getName(attributes, provider));
        user.setProfileImage(getProfileImage(attributes, provider));
        user.setLastActive(LocalDateTime.now());
        user.setRole(Role.USER);
        
        return userRepository.save(user);
    }
    
    private String generateUniqueUsername(String email) {
        String username = email.substring(0, email.indexOf('@'));
        while (userRepository.findByUsername(username).isPresent()) {
            // Append a random number to make the username unique
            username = username + UUID.randomUUID().toString().substring(0, 5);
        }
        return username;
    }

} 