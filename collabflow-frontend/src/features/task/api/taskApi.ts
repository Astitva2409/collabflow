import { apiClient } from "../../../lib/axios";
import type { ApiResponse, Task } from "../types/task.types";

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
};
