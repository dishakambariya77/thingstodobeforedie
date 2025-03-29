package com.bucket.thingstodobeforedie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DashboardDTO (
     long completedGoals,
     long inProgressGoals,
     long totalBlogPosts,
     List<CategoryCountDTO> blogPostCategories ,
     List<?> currentGoals
){}