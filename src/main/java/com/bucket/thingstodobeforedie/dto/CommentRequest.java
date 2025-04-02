package com.bucket.thingstodobeforedie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Schema(description = "Request object for creating or updating a comment")
@Builder
public record CommentRequest(
    @Schema(description = "Content of the comment", required = true, example = "Great blog post! I really enjoyed reading it.")
    @NotBlank(message = "Comment content is required")
    @Size(min = 2, max = 1000, message = "Comment must be between 2 and 1000 characters")
    String content
) {} 