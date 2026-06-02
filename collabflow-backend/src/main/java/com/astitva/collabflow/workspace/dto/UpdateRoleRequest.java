package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for updating a workspace member's role.
 */
public record UpdateRoleRequest(

        /**
         * New role to assign to the member.
         */
        @NotNull(message = "Role is required")
        WorkspaceRole role
) {
}