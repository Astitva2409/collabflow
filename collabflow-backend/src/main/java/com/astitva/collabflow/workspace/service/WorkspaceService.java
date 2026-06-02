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

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final UserRepository userRepository;

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

    @Transactional(readOnly = true)
    public List<WorkspaceResponse> getMyWorkspaces(UUID currentUserId) {
        return workspaceMemberRepository.findAllByUserIdWithWorkspace(currentUserId)
                .stream()
                .map(member -> mapToResponse(member.getWorkspace(), member.getRole()))
                .toList();
    }

    @Transactional(readOnly = true)
    public WorkspaceResponse getWorkspaceById(UUID workspaceId, UUID currentUserId) {
        WorkspaceMember membership = workspaceMemberRepository
                .findByWorkspace_IdAndUser_Id(workspaceId, currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));

        return mapToResponse(membership.getWorkspace(), membership.getRole());
    }

    @Transactional
    public void addMember(UUID workspaceId, UUID currentUserId, AddMemberRequest request) {

        WorkspaceMember currentUserMembership = getMembership(workspaceId, currentUserId);
        validateOwnerOrAdmin(currentUserMembership);

        if (workspaceMemberRepository.existsByWorkspace_IdAndUser_Id(workspaceId, request.userId())) {
            throw new BadRequestException("User is already a member of this workspace");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.role() == WorkspaceRole.OWNER) {
            throw new BadRequestException("Cannot assign OWNER role while adding member");
        }

        WorkspaceMember newMember = WorkspaceMember.builder()
                .workspace(currentUserMembership.getWorkspace())
                .user(user)
                .role(request.role())
                .build();

        workspaceMemberRepository.save(newMember);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceMemberResponse> getMembers(UUID workspaceId, UUID currentUserId) {

        getMembership(workspaceId, currentUserId); // ensures user belongs

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

    @Transactional
    public void updateMemberRole(UUID workspaceId, UUID currentUserId, UUID targetUserId, UpdateRoleRequest request) {

        if (request.role() == WorkspaceRole.OWNER) {
            throw new BadRequestException("Cannot assign OWNER role");
        }

        WorkspaceMember currentUserMembership = getMembership(workspaceId, currentUserId);

        if (currentUserMembership.getRole() != WorkspaceRole.OWNER) {
            throw new ForbiddenException("Only workspace owner can update roles");
        }

        WorkspaceMember targetMembership = getMembership(workspaceId, targetUserId);

        if (targetMembership.getRole() == WorkspaceRole.OWNER) {
            throw new BadRequestException("Cannot change role of workspace owner");
        }

        targetMembership.setRole(request.role());
    }

    @Transactional
    public void removeMember(UUID workspaceId, UUID currentUserId, UUID targetUserId) {

        WorkspaceMember currentUserMembership = getMembership(workspaceId, currentUserId);

        if (currentUserMembership.getRole() != WorkspaceRole.OWNER) {
            throw new BadRequestException("Only workspace owner can remove members");
        }

        if (currentUserId.equals(targetUserId)) {
            throw new BadRequestException("Owner cannot remove himself");
        }

        WorkspaceMember targetMembership = getMembership(workspaceId, targetUserId);

        workspaceMemberRepository.delete(targetMembership);
    }

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

    private String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }

    private WorkspaceMember getMembership(UUID workspaceId, UUID userId) {
        return workspaceMemberRepository
                .findByWorkspace_IdAndUser_Id(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
    }

    private void validateOwnerOrAdmin(WorkspaceMember member) {
        if (member.getRole() != WorkspaceRole.OWNER && member.getRole() != WorkspaceRole.ADMIN) {
            throw new BadRequestException("You do not have permission to perform this action");
        }
    }
}