import { apiClient } from "../../../lib/axios";
import type {
  ApiResponse,
  AuthUser,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  RegisterResponse,
} from "../types/auth.types";

export const authApi = {
  register: async (payload: RegisterRequest): Promise<RegisterResponse> => {
    const response = await apiClient.post<ApiResponse<RegisterResponse>>(
      "/auth/register",
      payload
    );

    return response.data.data as RegisterResponse;
  },

  login: async (payload: LoginRequest): Promise<LoginResponse> => {
    const response = await apiClient.post<ApiResponse<LoginResponse>>(
      "/auth/login",
      payload
    );

    return response.data.data as LoginResponse;
  },

  getCurrentUser: async (): Promise<AuthUser> => {
    const response = await apiClient.get<ApiResponse<AuthUser>>("/users/me");

    return response.data.data as AuthUser;
  },
};