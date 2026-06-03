package com.astitva.collabflow.project.repository;

import com.astitva.collabflow.project.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Project entity.
 */
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * Finds all active/non-archived projects inside a workspace.
     */
    List<Project> findByWorkspace_IdAndArchivedFalseOrderByCreatedAtDesc(UUID workspaceId);

    /**
     * Finds one active/non-archived project by project ID and workspace ID.
     *
     * We include workspaceId to ensure project belongs to the requested workspace.
     */
    Optional<Project> findByIdAndWorkspace_IdAndArchivedFalse(UUID projectId, UUID workspaceId);

    /**
     * Checks whether active project with same name already exists in a workspace.
     *
     * Used to prevent duplicate active project names inside same workspace.
     */
    boolean existsByWorkspace_IdAndNameIgnoreCaseAndArchivedFalse(UUID workspaceId, String name);
}