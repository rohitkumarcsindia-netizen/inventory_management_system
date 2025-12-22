"use client";
import { useState, useEffect } from "react";
import { useForm } from "react-hook-form";
import httpService from "../service/httpService";
import { motion } from "framer-motion";
import { removeToken, getUsernameFromToken } from "../service/cookieService";

import { useRouter } from "next/navigation";

export default function OrderInitiation() {
  const [loading, setLoading] = useState(false);
  const [username, setUsername] = useState("");
  const today = new Date().toISOString().split("T")[0];

  const [projects, setProjects] = useState([]);
const [products, setProducts] = useState([]);
const [projectLoaded, setProjectLoaded] = useState(false);
const [productLoaded, setProductLoaded] = useState(false);


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
      const res = await httpService.postWithAuth("/api/v1/orders/project/create", order);
      alert(res);
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
      const res = await httpService.postWithAuth("/api/v1/orders/project/save", order);
      alert(res);
      reset();
    } catch (err) {
      alert("❌ Failed to Save Order");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

const fetchProjects = async () => {
  if (projectLoaded) return;

  try {
    const res = await httpService.get(
      "/api/v1/admin/project-types"
    );

    setProjects(res); // assume array of strings
    setProjectLoaded(true);
  } catch (err) {
    console.error("Failed to load projects", err);
  }
};

const fetchProducts = async () => {
  if (productLoaded) return;

  try {
    const res = await httpService.get(
      "/api/v1/admin/product-types"
    );

    setProducts(res); // assume array of strings
    setProductLoaded(true);
  } catch (err) {
    console.error("Failed to load products", err);
  }
};


 const capitalizeWords = (text = "") =>
  text
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());

  

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
        <img
          src="/cyanconnode-logo.png"
          alt="Logo"
          className="w-60 opacity-90"
        />
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
              <label className="block text-sm text-gray-600 mb-1">
                Expected Date
              </label>
              <input
                type="date"
                min={today}
                {...register("expectedOrderDate", {
                  required: "Expected date is required",
                  validate: (v) => v >= today || "Past date is not allowed",
                })}
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              />
              {errors.expectedOrderDate && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.expectedOrderDate.message}
                </p>
              )}
            </div>

            {/* Project */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">
                Project
              </label>
           <select
  {...register("project", { required: "Project is required" })}
  onFocus={fetchProjects}
  className="w-full border border-gray-300 rounded-lg p-2 text-black"
>
  <option value="">Select Project</option>

  {projects.map((project) => (
    <option key={project.id} value={project.projectType}>
      {project.projectType}
    </option>
  ))}
</select>



              {errors.project && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.project.message}
                </p>
              )}
            </div>

            {/* Product Type */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">
                Product Type
              </label>
          <select
  {...register("productType", {
    required: "Product type is required",
  })}
  onFocus={fetchProducts}
  className="w-full border border-gray-300 rounded-lg p-2 text-black"
>
  <option value="">Select Product</option>

  {products.map((product) => (
    <option key={product.id} value={product.productType}>
      {product.productType}
    </option>
  ))}
</select>



              {errors.productType && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.productType.message}
                </p>
              )}
            </div>

            {/* Quantity */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">
                Proposed Build Plan Qty
              </label>
              <input
                type="number"
                {...register("proposedBuildPlanQty", {
                  required: "Quantity is required",
                  min: { value: 1, message: "Quantity must be at least 1" },
                })}
                 onKeyDown={(e) => {
    if (["e", "E", "+", "-"].includes(e.key)) {
      e.preventDefault();
    }
  }}
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              />
              {errors.proposedBuildPlanQty && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.proposedBuildPlanQty.message}
                </p>
              )}
            </div>

            {/* Reason */}
            <div className="sm:col-span-2">
              <label className="block text-sm text-gray-600 mb-1">
                Reason for Build Request
              </label>
              <textarea
                rows="2"
                {...register("reasonForBuildRequest", {
                  required: "Reason is required",
                  setValueAs: (value) => capitalizeWords(value),
                  minLength: {
                    value: 10,
                    message: "Minimum 10 characters required",
                  },
                })}
                className="capitalize w-full border border-gray-300 rounded-lg p-2 text-black"
              />
              {errors.reasonForBuildRequest && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.reasonForBuildRequest.message}
                </p>
              )}
            </div>

            {/* ORDER TYPE */}
            <div>
              <label className="block text-sm text-gray-600 mb-1">
                Order Type
              </label>
              <select
                {...register("orderType", {
                  required: "Order type is required",
                  setValueAs: (value) => capitalizeWords(value),
                })}
                className="w-full border border-gray-300 rounded-lg p-2 text-black"
              >
                <option value="">Select Order Type</option>
                <option value="purchase">Purchase</option>
                <option value="free of cost">Free of Cost</option>
              </select>
              {errors.orderType && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.orderType.message}
                </p>
              )}
            </div>

            {/* PMS Remarks */}
            <div className="sm:col-span-2">
              <label className="block text-sm text-gray-600 mb-1">
                PM's Remarks
              </label>
              <textarea
                rows="2"
                {...register("pmsRemarks", {
                  required: "PM remarks are required",
                  setValueAs: (value) => capitalizeWords(value),
                  minLength: {
                    value: 5,
                    message: "Minimum 5 characters required",
                  },
                })}
                className="capitalize w-full border border-gray-300 rounded-lg p-2 text-black"
              />
              {errors.pmsRemarks && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.pmsRemarks.message}
                </p>
              )}
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
                ${
                  loading ? "bg-[#8ad4f9]" : "bg-[#02A3EE] hover:bg-[#008ac5]"
                }`}
            >
              {loading ? "Submitting..." : "Submit"}
            </button>
          </div>
        </form>
      </motion.div>
    </div>
  );
}
