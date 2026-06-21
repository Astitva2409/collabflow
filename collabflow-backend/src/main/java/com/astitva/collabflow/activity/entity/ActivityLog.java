package com.astitva.collabflow.activity.entity;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.user.entity.User;
import com.astitva.collabflow.workspace.entity.Workspace;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an audit/activity event in CollabFlow.
 *
 * Example:
 * - Task created
 * - Task moved
 * - Comment added
 * - Project archived
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    /**
     * Primary key of activity log.
     */
    @Id
    private UUID id;

    /**
     * Workspace where activity happened.
     *
     * Every activity belongs to a workspace.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    /**
     * Project where activity happened.
     *
     * Nullable because workspace-level activities may not belong to a project.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    /**
     * User who performed the activity.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    /**
     * ID of the target entity.
     *
     * Example:
     * taskId, commentId, projectId, member userId.
     */
    @Column(name = "target_id")
    private UUID targetId;

    /**
     * Type of target entity.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false, length = 50)
    private ActivityTargetType targetType;

    /**
     * Type of activity.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "activity_type", nullable = false, length = 80)
    private ActivityType activityType;

    /**
     * Human-readable activity description.
     */
    @Column(nullable = false, length = 1000)
    private String description;

    /**
     * Time when activity was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Called before inserting activity log.
     */
    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        createdAt = LocalDateTime.now();
    }
}