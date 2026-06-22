export type ApiResponse<T> = {
  success: boolean;
  message: string;
  data?: T;
  errors?: unknown;
  timestamp: string;
};

export type UserRole = "USER" | "ADMIN";

export type AuthUser = {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
};

export type RegisterRequest = {
  fullName: string;
  email: string;
  password: string;
};

export type RegisterResponse = {
  id: string;
  fullName: string;
  email: string;
  role: UserRole;
};

export type LoginRequest = {
  email: string;
  password: string;
};

export type LoginResponse = {
  accessToken: string;
  tokenType: "Bearer";
  user: AuthUser;
};