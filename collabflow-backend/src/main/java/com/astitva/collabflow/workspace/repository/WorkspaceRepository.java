package com.astitva.collabflow.workspace.repository;

import com.astitva.collabflow.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

/**
 * Repository for Workspace entity.
 *
 * JpaRepository gives us built-in methods like:
 * - save()
 * - findById()
 * - findAll()
 * - delete()
 * - existsById()
 */
public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
}