package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.CategoryType;

import java.time.LocalDateTime;

public record CategoryResponse(
    Long id,
    String name,
    String description,
    String icon,
    CategoryType type,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 