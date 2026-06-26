import { z } from "zod";

export const createProjectSchema = z.object({
  name: z
    .string()
    .min(2, "Project name must be at least 2 characters")
    .max(120, "Project name cannot exceed 120 characters"),

  description: z
    .string()
    .max(1000, "Project description cannot exceed 1000 characters")
    .optional()
    .or(z.literal("")),

  priority: z.enum(["LOW", "MEDIUM", "HIGH", "CRITICAL"]).optional(),
});

export type CreateProjectFormValues = z.infer<typeof createProjectSchema>;