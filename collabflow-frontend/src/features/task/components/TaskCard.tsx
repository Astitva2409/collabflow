import type { Task } from "../types/task.types";

type TaskCardProps = {
  task: Task;
};

const priorityStyles: Record<Task["priority"], string> = {
  LOW: "bg-slate-100 text-slate-700",
  MEDIUM: "bg-blue-50 text-blue-700",
  HIGH: "bg-orange-50 text-orange-700",
  URGENT: "bg-red-50 text-red-700",
};

export default function TaskCard({ task }: TaskCardProps) {
  return (
    <article className="rounded-xl border border-slate-200 bg-white p-4 shadow-sm">
      <div className="flex items-start justify-between gap-3">
        <h4 className="text-sm font-semibold text-slate-900">{task.title}</h4>

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
    </article>
  );
}