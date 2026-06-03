package com.astitva.collabflow.project.dto;

import com.astitva.collabflow.project.entity.ProjectPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for creating a project inside a workspace.
 */
public record CreateProjectRequest(

        @NotBlank(message = "Project name is required")
        @Size(min = 2, max = 120, message = "Project name must be between 2 and 120 characters")
        String name,

        @Size(max = 1000, message = "Project description cannot exceed 1000 characters")
        String description,

        /**
         * Priority is optional.
         *
         * If not provided, service will default it to MEDIUM.
         */
        ProjectPriority priority
) {
}