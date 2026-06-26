export type ProjectStatus = "ACTIVE" | "ON_HOLD" | "COMPLETED" | "ARCHIVED";

export type ProjectPriority = "LOW" | "MEDIUM" | "HIGH" | "CRITICAL";

export type Project = {
  id: string;
  workspaceId: string;
  name: string;
  description: string | null;
  status: ProjectStatus;
  priority: ProjectPriority;
  createdBy: string;
  archived: boolean;
  createdAt: string;
  updatedAt: string;
};

export type CreateProjectRequest = {
  name: string;
  description?: string;
  priority?: ProjectPriority;
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  errors?: unknown;
  timestamp: string;
};