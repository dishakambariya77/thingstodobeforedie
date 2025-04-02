package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.*;
import com.bucket.thingstodobeforedie.entity.ActivityIcon;
import com.bucket.thingstodobeforedie.entity.ActivityType;
import com.bucket.thingstodobeforedie.entity.BucketStatus;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.BlogPostRepository;
import com.bucket.thingstodobeforedie.repository.BucketListRepository;
import com.bucket.thingstodobeforedie.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final BucketListRepository bucketListRepository;
    private final ActivityService activityService;
    private final S3Service s3Service;

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

        return getEnhancedUserProfile(user);
    }

    /**
     * Get enhanced user profile with comprehensive data
     */
    @Transactional(readOnly = true)
    private UserProfileResponse getEnhancedUserProfile(User user) {

        int totalBucketLists = (int) bucketListRepository.countByUserId(user.getId());
        int completedBucketLists = (int) bucketListRepository.countByUserIdAndStatus(user.getId(), BucketStatus.COMPLETED);
        int activeBucketLists = (int) bucketListRepository.countByUserIdAndStatus(user.getId(), BucketStatus.ACTIVE);
        int totalBlogPosts = (int) blogPostRepository.countByUserId(user.getId());

        List<ActivityResponse> recentActivities = activityService.getRecentUserActivities(user, 5);

        List<AchievementResponse> achievements = getUserAchievements(totalBucketLists,completedBucketLists,totalBlogPosts);


        int profileCompletionPercentage = calculateProfileCompletionPercentage(user);

        // Build the enhanced response
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .bio(user.getBio())
                .profileImage(user.getProfileImage())
                .role(user.getRole().name())
                .totalBucketLists(totalBucketLists)
                .completedBucketLists(completedBucketLists)
                .activeBucketLists(activeBucketLists)
                .totalBlogPosts(totalBlogPosts)
                .createdAt(user.getCreatedAt())
                .lastActive(LocalDateTime.now())
                .location(user.getLocation())
                .website(user.getWebsite())
                .socialLinks(user.getSocialLinks())
                .interests(user.getInterests())
                .recentActivities(recentActivities)
                .profileCompletionPercentage(profileCompletionPercentage)
                .achievements(achievements)
                .build();
    }

    private List<AchievementResponse> getUserAchievements(int totalBucketLists, int completedBucketLists, int totalBlogPosts) {
        List<AchievementResponse> achievements = new ArrayList<>();
        achievements.add(new AchievementResponse("Adventurer", "Complete "+ completedBucketLists +" items from your Travel bucket list", "stars", "completed", true, 80));
        achievements.add(new AchievementResponse("Goal Setter", "Create "+totalBucketLists+" different bucket lists", "format_list_bulleted", "completed", true, 100));
        achievements.add(new AchievementResponse("Storyteller", "Write "+totalBlogPosts+" blog posts about your experiences", "edit", "locked", false, 80));
        return achievements;
    }

    @Transactional
    public ImageUploadResponse uploadUserProfileImage(Long userId, MultipartFile file) {
        User currentUser = getCurrentUser();

        try {
            String directory = String.format("users/profiles/%d/%d", currentUser.getId(), userId);

            // Step 1: Get old image URL before uploading
            String oldImageUrl = currentUser.getProfileImage();

            // Step 2: Upload new image
            String newImageUrl = s3Service.uploadFile(file, directory);

            // Step 3: Delete old image from S3 if it exists
            if (oldImageUrl != null) {
                String oldImageKey = oldImageUrl.substring(oldImageUrl.indexOf("users/profiles"));
                s3Service.deleteFile(oldImageKey);
            }

            // Step 4: Update DB with new image URL
            currentUser.setProfileImage(newImageUrl);
            userRepository.save(currentUser);

            // Track profile image update
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("imageUrl", newImageUrl);

            activityService.trackActivity(
                    currentUser,
                    ActivityType.PROFILE_IMAGE_UPDATED,
                    "Updated profile image",
                    ActivityIcon.PROFILE_IMAGE_UPDATED,
                    metadata
            );

            return ImageUploadResponse.builder()
                    .imageUrl(newImageUrl)
                    .message("Image uploaded successfully")
                    .build();

        } catch (IOException e) {
            log.error("Failed to upload image for userId {}: {}", userId, e.getMessage());
            throw new RuntimeException("Failed to upload image: " + e.getMessage());
        }

    }

    /**
     * Calculate profile completion percentage based on filled fields
     */
    private int calculateProfileCompletionPercentage(User user) {
        int totalFields = 8; // Count of important profile fields (including interests)
        int filledFields = 0;

        if (user.getFullName() != null && !user.getFullName().trim().isEmpty()) filledFields++;
        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) filledFields++;
        if (user.getBio() != null && !user.getBio().trim().isEmpty()) filledFields++;
        if (user.getProfileImage() != null && !user.getProfileImage().trim().isEmpty()) filledFields++;
        if (user.getLocation() != null && !user.getLocation().trim().isEmpty()) filledFields++;
        if (user.getWebsite() != null && !user.getWebsite().trim().isEmpty()) filledFields++;
        if (user.getSocialLinks() != null && !user.getSocialLinks().trim().isEmpty()) filledFields++;
        if (user.getInterests() != null && !user.getInterests().isEmpty()) filledFields++;

        return (int) ((filledFields / (double) totalFields) * 100);
    }

    /**
     * Get current user profile
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile() {
        User user = getCurrentUser();
        return getEnhancedUserProfile(user);
    }

    /**
     * Get user by ID
     *
     * @param id User ID
     * @return User entity
     */
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Get user profile by ID
     *
     * @param id User ID
     * @return Enhanced user profile
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileById(Long id) {
        User user = getUserById(id);
        return getEnhancedUserProfile(user);
    }

    /**
     * Update current user profile
     *
     * @param request Profile update request
     * @return Updated user profile
     */
    @Transactional
    public UserProfileResponse updateCurrentUserProfile(UserProfileUpdateRequest request) {
        User currentUser = getCurrentUser();

        // Update fields from the request (only if not null)
        if (StringUtils.hasText(request.fullName()) && !request.fullName().equals(currentUser.getFullName())) {
            currentUser.setFullName(request.fullName());
        }
        
        if (StringUtils.hasText(request.bio()) && !request.bio().equals(currentUser.getBio())) {
            currentUser.setBio(request.bio());
        }
        
        if (StringUtils.hasText(request.profileImage()) && !request.profileImage().equals(currentUser.getProfileImage())) {
            currentUser.setProfileImage(request.profileImage());
        }
        
        if (StringUtils.hasText(request.location()) && !request.location().equals(currentUser.getLocation())) {
            currentUser.setLocation(request.location());
        }
        
        if (StringUtils.hasText(request.website()) && !request.website().equals(currentUser.getWebsite())) {
            currentUser.setWebsite(request.website());
        }
        
        if (StringUtils.hasText(request.socialLinks()) && !request.socialLinks().equals(currentUser.getSocialLinks())) {
            currentUser.setSocialLinks(request.socialLinks());
        }
        
        if (request.interests() != null && !request.interests().equals(currentUser.getInterests())) {
            currentUser.setInterests(request.interests());
        }

        // Update last active timestamp
        currentUser.setLastActive(LocalDateTime.now());

        // Save updates
        User updatedUser = userRepository.save(currentUser);

        // Return the updated profile
        return getEnhancedUserProfile(updatedUser);
    }
} 