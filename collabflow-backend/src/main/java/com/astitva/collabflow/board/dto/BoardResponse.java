package com.astitva.collabflow.board.dto;

import java.util.List;
import java.util.UUID;

/**
 * Response DTO for board with columns.
 */
public record BoardResponse(
        UUID id,
        UUID projectId,
        String name,
        List<BoardColumnResponse> columns
) {
}