import { create } from "zustand";
import { TOKEN_STORAGE_KEY } from "../../../lib/constants";
import type { AuthUser } from "../types/auth.types";

type AuthState = {
  user: AuthUser | null;
  accessToken: string | null;
  isAuthenticated: boolean;
  isAuthLoading: boolean;

  setAuth: (accessToken: string, user: AuthUser) => void;
  setUser: (user: AuthUser | null) => void;
  setAuthLoading: (isLoading: boolean) => void;
  logout: () => void;
};

const initialToken = localStorage.getItem(TOKEN_STORAGE_KEY);

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  accessToken: initialToken,
  isAuthenticated: Boolean(initialToken),
  isAuthLoading: Boolean(initialToken),

  setAuth: (accessToken, user) => {
    localStorage.setItem(TOKEN_STORAGE_KEY, accessToken);

    set({
      accessToken,
      user,
      isAuthenticated: true,
      isAuthLoading: false,
    });
  },

  setUser: (user) => {
    set({
      user,
      isAuthenticated: Boolean(user),
      isAuthLoading: false,
    });
  },

  setAuthLoading: (isLoading) => {
    set({
      isAuthLoading: isLoading,
    });
  },

  logout: () => {
    localStorage.removeItem(TOKEN_STORAGE_KEY);

    set({
      user: null,
      accessToken: null,
      isAuthenticated: false,
      isAuthLoading: false,
    });
  },
}));