package com.astitva.collabflow.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateWorkspaceRequest(

        @NotBlank(message = "Workspace name is required")
        @Size(min = 2, max = 120, message = "Workspace name must be between 2 and 120 characters")
        String name,

        @Size(max = 500, message = "Workspace description cannot exceed 500 characters")
        String description
) {
}