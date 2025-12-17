"use client";

import { useState } from "react";
import DataTable from "react-data-table-component";
import httpService from "../service/httpService";   // <-- ADD THIS IMPORT
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";


export default function CloudTable({
  orders,
  totalOrders,
  currentPage,
  setCurrentPage,
  ordersPerPage,
  setOrdersPerPage,
  filteredData,
  filteredCount,
  noDataFound,
  startDate,
  endDate,
  setStartDate,
  setEndDate,
  applyDateFilter,
  searchText,
  applySearchFilter,
  searchFilteredData,
  refreshData        // <-- OPTIONAL: agar parent se refresh bhejna ho
}) {

  

  // POPUP STATES
  const [popupOrderId, setPopupOrderId] = useState(null);
 

const highlightText = (text) => {
  if (!searchText || text === null || text === undefined) return text;

  // convert everything to string safely
  const safeText = String(text);

  const regex = new RegExp(`(${searchText})`, "gi");

  return safeText.replace(
    regex,
    `<span class="bg-yellow-300 text-black font-bold px-1 rounded">$1</span>`
  );
};

    // REACT HOOK FORM
  // -------------------------
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors }
  } = useForm({
    
    defaultValues: {
      jiraDescription: "",
      priority: "",
      cloudComments: ""
    }
  });

  

  // =============================
  //  POST API CALL ON SUBMIT
  // =============================

   const submitTicket = async (data) => {
    try {
      if (!popupOrderId) return;

      const res = await httpService.postWithAuth(
        `/api/orders/cloud/update-jira-details/${popupOrderId}`,
        data
      );

      alert("Cloud ticket submitted successfully!");

      reset();
      setPopupOrderId(null);
      if (refreshData) refreshData();

    } catch (err) {
      console.error(err);
      alert("Error submitting cloud ticket!");
    }
  };

  // ===========================================
  //                TABLE PART SAME
  // ===========================================

   // table data
  const displayedData =
  searchFilteredData?.length > 0
    ? searchFilteredData
    : filteredData?.length > 0
    ? filteredData
    : orders;

  const columns = [
    {
      name: "ORDER ID",
      selector: (row) => row.orderId,
      cell: (row) => <span className="font-bold">{row.orderId}</span>,
    },
    { name: "ORDER DATE", selector: (row) => row.createAt, sortable: true },
    { name: "PROJECT", selector: (row) => row.project,
          cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.project),
      }}
    />
  ),
     },
    { name: "INITIATOR", selector: (row) => row.users?.username || row.initiator },
    { name: "PRODUCT TYPE", selector: (row) => row.productType,
          cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.productType),
      }}
    />
  ),
     },
    { name: "QTY", selector: (row) => row.proposedBuildPlanQty },
    { name: "REASON", selector: (row) => row.reasonForBuildRequest },

    {
      name: "ACTION",
      width: "150px",
      cell: (row) => (
        <button
          onClick={() => setPopupOrderId(row.orderId)}
          className="px-5 py-2 bg-cyan-600 text-white rounded-lg"
        >
          Certificate Generated And Shared
        </button>
      ),
    },
  ];

  return (
    <>
      <div className="w-full">

       <div className="flex justify-end mb-2 px-1 font-semibold text-black">
  Total Orders:&nbsp;
  {noDataFound ? 0 : (filteredCount > 0 ? filteredCount : totalOrders)}
</div>

      <div className="flex justify-end gap-3 mb-3 text-black">
        <span className="font-bold">From:</span>
        <input
          type="date"
          value={startDate}
          onChange={(e) => setStartDate(e.target.value)}
          className="px-3 py-2 border rounded-md shadow-sm"
        />

        <span className="font-bold">To:</span>
        <input
          type="date"
          value={endDate}
          onChange={(e) => setEndDate(e.target.value)}
          className="px-3 py-2 border rounded-md shadow-sm"
        />

        <button
          onClick={() => {
            setCurrentPage(1);
            applyDateFilter();
          }}
          className="bg-cyan-500 text-white px-3 rounded-md"
        >
          Search
        </button>


        {/* LIVE SEARCH BACKEND CALL */}
        <div className="relative">
          <input
            type="text"
            placeholder="Search orders..."
            value={searchText}
            onChange={(e) => {
              setCurrentPage(1);
              applySearchFilter(e.target.value);
            }}
            className="px-4 py-2 border rounded-md w-64 shadow-sm pr-8"
          />

          {searchText && (
            <button
              onClick={() => applySearchFilter("")}
              className="absolute right-2 top-2 text-gray-500 hover:text-red-500 text-lg"
            >
              ✖
            </button>
          )}
        </div>
      </div>

        {/* TABLE */}
        <DataTable
          columns={columns}
          data={!noDataFound ? displayedData : []}
          highlightOnHover
          fixedHeader
          fixedHeaderScrollHeight="500px"
          pagination
          paginationServer
          paginationTotalRows={filteredCount > 0 ? filteredCount : totalOrders}
          paginationPerPage={ordersPerPage}
          paginationDefaultPage={currentPage}
          onChangePage={(page) => setCurrentPage(page)}
          onChangeRowsPerPage={(newPerPage) => {
            setOrdersPerPage(newPerPage);
            setCurrentPage(1);
          }}

           customStyles={{
          rows: {
            style: {
              borderBottom: "1px solid #e5e7eb",
              paddingTop: "12px",
              paddingBottom: "12px",
            },
          },
          headRow: {
            style: {
              backgroundColor: "#e8f3ff",   
              fontWeight: "bold",
              fontSize: "16px",
              borderBottom: "2px solid #c0d9ff",
            },
          },
          cells: {
            style: {
              borderRight: "1px solid #7eb8df",
              paddingLeft: "14px",
              paddingRight: "14px",
            },
          },
        }}
        />
      </div>

      {/* =============================
            POPUP UI
      ============================= */}
     {popupOrderId && (
        <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-start md:items-center z-50 overflow-auto">
          <div className="my-10 md:my-0 max-h-[90vh] overflow-auto">
            <div className="bg-white p-6 rounded-lg shadow-xl w-[800px] relative">

              <h2 className="text-2xl font-bold mb-3 text-cyan-700 text-center">
                GENERATE CERTIFICATE DETAILS
              </h2>

              <p className="text-gray-600 text-center mb-4">
                Order ID: <b>{popupOrderId}</b>
              </p>

              {/* FORM */}
              <form onSubmit={handleSubmit(submitTicket)} className="flex flex-col gap-3 text-black">

                {/* Jira Description */}
                <div>
                  <textarea
                    {...register("jiraDescription", { required: "Description is required" })}
                    placeholder="Jira Description"
                    className={`border p-2 rounded h-20 w-full ${errors.jiraDescription ? "border-red-500" : ""}`}
                  />
                  {errors.jiraDescription && (
                    <p className="text-red-500 text-sm">{errors.jiraDescription.message}</p>
                  )}
                </div>

                {/* Priority */}
                <div>
                  <input
                    {...register("priority", { required: "Priority is required" })}
                    placeholder="Priority"
                    className={`border p-2 rounded w-full ${errors.priority ? "border-red-500" : ""}`}
                  />
                  {errors.priority && (
                    <p className="text-red-500 text-sm">{errors.priority.message}</p>
                  )}
                </div>

                {/* Cloud Comments */}
                <div>
                  <textarea
                    {...register("cloudComments", { required: "Comments required" })}
                    placeholder="Cloud Comments"
                    className={`border p-2 rounded h-20 w-full ${errors.cloudComments ? "border-red-500" : ""}`}
                  />
                  {errors.cloudComments && (
                    <p className="text-red-500 text-sm">{errors.cloudComments.message}</p>
                  )}
                </div>

                {/* BUTTONS */}
                <div className="flex justify-center gap-4 mt-5">
                  <button
                    type="button"
                    onClick={() => {
                      reset();
                      setPopupOrderId(null);
                    }}
                    className="px-6 py-2 bg-red-500 text-white rounded-lg"
                  >
                    ✖
                  </button>

                  <button
                    type="submit"
                    className="px-6 py-2 bg-green-600 text-white rounded-lg"
                  >
                    Submit
                  </button>
                </div>
              </form>

            </div>
          </div>
        </div>
      )}

    </>
  );
}
