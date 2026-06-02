package com.astitva.collabflow.workspace.repository;

import com.astitva.collabflow.workspace.entity.WorkspaceMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

    boolean existsByWorkspace_IdAndUser_Id(UUID workspaceId, UUID userId);
    Optional<WorkspaceMember> findByWorkspace_IdAndUser_Id(UUID workspaceId, UUID userId);

    @Query("""
            SELECT wm
            FROM WorkspaceMember wm
            JOIN FETCH wm.workspace
            WHERE wm.user.id = :userId
            ORDER BY wm.workspace.createdAt DESC
            """)
    List<WorkspaceMember> findAllByUserIdWithWorkspace(UUID userId);


    @Query("""
            SELECT wm FROM WorkspaceMember wm
            JOIN FETCH wm.user
            WHERE wm.workspace.id = :workspaceId
            """)
    List<WorkspaceMember> findAllByWorkspace_Id(UUID workspaceId);
}