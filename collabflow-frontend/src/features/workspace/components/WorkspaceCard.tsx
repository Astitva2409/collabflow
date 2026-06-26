import { Link } from "react-router-dom";
import type { Workspace } from "../types/workspace.types";

type WorkspaceCardProps = {
  workspace: Workspace;
};

export default function WorkspaceCard({ workspace }: WorkspaceCardProps) {
  return (
    <Link
      to={`/workspaces/${workspace.id}`}
      className="block rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
    >
      <article>
        <div className="flex items-start justify-between gap-4">
          <div>
            <h3 className="text-lg font-semibold text-slate-900">
              {workspace.name}
            </h3>

            <p className="mt-2 line-clamp-2 text-sm text-slate-500">
              {workspace.description || "No description provided."}
            </p>
          </div>

          <span className="rounded-full bg-blue-50 px-3 py-1 text-xs font-semibold text-blue-700">
            {workspace.role}
          </span>
        </div>

        <div className="mt-5 flex items-center justify-between text-xs text-slate-400">
          <span>Workspace</span>
          <span>{new Date(workspace.createdAt).toLocaleDateString()}</span>
        </div>
      </article>
    </Link>
  );
}