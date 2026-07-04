import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";

import { workspaceMemberApi } from "../api/workspaceMemberApi";
import {
  addWorkspaceMemberSchema,
  type AddWorkspaceMemberFormValues,
} from "../types/workspaceMember.schema";

type WorkspaceMembersPanelProps = {
  workspaceId: string;
};

export default function WorkspaceMembersPanel({
  workspaceId,
}: WorkspaceMembersPanelProps) {
  const queryClient = useQueryClient();

  const {
    data: members = [],
    isLoading,
    isError,
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
  } = useForm<AddWorkspaceMemberFormValues>({
    resolver: zodResolver(addWorkspaceMemberSchema),
    defaultValues: {
      email: "",
      role: "MEMBER",
    },
  });

  const addMemberMutation = useMutation({
    mutationFn: (values: AddWorkspaceMemberFormValues) =>
      workspaceMemberApi.addMember(workspaceId, {
        email: values.email.trim().toLowerCase(),
        role: values.role,
      }),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["workspace-members", workspaceId],
      });

      reset({
        email: "",
        role: "MEMBER",
      });
    },

    onError: (error: any) => {
      alert(
        error?.response?.data?.message ||
          "Failed to add member. Please try again."
      );
    },
  });

  const onSubmit = (values: AddWorkspaceMemberFormValues) => {
    addMemberMutation.mutate(values);
  };

  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <div>
        <p className="text-sm text-slate-500">Participants</p>
        <h3 className="text-lg font-semibold text-slate-900">
          Workspace Members
        </h3>
      </div>

      <form
        onSubmit={handleSubmit(onSubmit)}
        className="mt-5 grid gap-4 lg:grid-cols-[1fr_180px_140px]"
      >
        <div>
          <input
            type="email"
            placeholder="Enter member email"
            {...register("email")}
            className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          />

          {errors.email && (
            <p className="mt-1 text-sm text-red-500">
              {errors.email.message}
            </p>
          )}
        </div>

        <div>
          <select
            {...register("role")}
            className="w-full rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
          >
            <option value="ADMIN">ADMIN</option>
            <option value="MEMBER">MEMBER</option>
            <option value="VIEWER">VIEWER</option>
          </select>

          {errors.role && (
            <p className="mt-1 text-sm text-red-500">{errors.role.message}</p>
          )}
        </div>

        <button
          type="submit"
          disabled={addMemberMutation.isPending}
          className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70"
        >
          {addMemberMutation.isPending ? "Adding..." : "Add Member"}
        </button>
      </form>

      <div className="mt-6">
        {isLoading && (
          <div className="rounded-xl bg-slate-50 p-4 text-center text-sm text-slate-500">
            Loading members...
          </div>
        )}

        {isError && (
          <div className="rounded-xl bg-red-50 p-4 text-center text-sm text-red-600">
            Failed to load members.
          </div>
        )}

        {!isLoading && !isError && members.length === 0 && (
          <div className="rounded-xl border border-dashed border-slate-300 p-4 text-center text-sm text-slate-400">
            No members found.
          </div>
        )}

        {!isLoading && !isError && members.length > 0 && (
          <div className="space-y-3">
            {members.map((member) => (
              <article
                key={member.userId}
                className="flex flex-col justify-between gap-3 rounded-xl border border-slate-100 bg-slate-50 p-4 sm:flex-row sm:items-center"
              >
                <div>
                  <p className="text-sm font-semibold text-slate-900">
                    {member.fullName}
                  </p>
                  <p className="text-xs text-slate-500">{member.email}</p>
                </div>

                <span className="w-fit rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold text-blue-700">
                  {member.role}
                </span>
              </article>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}