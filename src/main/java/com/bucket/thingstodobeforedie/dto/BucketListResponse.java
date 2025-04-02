package com.bucket.thingstodobeforedie.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BucketListResponse(
    Long id,
    String name,
    String description,
    String imageUrl,
    Long userId,
    List<String> tags,
    CategoryResponse category,
    List<BucketListItemResponse> bucketItems,
    int completedItems,
    int totalItems,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    int progress,
    LocalDateTime dueDate
) {} 