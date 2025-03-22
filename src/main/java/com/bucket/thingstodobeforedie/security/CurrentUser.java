package com.bucket.thingstodobeforedie.security;

import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * Utility class to get the current authenticated user
 */
@Component
@RequiredArgsConstructor
public class CurrentUser {

    private final UserRepository userRepository;

    /**
     * Get the currently authenticated user
     * @return The current user
     * @throws ResourceNotFoundException if user not found
     */
    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }
        
        Object principal = authentication.getPrincipal();
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
    
    /**
     * Get the current user's ID
     * @return The current user's ID
     */
    public Long getUserId() {
        return getUser().getId();
    }
    
    /**
     * Check if the current user is the owner of a resource
     * @param userId The user ID to check against
     * @return true if the current user is the owner, false otherwise
     */
    public boolean isOwner(Long userId) {
        return getUserId().equals(userId);
    }
} 