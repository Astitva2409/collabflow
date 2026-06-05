package com.astitva.collabflow.project.service;

import com.astitva.collabflow.board.service.BoardService;
import com.astitva.collabflow.common.exception.BadRequestException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.project.dto.CreateProjectRequest;
import com.astitva.collabflow.project.dto.ProjectResponse;
import com.astitva.collabflow.project.dto.UpdateProjectRequest;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.project.entity.ProjectPriority;
import com.astitva.collabflow.project.entity.ProjectStatus;
import com.astitva.collabflow.project.repository.ProjectRepository;
import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import com.astitva.collabflow.workspace.service.WorkspaceAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for project-related business logic.
 *
 * This service uses WorkspaceAccessService for workspace membership
 * and role-based permission checks.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final WorkspaceAccessService workspaceAccessService;
    private final BoardService boardService;

    /**
     * Creates a project inside a workspace.
     *
     * Rules:
     * - Current user must be a member of workspace.
     * - VIEWER cannot create project.
     * - Project name should not already exist as an active project in same workspace.
     */
    @Transactional
    public ProjectResponse createProject(UUID workspaceId, UUID currentUserId, CreateProjectRequest request) {
        WorkspaceMember membership = workspaceAccessService.getMembership(workspaceId, currentUserId);
        workspaceAccessService.validateCanManageProjects(membership);

        String normalizedName = request.name().trim();

        if (projectRepository.existsByWorkspace_IdAndNameIgnoreCaseAndArchivedFalse(workspaceId, normalizedName)) {
            throw new BadRequestException("Project with this name already exists in workspace");
        }

        Project project = Project.builder()
                .workspace(membership.getWorkspace())
                .name(normalizedName)
                .description(normalizeDescription(request.description()))
                .priority(request.priority() == null ? ProjectPriority.MEDIUM : request.priority())
                .status(ProjectStatus.ACTIVE)
                .createdBy(membership.getUser())
                .build();

        Project savedProject = projectRepository.save(project);

        boardService.createDefaultBoardForProject(savedProject);
        return mapToResponse(savedProject);
    }

    /**
     * Fetches all active projects inside a workspace.
     *
     * Rule:
     * - Current user must be a member of workspace.
     */
    @Transactional(readOnly = true)
    public List<ProjectResponse> getProjects(UUID workspaceId, UUID currentUserId) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        return projectRepository.findByWorkspace_IdAndArchivedFalseOrderByCreatedAtDesc(workspaceId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Fetches one project by ID.
     *
     * Rule:
     * - Current user must be a member of workspace.
     * - Project must belong to that workspace.
     */
    @Transactional(readOnly = true)
    public ProjectResponse getProjectById(UUID workspaceId, UUID projectId, UUID currentUserId) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        Project project = getProject(workspaceId, projectId);

        return mapToResponse(project);
    }

    /**
     * Updates project fields.
     *
     * Rules:
     * - Current user must be workspace member.
     * - VIEWER cannot update.
     * - Duplicate active project name is not allowed.
     */
    @Transactional
    public ProjectResponse updateProject(
            UUID workspaceId,
            UUID projectId,
            UUID currentUserId,
            UpdateProjectRequest request
    ) {
        WorkspaceMember membership = workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanManageProjects(membership);

        validateAtLeastOneFieldPresent(request);

        Project project = getProject(workspaceId, projectId);

        if (request.name() != null) {
            String normalizedName = request.name().trim();

            boolean nameChanged = !project.getName().equalsIgnoreCase(normalizedName);

            if (nameChanged &&
                    projectRepository.existsByWorkspace_IdAndNameIgnoreCaseAndArchivedFalse(workspaceId, normalizedName)) {
                throw new BadRequestException("Project with this name already exists in workspace");
            }

            project.setName(normalizedName);
        }

        if (request.description() != null) {
            project.setDescription(normalizeDescription(request.description()));
        }

        if (request.status() != null) {
            project.setStatus(request.status());
        }

        if (request.priority() != null) {
            project.setPriority(request.priority());
        }

        return mapToResponse(project);
    }

    /**
     * Archives a project.
     *
     * This is soft delete.
     *
     * Rules:
     * - Current user must be workspace member.
     * - VIEWER cannot archive.
     */
    @Transactional
    public void archiveProject(UUID workspaceId, UUID projectId, UUID currentUserId) {
        WorkspaceMember membership = workspaceAccessService.getMembership(workspaceId, currentUserId);

        workspaceAccessService.validateCanManageProjects(membership);

        Project project = getProject(workspaceId, projectId);

        project.setArchived(true);
        project.setStatus(ProjectStatus.ARCHIVED);
    }

    /**
     * Fetches project and ensures project belongs to requested workspace.
     */
    private Project getProject(UUID workspaceId, UUID projectId) {
        return projectRepository
                .findByIdAndWorkspace_IdAndArchivedFalse(projectId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    /**
     * Prevents empty PATCH request.
     */
    private void validateAtLeastOneFieldPresent(UpdateProjectRequest request) {
        if (request.name() == null &&
                request.description() == null &&
                request.status() == null &&
                request.priority() == null) {
            throw new BadRequestException("At least one field must be provided for update");
        }
    }

    /**
     * Converts Project entity into ProjectResponse DTO.
     */
    private ProjectResponse mapToResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getWorkspace().getId(),
                project.getName(),
                project.getDescription(),
                project.getStatus(),
                project.getPriority(),
                project.getCreatedBy().getId(),
                project.isArchived(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }

    /**
     * Normalizes description.
     *
     * If blank, stores null.
     */
    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}