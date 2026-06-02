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

@RestController
@RequestMapping("/api/v1/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<WorkspaceResponse> createWorkspace(
            @Valid @RequestBody CreateWorkspaceRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        WorkspaceResponse response = workspaceService.createWorkspace(request, currentUser.getId());

        return ApiResponse.success("Workspace created successfully", response);
    }

    @GetMapping
    public ApiResponse<List<WorkspaceResponse>> getMyWorkspaces(
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<WorkspaceResponse> response = workspaceService.getMyWorkspaces(currentUser.getId());

        return ApiResponse.success("Workspaces fetched successfully", response);
    }

    @GetMapping("/{workspaceId}")
    public ApiResponse<WorkspaceResponse> getWorkspaceById(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        WorkspaceResponse response = workspaceService.getWorkspaceById(workspaceId, currentUser.getId());

        return ApiResponse.success("Workspace fetched successfully", response);
    }

    @PostMapping("/{workspaceId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<Void> addMember(
            @PathVariable UUID workspaceId,
            @Valid @RequestBody AddMemberRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        workspaceService.addMember(workspaceId, currentUser.getId(), request);

        return ApiResponse.success("Member added successfully");
    }

    @GetMapping("/{workspaceId}/members")
    public ApiResponse<List<WorkspaceMemberResponse>> getMembers(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<WorkspaceMemberResponse> response =
                workspaceService.getMembers(workspaceId, currentUser.getId());

        return ApiResponse.success("Members fetched successfully", response);
    }

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