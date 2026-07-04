import { z } from "zod";

export const addWorkspaceMemberSchema = z.object({
    email: z.string().email("Please enter a valid email"),
    role: z.enum(["ADMIN", "MEMBER", "VIEWER"]),
});

export type AddWorkspaceMemberFormValues = z.infer<
  typeof addWorkspaceMemberSchema
>;