package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddMemberRequest(

        @NotNull(message = "User ID is required")
        UUID userId,

        @NotNull(message = "Role is required")
        WorkspaceRole role
) {
}