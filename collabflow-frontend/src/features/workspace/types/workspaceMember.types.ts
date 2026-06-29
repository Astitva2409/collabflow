export type WorkspaceMemberRole = "OWNER" | "ADMIN" | "MEMBER" | "VIEWER";

export type WorkspaceMember = {
  userId: string;
  fullName: string;
  email: string;
  role: WorkspaceMemberRole;
  joinedAt: string;
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  errors?: unknown;
  timestamp: string;
};