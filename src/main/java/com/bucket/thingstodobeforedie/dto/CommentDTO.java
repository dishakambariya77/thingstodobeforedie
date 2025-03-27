package com.bucket.thingstodobeforedie.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Comment data transfer object")
public record CommentDTO(
    @Schema(description = "Unique identifier of the comment")
    Long id,
    
    @Schema(description = "Content of the comment")
    String content,
    
    @Schema(description = "ID of the user who created the comment")
    Long userId,
    
    @Schema(description = "Username of the comment author")
    String username,
    
    @Schema(description = "ID of the blog post this comment belongs to")
    Long blogPostId,
    
    @Schema(description = "Date and time when the comment was created")
    LocalDateTime createdAt,
    
    @Schema(description = "Date and time when the comment was last updated")
    LocalDateTime updatedAt
) {
    public static CommentDTOBuilder builder() {
        return new CommentDTOBuilder();
    }

    public static class CommentDTOBuilder {
        private Long id;
        private String content;
        private Long userId;
        private String username;
        private Long blogPostId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public CommentDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CommentDTOBuilder content(String content) {
            this.content = content;
            return this;
        }

        public CommentDTOBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public CommentDTOBuilder username(String username) {
            this.username = username;
            return this;
        }

        public CommentDTOBuilder blogPostId(Long blogPostId) {
            this.blogPostId = blogPostId;
            return this;
        }

        public CommentDTOBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommentDTOBuilder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public CommentDTO build() {
            return new CommentDTO(id, content, userId, username, blogPostId, createdAt, updatedAt);
        }
    }
} 