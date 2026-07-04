import { useEffect } from "react";
import { authApi } from "../api/authApi";
import { useAuthStore } from "../store/authStore";
import { TOKEN_STORAGE_KEY } from "../../../lib/constants";

type AuthBootstrapProps = {
  children: React.ReactNode;
};

export default function AuthBootstrap({ children }: AuthBootstrapProps) {
  const setUser = useAuthStore((state) => state.setUser);
  const logout = useAuthStore((state) => state.logout);
  const setAuthLoading = useAuthStore((state) => state.setAuthLoading);

  useEffect(() => {
    const bootstrapAuth = async () => {
      const token = localStorage.getItem(TOKEN_STORAGE_KEY);

      if (!token) {
        setAuthLoading(false);
        return;
      }

      try {
        setAuthLoading(true);

        const currentUser = await authApi.getCurrentUser();

        setUser(currentUser);
      } catch (error) {
        console.log("Auth bootstrap failed:", error);
        logout();
      } finally {
        setAuthLoading(false);
      }
    };

    bootstrapAuth();
  }, [setUser, logout, setAuthLoading]);

  return <>{children}</>;
}