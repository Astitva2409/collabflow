package com.astitva.collabflow.workspace.service;

import com.astitva.collabflow.common.exception.BadRequestException;
import com.astitva.collabflow.common.exception.ForbiddenException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.user.repository.UserRepository;
import com.astitva.collabflow.workspace.dto.*;
import com.astitva.collabflow.workspace.entity.Workspace;
import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import com.astitva.collabflow.workspace.entity.WorkspaceRole;
import com.astitva.collabflow.workspace.repository.WorkspaceMemberRepository;
import com.astitva.collabflow.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for workspace-related business logic.
 *
 * Controllers should only handle HTTP request/response.
 * Actual business rules should stay in service layer.
 */
@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

    /**
     * Creates a new workspace.
     *
     * Business flow:
     * 1. Find current logged-in user.
     * 2. Create workspace.
     * 3. Add current user as OWNER in workspace_members table.
     * 4. Return workspace response.
     *
     * @Transactional is important because we are inserting into two tables:
     * - workspaces
     * - workspace_members
     *
     * If one insert fails, both should be rolled back.
     */
    @Transactional
    public WorkspaceResponse createWorkspace(CreateWorkspaceRequest request, UUID currentUserId) {
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Workspace workspace = Workspace.builder()
                .name(request.name().trim())
                .description(normalizeDescription(request.description()))
                .createdBy(currentUser)
                .build();

        Workspace savedWorkspace = workspaceRepository.save(workspace);

        WorkspaceMember ownerMembership = WorkspaceMember.builder()
                .workspace(savedWorkspace)
                .user(currentUser)
                .role(WorkspaceRole.OWNER)
                .build();

        WorkspaceMember savedMembership = workspaceMemberRepository.save(ownerMembership);

        return mapToResponse(savedWorkspace, savedMembership.getRole());
    }

    /**
     * Fetches all workspaces where current user is a member.
     *
     * We query workspace_members because membership defines access.
     */
    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(UUID currentUserId) {
        return workspaceMemberRepository.findAllByUserIdWithWorkspace(currentUserId)
                .stream()
                .map(member -> mapToResponse(member.getWorkspace(), member.getRole()))
                .toList();
    }

    /**
     * Fetches one workspace by ID.
     *
     * Important:
     * User should only be able to fetch workspace if user is a member.
     *
     * So we do not simply call workspaceRepository.findById().
     * Instead, we check membership using workspaceId + currentUserId.
     */
    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceById(UUID workspaceId, UUID currentUserId) {
        WorkspaceMember membership = getMembership(workspaceId, currentUserId);

        return mapToResponse(membership.getWorkspace(), membership.getRole());
    }

    /**
     * Adds a new member to workspace.
     *
     * Rules:
     * - Current user must be OWNER or ADMIN.
     * - User to be added must exist.
     * - User should not already be a member.
     * - OWNER role cannot be assigned through this API.
     */
    @Transactional
    public void addMember(UUID workspaceId, UUID currentUserId, AddMemberRequest request) {
        WorkspaceMember currentUserMembership = getMembership(workspaceId, currentUserId);

        validateOwnerOrAdmin(currentUserMembership);

        if (request.role() == WorkspaceRole.OWNER) {
            throw new BadRequestException("Cannot assign OWNER role while adding member");
        }

        if (workspaceMemberRepository.existsByWorkspace_IdAndUser_Id(workspaceId, request.userId())) {
            throw new BadRequestException("User is already a member of this workspace");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        WorkspaceMember newMember = WorkspaceMember.builder()
                .workspace(currentUserMembership.getWorkspace())
                .user(user)
                .role(request.role())
                .build();

        workspaceMemberRepository.save(newMember);
    }

    /**
     * Fetches all members of a workspace.
     *
     * Rule:
     * Only existing workspace members can view member list.
     */
    @Transactional(readOnly = true)
    public List<WorkspaceMemberResponse> getMembers(UUID workspaceId, UUID currentUserId) {
        getMembership(workspaceId, currentUserId);

        return workspaceMemberRepository.findAllByWorkspace_Id(workspaceId)
                .stream()
                .map(member -> new WorkspaceMemberResponse(
                        member.getUser().getId(),
                        member.getUser().getFullName(),
                        member.getUser().getEmail(),
                        member.getRole(),
                        member.getJoinedAt()
                ))
                .toList();
    }

    /**
     * Updates role of a workspace member.
     *
     * Rules:
     * - Only OWNER can update roles.
     * - OWNER's role cannot be changed.
     * - No one can assign OWNER role through this API.
     */
    @Transactional
    public void updateMemberRole(UUID workspaceId, UUID currentUserId, UUID targetUserId, UpdateRoleRequest request) {
        WorkspaceMember currentUserMembership = getMembership(workspaceId, currentUserId);

        if (currentUserMembership.getRole() != WorkspaceRole.OWNER) {
            throw new ForbiddenException("Only workspace owner can update roles");
        }

        if (request.role() == WorkspaceRole.OWNER) {
            throw new BadRequestException("Cannot assign OWNER role");
        }

        WorkspaceMember targetMembership = getMembership(workspaceId, targetUserId);

        if (targetMembership.getRole() == WorkspaceRole.OWNER) {
            throw new BadRequestException("Cannot change role of workspace owner");
        }

        targetMembership.setRole(request.role());
    }

    /**
     * Removes a member from workspace.
     *
     * Rules:
     * - Only OWNER can remove members.
     * - OWNER cannot remove himself.
     */
    @Transactional
    public void removeMember(UUID workspaceId, UUID currentUserId, UUID targetUserId) {
        WorkspaceMember currentUserMembership = getMembership(workspaceId, currentUserId);

        if (currentUserMembership.getRole() != WorkspaceRole.OWNER) {
            throw new ForbiddenException("Only workspace owner can remove members");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("Owner cannot remove himself");
        }

        WorkspaceMember targetMembership = getMembership(workspaceId, targetUserId);

        workspaceMemberRepository.delete(targetMembership);
    }

    /**
     * Common helper method to fetch membership.
     *
     * If membership does not exist, we return "Workspace not found".
     *
     * Why not "Membership not found"?
     * Because from security perspective, we should not reveal whether workspace exists
     * if current user does not belong to that workspace.
     */
    private WorkspaceMember getMembership(UUID workspaceId, UUID userId) {
        return workspaceMemberRepository
                .findByWorkspace_IdAndUser_Id(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
    }

    /**
     * Checks whether current user has OWNER or ADMIN role.
     *
     * Used for actions like adding members.
     */
    private void validateOwnerOrAdmin(WorkspaceMember member) {
        if (member.getRole() != WorkspaceRole.OWNER && member.getRole() != WorkspaceRole.ADMIN) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    /**
     * Converts Workspace entity + user's workspace role into response DTO.
     *
     * We keep this method private to avoid repeating mapping logic.
     */
    private WorkspaceResponse mapToResponse(Workspace workspace, WorkspaceRole role) {
        return new WorkspaceResponse(
                workspace.getId(),
                workspace.getName(),
                workspace.getDescription(),
                role,
                workspace.getCreatedAt(),
                workspace.getUpdatedAt()
        );
    }

    /**
     * Normalizes description before saving.
     *
     * If description is null or blank, store null.
     * Otherwise, trim extra spaces.
     */
    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}