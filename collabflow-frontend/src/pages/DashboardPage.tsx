import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../features/auth/store/authStore";

export default function DashboardPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <main className="min-h-screen bg-slate-50 p-6">
      <div className="mx-auto max-w-5xl rounded-2xl bg-white p-6 shadow-sm">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-slate-500">Dashboard</p>
            <h1 className="text-2xl font-bold text-slate-900">
              Welcome{user ? `, ${user.fullName}` : ""}
            </h1>
            {user && (
              <p className="mt-1 text-sm text-slate-500">
                {user.email} • {user.role}
              </p>
            )}
          </div>

          <button
            onClick={handleLogout}
            className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
          >
            Logout
          </button>
        </div>

        <div className="mt-8 rounded-xl border border-dashed border-slate-300 p-8 text-center text-slate-500">
          Workspace dashboard will be built here.
        </div>
      </div>
    </main>
  );
}