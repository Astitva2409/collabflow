import { z } from "zod";

export const createTaskSchema = z.object({
  title: z
    .string()
    .min(2, "Task title must be at least 2 characters")
    .max(160, "Task title cannot exceed 160 characters"),

  description: z
    .string()
    .max(2000, "Task description cannot exceed 2000 characters")
    .optional()
    .or(z.literal("")),

  priority: z.enum(["LOW", "MEDIUM", "HIGH", "URGENT"]).optional(),

  boardColumnId: z.string().optional(),

  assignedTo: z.string().optional().or(z.literal("")),

  dueDate: z.string().optional().or(z.literal("")),
});

export type CreateTaskFormValues = z.infer<typeof createTaskSchema>;