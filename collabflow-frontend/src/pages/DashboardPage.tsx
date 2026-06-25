import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../features/auth/store/authStore";
import { workspaceApi } from "../features/workspace/api/workspaceApi";
import WorkspaceCard from "../features/workspace/components/WorkspaceCard";
import CreateWorkspaceForm from "../features/workspace/components/CreateWorkspaceForm";

export default function DashboardPage() {
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const [showCreateForm, setShowCreateForm] = useState(false);

  const {
    data: workspaces = [],
    isLoading,
    isError,
  } = useQuery({
    queryKey: ["workspaces"],
    queryFn: workspaceApi.getMyWorkspaces,
  });

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <main className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <div>
            <p className="text-sm font-medium text-blue-600">CollabFlow</p>
            <h1 className="text-xl font-bold text-slate-900">
              Workspace Dashboard
            </h1>
          </div>

          <div className="flex items-center gap-4">
            {user && (
              <div className="hidden text-right sm:block">
                <p className="text-sm font-semibold text-slate-800">
                  {user.fullName}
                </p>
                <p className="text-xs text-slate-500">
                  {user.email} • {user.role}
                </p>
              </div>
            )}

            <button
              onClick={handleLogout}
              className="rounded-xl bg-slate-900 px-4 py-2 text-sm font-medium text-white hover:bg-slate-700"
            >
              Logout
            </button>
          </div>
        </div>
      </header>

      <section className="mx-auto max-w-6xl px-6 py-8">
        <div className="flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <p className="text-sm text-slate-500">Your workspaces</p>
            <h2 className="text-2xl font-bold text-slate-900">
              Welcome{user ? `, ${user.fullName}` : ""}
            </h2>
          </div>

          <button
            onClick={() => setShowCreateForm((prev) => !prev)}
            className="rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700"
          >
            {showCreateForm ? "Close Form" : "Create Workspace"}
          </button>
        </div>

        {showCreateForm && (
          <div className="mt-6 max-w-xl rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-900">
              Create New Workspace
            </h3>
            <p className="mt-1 text-sm text-slate-500">
              Create a workspace to manage projects, boards, tasks, and team
              members.
            </p>

            <div className="mt-5">
              <CreateWorkspaceForm onSuccess={() => setShowCreateForm(false)} />
            </div>
          </div>
        )}

        <div className="mt-8">
          {isLoading && (
            <div className="rounded-2xl bg-white p-8 text-center text-slate-500 shadow-sm">
              Loading workspaces...
            </div>
          )}

          {isError && (
            <div className="rounded-2xl bg-red-50 p-8 text-center text-red-600">
              Failed to load workspaces.
            </div>
          )}

          {!isLoading && !isError && workspaces.length === 0 && (
            <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center">
              <h3 className="text-lg font-semibold text-slate-900">
                No workspaces yet
              </h3>
              <p className="mt-2 text-sm text-slate-500">
                Create your first workspace to start organizing projects and
                tasks.
              </p>
            </div>
          )}

          {!isLoading && !isError && workspaces.length > 0 && (
            <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
              {workspaces.map((workspace) => (
                <WorkspaceCard key={workspace.id} workspace={workspace} />
              ))}
            </div>
          )}
        </div>
      </section>
    </main>
  );
}