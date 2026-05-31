package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record WorkspaceResponse(
        UUID id,
        String name,
        String description,
        WorkspaceRole role,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}