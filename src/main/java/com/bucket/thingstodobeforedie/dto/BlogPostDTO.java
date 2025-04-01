package com.bucket.thingstodobeforedie.dto;

import com.bucket.thingstodobeforedie.entity.BlogStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Blog post data transfer object")
@Builder
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

        @Schema(description = "User profile image of the author")
        String authorProfileImage,

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
        Boolean isLikedByCurrentUser,
        List<String> tags
) {
} 