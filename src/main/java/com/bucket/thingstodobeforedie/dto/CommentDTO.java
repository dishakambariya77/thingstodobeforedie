package com.bucket.thingstodobeforedie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Comment data transfer object")
@Builder
public record CommentDTO(
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
){}