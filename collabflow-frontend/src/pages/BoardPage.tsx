import { useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useQuery } from "@tanstack/react-query";

import { boardApi } from "../features/board/api/boardApi";
import { taskApi } from "../features/task/api/taskApi";
import BoardColumn from "../features/board/components/BoardColumn";
import CreateTaskForm from "../features/task/components/CreateTaskForm";
import { useAuthStore } from "../features/auth/store/authStore";
import type { Task } from "../features/task/types/task.types";

export default function BoardPage() {
  const navigate = useNavigate();

  const { workspaceId, projectId } = useParams<{
    workspaceId: string;
    projectId: string;
  }>();

  const { user, logout } = useAuthStore();

  const [showCreateTaskForm, setShowCreateTaskForm] = useState(false);

  const handleLogout = () => {
    logout();
    navigate("/login", { replace: true });
  };

  const {
    data: board,
    isLoading: isBoardLoading,
    isError: isBoardError,
  } = useQuery({
    queryKey: ["board", workspaceId, projectId],
    queryFn: () =>
      boardApi.getBoard(workspaceId as string, projectId as string),
    enabled: Boolean(workspaceId && projectId),
  });

  const {
    data: tasks = [],
    isLoading: isTasksLoading,
    isError: isTasksError,
  } = useQuery({
    queryKey: ["tasks", workspaceId, projectId],
    queryFn: () =>
      taskApi.getTasks(workspaceId as string, projectId as string),
    enabled: Boolean(workspaceId && projectId),
  });

  if (!workspaceId || !projectId) {
    return (
      <main className="min-h-screen bg-slate-50 flex items-center justify-center">
        <p className="text-sm text-red-600">
          Workspace ID or Project ID is missing.
        </p>
      </main>
    );
  }

  const getTasksForColumn = (columnId: string): Task[] => {
    return tasks
      .filter((task) => task.boardColumnId === columnId)
      .sort((a, b) => a.position - b.position);
  };

  return (
    <main className="min-h-screen bg-slate-50">
      <header className="border-b border-slate-200 bg-white">
        <div className="mx-auto flex max-w-7xl items-center justify-between px-6 py-4">
          <div>
            <Link
              to={`/workspaces/${workspaceId}`}
              className="text-sm font-medium text-blue-600 hover:text-blue-700"
            >
              ← Back to Workspace
            </Link>

            <h1 className="mt-1 text-xl font-bold text-slate-900">
              {isBoardLoading ? "Loading board..." : board?.name || "Board"}
            </h1>

            {board && (
              <p className="mt-1 text-sm text-slate-500">
                Project ID: {board.projectId}
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

      <section className="mx-auto max-w-7xl px-6 py-8">
        {isBoardError && (
          <div className="rounded-2xl bg-red-50 p-6 text-red-600">
            Failed to load board.
          </div>
        )}

        {isTasksError && (
          <div className="mt-4 rounded-2xl bg-red-50 p-6 text-red-600">
            Failed to load tasks.
          </div>
        )}

        {(isBoardLoading || isTasksLoading) && (
          <div className="rounded-2xl bg-white p-8 text-center text-slate-500 shadow-sm">
            Loading board and tasks...
          </div>
        )}

        {!isBoardLoading && !isTasksLoading && board && (
          <>
            <div className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-center">
              <div>
                <p className="text-sm text-slate-500">Kanban Board</p>
                <h2 className="text-2xl font-bold text-slate-900">
                  {board.name}
                </h2>
              </div>

              <button
                onClick={() => setShowCreateTaskForm((prev) => !prev)}
                className="rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700"
              >
                {showCreateTaskForm ? "Close Form" : "Create Task"}
              </button>
            </div>

            {showCreateTaskForm && (
              <div className="mb-6 max-w-2xl rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                <h3 className="text-lg font-semibold text-slate-900">
                  Create New Task
                </h3>

                <p className="mt-1 text-sm text-slate-500">
                  Add a task to one of the Kanban columns. By default, tasks
                  are added to TODO.
                </p>

                <div className="mt-5">
                  <CreateTaskForm
                    workspaceId={workspaceId}
                    projectId={projectId}
                    columns={board.columns}
                    onSuccess={() => setShowCreateTaskForm(false)}
                  />
                </div>
              </div>
            )}

            <div className="overflow-x-auto pb-6">
              <div className="flex gap-5">
                {board.columns.map((column) => (
                  <BoardColumn
                    key={column.id}
                    column={column}
                    tasks={getTasksForColumn(column.id)}
                  />
                ))}
              </div>
            </div>
          </>
        )}
      </section>
    </main>
  );
}