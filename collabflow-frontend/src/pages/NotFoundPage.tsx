import { Link } from "react-router-dom";

export default function NotFoundPage() {
  return (
    <main className="min-h-screen flex items-center justify-center bg-slate-50 px-6">
      <section className="text-center">
        <h1 className="text-5xl font-bold text-slate-900">404</h1>
        <p className="mt-3 text-slate-600">Page not found.</p>

        <Link
          to="/"
          className="mt-6 inline-block rounded-xl bg-blue-600 px-5 py-3 text-sm font-semibold text-white hover:bg-blue-700"
        >
          Go Home
        </Link>
      </section>
    </main>
  );
}