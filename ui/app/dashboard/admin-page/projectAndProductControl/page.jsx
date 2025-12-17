"use client";

import { useEffect, useState } from "react";
import httpService from "../../../service/httpService";
import ProjectAndProductControlTable from "../../../service/projectAndProductControlTable";
import { Cpu } from "lucide-react";
import { getUsernameFromToken, removeToken } from "../../../service/cookieService";
import { useRouter } from "next/navigation";

export default function ProjectAndProductControl() {
  const [orders, setOrders] = useState([]);
  const [username, setUsername] = useState("");
  const router = useRouter();

  // 🔹 FETCH API DATA
  useEffect(() => {
    const fetchData = async () => {
      try {
        const name = getUsernameFromToken();
        setUsername(name || "");

        const data = await httpService.get(
          "/api/admin/project-product-types"
        );

        setOrders(data || []);
      } catch (err) {
        console.error("Fetch Error:", err);
      }
    };

    fetchData();
  }, []);

  const handleLogout = () => {
    removeToken();
    router.push("/login");
  };

  return (
    <div className="min-h-screen w-full bg-[#e3f3ff] flex flex-col items-center py-10 relative">

      {/* TOP RIGHT */}
      <div className="absolute top-5 right-6 flex items-center gap-5 bg-white shadow-md px-5 py-2 rounded-lg">
        <span className="text-black font-semibold">👤 {username}</span>
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-4 py-1.5 rounded-md"
        >
          Logout
        </button>
      </div>

      {/* LOGO */}
      <div className="absolute top-4 left-6">
        <img src="/cyanconnode-logo.png" className="w-60" />
      </div>

      {/* TITLE */}
      <div className="flex items-center gap-3 mb-4 mt-5">
        <Cpu className="w-10 h-10 text-[#02A3EE]" />
        <h1 className="text-4xl font-bold text-[#02A3EE]">
          PROJECT AND PRODUCT TYPES
        </h1>
      </div>

      {/* TABLE */}
      <div className="w-[95%] bg-white shadow-xl rounded-xl p-6">
        <ProjectAndProductControlTable orders={orders} />
      </div>

      <button
        className="fixed bottom-8 left-8 bg-[#02A3EE] text-white px-6 py-4 rounded-2xl shadow-xl"
      >
        Add New Project
        <div className="text-sm opacity-70">And Product Type</div>
      </button>
    </div>
  );
}
