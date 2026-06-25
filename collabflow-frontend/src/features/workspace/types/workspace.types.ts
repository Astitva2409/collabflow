export type WorkspaceRole = "OWNER" | "ADMIN" | "MEMBER" | "VIEWER";

export type Workspace = {
  id: string;
  name: string;
  description: string | null;
  role: WorkspaceRole;
  createdAt: string;
  updatedAt: string;
};

export type CreateWorkspaceRequest = {
  name: string;
  description?: string;
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  errors?: unknown;
  timestamp: string;
};