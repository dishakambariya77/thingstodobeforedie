package com.bucket.thingstodobeforedie.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Category with count information
 */
public record CategoryCount(
    @Schema(description = "Category name")
    String name,
    
    @Schema(description = "Number of blog posts in this category")
    Long count
) {}
