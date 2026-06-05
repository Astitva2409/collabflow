package com.astitva.collabflow.board.dto;

import java.util.UUID;

/**
 * Response DTO for board column.
 */
public record BoardColumnResponse(
        UUID id,
        String name,
        Integer position
) {
}