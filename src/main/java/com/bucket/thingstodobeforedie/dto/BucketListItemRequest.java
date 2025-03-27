package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * DTO for creating a new bucket list item
 */
public record BucketListItemRequest(
        @NotBlank(message = "Title is required")
        @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
        String name,

        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        boolean completed,
        String notes,
        LocalDateTime deadline,
        String priority
) {}
