package com.bucket.thingstodobeforedie.service;

import com.bucket.thingstodobeforedie.dto.CategoryCount;
import com.bucket.thingstodobeforedie.dto.DashboardResponse;
import com.bucket.thingstodobeforedie.entity.BucketStatus;
import com.bucket.thingstodobeforedie.repository.BlogPostRepository;
import com.bucket.thingstodobeforedie.repository.BucketListRepository;
import com.bucket.thingstodobeforedie.security.CurrentUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final BlogService blogService;
    private final BucketListService bucketListService;
    private final BlogPostRepository blogPostRepository;
    private final BucketListRepository bucketListRepository;
    private final CurrentUser currentUser;

    /**
     * Get dashboard data for the current user
     * @return Dashboard data including counts and recent items
     */
    public DashboardResponse getDashboardData() {
        // Get the current user ID
        Long userId = currentUser.getUser().getId();
        
        // Get count metrics with error handling
        long completedBucketListCount = 0;
        long activeBucketListCount = 0;
        long blogPostCount = 0;
        
        try {
            completedBucketListCount = bucketListRepository.countByUserIdAndStatus(userId, BucketStatus.COMPLETED);
            activeBucketListCount = bucketListRepository.countByUserIdAndStatus(userId, BucketStatus.ACTIVE);
            blogPostCount = blogPostRepository.countByUserId(userId);
        } catch (Exception e) {
            log.error("Error fetching dashboard counts: {}", e.getMessage());
        }
        
        // Get category-wise blog post counts
        List<CategoryCount> blogCategories = Collections.emptyList();
        try {
            blogCategories = blogService.getCategoryWiseBlogCount();
        } catch (Exception e) {
            log.error("Error fetching category counts: {}", e.getMessage());
        }
        
        // Get recent ongoing bucket list items
        List<?> recentOngoingItems = Collections.emptyList();
        try {
            recentOngoingItems = bucketListService.getRecentOngoingBucketListItems(userId, 3);
        } catch (Exception e) {
            log.error("Error fetching recent bucket items: {}", e.getMessage());
        }
        
        // Build and return the dashboard DTO
        return DashboardResponse.builder()
                .completedGoals(completedBucketListCount)
                .inProgressGoals(activeBucketListCount)
                .totalBlogPosts(blogPostCount)
                .blogPostCategories(blogCategories)
                .currentGoals(recentOngoingItems)
                .build();
    }
} 