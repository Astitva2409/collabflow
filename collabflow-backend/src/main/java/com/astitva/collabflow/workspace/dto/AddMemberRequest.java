package com.astitva.collabflow.workspace.dto;

import com.astitva.collabflow.workspace.entity.WorkspaceRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for adding a user to workspace by email.
 */
public record AddMemberRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Role is required")
        WorkspaceRole role
) {
}