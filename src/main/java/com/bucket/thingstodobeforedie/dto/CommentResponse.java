package com.bucket.thingstodobeforedie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

/**
 * Comment response with author details
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Comment information")
@Builder
public record CommentResponse(
    @Schema(description = "Unique identifier of the comment")
    Long id,
    
    @Schema(description = "Content of the comment")
    String content,
    
    @Schema(description = "ID of the user who created the comment")
    Long userId,
    
    @Schema(description = "Username of the comment author")
    String username,

    @Schema(description = "User profile image of the comment author")
    String userProfileImage,
    
    @Schema(description = "ID of the blog post this comment belongs to")
    Long blogPostId,
    
    @Schema(description = "Date and time when the comment was created")
    LocalDateTime createdAt,
    
    @Schema(description = "Date and time when the comment was last updated")
    LocalDateTime updatedAt
) {} 