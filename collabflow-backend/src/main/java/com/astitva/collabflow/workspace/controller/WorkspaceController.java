package com.astitva.collabflow.workspace.controller;

import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.common.response.ApiResponse;
import com.astitva.collabflow.workspace.dto.*;
import com.astitva.collabflow.workspace.service.WorkspaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for workspace APIs.
 *
 * This layer is responsible for:
 * - Receiving HTTP requests
 * - Reading path variables and request bodies
 * - Getting current authenticated user
 * - Calling service layer
 * - Returning API responses
 *
 * Business logic should not be written here.
 */
@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    /**
     * Creates a new workspace.
     *
     * Current logged-in user automatically becomes OWNER.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorkspaceResponse> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        WorkspaceResponse response = workspaceService.createWorkspace(request, currentUser.getId());

        return ApiResponse.success("Workspace created successfully", response);
    }

    /**
     * Fetches all workspaces of current logged-in user.
     */
    @GetMapping
    public ApiResponse<List<WorkspaceResponse>> getMyWorkspaces(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<WorkspaceResponse> response = workspaceService.getMyWorkspaces(currentUser.getId());

        return ApiResponse.success("Workspaces fetched successfully", response);
    }

    /**
     * Fetches a workspace by ID.
     *
     * Current user must be a member of that workspace.
     */
    @GetMapping("/{workspaceId}")
    public ApiResponse<WorkspaceResponse> getWorkspaceById(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        WorkspaceResponse response = workspaceService.getWorkspaceById(workspaceId, currentUser.getId());

        return ApiResponse.success("Workspace fetched successfully", response);
    }

    /**
     * Adds a user as member to workspace.
     *
     * Only OWNER or ADMIN can add members.
     */
    @PostMapping("/{workspaceId}/members")
    public ApiResponse<Void> addMember(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        workspaceService.addMember(workspaceId, currentUser.getId(), request);

        return ApiResponse.success("Member added successfully");
    }

    /**
     * Fetches all members of a workspace.
     *
     * Current user must be a member of workspace.
     */
    @GetMapping("/{workspaceId}/members")
    public ApiResponse<List<WorkspaceMemberResponse>> getMembers(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<WorkspaceMemberResponse> response =
                workspaceService.getMembers(workspaceId, currentUser.getId());

        return ApiResponse.success("Members fetched successfully", response);
    }

    /**
     * Updates role of a workspace member.
     *
     * Only OWNER can update member roles.
     */
    @PatchMapping("/{workspaceId}/members/{userId}/role")
    public ApiResponse<Void> updateRole(
            @PathVariable UUID workspaceId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        workspaceService.updateMemberRole(workspaceId, currentUser.getId(), userId, request);

        return ApiResponse.success("Member role updated successfully");
    }

    /**
     * Removes a member from workspace.
     *
     * Only OWNER can remove members.
     */
    @DeleteMapping("/{workspaceId}/members/{userId}")
    public ApiResponse<Void> removeMember(
            @PathVariable UUID workspaceId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        workspaceService.removeMember(workspaceId, currentUser.getId(), userId);

        return ApiResponse.success("Member removed successfully");
    }
}