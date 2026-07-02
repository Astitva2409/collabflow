import { apiClient } from "../../../lib/axios";
import type {
  ApiResponse,
  Comment,
  CreateCommentRequest,
} from "../types/comment.types";

export const commentApi = {
  getComments: async (
    workspaceId: string,
    projectId: string,
    taskId: string
  ): Promise<Comment[]> => {
    const response = await apiClient.get<ApiResponse<Comment[]>>(
      `/workspaces/${workspaceId}/projects/${projectId}/tasks/${taskId}/comments`
    );

    return response.data.data || [];
  },

  addComment: async (
    workspaceId: string,
    projectId: string,
    taskId: string,
    payload: CreateCommentRequest
  ): Promise<Comment> => {
    const response = await apiClient.post<ApiResponse<Comment>>(
      `/workspaces/${workspaceId}/projects/${projectId}/tasks/${taskId}/comments`,
      payload
    );

    return response.data.data as Comment;
  },
};