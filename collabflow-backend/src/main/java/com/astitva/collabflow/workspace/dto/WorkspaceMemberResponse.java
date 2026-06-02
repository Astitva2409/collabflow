package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkspaceMemberResponse(
        UUID userId,
        String fullName,
        String email,
        WorkspaceRole role,
        LocalDateTime joinedAt
) {
}