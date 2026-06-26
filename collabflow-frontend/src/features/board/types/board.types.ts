export type BoardColumn = {
  id: string;
  name: string;
  position: number;
};

export type Board = {
  id: string;
  projectId: string;
  name: string;
  columns: BoardColumn[];
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  errors?: unknown;
  timestamp: string;
};