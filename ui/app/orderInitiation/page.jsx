"use client";
import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import httpService from "../service/httpService";
import { motion } from "framer-motion";
import {removeToken, getUsernameFromToken } from "../service/cookieService";

import { useRouter } from "next/navigation";

export default function OrderInitiation() {
  const [loading, setLoading] = useState(false);
  const [username, setUsername] = useState("");

  const router = useRouter();

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();

  useEffect(() => {
    const name = getUsernameFromToken();
    setUsername(name || "");
  }, []);

 



  //  -----SUBMIT-----
  const handleOnSubmit = async (order) => {
    setLoading(true);

    try {
      await httpService.postWithAuth("/api/orders/project/create", order);
      alert("✅ Order submitted!");
      reset();
    } catch (err) {
      alert("❌ Failed to Submit Order");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  //  -----SAVE-----
  const handleOnSave = async (order) => {
    setLoading(true);


    try {
      await httpService.postWithAuth("/api/orders/project/save", order);
      alert("✅ Order saved!");
      reset();
    } catch (err) {
      alert("❌ Failed to Save Order");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

   // LOGOUT
  const handleLogout = () => {
    removeToken();
    router.push("/login");
  };

  return (
    <div className="min-h-screen bg-[#e3f3ff] flex justify-center items-center p-4 relative overflow-hidden">

      {/* 🔹 SAME USERNAME + LOGOUT WHITE BOX (GLOBAL STYLE) */}
      <div className="absolute top-5 right-6 flex items-center gap-5 bg-white shadow-md px-4 py-2 rounded-lg border border-[#cce7ff] z-50">
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

      {/* Logo */}
      <div className="absolute top-6 left-6 z-20">
        <img src="/cyanconnode-logo.png" alt="Logo" className="w-60 opacity-90" />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 30 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.6 }}
        className="relative bg-white shadow-2xl rounded-2xl w-full max-w-6xl p-8 mt-20"
      >
        <h1 className="text-3xl font-bold text-[#02A3EE] text-center mb-6 tracking-wide">
          ORDER INITIATION PANEL
        </h1>

        <form onSubmit={handleSubmit(handleOnSubmit)}>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-5">

            {/* Order Date */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">Expected Date</label>
              <input
                type="date"
                {...register("expectedOrderDate", { required: true })}
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              />
            </div>

            {/* Project */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">Project</label>
              <input
                type="text"
                {...register("project", { required: true })}
                placeholder="Enter Project Name"
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              />
            </div>

            {/* Product Type */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">Product Type</label>
              <input
                type="text"
                {...register("productType", { required: true })}
                placeholder="Enter Product Type"
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              />
            </div>

            {/* Quantity */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">Proposed Build Plan Qty</label>
              <input
                type="number"
                {...register("proposedBuildPlanQty", { required: true })}
                placeholder="Enter Quantity"
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              />
            </div>

            {/* Reason */}
            <div className="sm:col-span-2">
              <label className="block text-sm text-gray-600 mb-1">Reason for Build Request</label>
              <textarea
                rows="2"
                {...register("reasonForBuildRequest", { required: true })}
                placeholder="Write Reason..."
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              ></textarea>
            </div>

            {/* ORDER TYPE */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">Order Type</label>
              <select
                {...register("orderType", { required: true })}
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              >
                <option value="purchase">Purchase</option>
                <option value="free of cost">Free of Cost</option>
              </select>
            </div>

            {/* PMS Remarks */}
            <div className="sm:col-span-2">
              <label className="block text-sm text-gray-600 mb-1">PM's Remarks</label>
              <textarea
                rows="2"
                {...register("pmsRemarks", { required: true })}
                placeholder="Write PM's Remarks..."
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              ></textarea>
            </div>

          </div>

          {/* Buttons */}
          <div className="mt-8 flex justify-center gap-4">
            <button
              type="button"
              onClick={handleSubmit(handleOnSave)}
              className="px-8 py-3 bg-gray-500 hover:bg-gray-600 text-white rounded-lg shadow-lg"
            >
              Save
            </button>

            <button
              type="submit"
              disabled={loading}
              className={`px-8 py-3 rounded-lg text-white shadow-lg 
                ${loading ? "bg-[#8ad4f9]" : "bg-[#02A3EE] hover:bg-[#008ac5]"}`}
            >
              {loading ? "Submitting..." : "Submit"}
            </button>
          </div>
        </form>
      </motion.div>
    </div>
  );
}
