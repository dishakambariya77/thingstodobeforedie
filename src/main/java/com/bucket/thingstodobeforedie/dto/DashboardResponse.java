package com.bucket.thingstodobeforedie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

/**
 * Dashboard data with user statistics and information
 */
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DashboardResponse(
     long completedGoals,
     long inProgressGoals,
     long totalBlogPosts,
     List<CategoryCount> blogPostCategories,
     List<?> currentGoals
) {} 