package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.BlogStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Blog post data transfer object")
public record BlogPostDTO(
    @Schema(description = "Unique identifier of the blog post")
    Long id,
    
    @Schema(description = "Title of the blog post")
    String title,
    
    @Schema(description = "Content of the blog post")
    String content,
    
    @Schema(description = "URL of the featured image")
    String featuredImage,
    
    @Schema(description = "Status of the blog post (DRAFT or PUBLISHED)")
    BlogStatus status,
    
    @Schema(description = "ID of the user who created the blog post")
    Long userId,
    
    @Schema(description = "Username of the author")
    String author,
    
    @Schema(description = "ID of the category the blog post belongs to")
    Long categoryId,
    
    @Schema(description = "Name of the category")
    String categoryName,
    
    @Schema(description = "Number of likes the blog post has received")
    Long likesCount,
    
    @Schema(description = "Number of comments on the blog post")
    Integer commentsCount,
    
    @Schema(description = "Number of views the blog post has received")
    Long views,
    
    @Schema(description = "Date and time when the blog post was created")
    LocalDateTime createdAt,
    
    @Schema(description = "Date and time when the blog post was last updated")
    LocalDateTime updatedAt,
    
    @Schema(description = "Whether the current user has liked this blog post")
    Boolean isLikedByCurrentUser
) {
    public static BlogPostDTOBuilder builder() {
        return new BlogPostDTOBuilder();
    }

    public static class BlogPostDTOBuilder {
        private Long id;
        private String title;
        private String content;
        private String featuredImage;
        private BlogStatus status;
        private Long userId;
        private String author;
        private Long categoryId;
        private String categoryName;
        private Long likesCount;
        private Integer commentsCount;
        private Long views;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isLikedByCurrentUser;

        public BlogPostDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BlogPostDTOBuilder title(String title) {
            this.title = title;
            return this;
        }

        public BlogPostDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public BlogPostDTOBuilder featuredImage(String featuredImage) {
            this.featuredImage = featuredImage;
            return this;
        }

        public BlogPostDTOBuilder status(BlogStatus status) {
            this.status = status;
            return this;
        }

        public BlogPostDTOBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public BlogPostDTOBuilder author(String author) {
            this.author = author;
            return this;
        }

        public BlogPostDTOBuilder categoryId(Long categoryId) {
            this.categoryId = categoryId;
            return this;
        }

        public BlogPostDTOBuilder categoryName(String categoryName) {
            this.categoryName = categoryName;
            return this;
        }

        public BlogPostDTOBuilder likesCount(Long likesCount) {
            this.likesCount = likesCount;
            return this;
        }

        public BlogPostDTOBuilder commentsCount(Integer commentsCount) {
            this.commentsCount = commentsCount;
            return this;
        }
        
        public BlogPostDTOBuilder views(Long views) {
            this.views = views;
            return this;
        }

        public BlogPostDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public BlogPostDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public BlogPostDTOBuilder isLikedByCurrentUser(Boolean isLikedByCurrentUser) {
            this.isLikedByCurrentUser = isLikedByCurrentUser;
            return this;
        }

        public BlogPostDTO build() {
            return new BlogPostDTO(id, title, content, featuredImage, status, userId, author,
                    categoryId, categoryName, likesCount, commentsCount, views, createdAt, updatedAt, isLikedByCurrentUser);
        }
    }
} 