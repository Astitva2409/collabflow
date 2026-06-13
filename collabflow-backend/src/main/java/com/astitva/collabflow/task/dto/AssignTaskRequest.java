package com.astitva.collabflow.task.dto;

import java.util.UUID;

/**
 * Request DTO for assigning or unassigning a task.
 *
 * If assignedTo is null, task will be unassigned.
 */
public record AssignTaskRequest(

        /**
         * User ID of assignee.
         *
         * Can be null to unassign task.
         */
        UUID assignedTo
) {
}