package com.bucket.thingstodobeforedie.dto;

import java.util.List;

/**
 * Generic paged response for paginated data
 * @param <T> Type of content
 */
public record PagedResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean last
) {} 