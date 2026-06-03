package com.astitva.collabflow.project.dto;

import com.astitva.collabflow.project.entity.ProjectPriority;
import com.astitva.collabflow.project.entity.ProjectStatus;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for updating a project.
 *
 * All fields are optional because this is PATCH.
 * If a field is null, we do not update that field.
 */
public record UpdateProjectRequest(

        @Size(min = 2, max = 120, message = "Project name must be between 2 and 120 characters")
        String name,

        @Size(max = 1000, message = "Project description cannot exceed 1000 characters")
        String description,

        ProjectStatus status,

        ProjectPriority priority
) {
}