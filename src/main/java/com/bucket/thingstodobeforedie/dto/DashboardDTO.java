package com.bucket.thingstodobeforedie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    // Count metrics
    private long completedGoals;
    private long inProgressGoals;
    private long totalBlogPosts;
    
    // Category-wise blog post counts
    private List<CategoryCountDTO> blogPostCategories;
    
    // Recent bucket list items
    private List<?> currentGoals;
} 