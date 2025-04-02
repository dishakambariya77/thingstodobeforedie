package com.bucket.thingstodobeforedie.dto;

import lombok.Builder;
import java.time.LocalDateTime;

/**
 * Response for bucket list items
 */
@Builder
public record BucketListItemResponse(
    Long id,
    String name,
    String description,
    boolean completed,
    LocalDateTime deadline,
    LocalDateTime completedAt,
    String priority,
    Long bucketListId,
    String bucketListName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 