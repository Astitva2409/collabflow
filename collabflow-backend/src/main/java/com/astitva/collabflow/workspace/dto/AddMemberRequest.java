package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Request DTO for adding a user to a workspace.
 */
public record AddMemberRequest(

        /**
         * ID of the user who should be added to the workspace.
         */
        @NotNull(message = "User ID is required")
        UUID userId,

        /**
         * Role to assign to the new member.
         *
         * Example:
         * ADMIN, MEMBER, VIEWER
         *
         * OWNER should not be assigned through this API.
         */
        @NotNull(message = "Role is required")
        WorkspaceRole role
) {
}