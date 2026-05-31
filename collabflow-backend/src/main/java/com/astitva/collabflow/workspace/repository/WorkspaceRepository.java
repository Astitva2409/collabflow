package com.astitva.collabflow.workspace.repository;

import com.astitva.collabflow.workspace.entity.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WorkspaceRepository extends JpaRepository<Workspace, UUID> {
}