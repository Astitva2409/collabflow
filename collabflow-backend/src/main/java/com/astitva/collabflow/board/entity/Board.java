package com.astitva.collabflow.board.entity;

import com.astitva.collabflow.project.entity.Project;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a Kanban board for a project.
 *
 * For current design:
 * One project has one default board.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "boards")
public class Board {

    /**
     * Primary key of board.
     */
    @Id
    private UUID id;

    /**
     * Project to which this board belongs.
     *
     * unique = true because one project currently has only one board.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false, unique = true)
    private Project project;

    /**
     * Board name.
     *
     * For now, we auto-create "Default Board".
     */
    @Column(nullable = false, length = 120)
    private String name;

    /**
     * Timestamp when board was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when board was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Called automatically before inserting board row.
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
     * Called automatically before updating board row.
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}