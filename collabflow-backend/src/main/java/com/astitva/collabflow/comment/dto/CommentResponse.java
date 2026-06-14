package com.astitva.collabflow.comment.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for task comments.
 */
public record CommentResponse(
        UUID id,
        UUID taskId,
        UUID authorId,
        String authorName,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}