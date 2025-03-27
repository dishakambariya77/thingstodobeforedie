package com.bucket.thingstodobeforedie.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status of a bucket list item")
public enum BucketStatus {
    @Schema(description = "Bucket list is active and in progress")
    ACTIVE,
    
    @Schema(description = "Bucket list is completed")
    COMPLETED,
    
    @Schema(description = "Bucket list is archived")
    ARCHIVED
} 