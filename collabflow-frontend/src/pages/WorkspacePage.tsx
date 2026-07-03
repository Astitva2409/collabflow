import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";
import { workspaceApi } from "../features/workspace/api/workspaceApi";
import { projectApi } from "../features/project/api/projectApi";
import ProjectCard from "../features/project/components/ProjectCard";
import CreateProjectForm from "../features/project/components/CreateProjectForm";
import { useAuthStore } from "../features/auth/store/authStore";
import { activityApi } from "../features/activity/api/activityApi";
import ActivityFeed from "../features/activity/components/ActivityFeed";

export default function WorkspacePage() {
  const navigate = useNavigate();
  const { workspaceId } = useParams<{ workspaceId: string }>();
  const { user, logout } = useAuthStore();
  const [showCreateForm, setShowCreateForm] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  const {
    data: workspace,
    isLoading: isWorkspaceLoading,
    isError: isWorkspaceError,
  } = useQuery({
    queryKey: ["workspace", workspaceId],
    queryFn: () => workspaceApi.getWorkspaceById(workspaceId as string),
    enabled: Boolean(workspaceId),
  });

  const {
    data: projects = [],
    isLoading: isProjectsLoading,
    isError: isProjectsError,
  } = useQuery({
    queryKey: ["projects", workspaceId],
    queryFn: () => projectApi.getProjects(workspaceId as string),
    enabled: Boolean(workspaceId),
  });

  const {
    data: workspaceActivities = [],
    isLoading: isActivitiesLoading,
    isError: isActivitiesError,
  } = useQuery({
    queryKey: ["workspace-activities", workspaceId],
    queryFn: () => activityApi.getWorkspaceActivities(workspaceId as string),
    enabled: Boolean(workspaceId),
  });

  if (!workspaceId) {
    return (
      <main className="min-h-screen bg-slate-50 flex items-center justify-center">
        <p className="text-sm text-red-600">Workspace ID is missing.</p>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-4">
          <div>
            <Link
              to="/dashboard"
              className="text-sm font-medium text-blue-600 hover:text-blue-700"
            >
              ← Back to Dashboard
            </Link>

            <h1 className="mt-1 text-xl font-bold text-slate-900">
              {isWorkspaceLoading
                ? "Loading workspace..."
                : workspace?.name || "Workspace"}
            </h1>

            {workspace && (
              <p className="mt-1 text-sm text-slate-500">
                Role: {workspace.role}
              </p>
            )}
          </div>

          <div className="flex items-center gap-4">
            {user && (
              <div className="hidden text-right sm:block">
                <p className="text-sm font-semibold text-slate-800">
                  {user.fullName}
                </p>
                <p className="text-xs text-slate-500">{user.email}</p>
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
        {isWorkspaceError && (
          <div className="rounded-2xl bg-red-50 p-6 text-red-600">
            Failed to load workspace.
          </div>
        )}

        {workspace && (
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <p className="text-sm text-slate-500">Workspace Description</p>
            <p className="mt-2 text-slate-700">
              {workspace.description || "No description provided."}
            </p>
          </div>
        )}

        <div className="mt-8 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
          <div>
            <p className="text-sm text-slate-500">Projects</p>
            <h2 className="text-2xl font-bold text-slate-900">
              Workspace Projects
            </h2>
          </div>

          <button
            onClick={() => setShowCreateForm((prev) => !prev)}
            className="rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700"
          >
            {showCreateForm ? "Close Form" : "Create Project"}
          </button>
        </div>

        {showCreateForm && (
          <div className="mt-6 max-w-xl rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="text-lg font-semibold text-slate-900">
              Create New Project
            </h3>
            <p className="mt-1 text-sm text-slate-500">
              Create a project inside this workspace. A default Kanban board
              will be created automatically.
            </p>

            <div className="mt-5">
              <CreateProjectForm
                workspaceId={workspaceId}
                onSuccess={() => setShowCreateForm(false)}
              />
            </div>
          </div>
        )}

        <div className="mt-8">
          {isProjectsLoading && (
            <div className="rounded-2xl bg-white p-8 text-center text-slate-500 shadow-sm">
              Loading projects...
            </div>
          )}

          {isProjectsError && (
            <div className="rounded-2xl bg-red-50 p-8 text-center text-red-600">
              Failed to load projects.
            </div>
          )}

          {!isProjectsLoading && !isProjectsError && projects.length === 0 && (
            <div className="rounded-2xl border border-dashed border-slate-300 bg-white p-10 text-center">
              <h3 className="text-lg font-semibold text-slate-900">
                No projects yet
              </h3>
              <p className="mt-2 text-sm text-slate-500">
                Create your first project to start managing boards and tasks.
              </p>
            </div>
          )}

          {!isProjectsLoading && !isProjectsError && projects.length > 0 && (
            <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
              {projects.map((project) => (
                <ProjectCard key={project.id} project={project} />
              ))}
            </div>
          )}
        </div>
        <div className="mt-10">
          <ActivityFeed
            title="Workspace Activity"
            activities={workspaceActivities}
            isLoading={isActivitiesLoading}
            isError={isActivitiesError}
          />
        </div>
      </section>
    </main>
  );
}