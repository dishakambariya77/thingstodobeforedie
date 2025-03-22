package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.UserProfileResponse;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.BlogPostRepository;
import com.bucket.thingstodobeforedie.repository.BucketListRepository;
import com.bucket.thingstodobeforedie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final BucketListRepository bucketListRepository;

    /**
     * Get current authenticated user
     */
    @Transactional(readOnly = true)
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    /**
     * Get user profile by username
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        
        int bucketListsCount = bucketListRepository.findByUserOrderByCreatedAtDesc(user, null).getSize();
        int blogPostsCount = blogPostRepository.findByUserOrderByCreatedAtDesc(user, null).getSize();
        
        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getBio(),
                user.getProfileImage(),
                user.getRole().name(),
                bucketListsCount,
                blogPostsCount,
                user.getCreatedAt()
        );
    }

    /**
     * Get current user profile
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return getUserProfile(user.getUsername());
    }
} 