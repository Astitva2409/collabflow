import { useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";

import type { BoardColumn } from "../../board/types/board.types";
import { taskApi } from "../api/taskApi";
import type { Task } from "../types/task.types";

type TaskCardProps = {
  task: Task;
  workspaceId: string;
  projectId: string;
  columns: BoardColumn[];
  onOpenTask: (taskId: string) => void;
};

const priorityStyles: Record<Task["priority"], string> = {
  LOW: "bg-slate-100 text-slate-700",
  MEDIUM: "bg-blue-50 text-blue-700",
  HIGH: "bg-orange-50 text-orange-700",
  URGENT: "bg-red-50 text-red-700",
};

export default function TaskCard({
  task,
  workspaceId,
  projectId,
  columns,
  onOpenTask,
}: TaskCardProps) {
  const queryClient = useQueryClient();

  const [targetColumnId, setTargetColumnId] = useState(task.boardColumnId);

  const moveMutation = useMutation({
    mutationFn: () =>
      taskApi.moveTask(workspaceId, projectId, task.id, {
        boardColumnId: targetColumnId,
        position: 9999,
      }),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["tasks", workspaceId, projectId],
      });
      queryClient.invalidateQueries({
        queryKey: ["project-activities", workspaceId, projectId],
      });
    },

    onError: (error: any) => {
      alert(
        error?.response?.data?.message ||
          "Failed to move task. Please try again."
      );
    },
  });

  const isSameColumn = targetColumnId === task.boardColumnId;

  return (
    <article className="group rounded-xl border border-slate-200 bg-white p-4 shadow-sm transition-all duration-200 hover:-translate-y-0.5 hover:border-blue-200 hover:shadow-md">
      <button
        type="button"
        onClick={() => onOpenTask(task.id)}
        className="block w-full cursor-pointer rounded-lg text-left focus:outline-none focus:ring-2 focus:ring-blue-200"
      >
        <div className="flex items-start justify-between gap-3">
          <h4 className="text-sm font-semibold text-slate-900 transition-colors group-hover:text-blue-700">
            {task.title}
          </h4>

          <span
            className={`shrink-0 rounded-full px-2.5 py-1 text-[11px] font-semibold ${priorityStyles[task.priority]}`}
          >
            {task.priority}
          </span>
        </div>

        {task.description && (
          <p className="mt-2 line-clamp-2 text-sm text-slate-500">
            {task.description}
          </p>
        )}

        <div className="mt-4 flex flex-col gap-1 text-xs text-slate-400">
          {task.assignedToName ? (
            <span>Assigned to: {task.assignedToName}</span>
          ) : (
            <span>Unassigned</span>
          )}

          {task.dueDate && (
            <span>Due: {new Date(task.dueDate).toLocaleDateString()}</span>
          )}
        </div>

        <p className="mt-3 text-[11px] font-medium text-slate-400 opacity-0 transition-opacity duration-200 group-hover:opacity-100">
          Click to view details
        </p>
      </button>

      <div className="mt-4 border-t border-slate-100 pt-3">
        <label className="text-xs font-medium text-slate-500">Move to</label>

        <div className="mt-2 flex gap-2">
          <select
            value={targetColumnId}
            onChange={(event) => setTargetColumnId(event.target.value)}
            className="min-w-0 flex-1 rounded-lg border border-slate-300 px-2 py-1.5 text-xs outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            {columns.map((column) => (
              <option key={column.id} value={column.id}>
                {column.name}
              </option>
            ))}
          </select>

          <button
            type="button"
            disabled={isSameColumn || moveMutation.isPending}
            onClick={() => moveMutation.mutate()}
            className="rounded-lg bg-slate-900 px-3 py-1.5 text-xs font-semibold text-white hover:bg-slate-700 disabled:cursor-not-allowed disabled:opacity-50"
          >
            {moveMutation.isPending ? "Moving" : "Move"}
          </button>
        </div>
      </div>
    </article>
  );
}