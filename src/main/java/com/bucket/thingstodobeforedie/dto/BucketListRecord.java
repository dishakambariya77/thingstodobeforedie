package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.BucketListItem;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for BucketList entity
 */
public record BucketListRecord(
    Long id,
    String name,
    String description,
    String imageUrl,
    Long userId,
    List<String> tags,
    CategoryRecord category,
    List<BucketListItemRecord> bucketItems,
    int completedItems,
    int totalItems,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 