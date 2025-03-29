package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.ActivityType;
import lombok.Builder;

@Builder
public record ActivityResponse
        (Long id,
         Long userId,
         String username,
         String userProfileImage,
         ActivityType activityType,
         String activityIcon,
         String text,
         String time,
         String metadata) {

} 