package com.astitva.collabflow.activity.controller;

import com.astitva.collabflow.activity.dto.ActivityLogResponse;
import com.astitva.collabflow.activity.service.ActivityLogService;
import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for activity log APIs.
 */
@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/activities")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    /**
     * Fetches all activity logs for a workspace.
     */
    @GetMapping
    public ApiResponse<List<ActivityLogResponse>> getWorkspaceActivities(
            @PathVariable UUID workspaceId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<ActivityLogResponse> response = activityLogService.getWorkspaceActivities(
                workspaceId,
                currentUser.getId()
        );

        return ApiResponse.success("Workspace activities fetched successfully", response);
    }

    /**
     * Fetches activity logs for a specific project inside workspace.
     */
    @GetMapping("/projects/{projectId}")
    public ApiResponse<List<ActivityLogResponse>> getProjectActivities(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<ActivityLogResponse> response = activityLogService.getProjectActivities(
                workspaceId,
                projectId,
                currentUser.getId()
        );

        return ApiResponse.success("Project activities fetched successfully", response);
    }
}