import { z } from "zod";

export const addWorkspaceMemberSchema = z.object({
  userId: z.string().uuid("Please enter a valid user ID"),
  role: z.enum(["ADMIN", "MEMBER", "VIEWER"]),
});

export type AddWorkspaceMemberFormValues = z.infer<
  typeof addWorkspaceMemberSchema
>;