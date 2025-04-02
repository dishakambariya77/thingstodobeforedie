package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.enums.Role;
import com.bucket.thingstodobeforedie.enums.SocialProvider;
import com.bucket.thingstodobeforedie.repository.UserRepository;
import com.bucket.thingstodobeforedie.security.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        SocialProvider provider = SocialProvider.fromString(registrationId);
        
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String providerId = getProviderId(attributes, provider);
        String email = getEmail(attributes, provider);
        
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user with provider info if they log in with a different provider
            if (provider != SocialProvider.LOCAL && 
                (user.getProvider() == SocialProvider.LOCAL || user.getProvider() != provider)) {
                user.setProvider(provider);
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        } else {
            // Create a new user
            user = registerNewUser(oAuth2UserRequest, providerId, email, provider, attributes);
        }

        return new CustomOAuth2User(user, attributes);
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
                @SuppressWarnings("unchecked")
                Map<String, Object> picture = (Map<String, Object>) attributes.get("picture");
                if (picture != null) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> data = (Map<String, Object>) picture.get("data");
                    if (data != null) {
                        return (String) data.get("url");
                    }
                }
                return null;
            default:
                return null;
        }
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, String providerId, String email, 
                                SocialProvider provider, Map<String, Object> attributes) {
        User user = new User();
        user.setProvider(provider);
        user.setProviderId(providerId);
        user.setEmail(email);
        user.setUsername(email);
        user.setPassword(UUID.randomUUID().toString()); // Generate a random password
        user.setFullName(getName(attributes, provider));
        user.setProfileImage(getProfileImage(attributes, provider));
        user.setLastActive(LocalDateTime.now());
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

} 