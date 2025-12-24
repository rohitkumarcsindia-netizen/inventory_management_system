"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { getUsernameFromToken, removeToken } from "../../service/cookieService";

export default function ScmMenu() {
  const router = useRouter();
  const [username, setUsername] = useState("");

  useEffect(() => {
    const name = getUsernameFromToken();
    setUsername(name || "");
  }, []);

  const handleLogout = () => {
    removeToken();
    setUsername("");
    router.push("/");
  };

  return (
    <div className="min-h-screen w-screen bg-[#e3f3ff] flex flex-col items-center relative">

      {/* ===== TOP BAR (Exactly like pending orders UI) ===== */}
      <div className="w-full flex items-center justify-between px-10 pt-6">

        {/* Logo Left */}
        <img src="/cyanconnode-logo.png" alt="Logo" className="w-56" />

        {/* Username + Logout — slightly lower (same as pending page) */}
        <div className="flex items-center gap-5 bg-white shadow-md px-4 py-2 rounded-lg border border-[#cce7ff]">
          <span className="text-lg font-semibold text-[#003b66]">
            👤 {username || "User"}
          </span>

          <button
            onClick={handleLogout}
            className="bg-red-500 text-white px-4 py-1.5 rounded-md hover:bg-red-600 transition font-medium shadow"
          >
            Logout
          </button>
        </div>
      </div>

      {/* Heading — kept center with margin similar to pending screen */}
      <h1 className="text-4xl font-bold text-[#02A3EE] tracking-wide mt-10 mb-20">
        SCM TEAM
      </h1>

      {/* ===== Buttons Section ===== */}
      <div className="flex gap-12">
        <button
          onClick={() => router.push("/dashboard/scm-team/pending")}
          className="bg-[#02A3EE] text-white px-12 py-6 rounded-xl text-2xl font-semibold shadow-lg hover:bg-[#0288c2]"
        >
          Pending Orders
        </button>
       

        <button
          onClick={() => router.push("/dashboard/scm-team/complete")}
          className="bg-[#003b66] text-white px-12 py-6 rounded-xl text-2xl font-semibold shadow-lg hover:bg-[#022f52]"
        >
          Complete Orders
        </button>
      </div>

    </div>
  );
}
