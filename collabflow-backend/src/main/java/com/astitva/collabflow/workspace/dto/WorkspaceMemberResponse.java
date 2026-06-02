package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for workspace member list.
 */
public record WorkspaceMemberResponse(

        /**
         * User ID of the workspace member.
         */
        UUID userId,

        /**
         * Full name of the workspace member.
         */
        String fullName,

        /**
         * Email of the workspace member.
         */
        String email,

        /**
         * Role of the user inside this workspace.
         */
        WorkspaceRole role,

        /**
         * Timestamp when the user joined this workspace.
         */
        LocalDateTime joinedAt
) {
}