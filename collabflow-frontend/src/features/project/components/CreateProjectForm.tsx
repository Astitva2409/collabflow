import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";

import { projectApi } from "../api/projectApi";
import {
  createProjectSchema,
  type CreateProjectFormValues,
} from "../types/project.schema";

type CreateProjectFormProps = {
  workspaceId: string;
  onSuccess?: () => void;
};

export default function CreateProjectForm({
  workspaceId,
  onSuccess,
}: CreateProjectFormProps) {
  const queryClient = useQueryClient();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateProjectFormValues>({
    resolver: zodResolver(createProjectSchema),
    defaultValues: {
      name: "",
      description: "",
      priority: "MEDIUM",
    },
  });

  const mutation = useMutation({
    mutationFn: (values: CreateProjectFormValues) =>
      projectApi.createProject(workspaceId, {
        name: values.name.trim(),
        description: values.description?.trim() || undefined,
        priority: values.priority || "MEDIUM",
      }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["projects", workspaceId] });
      reset();
      onSuccess?.();
    },
    onError: (error: any) => {
      alert(
        error?.response?.data?.message ||
          "Failed to create project. Please try again."
      );
    },
  });

  const onSubmit = (values: CreateProjectFormValues) => {
    mutation.mutate(values);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="text-sm font-medium text-slate-700">
          Project Name
        </label>
        <input
          type="text"
          placeholder="e.g. Task Management Module"
          {...register("name")}
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
        />
        {errors.name && (
          <p className="mt-1 text-sm text-red-500">{errors.name.message}</p>
        )}
      </div>

      <div>
        <label className="text-sm font-medium text-slate-700">
          Description
        </label>
        <textarea
          rows={3}
          placeholder="What is this project about?"
          {...register("description")}
          className="mt-1 w-full resize-none rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
        />
        {errors.description && (
          <p className="mt-1 text-sm text-red-500">
            {errors.description.message}
          </p>
        )}
      </div>

      <div>
        <label className="text-sm font-medium text-slate-700">Priority</label>
        <select
          {...register("priority")}
          className="mt-1 w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
        >
          <option value="LOW">LOW</option>
          <option value="MEDIUM">MEDIUM</option>
          <option value="HIGH">HIGH</option>
          <option value="CRITICAL">CRITICAL</option>
        </select>
      </div>

      <button
        type="submit"
        disabled={mutation.isPending}
        className="w-full rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70"
      >
        {mutation.isPending ? "Creating..." : "Create Project"}
      </button>
    </form>
  );
}