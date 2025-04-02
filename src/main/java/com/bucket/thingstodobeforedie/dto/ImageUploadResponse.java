package com.bucket.thingstodobeforedie.dto;

import lombok.Builder;

/**
 * Response for image uploads
 */
@Builder
public record ImageUploadResponse(
    String imageUrl,
    String message
) {} 