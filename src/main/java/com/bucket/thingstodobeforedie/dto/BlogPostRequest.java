package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.BlogStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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
    BlogStatus status
) {
    public BlogPostRequest {
        if (status == null) {
            status = BlogStatus.DRAFT;
        }
    }
    
    public static BlogPostRequestBuilder builder() {
        return new BlogPostRequestBuilder();
    }

    public static class BlogPostRequestBuilder {
        private String title;
        private String content;
        private String featuredImage;
        private Long categoryId;
        private BlogStatus status = BlogStatus.DRAFT;

        public BlogPostRequestBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BlogPostRequestBuilder content(String content) {
            this.content = content;
            return this;
        }

        public BlogPostRequestBuilder featuredImage(String featuredImage) {
            this.featuredImage = featuredImage;
            return this;
        }

        public BlogPostRequestBuilder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public BlogPostRequestBuilder status(BlogStatus status) {
            this.status = status;
            return this;
        }

        public BlogPostRequest build() {
            return new BlogPostRequest(title, content, featuredImage, categoryId, status);
        }
    }
} 