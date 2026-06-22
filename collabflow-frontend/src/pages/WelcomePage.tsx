import { Link } from "react-router-dom";
import { APP_NAME } from "../lib/constants";

export default function WelcomePage() {
  return (
    <main className="min-h-screen bg-slate-50 flex items-center justify-center px-6">
      <section className="max-w-3xl text-center">
        <p className="text-sm font-semibold text-blue-600 mb-3">
          Project Collaboration Platform
        </p>

        <h1 className="text-5xl font-bold tracking-tight text-slate-900">
          Welcome to {APP_NAME}
        </h1>

        <p className="mt-5 text-lg text-slate-600">
          Manage workspaces, projects, Kanban boards, tasks, comments, and
          activities from one collaborative platform.
        </p>

        <div className="mt-8 flex justify-center gap-4">
          <Link
            to="/login"
            className="rounded-xl bg-blue-600 px-5 py-3 text-sm font-semibold text-white shadow hover:bg-blue-700"
          >
            Login
          </Link>

          <Link
            to="/register"
            className="rounded-xl border border-slate-300 bg-white px-5 py-3 text-sm font-semibold text-slate-800 shadow-sm hover:bg-slate-100"
          >
            Create Account
          </Link>
        </div>
      </section>
    </main>
  );
}