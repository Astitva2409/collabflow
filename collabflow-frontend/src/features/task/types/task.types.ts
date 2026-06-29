export type TaskPriority = "LOW" | "MEDIUM" | "HIGH" | "URGENT";

export type Task = {
  id: string;
  projectId: string;
  boardColumnId: string;
  columnName: string;
  title: string;
  description: string | null;
  priority: TaskPriority;
  createdBy: string;
  assignedTo: string | null;
  assignedToName: string | null;
  position: number;
  dueDate: string | null;
  archived: boolean;
  createdAt: string;
  updatedAt: string;
};

export type CreateTaskRequest = {
  title: string;
  description?: string;
  priority?: TaskPriority;
  assignedTo?: string;
  dueDate?: string;
  boardColumnId?: string;
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  errors?: unknown;
  timestamp: string;
};