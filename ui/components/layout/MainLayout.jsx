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
  },[]);

  useEffect(() => {
    if (!isClient) return; // Skip on server
    
    const token = getToken();
    const department = localStorage.getItem("department");

    const pathname =
      typeof window === "undefined" ? "" : window.location.pathname;
    const isLoginPage = window.location.pathname.startsWith("/login");

    if (isLoginPage && token && isTokenValid(token)) {
      if (department === "Project Team") {
        router.push("/dashboard/project-team");
      } else if (department === "Finance") {
        router.push("/dashboard/finance-team");
      } else if (department === "SCM") {
        router.push("/dashboard/scm-team");
      }  else if (department === "Cloud Team") {
        router.push("/dashboard/cloud-team");
      } else if (department === "Syrma") {
        router.push("/dashboard/syrma-team");
      } else if (department === "RMA") {
        router.push("/dashboard/rma-team");
      } else if (department === "Admin") {
        router.push("/dashboard/admin-page");
      } else if (department === "Logistic") {
        router.push("/dashboard/logistic-team");
      } else {
        router.push("/login");
      }
    }

    // Example auth guard logic
    if (!isLoginPage && (!token || !isTokenValid(token) )) {
      router.push("/login");
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
