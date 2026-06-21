package com.astitva.collabflow.activity.repository;

import com.astitva.collabflow.activity.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.UUID;

/**
 * Repository for activity logs.
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    /**
     * Fetches recent activities for a workspace.
     */
    @Query("""
            SELECT al
            FROM ActivityLog al
            JOIN FETCH al.actor
            LEFT JOIN FETCH al.project
            WHERE al.workspace.id = :workspaceId
            ORDER BY al.createdAt DESC
            """)
    List<ActivityLog> findByWorkspaceIdOrderByCreatedAtDesc(UUID workspaceId);

    /**
     * Fetches recent activities for a project.
     */
    @Query("""
            SELECT al
            FROM ActivityLog al
            JOIN FETCH al.actor
            LEFT JOIN FETCH al.project
            WHERE al.workspace.id = :workspaceId
              AND al.project.id = :projectId
            ORDER BY al.createdAt DESC
            """)
    List<ActivityLog> findByWorkspaceIdAndProjectIdOrderByCreatedAtDesc(UUID workspaceId, UUID projectId);
}