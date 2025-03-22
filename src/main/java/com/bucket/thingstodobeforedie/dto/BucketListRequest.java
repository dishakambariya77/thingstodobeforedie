package com.bucket.thingstodobeforedie.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for creating a new bucket list
 */
public record BucketListRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    String name,
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    String description,
    
    Long categoryId,

    List<String> tags,
    List<BucketListItemRequest> bucketItems
) {} 