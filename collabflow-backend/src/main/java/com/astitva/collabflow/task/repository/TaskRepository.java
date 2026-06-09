package com.astitva.collabflow.task.repository;

import com.astitva.collabflow.task.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Task entity.
 */
public interface TaskRepository extends JpaRepository<Task, UUID> {

    /**
     * Finds all active tasks inside a project ordered by column position and task position.
     *
     * JOIN FETCH is used to load board column and assigned user in fewer queries.
     */
    @Query("""
            SELECT t
            FROM Task t
            JOIN FETCH t.boardColumn bc
            LEFT JOIN FETCH t.assignedTo
            JOIN FETCH t.createdBy
            WHERE t.project.id = :projectId
              AND t.archived = false
            ORDER BY bc.position ASC, t.position ASC
            """)
    List<Task> findActiveTasksByProjectId(UUID projectId);

    /**
     * Finds a task by task ID and project ID.
     *
     * Ensures task belongs to the requested project.
     */
    @Query("""
            SELECT t
            FROM Task t
            JOIN FETCH t.boardColumn
            LEFT JOIN FETCH t.assignedTo
            JOIN FETCH t.createdBy
            WHERE t.id = :taskId
              AND t.project.id = :projectId
              AND t.archived = false
            """)
    Optional<Task> findActiveTaskByIdAndProjectId(UUID taskId, UUID projectId);

    /**
     * Finds maximum task position inside a board column.
     *
     * Used when creating a new task at the bottom of a column.
     */
    @Query("""
            SELECT COALESCE(MAX(t.position), 0)
            FROM Task t
            WHERE t.boardColumn.id = :boardColumnId
              AND t.archived = false
            """)
    Integer findMaxPositionByBoardColumnId(UUID boardColumnId);
}