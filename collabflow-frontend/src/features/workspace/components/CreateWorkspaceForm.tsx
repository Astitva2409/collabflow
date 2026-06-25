import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";

import { workspaceApi } from "../api/workspaceApi";
import {
  createWorkspaceSchema,
  type CreateWorkspaceFormValues,
} from "../types/workspace.schema";

type CreateWorkspaceFormProps = {
  onSuccess?: () => void;
};

export default function CreateWorkspaceForm({
  onSuccess,
}: CreateWorkspaceFormProps) {
  const queryClient = useQueryClient();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateWorkspaceFormValues>({
    resolver: zodResolver(createWorkspaceSchema),
    defaultValues: {
      name: "",
      description: "",
    },
  });

  const mutation = useMutation({
    mutationFn: workspaceApi.createWorkspace,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["workspaces"] });
      reset();
      onSuccess?.();
    },
    onError: (error: any) => {
      alert(
        error?.response?.data?.message ||
          "Failed to create workspace. Please try again."
      );
    },
  });

  const onSubmit = (values: CreateWorkspaceFormValues) => {
    mutation.mutate({
      name: values.name.trim(),
      description: values.description?.trim() || undefined,
    });
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
      <div>
        <label className="text-sm font-medium text-slate-700">
          Workspace Name
        </label>
        <input
          type="text"
          placeholder="e.g. CollabFlow Development Team"
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
          placeholder="What is this workspace for?"
          {...register("description")}
          className="mt-1 w-full resize-none rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
        />
        {errors.description && (
          <p className="mt-1 text-sm text-red-500">
            {errors.description.message}
          </p>
        )}
      </div>

      <button
        type="submit"
        disabled={mutation.isPending}
        className="w-full rounded-xl bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white shadow-sm hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70"
      >
        {mutation.isPending ? "Creating..." : "Create Workspace"}
      </button>
    </form>
  );
}