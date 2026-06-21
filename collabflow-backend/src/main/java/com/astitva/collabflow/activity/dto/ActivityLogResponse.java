package com.astitva.collabflow.activity.dto;

import com.astitva.collabflow.activity.entity.ActivityTargetType;
import com.astitva.collabflow.activity.entity.ActivityType;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for activity logs.
 */
public record ActivityLogResponse(
        UUID id,
        UUID workspaceId,
        UUID projectId,
        UUID actorId,
        String actorName,
        UUID targetId,
        ActivityTargetType targetType,
        ActivityType activityType,
        String description,
        LocalDateTime createdAt
) {
}