import { apiClient } from "../../../lib/axios";
import type {
  ApiResponse,
  CreateWorkspaceRequest,
  Workspace,
} from "../types/workspace.types";

export const workspaceApi = {
  getMyWorkspaces: async (): Promise<Workspace[]> => {
    const response = await apiClient.get<ApiResponse<Workspace[]>>(
      "/workspaces"
    );

    return response.data.data || [];
  },

  createWorkspace: async (
    payload: CreateWorkspaceRequest
  ): Promise<Workspace> => {
    const response = await apiClient.post<ApiResponse<Workspace>>(
      "/workspaces",
      payload
    );

    return response.data.data as Workspace;
  },
};