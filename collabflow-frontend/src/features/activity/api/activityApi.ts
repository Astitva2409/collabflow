import { apiClient } from "../../../lib/axios";
import type { ActivityLog, ApiResponse } from "../types/activity.types";

export const activityApi = {
  getWorkspaceActivities: async (
    workspaceId: string
  ): Promise<ActivityLog[]> => {
    const response = await apiClient.get<ApiResponse<ActivityLog[]>>(
      `/workspaces/${workspaceId}/activities`
    );

    return response.data.data || [];
  },

  getProjectActivities: async (
    workspaceId: string,
    projectId: string
  ): Promise<ActivityLog[]> => {
    const response = await apiClient.get<ApiResponse<ActivityLog[]>>(
      `/workspaces/${workspaceId}/activities/projects/${projectId}`
    );

    return response.data.data || [];
  },
};