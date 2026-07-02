import { z } from "zod";

export const createCommentSchema = z.object({
  content: z
    .string()
    .min(1, "Comment is required")
    .max(2000, "Comment cannot exceed 2000 characters"),
});

export type CreateCommentFormValues = z.infer<typeof createCommentSchema>;
