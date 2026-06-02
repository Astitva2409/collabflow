package com.astitva.collabflow.workspace.repository;

import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for WorkspaceMember entity.
 *
 * This repository is very important because most workspace access checks
 * depend on membership.
 */
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

    /**
     * Checks whether a user is already part of a workspace.
     *
     * Used when adding a new member to prevent duplicates.
     */
    boolean existsByWorkspace_IdAndUser_Id(UUID workspaceId, UUID userId);

    /**
     * Finds the membership record of a specific user inside a specific workspace.
     *
     * This is used for authorization checks.
     *
     * Example:
     * Is current user a member of this workspace?
     * Is current user OWNER or ADMIN?
     */
    Optional<WorkspaceMember> findByWorkspace_IdAndUser_Id(UUID workspaceId, UUID userId);

    /**
     * Finds all workspaces where a user is a member.
     *
     * JOIN FETCH wm.workspace is used to load workspace data together with membership.
     *
     * Without JOIN FETCH:
     * Hibernate may execute extra queries when member.getWorkspace() is called.
     *
     * This helps reduce unnecessary database queries.
     */
    @Query("""
            SELECT wm
            FROM WorkspaceMember wm
            JOIN FETCH wm.workspace
            WHERE wm.user.id = :userId
            ORDER BY wm.workspace.createdAt DESC
            """)
    List<WorkspaceMember> findAllByUserIdWithWorkspace(UUID userId);

    /**
     * Finds all members of a workspace.
     *
     * Used in GET /api/v1/workspaces/{workspaceId}/members
     */
    @Query("""
            SELECT wm FROM WorkspaceMember wm
            JOIN FETCH wm.user
            WHERE wm.workspace.id = :workspaceId
            """)
    List<WorkspaceMember> findAllByWorkspace_Id(UUID workspaceId);
}