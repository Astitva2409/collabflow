import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useMutation } from "@tanstack/react-query";
import { useNavigate, Link } from "react-router-dom";

import {
  registerSchema,
  type RegisterFormValues,
} from "../types/auth.schema";
import { authApi } from "../api/authApi";

export default function RegisterPage() {
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
  });

  const mutation = useMutation({
    mutationFn: authApi.register,
    onSuccess: () => {
      alert("Registration successful. Please login.");
      navigate("/login");
    },
    onError: (error: any) => {
      console.log("Register error:", error);

      alert(
        error?.response?.data?.message ||
          error?.message ||
          "Registration failed. Check console."
      );
    },
  });

  const onSubmit = (values: RegisterFormValues) => {
    mutation.mutate(values);
  };

  return (
    <main className="min-h-screen bg-slate-50 flex items-center justify-center px-6">
      <section className="w-full max-w-md rounded-2xl bg-white p-8 shadow-sm">
        <h1 className="text-2xl font-bold text-slate-900">
          Create Account
        </h1>

        <form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-4">
          <div>
            <input
              type="text"
              placeholder="Full Name"
              {...register("fullName")}
              className="w-full rounded-lg border px-3 py-2"
            />
            {errors.fullName && (
              <p className="text-red-500 text-sm">
                {errors.fullName.message}
              </p>
            )}
          </div>

          <div>
            <input
              type="email"
              placeholder="Email"
              {...register("email")}
              className="w-full rounded-lg border px-3 py-2"
            />
            {errors.email && (
              <p className="text-red-500 text-sm">
                {errors.email.message}
              </p>
            )}
          </div>

          <div>
            <input
              type="password"
              placeholder="Password"
              {...register("password")}
              className="w-full rounded-lg border px-3 py-2"
            />
            {errors.password && (
              <p className="text-red-500 text-sm">
                {errors.password.message}
              </p>
            )}
          </div>

          <button className="w-full rounded-lg bg-blue-600 py-2 text-white">
            {mutation.isPending ? "Creating..." : "Register"}
          </button>
        </form>

        <div className="mt-4 text-sm">
          Already have an account?{" "}
          <Link to="/login" className="text-blue-600">
            Login
          </Link>
        </div>
      </section>
    </main>
  );
}