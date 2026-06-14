package com.astitva.collabflow.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for adding a comment to a task.
 */
public record CreateCommentRequest(

        @NotBlank(message = "Comment content is required")
        @Size(min = 1, max = 2000, message = "Comment content must be between 1 and 2000 characters")
        String content
) {
}