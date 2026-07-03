import type { ActivityLog } from "../types/activity.types";

type ActivityFeedProps = {
  activities: ActivityLog[];
  isLoading?: boolean;
  isError?: boolean;
  title?: string;
};

const activityBadgeStyles: Record<string, string> = {
  WORKSPACE_CREATED: "bg-blue-50 text-blue-700",
  PROJECT_CREATED: "bg-violet-50 text-violet-700",
  TASK_CREATED: "bg-green-50 text-green-700",
  TASK_MOVED: "bg-orange-50 text-orange-700",
  COMMENT_ADDED: "bg-slate-100 text-slate-700",
  DEFAULT: "bg-slate-100 text-slate-700",
};

function formatActivityType(activityType: string) {
  return activityType.replaceAll("_", " ");
}

export default function ActivityFeed({
  activities,
  isLoading,
  isError,
  title = "Activity Feed",
}: ActivityFeedProps) {
  return (
    <section className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <div className="mb-4 flex items-center justify-between">
        <div>
          <p className="text-sm text-slate-500">Recent updates</p>
          <h3 className="text-lg font-semibold text-slate-900">{title}</h3>
        </div>

        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-600">
          {activities.length}
        </span>
      </div>

      {isLoading && (
        <div className="rounded-xl bg-slate-50 p-4 text-center text-sm text-slate-500">
          Loading activities...
        </div>
      )}

      {isError && (
        <div className="rounded-xl bg-red-50 p-4 text-center text-sm text-red-600">
          Failed to load activities.
        </div>
      )}

      {!isLoading && !isError && activities.length === 0 && (
        <div className="rounded-xl border border-dashed border-slate-300 p-4 text-center text-sm text-slate-400">
          No activities yet.
        </div>
      )}

      {!isLoading && !isError && activities.length > 0 && (
        <div className="max-h-[420px] space-y-3 overflow-y-auto pr-1">
          {activities.map((activity) => (
            <article
              key={activity.id}
              className="rounded-xl border border-slate-100 bg-slate-50 p-4"
            >
              <div className="flex flex-wrap items-center gap-2">
                <span
                  className={`rounded-full px-2.5 py-1 text-[11px] font-semibold ${
                    activityBadgeStyles[activity.activityType] ||
                    activityBadgeStyles.DEFAULT
                  }`}
                >
                  {formatActivityType(activity.activityType)}
                </span>

                <span className="text-xs text-slate-400">
                  {new Date(activity.createdAt).toLocaleString()}
                </span>
              </div>

              <p className="mt-2 text-sm font-medium text-slate-800">
                {activity.description}
              </p>

              <p className="mt-1 text-xs text-slate-500">
                By {activity.actorName}
              </p>
            </article>
          ))}
        </div>
      )}
    </section>
  );
}