package com.astitva.collabflow.workspace.entity;

import com.astitva.collabflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents membership of a user inside a workspace.
 *
 * This is a join entity between User and Workspace.
 *
 * Why not use direct @ManyToMany?
 *
 * Because we need extra fields:
 * - role
 * - joinedAt
 *
 * So instead of:
 * User <-> Workspace
 *
 * We use:
 * User <-> WorkspaceMember <-> Workspace
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "workspace_members",
        uniqueConstraints = {
                /**
                 * This ensures the same user cannot be added twice
                 * to the same workspace.
                 */
                @UniqueConstraint(
                        name = "uk_workspace_members_workspace_user",
                        columnNames = {"workspace_id", "user_id"}
                )
        }
)
public class WorkspaceMember {

    /**
     * Primary key of workspace_members table.
     */
    @Id
    private UUID id;

    /**
     * Workspace to which this member belongs.
     *
     * Many members can belong to one workspace.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    /**
     * User who is a member of the workspace.
     *
     * One user can be part of many workspaces.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Role of this user inside this workspace.
     *
     * EnumType.STRING stores enum as text:
     * OWNER, ADMIN, MEMBER, VIEWER
     *
     * This is better than EnumType.ORDINAL because ordinal values can break
     * if enum order changes.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkspaceRole role;

    /**
     * Timestamp when user joined this workspace.
     */
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    /**
     * Called automatically before inserting a new membership row.
     *
     * We use this to:
     * - Generate UUID
     * - Set default role as MEMBER if not explicitly provided
     * - Set joinedAt timestamp
     */
    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (role == null) {
            role = WorkspaceRole.MEMBER;
        }

        joinedAt = LocalDateTime.now();
    }
}