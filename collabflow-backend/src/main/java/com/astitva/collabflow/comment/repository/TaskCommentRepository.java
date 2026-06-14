package com.astitva.collabflow.comment.repository;

import com.astitva.collabflow.comment.entity.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for task comments.
 */
public interface TaskCommentRepository extends JpaRepository<TaskComment, UUID> {

    /**
     * Fetches all active comments for a task ordered by creation time.
     */
    @Query("""
            SELECT c
            FROM TaskComment c
            JOIN FETCH c.author
            WHERE c.task.id = :taskId
              AND c.archived = false
            ORDER BY c.createdAt ASC
            """)
    List<TaskComment> findActiveCommentsByTaskId(UUID taskId);

    /**
     * Fetches one active comment by comment ID and task ID.
     */
    @Query("""
            SELECT c
            FROM TaskComment c
            JOIN FETCH c.author
            WHERE c.id = :commentId
              AND c.task.id = :taskId
              AND c.archived = false
            """)
    Optional<TaskComment> findActiveCommentByIdAndTaskId(UUID commentId, UUID taskId);
}