package com.bucket.thingstodobeforedie.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of a blog post")
public enum BlogStatus {
    @Schema(description = "Draft blog post, not visible to other users")
    DRAFT, 
    
    @Schema(description = "Published blog post, visible to all users")
    PUBLISHED
} 