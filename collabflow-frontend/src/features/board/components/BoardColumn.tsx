import type { BoardColumn as BoardColumnType } from "../types/board.types";
import type { Task } from "../../task/types/task.types";
import TaskCard from "../../task/components/TaskCard";

type BoardColumnProps = {
  column: BoardColumnType;
  columns: BoardColumnType[];
  tasks: Task[];
  workspaceId: string;
  projectId: string;
};

export default function BoardColumn({
  column,
  columns,
  tasks,
  workspaceId,
  projectId,
}: BoardColumnProps) {
  return (
    <section className="flex min-h-[500px] w-80 shrink-0 flex-col rounded-2xl bg-slate-100 p-4">
      <div className="mb-4 flex items-center justify-between">
        <h3 className="text-sm font-bold text-slate-800">{column.name}</h3>

        <span className="rounded-full bg-white px-2.5 py-1 text-xs font-semibold text-slate-500">
          {tasks.length}
        </span>
      </div>

      <div className="space-y-3">
        {tasks.length === 0 ? (
          <div className="rounded-xl border border-dashed border-slate-300 bg-white/60 p-4 text-center text-sm text-slate-400">
            No tasks
          </div>
        ) : (
          tasks.map((task) => (
            <TaskCard
              key={task.id}
              task={task}
              workspaceId={workspaceId}
              projectId={projectId}
              columns={columns}
            />
          ))
        )}
      </div>
    </section>
  );
}