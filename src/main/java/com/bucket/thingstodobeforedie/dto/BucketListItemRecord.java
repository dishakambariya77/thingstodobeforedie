package com.bucket.thingstodobeforedie.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for bucket list item responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketListItemRecord {

    private Long id;
    private String name;
    private String description;
    private boolean completed;
    private LocalDateTime deadline;
    private LocalDateTime completedAt;
    private String priority;
    private Long bucketListId;
    private String bucketListName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 