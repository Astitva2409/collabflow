package com.astitva.collabflow.task.service;

import com.astitva.collabflow.board.entity.Board;
import com.astitva.collabflow.board.entity.BoardColumn;
import com.astitva.collabflow.board.repository.BoardColumnRepository;
import com.astitva.collabflow.board.repository.BoardRepository;
import com.astitva.collabflow.common.exception.BadRequestException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.project.repository.ProjectRepository;
import com.astitva.collabflow.task.dto.AssignTaskRequest;
import com.astitva.collabflow.task.dto.CreateTaskRequest;
import com.astitva.collabflow.task.dto.MoveTaskRequest;
import com.astitva.collabflow.task.dto.TaskResponse;
import com.astitva.collabflow.task.dto.UpdateTaskRequest;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for task-related business logic.
 *
 * A task belongs to:
 * - One project
 * - One board column
 *
 * Task status is derived from the board column.
 * Example:
 * If task is in TODO column, task status is TODO.
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
     * Creates a new task inside a project.
     *
     * Rules:
     * - Current user must be a workspace member.
     * - VIEWER cannot create tasks.
     * - If boardColumnId is not provided, task goes to TODO column.
     * - If assignee is provided, assignee must be a workspace member.
     * - New task is added at the bottom of the selected column.
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

        Board board = getBoard(projectId);

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
     * Fetches all active tasks inside a project.
     *
     * Rules:
     * - Current user must be a workspace member.
     * - Project must belong to the workspace.
     */
    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(
            UUID workspaceId,
            UUID projectId,
            UUID currentUserId
    ) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        getProject(workspaceId, projectId);

        return taskRepository.findActiveTasksByProjectId(projectId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Fetches one active task by ID.
     *
     * Rules:
     * - Current user must be a workspace member.
     * - Project must belong to workspace.
     * - Task must belong to project.
     */
    @Transactional(readOnly = true)
    public TaskResponse getTaskById(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID currentUserId
    ) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        getProject(workspaceId, projectId);

        Task task = getTask(projectId, taskId);

        return mapToResponse(task);
    }

    /**
     * Updates task details.
     *
     * Rules:
     * - Current user must be workspace member.
     * - VIEWER cannot update tasks.
     * - At least one field must be present.
     *
     * Note:
     * Since this is PATCH, null means "do not update".
     */
    @Transactional
    public TaskResponse updateTask(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID currentUserId,
            UpdateTaskRequest request
    ) {
        WorkspaceMember membership =
                workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanManageProjects(membership);

        validateAtLeastOneFieldPresent(request);

        getProject(workspaceId, projectId);

        Task task = getTask(projectId, taskId);

        if (request.title() != null) {
            task.setTitle(request.title().trim());
        }

        if (request.description() != null) {
            task.setDescription(normalizeDescription(request.description()));
        }

        if (request.priority() != null) {
            task.setPriority(request.priority());
        }

        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }

        return mapToResponse(task);
    }

    /**
     * Moves task between columns or reorders within the same column.
     *
     * Example 1:
     * TODO position 3 -> IN_PROGRESS position 1
     *
     * Example 2:
     * TODO position 3 -> TODO position 1
     *
     * Position logic:
     * - Remove task from old ordering list.
     * - Insert task into new ordering list.
     * - Recalculate positions from 1 to N.
     */
    @Transactional
    public TaskResponse moveTask(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID currentUserId,
            MoveTaskRequest request
    ) {
        WorkspaceMember membership =
                workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanManageProjects(membership);

        Project project = getProject(workspaceId, projectId);

        Board board = getBoard(project.getId());

        Task task = getTask(projectId, taskId);

        BoardColumn sourceColumn = task.getBoardColumn();

        BoardColumn targetColumn = resolveTargetColumn(board, request.boardColumnId());

        boolean movingWithinSameColumn = sourceColumn.getId().equals(targetColumn.getId());

        if (movingWithinSameColumn) {
            reorderTaskWithinSameColumn(task, targetColumn, request.position());
        } else {
            moveTaskToDifferentColumn(task, sourceColumn, targetColumn, request.position());
        }

        return mapToResponse(task);
    }

    /**
     * Assigns or unassigns a task.
     *
     * Rules:
     * - Current user must be workspace member.
     * - VIEWER cannot assign/unassign tasks.
     * - If assignedTo is null, task becomes unassigned.
     * - If assignedTo is present, user must be workspace member.
     */
    @Transactional
    public TaskResponse assignTask(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID currentUserId,
            AssignTaskRequest request
    ) {
        WorkspaceMember membership =
                workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanManageProjects(membership);

        getProject(workspaceId, projectId);

        Task task = getTask(projectId, taskId);

        User assignee = resolveAssignee(workspaceId, request.assignedTo());

        task.setAssignedTo(assignee);

        return mapToResponse(task);
    }

    /**
     * Archives a task.
     *
     * This is soft delete.
     *
     * After archiving:
     * - task.archived = true
     * - active task positions in that column are normalized
     */
    @Transactional
    public void archiveTask(
            UUID workspaceId,
            UUID projectId,
            UUID taskId,
            UUID currentUserId
    ) {
        WorkspaceMember membership =
                workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanManageProjects(membership);

        getProject(workspaceId, projectId);

        Task task = getTask(projectId, taskId);

        UUID columnId = task.getBoardColumn().getId();

        task.setArchived(true);

        normalizeActivePositionsInColumn(columnId, task.getId());
    }

    /**
     * Reorders task inside the same column.
     */
    private void reorderTaskWithinSameColumn(
            Task task,
            BoardColumn column,
            Integer requestedPosition
    ) {
        List<Task> tasksInColumn = new ArrayList<>(
                taskRepository.findByBoardColumn_IdAndArchivedFalseOrderByPositionAsc(column.getId())
        );

        tasksInColumn.removeIf(existingTask -> existingTask.getId().equals(task.getId()));

        int insertIndex = calculateInsertIndex(requestedPosition, tasksInColumn.size());

        tasksInColumn.add(insertIndex, task);

        normalizePositions(tasksInColumn);
    }

    /**
     * Moves task from source column to target column.
     */
    private void moveTaskToDifferentColumn(
            Task task,
            BoardColumn sourceColumn,
            BoardColumn targetColumn,
            Integer requestedPosition
    ) {
        List<Task> sourceColumnTasks = new ArrayList<>(
                taskRepository.findByBoardColumn_IdAndArchivedFalseOrderByPositionAsc(sourceColumn.getId())
        );

        sourceColumnTasks.removeIf(existingTask -> existingTask.getId().equals(task.getId()));

        normalizePositions(sourceColumnTasks);

        List<Task> targetColumnTasks = new ArrayList<>(
                taskRepository.findByBoardColumn_IdAndArchivedFalseOrderByPositionAsc(targetColumn.getId())
        );

        int insertIndex = calculateInsertIndex(requestedPosition, targetColumnTasks.size());

        task.setBoardColumn(targetColumn);

        targetColumnTasks.add(insertIndex, task);

        normalizePositions(targetColumnTasks);
    }

    /**
     * Recalculates positions from 1 to N.
     *
     * Example:
     * Before:
     * Task A -> position 1
     * Task C -> position 4
     *
     * After:
     * Task A -> position 1
     * Task C -> position 2
     */
    private void normalizePositions(List<Task> tasks) {
        for (int index = 0; index < tasks.size(); index++) {
            tasks.get(index).setPosition(index + 1);
        }
    }

    /**
     * Normalizes active tasks in a column after one task is archived.
     *
     * archivedTaskId is removed defensively in case persistence context
     * still contains the archived task in query result.
     */
    private void normalizeActivePositionsInColumn(UUID columnId, UUID archivedTaskId) {
        List<Task> activeTasks = new ArrayList<>(
                taskRepository.findByBoardColumn_IdAndArchivedFalseOrderByPositionAsc(columnId)
        );

        activeTasks.removeIf(task -> task.getId().equals(archivedTaskId));

        normalizePositions(activeTasks);
    }

    /**
     * Converts requested 1-based position into Java list index.
     *
     * requestedPosition = 1 means insert at index 0.
     *
     * If requested position is greater than last possible position,
     * task is inserted at the end.
     */
    private int calculateInsertIndex(Integer requestedPosition, int currentSize) {
        if (requestedPosition <= 1) {
            return 0;
        }

        if (requestedPosition > currentSize + 1) {
            return currentSize;
        }

        return requestedPosition - 1;
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
     * Fetches board for project.
     */
    private Board getBoard(UUID projectId) {
        return boardRepository.findByProject_Id(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));
    }

    /**
     * Fetches active task and ensures task belongs to project.
     */
    private Task getTask(UUID projectId, UUID taskId) {
        return taskRepository.findActiveTaskByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    /**
     * Resolves target column.
     *
     * If boardColumnId is provided:
     * - Fetch column.
     * - Check column belongs to project board.
     *
     * If boardColumnId is null:
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
     * Resolves assignee for task.
     *
     * If assignedTo is null:
     * - Task remains unassigned.
     *
     * If assignedTo is provided:
     * - User must be part of workspace.
     * - User must exist.
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
     * Prevents empty PATCH request.
     */
    private void validateAtLeastOneFieldPresent(UpdateTaskRequest request) {
        if (request.title() == null
                && request.description() == null
                && request.priority() == null
                && request.dueDate() == null) {
            throw new BadRequestException("At least one field must be provided for update");
        }
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
     * Normalizes task description.
     */
    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}