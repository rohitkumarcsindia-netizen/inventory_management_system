"use client";

import { useRouter } from "next/navigation";

export default function Home() {
  const router = useRouter();

  const handleLogin = () => {
    router.push("/login");
  };

  return (
    <div className="flex flex-col items-center justify-center h-screen bg-gray-100">
      <h1 className="text-6xl font-bold text-gray-800 mb-8">Dashboard</h1>

      <button
        onClick={handleLogin}
        className="px-6 py-3 bg-blue-600 text-white text-lg rounded-xl shadow-md hover:bg-blue-700 transition-all duration-300"
      >
        Go to Login
      </button>
    </div>
  );
}
