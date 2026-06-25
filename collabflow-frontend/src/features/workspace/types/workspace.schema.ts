import { z } from "zod";

export const createWorkspaceSchema = z.object({
  name: z
    .string()
    .min(2, "Workspace name must be at least 2 characters")
    .max(120, "Workspace name cannot exceed 120 characters"),

  description: z
    .string()
    .max(500, "Description cannot exceed 500 characters")
    .optional()
    .or(z.literal("")),
});

export type CreateWorkspaceFormValues = z.infer<
  typeof createWorkspaceSchema
>;