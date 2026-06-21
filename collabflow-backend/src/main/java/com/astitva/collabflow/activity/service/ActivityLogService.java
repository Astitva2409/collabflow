package com.astitva.collabflow.activity.service;

import com.astitva.collabflow.activity.dto.ActivityLogResponse;
import com.astitva.collabflow.activity.entity.ActivityLog;
import com.astitva.collabflow.activity.entity.ActivityTargetType;
import com.astitva.collabflow.activity.entity.ActivityType;
import com.astitva.collabflow.activity.repository.ActivityLogRepository;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.project.repository.ProjectRepository;
import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.workspace.entity.Workspace;
import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import com.astitva.collabflow.workspace.repository.WorkspaceRepository;
import com.astitva.collabflow.workspace.service.WorkspaceAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for activity log logic.
 */
@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final WorkspaceRepository workspaceRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceAccessService workspaceAccessService;

    /**
     * Creates an activity log entry.
     *
     * This method will be reused by other modules.
     */
    @Transactional
    public void logActivity(
            Workspace workspace,
            Project project,
            User actor,
            UUID targetId,
            ActivityTargetType targetType,
            ActivityType activityType,
            String description
    ) {
        ActivityLog activityLog = ActivityLog.builder()
                .workspace(workspace)
                .project(project)
                .actor(actor)
                .targetId(targetId)
                .targetType(targetType)
                .activityType(activityType)
                .description(description)
                .build();

        activityLogRepository.save(activityLog);
    }

    /**
     * Fetches workspace activities.
     *
     * Rule:
     * - Current user must be workspace member.
     */
    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getWorkspaceActivities(UUID workspaceId, UUID currentUserId) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        return activityLogRepository.findByWorkspaceIdOrderByCreatedAtDesc(workspaceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Fetches project activities.
     *
     * Rule:
     * - Current user must be workspace member.
     * - Project must belong to workspace.
     */
    @Transactional(readOnly = true)
    public List<ActivityLogResponse> getProjectActivities(UUID workspaceId, UUID projectId, UUID currentUserId) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        projectRepository.findByIdAndWorkspace_IdAndArchivedFalse(projectId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        return activityLogRepository.findByWorkspaceIdAndProjectIdOrderByCreatedAtDesc(workspaceId, projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Helper method for logging workspace-created activity.
     */
    @Transactional
    public void logWorkspaceCreated(Workspace workspace, User actor) {
        logActivity(
                workspace,
                null,
                actor,
                workspace.getId(),
                ActivityTargetType.WORKSPACE,
                ActivityType.WORKSPACE_CREATED,
                "Created workspace '" + workspace.getName() + "'"
        );
    }

    /**
     * Helper method for logging project-created activity.
     */
    @Transactional
    public void logProjectCreated(Workspace workspace, Project project, User actor) {
        logActivity(
                workspace,
                project,
                actor,
                project.getId(),
                ActivityTargetType.PROJECT,
                ActivityType.PROJECT_CREATED,
                "Created project '" + project.getName() + "'"
        );
    }

    /**
     * Helper method for logging task-created activity.
     */
    @Transactional
    public void logTaskCreated(Workspace workspace, Project project, User actor, UUID taskId, String taskTitle) {
        logActivity(
                workspace,
                project,
                actor,
                taskId,
                ActivityTargetType.TASK,
                ActivityType.TASK_CREATED,
                "Created task '" + taskTitle + "'"
        );
    }

    /**
     * Helper method for logging task moved activity.
     */
    @Transactional
    public void logTaskMoved(
            Workspace workspace,
            Project project,
            User actor,
            UUID taskId,
            String taskTitle,
            String fromColumn,
            String toColumn
    ) {
        logActivity(
                workspace,
                project,
                actor,
                taskId,
                ActivityTargetType.TASK,
                ActivityType.TASK_MOVED,
                "Moved task '" + taskTitle + "' from " + fromColumn + " to " + toColumn
        );
    }

    /**
     * Helper method for logging comment added activity.
     */
    @Transactional
    public void logCommentAdded(Workspace workspace, Project project, User actor, UUID commentId, String taskTitle) {
        logActivity(
                workspace,
                project,
                actor,
                commentId,
                ActivityTargetType.COMMENT,
                ActivityType.COMMENT_ADDED,
                "Added a comment on task '" + taskTitle + "'"
        );
    }

    /**
     * Converts ActivityLog entity to response DTO.
     */
    private ActivityLogResponse mapToResponse(ActivityLog activityLog) {
        return new ActivityLogResponse(
                activityLog.getId(),
                activityLog.getWorkspace().getId(),
                activityLog.getProject() == null ? null : activityLog.getProject().getId(),
                activityLog.getActor().getId(),
                activityLog.getActor().getFullName(),
                activityLog.getTargetId(),
                activityLog.getTargetType(),
                activityLog.getActivityType(),
                activityLog.getDescription(),
                activityLog.getCreatedAt()
        );
    }
}