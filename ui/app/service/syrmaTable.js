"use client";

import { useState } from "react";
import DataTable from "react-data-table-component";
import httpService from "../service/httpService";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";


export default function SyrmaTable({
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
  setSearchText,
  setSearchFilteredData,
  isStatusApplied,
  SetIsStatusApplied,
  statusFilter,

  applyStatusFilter,
  refreshData
}) {

  

  // POPUP STATE
  const [popupOrderId, setPopupOrderId] = useState(null);
  const [syrmaComment, setSyrmaComment] = useState("");
  const [actionType, setActionType] = useState("");
   
  // VALIDATION
  const syrmaSchema = yup.object().shape({
  syrmaComments: yup.string().required("Syrma Comment is required"),
});

// REACT HOOK FORM
const {
  register,
  handleSubmit,
  reset,
  formState: { errors }
} = useForm({
  resolver: yupResolver(syrmaSchema),
  defaultValues: {
    syrmaComments: ""
  }
});


  const submitCompletion = async (data) => {
  try {
    if (!popupOrderId) return;

    const body = {
      syrmaComments: data.syrmaComments
    };

    let apiUrl = "";
    let method = "";

    if (actionType === "RE_COMPLETE") {
      apiUrl = `/api/orders/syrma/re-production-testing/${popupOrderId}`;
      method = "put";
    } else {
      apiUrl = `/api/orders/syrma/production-testing/${popupOrderId}`;
      method = "post";
    }

    if (method === "post") {
      await httpService.postWithAuth(apiUrl, body);
    } else {
      await httpService.updateWithAuth(apiUrl, body);
    }

    alert(
      actionType === "RE_COMPLETE"
        ? "Syrma Re-Completion successfully updated!"
        : "Syrma completion successfully updated!"
    );

    reset();           // FORM RESET
    setPopupOrderId(null);
    setActionType("");

    if (refreshData) refreshData();

  } catch (err) {
    console.error("API ERROR:", err);
    alert("Error while submitting completion!");
  }
};




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
    { name: "ORDER DATE", selector: (row) => row.createAt },
    { name: "PROJECT", selector: (row) => row.project },
    { name: "INITIATOR", selector: (row) => row.users?.username || row.initiator },
    { name: "PRODUCT TYPE", selector: (row) => row.productType },
    { name: "QTY", selector: (row) => row.proposedBuildPlanQty },
    { name: "REASON", selector: (row) => row.reasonForBuildRequest },
    { name: "STATUS", selector: (row) => row.status,
       grow: 1.5,
  cell: (row) => (
    <span className="font-bold">
      {row.status}
    </span>
  ),
     },

    {
  name: "ACTION",
  width: "150px",
  cell: (row) => {

    if (row.status === "RMA QC FAIL > SYRMA RE-PROD/TEST PENDING") {
      return (
        <button
          onClick={() => {
            setPopupOrderId(row.orderId);
            setActionType("RE_COMPLETE");   // ✅
          }}
          className="px-5 py-2 bg-yellow-600 text-white rounded-lg"
        >
          Re-PROD/TEST Done
        </button>
      );
    }

    return (
      <button
        onClick={() => {
          setPopupOrderId(row.orderId);
          setActionType("COMPLETE");       // ✅
        }}
        className="px-5 py-2 bg-green-600 text-white rounded-lg"
      >
        PROD/TEST Done
      </button>
    );
  },
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

        {/* STATUS FILTER */}
        <select
          value={statusFilter}
          onChange={(e) => {
            setCurrentPage(1);
            applyStatusFilter(e.target.value);
          }}
          className="px-3 py-2 border rounded-md shadow-sm"
        >
          <option value="">Status</option>
          <option value="SCM JIRA TICKET CLOSURE > SYRMA PENDING">SCM JIRA TICKET CLOSURE {'>'} SYRMA PENDING</option>
          <option value="RMA QC FAIL > SYRMA RE-PROD/TEST PENDING">RMA QC FAIL {'>'} SYRMA RE-PROD/TEST PENDING</option>
        </select>

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

      {/* POPUP */}
     {popupOrderId && (
  <div className="fixed inset-0 bg-black/40 flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-xl w-[600px]">

      <h2 className="text-2xl font-bold mb-3 text-green-700 text-center">
        COMPLETE ORDER
      </h2>

      <p className="text-center mb-3 text-black">
        Order ID: <b>{popupOrderId}</b>
      </p>

      {/* FORM START */}
      <form onSubmit={handleSubmit(submitCompletion)} className="flex flex-col gap-3">

        {/* Textarea + error */}
        <div>
          <textarea
            {...register("syrmaComments")}
            placeholder="SYRMA Comment"
            className={`border p-2 rounded h-24 w-full text-black ${
              errors.syrmaComments ? "border-red-500" : ""
            }`}
          />
          {errors.syrmaComments && (
            <p className="text-red-500 text-sm">{errors.syrmaComments.message}</p>
          )}
        </div>

        {/* Buttons */}
        <div className="flex justify-center gap-4 mt-4">

          <button
            type="button"
            onClick={() => {
              reset();             // clear form
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
      {/* FORM END */}

    </div>
  </div>
)}

    </>
  );
}
