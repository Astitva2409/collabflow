package com.astitva.collabflow.board.service;

import com.astitva.collabflow.board.dto.BoardColumnResponse;
import com.astitva.collabflow.board.dto.BoardResponse;
import com.astitva.collabflow.board.entity.Board;
import com.astitva.collabflow.board.entity.BoardColumn;
import com.astitva.collabflow.board.repository.BoardColumnRepository;
import com.astitva.collabflow.board.repository.BoardRepository;
import com.astitva.collabflow.common.exception.BadRequestException;
import com.astitva.collabflow.common.exception.ResourceNotFoundException;
import com.astitva.collabflow.project.entity.Project;
import com.astitva.collabflow.project.repository.ProjectRepository;
import com.astitva.collabflow.workspace.service.WorkspaceAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Service layer for board-related business logic.
 */
@Service
@RequiredArgsConstructor
public class BoardService {

    private static final String DEFAULT_BOARD_NAME = "Default Board";

    private final BoardRepository boardRepository;
    private final BoardColumnRepository boardColumnRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceAccessService workspaceAccessService;

    /**
     * Creates default board and default columns for a project.
     *
     * This method is intended to be called automatically when a project is created.
     *
     * Default columns:
     * 1. TODO
     * 2. IN_PROGRESS
     * 3. REVIEW
     * 4. DONE
     */
    @Transactional
    public Board createDefaultBoardForProject(Project project) {
        if (boardRepository.existsByProject_Id(project.getId())) {
            throw new BadRequestException("Board already exists for this project");
        }

        Board board = Board.builder()
                .project(project)
                .name(DEFAULT_BOARD_NAME)
                .build();

        Board savedBoard = boardRepository.save(board);

        List<BoardColumn> defaultColumns = List.of(
                createColumn(savedBoard, "TODO", 1),
                createColumn(savedBoard, "IN_PROGRESS", 2),
                createColumn(savedBoard, "REVIEW", 3),
                createColumn(savedBoard, "DONE", 4)
        );

        boardColumnRepository.saveAll(defaultColumns);

        return savedBoard;
    }

    /**
     * Fetches board with columns for a project.
     *
     * Rules:
     * - Current user must be a member of workspace.
     * - Project must belong to workspace.
     */
    @Transactional(readOnly = true)
    public BoardResponse getBoard(UUID workspaceId, UUID projectId, UUID currentUserId) {
        workspaceAccessService.validateCanViewWorkspace(workspaceId, currentUserId);

        Project project = projectRepository.findByIdAndWorkspace_IdAndArchivedFalse(projectId, workspaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        Board board = boardRepository.findByProject_Id(project.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Board not found"));

        List<BoardColumnResponse> columns = boardColumnRepository
                .findByBoard_IdOrderByPositionAsc(board.getId())
                .stream()
                .map(this::mapColumnToResponse)
                .toList();

        return new BoardResponse(
                board.getId(),
                project.getId(),
                board.getName(),
                columns
        );
    }

    /**
     * Helper method to create board column entity.
     */
    private BoardColumn createColumn(Board board, String name, Integer position) {
        return BoardColumn.builder()
                .board(board)
                .name(name)
                .position(position)
                .build();
    }

    /**
     * Converts BoardColumn entity into response DTO.
     */
    private BoardColumnResponse mapColumnToResponse(BoardColumn column) {
        return new BoardColumnResponse(
                column.getId(),
                column.getName(),
                column.getPosition()
        );
    }
}