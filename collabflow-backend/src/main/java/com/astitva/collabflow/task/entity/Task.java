package com.astitva.collabflow.task.entity;

import com.astitva.collabflow.board.entity.BoardColumn;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a task inside a project Kanban board.
 *
 * Task status is derived from the board column.
 *
 * Example:
 * If task belongs to TODO column, task is considered TODO.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {

    /**
     * Primary key of task.
     */
    @Id
    private UUID id;

    /**
     * Project to which this task belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Board column where this task currently exists.
     *
     * This acts as task status.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_column_id", nullable = false)
    private BoardColumn boardColumn;

    /**
     * Task title.
     */
    @Column(nullable = false, length = 160)
    private String title;

    /**
     * Optional detailed task description.
     */
    @Column(length = 2000)
    private String description;

    /**
     * Task priority.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TaskPriority priority;

    /**
     * User who created the task.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    /**
     * User assigned to this task.
     *
     * Nullable because a task can be unassigned.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    /**
     * Task order inside a board column.
     *
     * This will support Kanban drag-and-drop later.
     */
    @Column(nullable = false)
    private Integer position;

    /**
     * Optional due date.
     */
    @Column(name = "due_date")
    private LocalDateTime dueDate;

    /**
     * Soft delete flag.
     *
     * Instead of deleting task permanently, we archive it.
     */
    @Column(nullable = false)
    private boolean archived;

    /**
     * Task creation timestamp.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Task last updated timestamp.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Called automatically before inserting a task.
     */
    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        if (priority == null) {
            priority = TaskPriority.MEDIUM;
        }

        if (archived) {
            archived = false;
        }

        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Called automatically before updating a task.
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}