package com.astitva.collabflow.workspace.entity;

import com.astitva.collabflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a workspace in CollabFlow.
 *
 * A workspace is a top-level collaboration area.
 *
 * Example:
 * - "CollabFlow Development Team"
 * Later, each workspace will contain:
 * - Projects
 * - Boards
 * - Tasks
 * - Members
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "workspaces")
public class Workspace {

    /**
     * Primary key of the workspace.
     *
     * We are using UUID instead of Long because UUIDs are safer for public APIs.
     * Example:
     * /api/v1/workspaces/643846ab-0899-4f4f-9267-758775b6439b
     */
    @Id
    private UUID id;

    /**
     * Name of the workspace.
     *
     * nullable = false means workspace name is mandatory.
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Optional workspace description.
     *
     * Can be null.
     */
    @Column(length = 500)
    private String description;

    /**
     * User who created this workspace.
     *
     * Many workspaces can be created by one user.
     *
     * FetchType.LAZY means:
     * Do not load the User object immediately when Workspace is loaded.
     * Load it only when workspace.getCreatedBy() is accessed.
     *
     * This improves performance.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * Timestamp when workspace was created.
     *
     * updatable = false means this value should not change after creation.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when workspace was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * This method is automatically called by JPA before inserting a new row.
     *
     * We use this to:
     * - Generate UUID if id is null
     * - Set createdAt
     * - Set updatedAt
     */
    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * This method is automatically called by JPA before updating an existing row.
     *
     * We use this to update updatedAt timestamp.
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}