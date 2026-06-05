package com.astitva.collabflow.board.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a column inside a Kanban board.
 *
 * Examples:
 * - TODO
 * - IN_PROGRESS
 * - REVIEW
 * - DONE
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "board_columns",
        uniqueConstraints = {
                /**
                 * Prevents two columns from having same position inside same board.
                 */
                @UniqueConstraint(
                        name = "uk_board_columns_board_position",
                        columnNames = {"board_id", "position"}
                ),

                /**
                 * Prevents duplicate column names inside same board.
                 */
                @UniqueConstraint(
                        name = "uk_board_columns_board_name",
                        columnNames = {"board_id", "name"}
                )
        }
)
public class BoardColumn {

    /**
     * Primary key of board column.
     */
    @Id
    private UUID id;

    /**
     * Board to which this column belongs.
     *
     * One board can have many columns.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /**
     * Column name.
     */
    @Column(nullable = false, length = 80)
    private String name;

    /**
     * Column position/order on Kanban board.
     *
     * Example:
     * TODO = 1
     * IN_PROGRESS = 2
     * REVIEW = 3
     * DONE = 4
     */
    @Column(nullable = false)
    private Integer position;

    /**
     * Timestamp when column was created.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when column was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Called before inserting board column row.
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
     * Called before updating board column row.
     */
    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}