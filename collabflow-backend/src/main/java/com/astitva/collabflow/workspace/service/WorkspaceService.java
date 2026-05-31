package com.astitva.collabflow.workspace.service;

import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.user.repository.UserRepository;
import com.astitva.collabflow.workspace.dto.CreateWorkspaceRequest;
import com.astitva.collabflow.workspace.dto.WorkspaceResponse;
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
}