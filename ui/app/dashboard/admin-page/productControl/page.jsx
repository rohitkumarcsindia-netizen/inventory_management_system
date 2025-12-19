"use client";

import { useEffect, useState } from "react";
import httpService from "../../../service/httpService";
import ProductControlTable from "../../../service/productControlTable";
import { Cpu } from "lucide-react";
import {
  getUsernameFromToken,
  removeToken,
} from "../../../service/cookieService";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";

export default function ProjectAndProductControl() {
  const [orders, setOrders] = useState([]);
  const [username, setUsername] = useState("");
  const [showPopup, setShowPopup] = useState(false);

  const router = useRouter();

  /* ---------------- FETCH DATA ---------------- */
  const fetchData = async () => {
    try {
      const data = await httpService.get("/api/admin/product-types");
      setOrders(data || []);
    } catch (err) {
      console.error("Fetch Error:", err);
    }
  };

  useEffect(() => {
    const name = getUsernameFromToken();
    setUsername(name || "");
    fetchData();
  }, []);

  const handleLogout = () => {
    removeToken();
    router.push("/login");
  };

  /* ---------------- FORM (POPUP) ---------------- */
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();

  const onSubmit = async (data) => {
    try {
      const body = {
        productType: data.productType,
      };

      const res = await httpService.postWithAuth(
        "/api/admin/product-types",
        body
      );

      alert(res);
      reset();
      setShowPopup(false);
      fetchData();
    } catch (err) {
      console.error("POST ERROR:", err);
      alert("Failed to save data");
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#e3f3ff] flex flex-col items-center py-10 relative">
      {/* ---------------- TOP RIGHT ---------------- */}
      <div className="absolute top-5 right-6 flex items-center gap-5 bg-white shadow-md px-5 py-2 rounded-lg">
        <span className="text-black font-semibold">👤 {username}</span>
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-4 py-1.5 rounded-md"
        >
          Logout
        </button>
      </div>

      {/* ---------------- LOGO ---------------- */}
      <div className="absolute top-4 left-6">
        <img src="/cyanconnode-logo.png" className="w-60" />
      </div>

      {/* ---------------- TITLE ---------------- */}
      <div className="flex items-center gap-3 mb-4 mt-5">
        <Cpu className="w-10 h-10 text-[#02A3EE]" />
        <h1 className="text-4xl font-bold text-[#02A3EE]">
          PRODUCT TYPES
        </h1>
      </div>

      {/* ---------------- TABLE ---------------- */}
      <div className="w-[95%] bg-white shadow-xl rounded-xl p-6">
        <ProductControlTable orders={orders} />
      </div>

      {/* ---------------- ADD BUTTON ---------------- */}
      <button
        onClick={() => {
          setShowPopup(true);
          reset();
        }}
        className="fixed bottom-8 left-8 bg-[#02A3EE] text-white px-6 py-4 rounded-2xl shadow-xl"
      >
        Add New Product
      </button>

      {/* ================= POPUP ================= */}
      {showPopup && (
        <>
          {/* BACKDROP + BLUR */}
          <div
            className="fixed inset-0 bg-black/40 backdrop-blur-sm z-40"
            onClick={() => setShowPopup(false)}
          />

          {/* MODAL */}
          <div className="fixed inset-0 z-50 flex items-center justify-center">
            <div className="bg-white w-[420px] rounded-xl shadow-2xl p-6">
              <h2 className="text-xl font-bold text-center text-[#02A3EE] mb-5">
                Add Product
              </h2>

              <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
                {/* PROJECT */}
                <div>
                  <label className="block text-black font-semibold mb-1">
                    Product
                  </label>
                  <input
                    {...register("productType", {
                      required: "Project is required",
                    })}
                    placeholder="Enter Project"
                    className="w-full px-3 py-2 text-black border rounded-md"
                  />
                  {errors.productType && (
                    <p className="text-red-500 text-xs mt-1">
                      {errors.projectType.message}
                    </p>
                  )}
                </div>

                {/* ACTIONS */}
                <div className="flex justify-end gap-3 pt-4">
                  <button
                    type="button"
                    onClick={() => {
                      reset();
                      setShowPopup(false);
                    }}
                    className="px-4 py-2 border bg-red-500 text-white rounded-md"
                  >
                    ✖
                  </button>

                  <button
                    type="submit"
                    className="bg-[#02A3EE] text-white px-4 py-2 rounded-md"
                  >
                    ADD
                  </button>
                </div>
              </form>
            </div>
          </div>
        </>
      )}
    </div>
  );
}
