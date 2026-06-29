import { apiClient } from "../../../lib/axios";
import type {
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
};