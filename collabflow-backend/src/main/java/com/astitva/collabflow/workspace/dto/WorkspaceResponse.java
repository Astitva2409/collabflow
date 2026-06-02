package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO for workspace APIs.
 *
 * This is returned to frontend instead of returning Workspace entity directly.
 */
public record WorkspaceResponse(

        /**
         * Workspace ID.
         */
        UUID id,

        /**
         * Workspace name.
         */
        String name,

        /**
         * Workspace description.
         */
        String description,

        /**
         * Current logged-in user's role in this workspace.
         *
         * Example:
         * OWNER, ADMIN, MEMBER, VIEWER
         */
        WorkspaceRole role,

        /**
         * Workspace creation timestamp.
         */
        LocalDateTime createdAt,

        /**
         * Workspace last updated timestamp.
         */
        LocalDateTime updatedAt
) {
}