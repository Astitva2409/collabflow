package com.astitva.collabflow.comment.entity;

import com.astitva.collabflow.task.entity.Task;
import com.astitva.collabflow.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a comment added to a task.
 *
 * Comments are soft-deleted using archived flag.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "task_comments")
public class TaskComment {

    /**
     * Primary key of comment.
     */
    @Id
    private UUID id;

    /**
     * Task to which this comment belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    /**
     * User who wrote the comment.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    /**
     * Comment text.
     */
    @Column(nullable = false, length = 2000)
    private String content;

    /**
     * Soft delete flag.
     */
    @Column(nullable = false)
    private boolean archived;

    /**
     * Comment creation timestamp.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Comment update timestamp.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Called before inserting comment.
     */
    @PrePersist
    void onCreate() {
        if (id == null) {
            id = UUID.randomUUID();
        }

        archived = false;

        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Called before updating comment.
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}