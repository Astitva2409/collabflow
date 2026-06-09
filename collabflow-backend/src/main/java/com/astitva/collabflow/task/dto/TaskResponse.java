package com.astitva.collabflow.task.dto;

import com.astitva.collabflow.task.entity.TaskPriority;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for task APIs.
 */
public record TaskResponse(
        UUID id,
        UUID projectId,
        UUID boardColumnId,
        String columnName,
        String title,
        String description,
        TaskPriority priority,
        UUID createdBy,
        UUID assignedTo,
        String assignedToName,
        Integer position,
        LocalDateTime dueDate,
        boolean archived,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}