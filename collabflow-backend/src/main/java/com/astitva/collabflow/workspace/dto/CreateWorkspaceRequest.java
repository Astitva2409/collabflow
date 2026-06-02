package com.astitva.collabflow.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a workspace.
 *
 * DTO means Data Transfer Object.
 *
 * We use DTOs instead of exposing Entity directly.
 * This keeps API layer separate from database layer.
 */
public record CreateWorkspaceRequest(

        /**
         * Workspace name is mandatory.
         */
        @NotBlank(message = "Workspace name is required")
        @Size(min = 2, max = 120, message = "Workspace name must be between 2 and 120 characters")
        String name,

        /**
         * Description is optional but cannot be more than 500 characters.
         */
        @Size(max = 500, message = "Workspace description cannot exceed 500 characters")
        String description
) {
}