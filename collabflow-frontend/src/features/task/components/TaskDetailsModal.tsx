import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useForm } from "react-hook-form";

import { commentApi } from "../../comment/api/commentApi";
import {
  createCommentSchema,
  type CreateCommentFormValues,
} from "../../comment/types/comment.schema";
import { taskApi } from "../api/taskApi";

type TaskDetailsModalProps = {
  workspaceId: string;
  projectId: string;
  taskId: string;
  onClose: () => void;
};

const priorityStyles: Record<string, string> = {
  LOW: "bg-slate-100 text-slate-700",
  MEDIUM: "bg-blue-50 text-blue-700",
  HIGH: "bg-orange-50 text-orange-700",
  URGENT: "bg-red-50 text-red-700",
};

export default function TaskDetailsModal({
  workspaceId,
  projectId,
  taskId,
  onClose,
}: TaskDetailsModalProps) {
  const queryClient = useQueryClient();

  const {
    data: task,
    isLoading: isTaskLoading,
    isError: isTaskError,
  } = useQuery({
    queryKey: ["task", workspaceId, projectId, taskId],
    queryFn: () => taskApi.getTaskById(workspaceId, projectId, taskId),
  });

  const {
    data: comments = [],
    isLoading: isCommentsLoading,
    isError: isCommentsError,
  } = useQuery({
    queryKey: ["comments", workspaceId, projectId, taskId],
    queryFn: () => commentApi.getComments(workspaceId, projectId, taskId),
  });

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateCommentFormValues>({
    resolver: zodResolver(createCommentSchema),
    defaultValues: {
      content: "",
    },
  });

  const addCommentMutation = useMutation({
    mutationFn: (values: CreateCommentFormValues) =>
      commentApi.addComment(workspaceId, projectId, taskId, {
        content: values.content.trim(),
      }),

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ["comments", workspaceId, projectId, taskId],
      });
      queryClient.invalidateQueries({
        queryKey: ["project-activities", workspaceId, projectId],
      });

      reset({ content: "" });
    },

    onError: (error: any) => {
      alert(
        error?.response?.data?.message ||
          "Failed to add comment. Please try again."
      );
    },
  });

  const onSubmit = (values: CreateCommentFormValues) => {
    addCommentMutation.mutate(values);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/40 px-4">
      <section className="max-h-[90vh] w-full max-w-3xl overflow-y-auto rounded-2xl bg-white shadow-xl">
        <div className="sticky top-0 z-10 flex items-center justify-between border-b border-slate-200 bg-white px-6 py-4">
          <div>
            <p className="text-sm text-slate-500">Task Details</p>
            <h2 className="text-xl font-bold text-slate-900">
              {isTaskLoading ? "Loading task..." : task?.title || "Task"}
            </h2>
          </div>

          <button
            onClick={onClose}
            className="rounded-xl bg-slate-100 px-3 py-2 text-sm font-medium text-slate-700 hover:bg-slate-200"
          >
            Close
          </button>
        </div>

        <div className="space-y-6 px-6 py-5">
          {isTaskError && (
            <div className="rounded-xl bg-red-50 p-4 text-sm text-red-600">
              Failed to load task details.
            </div>
          )}

          {task && (
            <div className="rounded-2xl border border-slate-200 p-5">
              <div className="flex flex-wrap items-center gap-2">
                <span
                  className={`rounded-full px-3 py-1 text-xs font-semibold ${
                    priorityStyles[task.priority]
                  }`}
                >
                  {task.priority}
                </span>

                <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
                  {task.columnName}
                </span>
              </div>

              <p className="mt-4 text-sm text-slate-500">Description</p>
              <p className="mt-1 text-sm text-slate-800">
                {task.description || "No description provided."}
              </p>

              <div className="mt-5 grid gap-4 sm:grid-cols-2">
                <div>
                  <p className="text-xs font-medium text-slate-500">
                    Assigned To
                  </p>
                  <p className="mt-1 text-sm text-slate-800">
                    {task.assignedToName || "Unassigned"}
                  </p>
                </div>

                <div>
                  <p className="text-xs font-medium text-slate-500">
                    Due Date
                  </p>
                  <p className="mt-1 text-sm text-slate-800">
                    {task.dueDate
                      ? new Date(task.dueDate).toLocaleDateString()
                      : "No due date"}
                  </p>
                </div>

                <div>
                  <p className="text-xs font-medium text-slate-500">
                    Position
                  </p>
                  <p className="mt-1 text-sm text-slate-800">
                    {task.position}
                  </p>
                </div>

                <div>
                  <p className="text-xs font-medium text-slate-500">
                    Created At
                  </p>
                  <p className="mt-1 text-sm text-slate-800">
                    {new Date(task.createdAt).toLocaleString()}
                  </p>
                </div>
              </div>
            </div>
          )}

          <div className="rounded-2xl border border-slate-200 p-5">
            <h3 className="text-lg font-semibold text-slate-900">Comments</h3>

            <form
              onSubmit={handleSubmit(onSubmit)}
              className="mt-4 space-y-3"
            >
              <textarea
                rows={3}
                placeholder="Add a comment..."
                {...register("content")}
                className="w-full resize-none rounded-xl border border-slate-300 px-3 py-2 text-sm outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-100"
              />

              {errors.content && (
                <p className="text-sm text-red-500">
                  {errors.content.message}
                </p>
              )}

              <button
                type="submit"
                disabled={addCommentMutation.isPending}
                className="rounded-xl bg-blue-600 px-4 py-2 text-sm font-semibold text-white hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-70"
              >
                {addCommentMutation.isPending ? "Adding..." : "Add Comment"}
              </button>
            </form>

            <div className="mt-6 space-y-3">
              {isCommentsLoading && (
                <p className="text-sm text-slate-500">Loading comments...</p>
              )}

              {isCommentsError && (
                <p className="text-sm text-red-500">
                  Failed to load comments.
                </p>
              )}

              {!isCommentsLoading &&
                !isCommentsError &&
                comments.length === 0 && (
                  <div className="rounded-xl border border-dashed border-slate-300 p-4 text-center text-sm text-slate-400">
                    No comments yet.
                  </div>
                )}

              {comments.map((comment) => (
                <article
                  key={comment.id}
                  className="rounded-xl bg-slate-50 p-4"
                >
                  <div className="flex items-center justify-between gap-3">
                    <p className="text-sm font-semibold text-slate-800">
                      {comment.authorName}
                    </p>

                    <p className="text-xs text-slate-400">
                      {new Date(comment.createdAt).toLocaleString()}
                    </p>
                  </div>

                  <p className="mt-2 text-sm text-slate-700">
                    {comment.content}
                  </p>
                </article>
              ))}
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}