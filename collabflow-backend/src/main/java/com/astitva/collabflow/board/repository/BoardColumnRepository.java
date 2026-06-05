package com.astitva.collabflow.board.repository;

import com.astitva.collabflow.board.entity.BoardColumn;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

/**
 * Repository for BoardColumn entity.
 */
public interface BoardColumnRepository extends JpaRepository<BoardColumn, UUID> {

    /**
     * Fetches all columns of a board ordered by position.
     *
     * This is important for Kanban UI.
     */
    List<BoardColumn> findByBoard_IdOrderByPositionAsc(UUID boardId);
}