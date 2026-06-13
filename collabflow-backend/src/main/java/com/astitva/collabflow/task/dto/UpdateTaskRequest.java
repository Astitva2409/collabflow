package com.astitva.collabflow.task.dto;

import com.astitva.collabflow.task.entity.TaskPriority;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Request DTO for updating task details.
 *
 * All fields are optional because this is a PATCH request.
 * If a field is null, that field will not be updated.
 */
public record UpdateTaskRequest(

        @Size(min = 2, max = 160, message = "Task title must be between 2 and 160 characters")
        String title,

        @Size(max = 2000, message = "Task description cannot exceed 2000 characters")
        String description,

        TaskPriority priority,

        LocalDateTime dueDate
) {
}