"use client";
import { useState, useEffect, useRef } from "react";
import { useForm } from "react-hook-form";
import httpService from "../service/httpService";
import { motion } from "framer-motion";
import { removeToken, getUsernameFromToken } from "../service/cookieService";
import AlertPopup from "../../components/layout/AlertPopup";


import { useRouter } from "next/navigation";

export default function OrderInitiation() {
  const [loading, setLoading] = useState(false);
  const [username, setUsername] = useState("");
  const today = new Date().toISOString().split("T")[0];

  const [projects, setProjects] = useState([]);
const [products, setProducts] = useState([]);
const [projectLoaded, setProjectLoaded] = useState(false);
const [productLoaded, setProductLoaded] = useState(false);

const [selectedProducts, setSelectedProducts] = useState([]);
const [showProductDropdown, setShowProductDropdown] = useState(false);
const productDropdownRef = useRef(null);


const [alertPopup, setAlertPopup] = useState({
  show: false,
  message: "",
  type: "success",
});

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

 const payload = {
  ...order,
  products: selectedProducts.map((p) => ({
    productName: p.productName,
    quantity: Number(p.quantity),
  })),
};


  try {
    const res = await httpService.postWithAuth(
      "/api/v1/orders/project/create",
      payload
    );

    setAlertPopup({
      show: true,
      message: res || "Order submitted successfully!",
      type: "success",
    });

    reset();
    setSelectedProducts([]);
  } catch (err) {
    setAlertPopup({
      show: true,
      message: "❌ Failed to Submit Order",
      type: "error",
    });
  } finally {
    setLoading(false);
  }
};


  //  -----SAVE-----
  const handleOnSave = async (order) => {
  setLoading(true);

 const payload = {
  ...order,
  products: selectedProducts.map((p) => ({
    productName: p.productName,
    quantity: Number(p.quantity),
  })),
};


  try {
    const res = await httpService.postWithAuth(
      "/api/v1/orders/project/save",
      payload
    );

    setAlertPopup({
      show: true,
      message: res || "Order saved successfully!",
      type: "success",
    });

    reset();
    setSelectedProducts([]);
  } catch (err) {
    setAlertPopup({
      show: true,
      message: "❌ Failed to saved Order",
      type: "error",
    });
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

useEffect(() => {
  fetchProducts();
}, []);

useEffect(() => {
  const handleClickOutside = (event) => {
    if (
      productDropdownRef.current &&
      !productDropdownRef.current.contains(event.target)
    ) {
      setShowProductDropdown(false);
    }
  };

  document.addEventListener("mousedown", handleClickOutside);
  return () => {
    document.removeEventListener("mousedown", handleClickOutside);
  };
}, []);




 const capitalizeWords = (text = "") =>
  text
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());

  

  // LOGOUT
  const handleLogout = () => {
    removeToken();
    router.push("/");
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

           {/* Product Type (Multi Select) */}
<div 
ref={productDropdownRef}
className="relative sm:col-span-1">
  <label className="block text-sm text-gray-600 mb-1">
    Product Type
  </label>

  {/* Dropdown button */}
  <div
  onClick={() => setShowProductDropdown((prev) => !prev)}
    className="w-full border border-gray-300 rounded-lg p-2 bg-white cursor-pointer flex justify-between items-center"
  >
    <span className="text-gray-700">
      {selectedProducts.length > 0
        ? `${selectedProducts.length} product(s) selected`
        : "Select Product"}
    </span>
    <span className="text-gray-500">▼</span>
  </div>

  {/* Dropdown list */}
  {showProductDropdown && (
    <div
      className="absolute z-50 mt-1 w-full bg-white border border-gray-300 rounded-lg shadow max-h-52 overflow-auto"
      onClick={(e) => e.stopPropagation()}
    >
      {products.length === 0 && (
        <p className="p-3 text-sm text-gray-500">
          No products available
        </p>
      )}

      {products.map((product) => {
        const checked = selectedProducts.some(
          (p) => p.productName === product.productType
        );

        return (
          <label
            key={product.id}
            className="flex items-center gap-2 px-3 py-2 hover:bg-[#f0f8ff] cursor-pointer"
          >
            <input
              type="checkbox"
              checked={checked}
              onChange={() => {
                setSelectedProducts((prev) => {
                  if (checked) {
                    return prev.filter(
                      (p) => p.productName !== product.productType
                    );
                  }
                  return [
                    ...prev,
                    {
                      productName: product.productType,
                      quantity: "",
                    },
                  ];
                });
              }}
            />
            <span className="text-gray-800">
              {product.productType}
            </span>
          </label>
        );
      })}
    </div>
  )}
</div>



           {/* Selected Products with Quantity */}
{selectedProducts.length > 0 && (
  <div className="sm:col-span-2 mt-4">
    <h3 className="text-sm font-semibold mb-2 text-gray-700">
      Selected Products
    </h3>

    <div className="space-y-2">
      {selectedProducts.map((item, index) => {
        const qtyInvalid =
          !item.quantity || Number(item.quantity) <= 0;

        return (
         <div
  key={item.productName}
  className="bg-[#f5faff] border border-[#cce7ff] rounded-lg overflow-hidden"
>
  <div className="grid grid-cols-2">
    {/* LEFT HALF : PRODUCT */}
    <div className="flex items-center gap-3 p-4 border-r border-[#cce7ff]">
      <span className="w-120 px-4 py-1 border rounded-lg  text-black text-sm font-semibold">
        {item.productName}
      </span>
    </div>

    {/* RIGHT HALF : QTY */}
    <div className="flex items-center justify-between p-4">
      <div>
        <input
          type="number"
          min={1}
          placeholder="Qty"
          value={item.quantity}
          onKeyDown={(e) => {
            if (["e", "E", "+", "-"].includes(e.key)) {
              e.preventDefault();
            }
          }}
          onChange={(e) => {
            const qty = e.target.value;
            setSelectedProducts((prev) =>
              prev.map((p, i) =>
                i === index ? { ...p, quantity: qty } : p
              )
            );
          }}
          className={`w-120 border rounded-lg p-2 text-black ${
            qtyInvalid ? "border-red-500" : "border-gray-300"
          }`}
        />

        {qtyInvalid && (
          <p className="text-xs text-red-500 mt-1">
            Qty required
          </p>
        )}
      </div>

      {/* REMOVE */}
      <button
        type="button"
        onClick={() =>
          setSelectedProducts((prev) =>
            prev.filter((_, i) => i !== index)
          )
        }
        className="text-red-500 font-bold text-xl"
      >
        ✕
      </button>
    </div>
  </div>
</div>


        );
      })}
    </div>
  </div>
)}



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

      <AlertPopup
      show={alertPopup.show}
      message={alertPopup.message}
      type={alertPopup.type}
      onClose={() => setAlertPopup({ ...alertPopup, show: false })}
    />
    </div>
  );
}
