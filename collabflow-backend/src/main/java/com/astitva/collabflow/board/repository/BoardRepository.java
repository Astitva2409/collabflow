package com.astitva.collabflow.board.repository;

import com.astitva.collabflow.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for Board entity.
 */
public interface BoardRepository extends JpaRepository<Board, UUID> {

    /**
     * Finds board by project id.
     *
     * Since our design is one project = one board,
     * this returns at most one board.
     */
    Optional<Board> findByProject_Id(UUID projectId);

    /**
     * Checks whether board already exists for project.
     *
     * Useful to prevent duplicate board creation.
     */
    boolean existsByProject_Id(UUID projectId);
}