package com.astitva.collabflow.workspace.entity;

/**
 * Represents the role of a user inside a workspace.
 * Important:
 * This role is different from application-level UserRole.
 * Example:
 * UserRole.USER means the user is a normal application user.
 * WorkspaceRole.OWNER means the user owns a particular workspace.
 */
public enum WorkspaceRole {

    /**
     * Full control over the workspace.
     * Usually the user who created the workspace becomes OWNER.
     */
    OWNER,

    /**
     * Can manage workspace members and workspace-level settings,
     * but should not be allowed to remove or change the OWNER.
     */
    ADMIN,

    /**
     * Normal workspace participant.
     * Can usually create/update project-related resources later.
     */
    MEMBER,

    /**
     * Read-only member.
     * Can view workspace data but should not modify resources.
     */
    VIEWER
}
