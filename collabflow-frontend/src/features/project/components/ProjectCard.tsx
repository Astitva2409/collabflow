import { Link } from "react-router-dom";
import type { Project } from "../types/project.types";

type ProjectCardProps = {
  project: Project;
};

const priorityStyles: Record<Project["priority"], string> = {
  LOW: "bg-slate-100 text-slate-700",
  MEDIUM: "bg-blue-50 text-blue-700",
  HIGH: "bg-orange-50 text-orange-700",
  CRITICAL: "bg-red-50 text-red-700",
};

export default function ProjectCard({ project }: ProjectCardProps) {
  return (
    <Link
      to={`/workspaces/${project.workspaceId}/projects/${project.id}/board`}
      className="block rounded-2xl border border-slate-200 bg-white p-5 shadow-sm transition hover:-translate-y-0.5 hover:shadow-md"
    >
      <article>
        <div className="flex items-start justify-between gap-4">
          <div>
            <h3 className="text-lg font-semibold text-slate-900">
              {project.name}
            </h3>

            <p className="mt-2 line-clamp-2 text-sm text-slate-500">
              {project.description || "No description provided."}
            </p>
          </div>

          <span
            className={`rounded-full px-3 py-1 text-xs font-semibold ${priorityStyles[project.priority]}`}
          >
            {project.priority}
          </span>
        </div>

        <div className="mt-5 flex items-center justify-between text-xs text-slate-400">
          <span>{project.status}</span>
          <span>{new Date(project.createdAt).toLocaleDateString()}</span>
        </div>
      </article>
    </Link>
  );
}