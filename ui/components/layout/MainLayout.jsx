"use client";

import { getToken } from "../../app/service/cookieService";
import { useRouter } from "next/navigation";
import { isTokenValid } from "../../app/service/cookieService";
import { useState, useEffect } from "react";

export default function MainLayout({ children }) {
  const router = useRouter();

  const [isClient, setIsClient] = useState(false);

  useEffect(() => {
    // Marks when the component has mounted (so window is defined)
    setIsClient(true);
  }, []);

  useEffect(() => {
    if (!isClient) return; // Skip on server

    const token = getToken();
    const department = localStorage.getItem("department");

    const pathname =
      typeof window === "undefined" ? "" : window.location.pathname;
    const isLoginPage = pathname === "/";

    if (isLoginPage && token && isTokenValid(token)) {
      if (department === "Project Team") {
        router.replace("/dashboard/project-team");
      } else if (department === "Finance") {
        router.replace("/dashboard/finance-team");
      } else if (department === "SCM") {
        router.replace("/dashboard/scm-team");
      } else if (department === "Cloud Team") {
        router.replace("/dashboard/cloud-team");
      } else if (department === "Syrma") {
        router.replace("/dashboard/syrma-team");
      } else if (department === "RMA") {
        router.replace("/dashboard/rma-team");
      } else if (department === "Admin") {
        router.replace("/dashboard/admin-page");
      } else if (department === "Logistic") {
        router.replace("/dashboard/logistic-team");
      } else if (department === "AUDITOR") {
        router.replace("/dashboard/auditor");
      }
      else {
        router.replace("/");
      }
    }

    // Example auth guard logic
    if (!isLoginPage && (!token || !isTokenValid(token))) {
      router.replace("/");
    }
  }, [isClient, router]);

  if (!isClient) {
    // Prevent SSR errors and flicker
    return null;
  }

  return (
    <div className="min-h-screen flex flex-col">
      <div className="flex flex-1">
        {/* Main content area */}
        <main className="flex-1 p-0 m-0 bg-gray-50">{children}</main>
      </div>
    </div>
  );
}
