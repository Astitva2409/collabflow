import { apiClient } from "../../../lib/axios";
import type {
  ApiResponse,
  CreateTaskRequest,
  MoveTaskRequest,
  Task,
} from "../types/task.types";

export const taskApi = {
  getTasks: async (
    workspaceId: string,
    projectId: string
  ): Promise<Task[]> => {
    const response = await apiClient.get<ApiResponse<Task[]>>(
      `/workspaces/${workspaceId}/projects/${projectId}/tasks`
    );

    return response.data.data || [];
  },

  createTask: async (
    workspaceId: string,
    projectId: string,
    payload: CreateTaskRequest
  ): Promise<Task> => {
    const response = await apiClient.post<ApiResponse<Task>>(
      `/workspaces/${workspaceId}/projects/${projectId}/tasks`,
      payload
    );

    return response.data.data as Task;
  },

  moveTask: async (
    workspaceId: string,
    projectId: string,
    taskId: string,
    payload: MoveTaskRequest
  ): Promise<Task> => {
    const response = await apiClient.patch<ApiResponse<Task>>(
      `/workspaces/${workspaceId}/projects/${projectId}/tasks/${taskId}/move`,
      payload
    );

    return response.data.data as Task;
  },
};