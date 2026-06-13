package com.astitva.collabflow.task.controller;

import com.astitva.collabflow.auth.security.CustomUserDetails;
import com.astitva.collabflow.common.response.ApiResponse;
import com.astitva.collabflow.task.dto.AssignTaskRequest;
import com.astitva.collabflow.task.dto.CreateTaskRequest;
import com.astitva.collabflow.task.dto.MoveTaskRequest;
import com.astitva.collabflow.task.dto.TaskResponse;
import com.astitva.collabflow.task.dto.UpdateTaskRequest;
import com.astitva.collabflow.task.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for task APIs.
 *
 * Tasks are nested under workspace and project.
 */
@RestController
@RequestMapping("/api/v1/workspaces/{workspaceId}/projects/{projectId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    /**
     * Creates a task inside a project.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<TaskResponse> createTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TaskResponse response = taskService.createTask(
                workspaceId,
                projectId,
                currentUser.getId(),
                request
        );

        return ApiResponse.success("Task created successfully", response);
    }

    /**
     * Fetches all tasks of a project.
     */
    @GetMapping
    public ApiResponse<List<TaskResponse>> getTasks(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        List<TaskResponse> response = taskService.getTasks(
                workspaceId,
                projectId,
                currentUser.getId()
        );

        return ApiResponse.success("Tasks fetched successfully", response);
    }

    /**
     * Fetches one task by ID.
     */
    @GetMapping("/{taskId}")
    public ApiResponse<TaskResponse> getTaskById(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TaskResponse response = taskService.getTaskById(
                workspaceId,
                projectId,
                taskId,
                currentUser.getId()
        );

        return ApiResponse.success("Task fetched successfully", response);
    }

    /**
     * Updates task details.
     */
    @PatchMapping("/{taskId}")
    public ApiResponse<TaskResponse> updateTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody UpdateTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TaskResponse response = taskService.updateTask(
                workspaceId,
                projectId,
                taskId,
                currentUser.getId(),
                request
        );

        return ApiResponse.success("Task updated successfully", response);
    }

    /**
     * Moves task between board columns or reorders it within same column.
     */
    @PatchMapping("/{taskId}/move")
    public ApiResponse<TaskResponse> moveTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @Valid @RequestBody MoveTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TaskResponse response = taskService.moveTask(
                workspaceId,
                projectId,
                taskId,
                currentUser.getId(),
                request
        );

        return ApiResponse.success("Task moved successfully", response);
    }

    /**
     * Assigns or unassigns task.
     */
    @PatchMapping("/{taskId}/assign")
    public ApiResponse<TaskResponse> assignTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @RequestBody AssignTaskRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        TaskResponse response = taskService.assignTask(
                workspaceId,
                projectId,
                taskId,
                currentUser.getId(),
                request
        );

        return ApiResponse.success("Task assignment updated successfully", response);
    }

    /**
     * Archives task.
     */
    @DeleteMapping("/{taskId}")
    public ApiResponse<Void> archiveTask(
            @PathVariable UUID workspaceId,
            @PathVariable UUID projectId,
            @PathVariable UUID taskId,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        taskService.archiveTask(
                workspaceId,
                projectId,
                taskId,
                currentUser.getId()
        );

        return ApiResponse.success("Task archived successfully");
    }
}