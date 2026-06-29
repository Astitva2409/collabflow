import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";

import type { BoardColumn } from "../../board/types/board.types";
import { workspaceMemberApi } from "../../workspace/api/workspaceMemberApi";
import { taskApi } from "../api/taskApi";
import {
  createTaskSchema,
  type CreateTaskFormValues,
} from "../types/task.schema";

type CreateTaskFormProps = {
  workspaceId: string;
  projectId: string;
  columns: BoardColumn[];
  onSuccess?: () => void;
};

export default function CreateTaskForm({
  workspaceId,
  projectId,
  columns,
  onSuccess,
}: CreateTaskFormProps) {
  const queryClient = useQueryClient();

  const defaultColumnId = columns.find(
    (column) => column.name.toUpperCase() === "TODO"
  )?.id;

  const {
    data: members = [],
    isLoading: isMembersLoading,
    isError: isMembersError,
  } = useQuery({
    queryKey: ["workspace-members", workspaceId],
    queryFn: () => workspaceMemberApi.getMembers(workspaceId),
    enabled: Boolean(workspaceId),
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateTaskFormValues>({
    resolver: zodResolver(createTaskSchema),
    defaultValues: {
      title: "",
      description: "",
      priority: "MEDIUM",
      boardColumnId: defaultColumnId,
      assignedTo: "",
      dueDate: "",
    },
  });

  const mutation = useMutation({
    mutationFn: (values: CreateTaskFormValues) =>
      taskApi.createTask(workspaceId, projectId, {
        title: values.title.trim(),
        description: values.description?.trim() || undefined,
        priority: values.priority || "MEDIUM",
        boardColumnId: values.boardColumnId || defaultColumnId,
        assignedTo: values.assignedTo || undefined,
        dueDate: values.dueDate ? `${values.dueDate}T18:00:00` : undefined,
      }),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["tasks", workspaceId, projectId],
      });

      reset({
        title: "",
        description: "",
        priority: "MEDIUM",
        boardColumnId: defaultColumnId,
        assignedTo: "",
        dueDate: "",
      });

      onSuccess?.();
    },

    onError: (error: any) => {
      alert(
        error?.response?.data?.message ||
          "Failed to create task. Please try again."
      );
    },
  });

  const onSubmit = (values: CreateTaskFormValues) => {
    mutation.mutate(values);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="text-sm font-medium text-slate-700">
          Task Title
        </label>
        <input
          type="text"
          placeholder="e.g. Build task creation UI"
          {...register("title")}
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
        />
        {errors.title && (
          <p className="mt-1 text-sm text-red-500">{errors.title.message}</p>
        )}
      </div>

      <div>
        <label className="text-sm font-medium text-slate-700">
          Description
        </label>
        <textarea
          rows={3}
          placeholder="What needs to be done?"
          {...register("description")}
          className="mt-1 w-full resize-none rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
        />
        {errors.description && (
          <p className="mt-1 text-sm text-red-500">
            {errors.description.message}
          </p>
        )}
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div>
          <label className="text-sm font-medium text-slate-700">
            Priority
          </label>
          <select
            {...register("priority")}
            className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            <option value="LOW">LOW</option>
            <option value="MEDIUM">MEDIUM</option>
            <option value="HIGH">HIGH</option>
            <option value="URGENT">URGENT</option>
          </select>
        </div>

        <div>
          <label className="text-sm font-medium text-slate-700">
            Column
          </label>
          <select
            {...register("boardColumnId")}
            className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            {columns.map((column) => (
              <option key={column.id} value={column.id}>
                {column.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="grid gap-4 sm:grid-cols-2">
        <div>
          <label className="text-sm font-medium text-slate-700">
            Assignee
          </label>
          <select
            {...register("assignedTo")}
            disabled={isMembersLoading || isMembersError}
            className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100 disabled:cursor-not-allowed disabled:bg-slate-100"
          >
            <option value="">Unassigned</option>

            {members.map((member) => (
              <option key={member.userId} value={member.userId}>
                {member.fullName} ({member.role})
              </option>
            ))}
          </select>

          {isMembersLoading && (
            <p className="mt-1 text-xs text-slate-500">
              Loading members...
            </p>
          )}

          {isMembersError && (
            <p className="mt-1 text-xs text-red-500">
              Failed to load workspace members.
            </p>
          )}
        </div>

        <div>
          <label className="text-sm font-medium text-slate-700">
            Due Date
          </label>
          <input
            type="date"
            {...register("dueDate")}
            className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          />
        </div>
      </div>

      <button
        type="submit"
        disabled={mutation.isPending}
        className="w-full rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70"
      >
        {mutation.isPending ? "Creating Task..." : "Create Task"}
      </button>
    </form>
  );
}