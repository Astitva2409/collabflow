package com.astitva.collabflow.comment.controller;

import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.comment.dto.CommentResponse;
import com.astitva.collabflow.comment.dto.CreateCommentRequest;
import com.astitva.collabflow.comment.service.TaskCommentService;
import com.astitva.collabflow.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for task comment APIs.
 *
 * Comments are nested under:
 *
 * Workspace -> Project -> Task -> Comments
 *
 * Base URL:
 * /api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments
 */
@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks/{taskId}/comments")
@RequiredArgsConstructor
public class TaskCommentController {

    private final TaskCommentService taskCommentService;

    /**
     * Adds a comment to a task.
     *
     * Permission rules:
     * - Current user must be a workspace member.
     * - VIEWER cannot add comments.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CommentResponse> addComment(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        CommentResponse response = taskCommentService.addComment(
                workspaceId,
                projectId,
                taskId,
                currentUser.getId(),
                request
        );

        return ApiResponse.success("Comment added successfully", response);
    }

    /**
     * Fetches all active comments for a task.
     *
     * Permission rules:
     * - Any workspace member can view comments.
     */
    @GetMapping
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<CommentResponse> response = taskCommentService.getComments(
                workspaceId,
                projectId,
                taskId,
                currentUser.getId()
        );

        return ApiResponse.success("Comments fetched successfully", response);
    }

    /**
     * Archives/deletes a comment.
     *
     * Permission rules:
     * - Comment author can delete own comment.
     * - Workspace OWNER or ADMIN can delete any comment.
     */
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        taskCommentService.deleteComment(
                workspaceId,
                projectId,
                taskId,
                commentId,
                currentUser.getId()
        );

        return ApiResponse.success("Comment deleted successfully");
    }
}