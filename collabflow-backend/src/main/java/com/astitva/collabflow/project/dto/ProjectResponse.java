package com.astitva.collabflow.project.dto;

import com.astitva.collabflow.project.entity.ProjectPriority;
import com.astitva.collabflow.project.entity.ProjectStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for project APIs.
 */
public record ProjectResponse(
        UUID id,
        UUID workspaceId,
        String name,
        String description,
        ProjectStatus status,
        ProjectPriority priority,
        UUID createdBy,
        boolean archived,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}