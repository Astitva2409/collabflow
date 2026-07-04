import { apiClient } from "../../../lib/axios";
import type {
  AddWorkspaceMemberRequest,
  ApiResponse,
  WorkspaceMember,
} from "../types/workspaceMember.types";

export const workspaceMemberApi = {
  getMembers: async (workspaceId: string): Promise<WorkspaceMember[]> => {
    const response = await apiClient.get<ApiResponse<WorkspaceMember[]>>(
      `/workspaces/${workspaceId}/members`
    );

    return response.data.data || [];
  },

  addMember: async (
    workspaceId: string,
    payload: AddWorkspaceMemberRequest
  ): Promise<void> => {
    await apiClient.post<ApiResponse<void>>(
      `/workspaces/${workspaceId}/members`,
      payload
    );
  },
};