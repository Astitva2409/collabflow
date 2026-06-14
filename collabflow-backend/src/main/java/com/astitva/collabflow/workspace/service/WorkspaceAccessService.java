package com.astitva.collabflow.workspace.service;

import com.astitva.collabflow.common.exception.ForbiddenException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import com.astitva.collabflow.workspace.entity.WorkspaceRole;
import com.astitva.collabflow.workspace.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

/**
 * Centralized service for handling workspace access and permission checks.
 *
 * This prevents duplication of:
 * - membership fetch logic
 * - role validation
 *
 * All modules (Workspace, Project, Board, Task) should use this.
 */
@Service
@RequiredArgsConstructor
public class WorkspaceAccessService {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    /**
     * Fetch membership of a user inside a workspace.
     *
     * If user is NOT a member:
     * -> We return "Workspace not found"
     *
     * Why?
     * Security reason:
     * We should not reveal whether a workspace exists to unauthorized users.
     */
    public WorkspaceMember getMembership(UUID workspaceId, UUID userId) {
        return workspaceMemberRepository
                .findByWorkspace_IdAndUser_Id(workspaceId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Workspace not found"));
    }

    /**
     * Validate user has at least VIEW access.
     *
     * Basically:
     * If membership exists → access allowed.
     */
    public void validateCanViewWorkspace(UUID workspaceId, UUID userId) {
        getMembership(workspaceId, userId);
    }

    /**
     * Validate user is OWNER.
     */
    public void validateOwner(WorkspaceMember membership) {
        if (membership.getRole() != WorkspaceRole.OWNER) {
            throw new ForbiddenException("Only workspace owner can perform this action");
        }
    }

    /**
     * Validate user is OWNER or ADMIN.
     */
    public void validateOwnerOrAdmin(WorkspaceMember membership) {
        if (membership.getRole() != WorkspaceRole.OWNER &&
                membership.getRole() != WorkspaceRole.ADMIN) {
            throw new ForbiddenException("You do not have permission to perform this action");
        }
    }

    /**
     * Validate user can manage projects.
     *
     * Rules:
     * VIEWER cannot create/update/archive projects.
     */
    public void validateCanManageProjects(WorkspaceMember membership) {
        if (membership.getRole() == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("You do not have permission to manage projects");
        }
    }

    /**
     * Validate user can manage members.
     *
     * OWNER & ADMIN allowed.
     */
    public void validateCanManageMembers(WorkspaceMember membership) {
        validateOwnerOrAdmin(membership);
    }

    /**
     * Validate user can add comments.
     *
     * VIEWER can only read comments.
     */
    public void validateCanAddComments(WorkspaceMember membership) {
        if (membership.getRole() == WorkspaceRole.VIEWER) {
            throw new ForbiddenException("You do not have permission to add comments");
        }
    }

    /**
     * Checks whether user is OWNER or ADMIN.
     *
     * Useful for delete permissions where OWNER/ADMIN can delete any comment.
     */
    public boolean isOwnerOrAdmin(WorkspaceMember membership) {
        return membership.getRole() == WorkspaceRole.OWNER ||
                membership.getRole() == WorkspaceRole.ADMIN;
    }
}