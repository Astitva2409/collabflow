package com.astitva.collabflow.task.service;

import com.astitva.collabflow.board.entity.Board;
import com.astitva.collabflow.board.entity.BoardColumn;
import com.astitva.collabflow.board.repository.BoardColumnRepository;
import com.astitva.collabflow.board.repository.BoardRepository;
import com.astitva.collabflow.common.exception.BadRequestException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.project.repository.ProjectRepository;
import com.astitva.collabflow.task.dto.CreateTaskRequest;
import com.astitva.collabflow.task.dto.TaskResponse;
import com.astitva.collabflow.task.entity.Task;
import com.astitva.collabflow.task.entity.TaskPriority;
import com.astitva.collabflow.task.repository.TaskRepository;
import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.user.repository.UserRepository;
import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import com.astitva.collabflow.workspace.service.WorkspaceAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for task-related business logic.
 */
@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final UserRepository userRepository;
    private final WorkspaceAccessService workspaceAccessService;

    /**
     * Creates a task inside a project.
     *
     * Rules:
     * - Current user must be workspace member.
     * - VIEWER cannot create tasks.
     * - Task is created in provided column, or TODO column by default.
     * - Assignee, if provided, must be a member of the workspace.
     * - Task position is assigned as last item in the selected column.
     */
    @Transactional
    public TaskResponse createTask(
            UUID workspaceId,
            UUID projectId,
            UUID currentUserId,
            CreateTaskRequest request
    ) {
        WorkspaceMember currentUserMembership =
                workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanManageProjects(currentUserMembership);

        Project project = getProject(workspaceId, projectId);

        Board board = boardRepository.findByProject_Id(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        BoardColumn targetColumn = resolveTargetColumn(board, request.boardColumnId());

        User assignee = resolveAssignee(workspaceId, request.assignedTo());

        Integer maxPosition = taskRepository.findMaxPositionByBoardColumnId(targetColumn.getId());
        Integer newPosition = maxPosition + 1;

        Task task = Task.builder()
                .project(project)
                .boardColumn(targetColumn)
                .title(request.title().trim())
                .description(normalizeDescription(request.description()))
                .priority(request.priority() == null ? TaskPriority.MEDIUM : request.priority())
                .createdBy(currentUserMembership.getUser())
                .assignedTo(assignee)
                .position(newPosition)
                .dueDate(request.dueDate())
                .build();

        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    /**
     * Fetches all active tasks in a project.
     *
     * Rule:
     * - Current user must be a workspace member.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(UUID workspaceId, UUID projectId, UUID currentUserId) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        getProject(workspaceId, projectId);

        return taskRepository.findActiveTasksByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Fetches one task by ID.
     *
     * Rule:
     * - Current user must be a workspace member.
     * - Task must belong to requested project.
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(UUID workspaceId, UUID projectId, UUID taskId, UUID currentUserId) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        getProject(workspaceId, projectId);

        Task task = taskRepository.findActiveTaskByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        return mapToResponse(task);
    }

    /**
     * Fetches project and ensures it belongs to the workspace.
     */
    private Project getProject(UUID workspaceId, UUID projectId) {
        return projectRepository
                .findByIdAndWorkspace_IdAndArchivedFalse(projectId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    /**
     * Resolves target column for task creation.
     *
     * If boardColumnId is provided:
     * - Verify column belongs to the board.
     *
     * If boardColumnId is not provided:
     * - Use TODO column by default.
     */
    private BoardColumn resolveTargetColumn(Board board, UUID boardColumnId) {
        if (boardColumnId != null) {
            BoardColumn column = boardColumnRepository.findById(boardColumnId)
                    .orElseThrow(() -> new ResourceNotFoundException("Board column not found"));

            if (!column.getBoard().getId().equals(board.getId())) {
                throw new BadRequestException("Board column does not belong to this project board");
            }

            return column;
        }

        return boardColumnRepository.findByBoard_IdOrderByPositionAsc(board.getId())
                .stream()
                .filter(column -> column.getName().equalsIgnoreCase("TODO"))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Default TODO column not found"));
    }

    /**
     * Resolves assignee.
     *
     * If assignedTo is null, task remains unassigned.
     *
     * If assignedTo is provided:
     * - User must exist.
     * - User must be a member of the workspace.
     */
    private User resolveAssignee(UUID workspaceId, UUID assignedTo) {
        if (assignedTo == null) {
            return null;
        }

        workspaceAccessService.getMembership(workspaceId, assignedTo);
        return userRepository.findById(assignedTo)
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
    }

    /**
     * Converts Task entity into response DTO.
     */
    private TaskResponse mapToResponse(Task task) {
        User assignedUser = task.getAssignedTo();

        return new TaskResponse(
                task.getId(),
                task.getProject().getId(),
                task.getBoardColumn().getId(),
                task.getBoardColumn().getName(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getCreatedBy().getId(),
                assignedUser == null ? null : assignedUser.getId(),
                assignedUser == null ? null : assignedUser.getFullName(),
                task.getPosition(),
                task.getDueDate(),
                task.isArchived(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    /**
     * Normalizes description before saving.
     */
    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}