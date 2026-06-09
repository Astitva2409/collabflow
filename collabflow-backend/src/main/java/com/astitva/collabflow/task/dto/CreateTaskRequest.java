package com.astitva.collabflow.task.dto;

import com.astitva.collabflow.task.entity.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request DTO for creating a task.
 */
public record CreateTaskRequest(

        /**
         * Task title is mandatory.
         */
        @NotBlank(message = "Task title is required")
        @Size(min = 2, max = 160, message = "Task title must be between 2 and 160 characters")
        String title,

        /**
         * Optional task description.
         */
        @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
        String description,

        /**
         * Optional priority.
         *
         * If not provided, backend defaults to MEDIUM.
         */
        TaskPriority priority,

        /**
         * Optional assignee user ID.
         *
         * If null, task will be unassigned.
         */
        UUID assignedTo,

        /**
         * Optional due date.
         */
        LocalDateTime dueDate,

        /**
         * Optional board column ID.
         *
         * If not provided, task will be created in TODO column by default.
         */
        UUID boardColumnId
) {
}