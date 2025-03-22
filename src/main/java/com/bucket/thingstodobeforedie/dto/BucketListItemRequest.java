package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for creating a new bucket list item
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BucketListItemRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 200, message = "Title must be between 2 and 200 characters")
    private String name;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;
    private String notes;
    private LocalDateTime deadline;
    private String priority;
} 