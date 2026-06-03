package com.astitva.collabflow.project.entity;

import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a project inside a workspace.
 *
 * Example:
 * Workspace: CollabFlow Development Team
 * Project: Authentication Module
 * Project: Workspace Module
 * Project: Frontend Dashboard
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {

    /**
     * Primary key of project.
     */
    @Id
    private UUID id;

    /**
     * Workspace to which this project belongs.
     *
     * Many projects can exist inside one workspace.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    /**
     * Project name.
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Optional project description.
     */
    @Column(length = 1000)
    private String description;

    /**
     * Current status of project.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectStatus status;

    /**
     * Project priority.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ProjectPriority priority;

    /**
     * User who created this project.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * Soft delete flag.
     *
     * Instead of deleting project permanently, we mark it archived.
     */
    @Column(nullable = false)
    private boolean archived;

    /**
     * Timestamp when project was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when project was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Called automatically before inserting a new project row.
     */
    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (status == null) {
            status = ProjectStatus.ACTIVE;
        }

        if (priority == null) {
            priority = ProjectPriority.MEDIUM;
        }

        archived = false;

        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Called automatically before updating project row.
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}