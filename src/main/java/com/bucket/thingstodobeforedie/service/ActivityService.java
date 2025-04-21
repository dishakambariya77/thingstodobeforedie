package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.ActivityResponse;
import com.bucket.thingstodobeforedie.entity.Activity;
import com.bucket.thingstodobeforedie.entity.ActivityIcon;
import com.bucket.thingstodobeforedie.entity.ActivityType;
import com.bucket.thingstodobeforedie.entity.User;
import com.bucket.thingstodobeforedie.exception.ResourceNotFoundException;
import com.bucket.thingstodobeforedie.repository.ActivityRepository;
import com.bucket.thingstodobeforedie.repository.UserRepository;
import com.bucket.thingstodobeforedie.security.CurrentUser;
import com.bucket.thingstodobeforedie.util.TimeAgoUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final CurrentUser currentUser;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    /**
     * Track a user activity
     */
    @Transactional
    public void trackActivity(User user, ActivityType activityType, String text, ActivityIcon activityIcon,
                                Map<String, Object> metadata) {
        
        String metadataJson = null;
        if (metadata != null) {
            try {
                metadataJson = objectMapper.writeValueAsString(metadata);
            } catch (JsonProcessingException e) {
                log.error("Error serializing metadata: {}", e.getMessage());
            }
        }
        
        Activity activity = Activity.builder()
                .user(user)
                .activityType(activityType)
                .text(text)
                .activityIcon(activityIcon)
                .metadata(metadataJson)
                .build();
        
        activityRepository.save(activity);
    }

    /**
     * Get activities for current user
     */
    @Transactional(readOnly = true)
    public Page<ActivityResponse> getCurrentUserActivities(Pageable pageable) {
        User user = currentUser.getUser();

        long start = System.nanoTime(); // Capture start time
        Page<Activity> activities = activityRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        long end = System.nanoTime(); // Capture end time

        System.out.println("DB Query Execution Time: " + (end - start) / 1_000_000 + " ms");

        return activities.map(this::mapToActivityResponse);
    }


    /**
     * Get activities for a specific user
     */
    @Transactional(readOnly = true)
    public Page<ActivityResponse> getUserActivities(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Page<Activity> activities = activityRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        return activities.map(this::mapToActivityResponse);
    }
    
    /**
     * Get recent activities for a user
     */
    @Transactional(readOnly = true)
    public List<ActivityResponse> getRecentUserActivities(User user, int limit) {
        List<Activity> activities = activityRepository.findTop10ByUserOrderByCreatedAtDesc(user);
        
        return activities.stream()
                .limit(limit)
                .map(this::mapToActivityResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Map Activity entity to ActivityResponse DTO
     */
    private ActivityResponse mapToActivityResponse(Activity activity) {
        return ActivityResponse.builder()
                .id(activity.getId())
                .userId(activity.getUser().getId())
                .username(activity.getUser().getUsername())
                .userProfileImage(activity.getUser().getProfileImage())
                .activityType(activity.getActivityType())
                .activityIcon(activity.getActivityIcon().getIcon())
                .text(activity.getText())
                .time(TimeAgoUtil.getTimeAgo(activity.getCreatedAt()))
                .metadata(activity.getMetadata())
                .build();
    }
} 