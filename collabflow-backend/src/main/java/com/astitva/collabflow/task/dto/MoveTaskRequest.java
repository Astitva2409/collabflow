package com.astitva.collabflow.task.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Request DTO for moving a task between board columns
 * or reordering it within the same column.
 */
public record MoveTaskRequest(

        /**
         * Target board column where the task should be moved.
         */
        @NotNull(message = "Target board column ID is required")
        UUID boardColumnId,

        /**
         * Target position inside the column.
         *
         * Example:
         * 1 means top of the column.
         */
        @NotNull(message = "Target position is required")
        @Min(value = 1, message = "Position must be at least 1")
        Integer position
) {
}