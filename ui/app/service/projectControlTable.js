"use client";

import { useState, useEffect } from "react";
import DataTable from "react-data-table-component";
import { useForm } from "react-hook-form";
import httpService from "./httpService";
import AlertPopup from "../../components/layout/AlertPopup";

export default function ProjectControlTable({ 
  orders,
  fetchData,
 }) {
  const [showPopup, setShowPopup] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [isEditMode, setIsEditMode] = useState(false);

  const [alertPopup, setAlertPopup] = useState({
  show: false,
  message: "",
  type: "success",
});

  /* ---------------- FORM ---------------- */
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm();

  /* ---------------- OPEN POPUP ---------------- */
  const openPopup = (row) => {
    setSelectedRow(row);
    setIsEditMode(false);
    reset({
      projectType: row.projectType,
      productType: row.productType,
    });
    setShowPopup(true);
  };

  /* ---------------- UPDATE ---------------- */
  const onUpdate = async (data) => {
    try {
      const res = await httpService.updateWithAuth(
        `/api/v1/admin/project-types/${selectedRow.id}`,
        data
      );

      setAlertPopup({
  show: true,
  message: res || "Product Updated",
  type: "success",
});
      setShowPopup(false);
      fetchData();
    } catch (err) {
      console.error(err);
      setAlertPopup({
  show: true,
  message: "Update Failed",
  type: "success",
});
    }
  };

  /* ---------------- DELETE ---------------- */
  const onDelete = async () => {
    try {

      const res = await httpService.deleteWithAuth(
        `/api/v1/admin/project-types/${selectedRow.id}`
      );

      setAlertPopup({
  show: true,
  message: res || "Product Deleted",
  type: "success",
});
      setShowPopup(false);
      fetchData();
    } catch (err) {
      console.error(err);
      setAlertPopup({
  show: true,
  message: "Delete failed",
  type: "success",
});
    }
  };

  /* ---------------- COLUMNS ---------------- */
  const columns = [
    {
      name: "ID",
      selector: (row) => row.id,
      cell: (row) => <b>{row.id}</b>,
    },
    {
      name: "PROJECT",
      selector: (row) => row.projectType,
    },
    {
      name: "CREATED BY",
      selector: (row) => row.createdBy?.username || "-",
    },
    {
      name: "ACTION",
      cell: (row) => (
        <button
          onClick={() => openPopup(row)}
          className="bg-[#02A3EE] text-white px-6 py-3 rounded-md"
        >
          Edit
        </button>
      ),
    },
  ];

  return (
    <>
      <div className="flex justify-end mb-2 text-black font-semibold">
        Total: {orders.length}
      </div>

      <DataTable
        columns={columns}
        data={orders}
        highlightOnHover
        fixedHeader
        fixedHeaderScrollHeight="500px"
        pagination
        customStyles={{
          headRow: {
            style: {
              backgroundColor: "#e8f3ff",
              fontWeight: "bold",
            },
          },
        }}
      />

      {/* ================= POPUP ================= */}
      {showPopup && (
        <>
          {/* BACKDROP */}
          <div
            className="fixed inset-0 bg-black/40 backdrop-blur-sm z-40"
            onClick={() => {
              reset();
              setShowPopup(false);
            }}
          />

          {/* MODAL */}
          <div className="fixed inset-0 z-50 flex items-center justify-center">
            <div className="bg-white w-[420px] rounded-xl shadow-2xl p-6">
              <h2 className="text-xl font-bold text-center text-[#02A3EE] mb-5">
                Project & Product Type
              </h2>

              <p className="text-gray-600 text-center mb-4">
                <b>Order ID:</b> <b>{selectedRow.id}</b>
              </p>

              <form onSubmit={handleSubmit(onUpdate)} className="space-y-4">
                {/* PROJECT */}
                <div>
                  <label className="block text-black font-semibold mb-1">
                    Project
                  </label>
                  <input
                    {...register("projectType", {
                      required: "Project is required",
                    })}
                    disabled={!isEditMode}
                    className="w-full px-3 py-2 border text-black rounded-md disabled:bg-gray-100"
                  />
                  {errors.projectType && (
                    <p className="text-red-500 text-xs">
                      {errors.projectType.message}
                    </p>
                  )}
                </div>

                

                {/* ACTION BUTTONS */}
                <div className="flex justify-end gap-3 pt-4">
                  {!isEditMode ? (
                    <>
                      <button
                        type="button"
                        onClick={() => {
                          reset();
                          setShowPopup(false);
                        }}
                        className="px-4 py-2 bg-red-500 text-white border rounded-md"
                      >
                        ✖
                      </button>

                      <button
                        type="button"
                        onClick={() => setIsEditMode(true)}
                        className="bg-yellow-500 text-white px-4 py-2 rounded-md"
                      >
                        Edit
                      </button>

                      <button
                        type="button"
                        onClick={onDelete}
                        className="bg-red-500 text-white px-4 py-2 rounded-md"
                      >
                        Delete
                      </button>
                    </>
                  ) : (
                    <button
                      type="submit"
                      className="bg-[#02A3EE] text-white px-4 py-2 rounded-md"
                    >
                      Update
                    </button>
                  )}
                </div>
              </form>
            </div>
          </div>
        </>
      )}
       <AlertPopup
      show={alertPopup.show}
      message={alertPopup.message}
      type={alertPopup.type}
      onClose={() => setAlertPopup({ ...alertPopup, show: false })}
    />
    </>
  );
}
