"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import httpService from "../service/httpService";
import { setToken } from "../service/cookieService";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [message, setMessage] = useState("");
  const router = useRouter();

  const handleLogin = async (e) => {
    e.preventDefault();
    const body = { username, password };

    try {
      const data = await httpService.postWithoutAuth("/auth/login", body);
      const token = data.token;
      const department = data.department;

      setToken(token);
      localStorage.setItem("department", department);

      if (department === "Project Team") router.push("/dashboard/project-team");
      else if (department === "Finance") router.push("/dashboard/finance-team");
      else if (department === "SCM") router.push("/dashboard/scm-team");
      else if (department === "Cloud Team") router.push("/dashboard/cloud-team");
      else if (department === "Syrma") router.push("/dashboard/syrma-team");
      else if (department === "RMA") router.push("/dashboard/rma-team");
      else if (department === "Admin") router.push("/dashboard/admin-page");
      else if (department === "Logistic") router.push("/dashboard/logistic-team");
      else router.push("/dashboard");

    } catch (error) {
      if (error.response && error.response.status === 401) {
        setMessage(error.response.data);
      } else {
        setMessage("Unable to connect to backend.");
      }
    }
  };

  return (
    <div className="w-screen h-screen flex flex-col items-center justify-center bg-[#e3f3ff]">

      {/* Logo */}
      <img
        src="/cyanconnode-logo.png"
        alt="CyanConnode Logo"
        className="w-[515px] mb-5"
      />

      {/* Login Card */}
      <form
        onSubmit={handleLogin}
        className="bg-[#1e1e1e] px-12 py-10 rounded-xl shadow-[0_8px_20px_rgba(0,0,0,0.25)] w-[500px] h-[350px] border border-[#333] flex flex-col justify-center"
      >
        {message && (
          <p className="mb-4 text-sm text-red-400 text-center">{message}</p>
        )}

        <input
          type="text"
          placeholder="Username"
          className="w-full p-4 mb-6 bg-[#2a2a2a] text-white rounded-lg border border-[#444] 
                     focus:outline-none focus:border-[#02A3EE]"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <input
          type="password"
          placeholder="Password"
          className="w-full p-4 mb-8 bg-[#2a2a2a] text-white rounded-lg border border-[#444] 
                     focus:outline-none focus:border-[#02A3EE]"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button
          type="submit"
          className="w-full bg-[#02A3EE] hover:bg-[#008ac5] text-white py-4 rounded-lg 
                     font-semibold tracking-wide shadow-lg"
        >
          LOGIN
        </button>
      </form>
    </div>
  );
}
