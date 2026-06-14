package com.astitva.collabflow.comment.service;

import com.astitva.collabflow.comment.dto.CommentResponse;
import com.astitva.collabflow.comment.dto.CreateCommentRequest;
import com.astitva.collabflow.comment.entity.TaskComment;
import com.astitva.collabflow.comment.repository.TaskCommentRepository;
import com.astitva.collabflow.common.exception.ForbiddenException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.project.repository.ProjectRepository;
import com.astitva.collabflow.task.entity.Task;
import com.astitva.collabflow.task.repository.TaskRepository;
import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import com.astitva.collabflow.workspace.service.WorkspaceAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for task comment business logic.
 */
@Service
@RequiredArgsConstructor
public class TaskCommentService {

    private final TaskCommentRepository taskCommentRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceAccessService workspaceAccessService;

    /**
     * Adds a comment to a task.
     *
     * Rules:
     * - Current user must be workspace member.
     * - VIEWER cannot add comments.
     * - Task must belong to requested project.
     * - Project must belong to requested workspace.
     */
    @Transactional
    public CommentResponse addComment(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID currentUserId,
            CreateCommentRequest request
    ) {
        WorkspaceMember membership =
                workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanAddComments(membership);

        getProject(workspaceId, projectId);

        Task task = getTask(projectId, taskId);

        TaskComment comment = TaskComment.builder()
                .task(task)
                .author(membership.getUser())
                .content(request.content().trim())
                .build();

        TaskComment savedComment = taskCommentRepository.save(comment);

        return mapToResponse(savedComment);
    }

    /**
     * Fetches all active comments for a task.
     *
     * Rules:
     * - Any workspace member can view comments.
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID currentUserId
    ) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        getProject(workspaceId, projectId);

        getTask(projectId, taskId);

        return taskCommentRepository.findActiveCommentsByTaskId(taskId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Deletes a comment using soft delete.
     *
     * Rules:
     * - Comment author can delete own comment.
     * - Workspace OWNER or ADMIN can delete any comment.
     * - Other users cannot delete someone else's comment.
     */
    @Transactional
    public void deleteComment(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID commentId,
            UUID currentUserId
    ) {
        WorkspaceMember membership =
                workspaceAccessService.getMembership(workspaceId, currentUserId);

        getProject(workspaceId, projectId);

        getTask(projectId, taskId);

        TaskComment comment = taskCommentRepository
                .findActiveCommentByIdAndTaskId(commentId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        boolean isAuthor = comment.getAuthor().getId().equals(currentUserId);
        boolean isOwnerOrAdmin = workspaceAccessService.isOwnerOrAdmin(membership);

        if (!isAuthor && !isOwnerOrAdmin) {
            throw new ForbiddenException("You do not have permission to delete this comment");
        }

        comment.setArchived(true);
    }

    /**
     * Fetches project and ensures project belongs to workspace.
     */
    private Project getProject(UUID workspaceId, UUID projectId) {
        return projectRepository
                .findByIdAndWorkspace_IdAndArchivedFalse(projectId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    /**
     * Fetches task and ensures task belongs to project.
     */
    private Task getTask(UUID projectId, UUID taskId) {
        return taskRepository.findActiveTaskByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    /**
     * Converts TaskComment entity to response DTO.
     */
    private CommentResponse mapToResponse(TaskComment comment) {
        User author = comment.getAuthor();

        return new CommentResponse(
                comment.getId(),
                comment.getTask().getId(),
                author.getId(),
                author.getFullName(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}