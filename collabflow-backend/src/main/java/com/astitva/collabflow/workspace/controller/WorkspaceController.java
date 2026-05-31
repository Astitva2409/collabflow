package com.astitva.collabflow.workspace.controller;

import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.common.response.ApiResponse;
import com.astitva.collabflow.workspace.dto.CreateWorkspaceRequest;
import com.astitva.collabflow.workspace.dto.WorkspaceResponse;
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
}