package com.astitva.collabflow.project.controller;

import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.common.response.ApiResponse;
import com.astitva.collabflow.project.dto.CreateProjectRequest;
import com.astitva.collabflow.project.dto.ProjectResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;import com.astitva.collabflow.project.dto.UpdateProjectRequest;
import com.astitva.collabflow.project.service.ProjectService;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for project APIs.
 *
 * Projects are nested under workspace.
 *
 * Example:
 * /api/v1/workspaces/{workspaceId}/projects
 */
@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    /**
     * Creates a project inside workspace.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProjectResponse> createProject(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody CreateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        ProjectResponse response = projectService.createProject(
                workspaceId,
                currentUser.getId(),
                request
        );

        return ApiResponse.success("Project created successfully", response);
    }

    /**
     * Fetches all active projects inside workspace.
     */
    @GetMapping
    public ApiResponse<List<ProjectResponse>> getProjects(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<ProjectResponse> response = projectService.getProjects(
                workspaceId,
                currentUser.getId()
        );

        return ApiResponse.success("Projects fetched successfully", response);
    }

    /**
     * Fetches one project by ID.
     */
    @GetMapping("/{projectId}")
    public ApiResponse<ProjectResponse> getProjectById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        ProjectResponse response = projectService.getProjectById(
                workspaceId,
                projectId,
                currentUser.getId()
        );

        return ApiResponse.success("Project fetched successfully", response);
    }

    /**
     * Updates project.
     */
    @PatchMapping("/{projectId}")
    public ApiResponse<ProjectResponse> updateProject(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @Valid @RequestBody UpdateProjectRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        ProjectResponse response = projectService.updateProject(
                workspaceId,
                projectId,
                currentUser.getId(),
                request
        );

        return ApiResponse.success("Project updated successfully", response);
    }

    /**
     * Archives project.
     *
     * This does soft delete.
     */
    @DeleteMapping("/{projectId}")
    public ApiResponse<Void> archiveProject(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        projectService.archiveProject(workspaceId, projectId, currentUser.getId());

        return ApiResponse.success("Project archived successfully");
    }
}