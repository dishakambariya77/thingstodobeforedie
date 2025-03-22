package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.CategoryType;

import java.time.LocalDateTime;

/**
 * DTO for Category entity
 */
public record CategoryRecord(
    Long id,
    String name,
    String description,
    String icon,
    CategoryType type,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {} 