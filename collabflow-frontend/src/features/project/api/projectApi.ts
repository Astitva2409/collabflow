import { apiClient } from "../../../lib/axios";
import type {
  ApiResponse,
  CreateProjectRequest,
  Project,
} from "../types/project.types";

export const projectApi = {
  getProjects: async (workspaceId: string): Promise<Project[]> => {
    const response = await apiClient.get<ApiResponse<Project[]>>(
      `/workspaces/${workspaceId}/projects`
    );

    return response.data.data || [];
  },

  createProject: async (
    workspaceId: string,
    payload: CreateProjectRequest
  ): Promise<Project> => {
    const response = await apiClient.post<ApiResponse<Project>>(
      `/workspaces/${workspaceId}/projects`,
      payload
    );

    return response.data.data as Project;
  },
};