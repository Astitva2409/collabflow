import { apiClient } from "../../../lib/axios";
import type { ApiResponse, Board } from "../types/board.types";

export const boardApi = {
  getBoard: async (
    workspaceId: string,
    projectId: string
  ): Promise<Board> => {
    const response = await apiClient.get<ApiResponse<Board>>(
      `/workspaces/${workspaceId}/projects/${projectId}/board`
    );

    return response.data.data as Board;
  },
};