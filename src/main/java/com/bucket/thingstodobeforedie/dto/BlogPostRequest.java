package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.BlogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

@Schema(description = "Request object for creating or updating a blog post")
public record BlogPostRequest(
    @Schema(description = "Title of the blog post", required = true, example = "My First Blog Post")
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    String title,
    
    @Schema(description = "Content of the blog post", required = true, example = "This is the content of my first blog post...")
    @NotBlank(message = "Content is required")
    @Size(min = 10, message = "Content must be at least 10 characters")
    String content,
    
    @Schema(description = "URL of the featured image", example = "https://example.com/images/featured.jpg")
    String featuredImage,
    
    @Schema(description = "ID of the category the blog post belongs to", example = "1")
    Long categoryId,
    
    @Schema(description = "Status of the blog post (DRAFT or PUBLISHED)", example = "DRAFT", defaultValue = "DRAFT")
    BlogStatus status,
    List<String> tags
) {
    public BlogPostRequest {
        if (status == null) {
            status = BlogStatus.DRAFT;
        }
    }



} 