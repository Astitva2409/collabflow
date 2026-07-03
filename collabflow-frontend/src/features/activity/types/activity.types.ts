export type ActivityTargetType =
  | "WORKSPACE"
  | "MEMBER"
  | "PROJECT"
  | "TASK"
  | "COMMENT";

export type ActivityType =
  | "WORKSPACE_CREATED"
  | "MEMBER_ADDED"
  | "MEMBER_ROLE_UPDATED"
  | "MEMBER_REMOVED"
  | "PROJECT_CREATED"
  | "PROJECT_UPDATED"
  | "PROJECT_ARCHIVED"
  | "TASK_CREATED"
  | "TASK_UPDATED"
  | "TASK_MOVED"
  | "TASK_ASSIGNED"
  | "TASK_UNASSIGNED"
  | "TASK_ARCHIVED"
  | "COMMENT_ADDED"
  | "COMMENT_DELETED";

export type ActivityLog = {
  id: string;
  workspaceId: string;
  projectId: string | null;
  actorId: string;
  actorName: string;
  targetId: string | null;
  targetType: ActivityTargetType;
  activityType: ActivityType;
  description: string;
  createdAt: string;
};

export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  errors?: unknown;
  timestamp: string;
};