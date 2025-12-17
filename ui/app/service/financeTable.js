"use client";

import { useState } from "react";
import DataTable from "react-data-table-component";
import httpService from "../service/httpService";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";


export default function FinanceTable({
  orders,
  totalOrders,
  currentPage,
  setCurrentPage,
  ordersPerPage,
  setOrdersPerPage,
  fetchOrders,
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
  statusFilter,

  applyStatusFilter,
}) {
  const [showPopup, setShowPopup] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [actionType, setActionType] = useState("");

  // 🔥 FINAL APPROVAL POPUP STATES
  const [showFinalPopup, setShowFinalPopup] = useState(false);
  const [finalRemark, setFinalRemark] = useState("");
  const [financeRemark, setFinanceRemark] = useState("");
  const [finalOrderId, setFinalOrderId] = useState(null);

  // 🔥 NEW CLOSURE POPUP STATES
  const [showClosurePopup, setShowClosurePopup] = useState(false);
  const [closureOrderId, setClosureOrderId] = useState(null);

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

  // open approve / reject popup
  const openPopup = (type, id) => {
    setActionType(type);
    setSelectedId(id);
    setShowPopup(true);
  };

  // FINAL REMARK open popup
const openFinalPopup = (type, id) => {
  setActionType(type);        // APPROVE or REJECT
  setFinalOrderId(id);
  setFinalRemark("");
  setFinanceRemark("");
  setShowFinalPopup(true);
};

// APPROVE / REJECT VALIDATION
const reasonSchema = yup.object().shape({
  reason: yup.string().required("Reason is required"),
});

const {
  register: registerReason,
  handleSubmit: handleReasonSubmit,
  reset: resetReason,
  formState: { errors: reasonErrors },
} = useForm({
  resolver: yupResolver(reasonSchema),
});


// FINAL REMARK VALIDATION
const finalSchema = yup.object().shape({
  financeRemark: yup.string().required("Finance Remark is required"),
  finalRemark: yup.string().required("Final Remark is required"),
});

const {
  register: registerFinal,
  handleSubmit: handleFinalSubmit,
  reset: resetFinal,
  formState: { errors: finalErrors },
} = useForm({
  resolver: yupResolver(finalSchema),
});


// CLOSURE VALIDATION
const closureSchema = yup.object().shape({
  financeApprovalDocumentUrl: yup.string().required("Document URL is required"),
  financeClosureStatus: yup.string().required("Closure Status is required"),
});

const {
  register: registerClosure,
  handleSubmit: handleClosureSubmit,
  reset: resetClosure,
  formState: { errors: closureErrors },
} = useForm({
  resolver: yupResolver(closureSchema),
});



  // APPROVE/REJECT Submit
 const submitApproveReject = (data) => {
  if (actionType === "approve") approveOrder(selectedId, data.reason);
  else rejectOrder(selectedId, data.reason);

  resetReason();
  setShowPopup(false);
};


  // APPROVE ORDER
    const approveOrder = async (orderId, reason) => {
      try {
        const res = await httpService.postWithAuth(`/api/orders/finance/approve/${orderId}`, { financeReason: reason });
        alert(res);
        fetchOrders();
      } catch (err) {
        console.log("Approve Error:", err);
      }
    };
  
    // REJECT ORDER
    const rejectOrder = async (orderId, reason) => {
      try {
        const res = await httpService.postWithAuth(`/api/orders/finance/reject/${orderId}`, { financeReason: reason });
        alert(res);
        fetchOrders();
      } catch (err) {
        console.log("Reject Error:", err);
      }
    };

  // FINAL APPROVE API
  const submitFinalApprove = async () => {
    if (!finalRemark.trim()) return alert("Please enter final remark!");
    if (!financeRemark.trim()) return alert("Please enter finance remark!");

    try {
      const res = await httpService.updateWithAuth(
        `/api/orders/finance/final/approve/${finalOrderId}`,
        {
          financeFinalRemark: finalRemark,
          financeReason: financeRemark,
        }
      );
      alert(res);
      setShowFinalPopup(false);
      fetchOrders();
    } catch (err) {
      console.log("Final Approve Error:", err);
    }
  };

  // FINAL REJECT API
 const submitFinal = async (data) => {
  try {
    const payload = {
      financeFinalRemark: data.finalRemark,
      financeReason: data.financeRemark,
    };

    let res;

    if (actionType === "APPROVE") {
      res = await httpService.updateWithAuth(
        `/api/orders/finance/final/approve/${finalOrderId}`,
        payload
      );
    } else {
      res = await httpService.updateWithAuth(
        `/api/orders/finance/final/reject/${finalOrderId}`,
        payload
      );
    }

    alert(res);
    resetFinal();
    setShowFinalPopup(false);
    fetchOrders();
  } catch (err) {
    console.log("Final Approval Error:", err);
  }
};


  // 🔥 NEW — Closure API Call
  const submitClosureForm = async (data) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/orders/finance/closure-document/${closureOrderId}`,
      data
    );

    alert(res);
    resetClosure();
    setShowClosurePopup(false);
    fetchOrders();
  } catch (err) {
    console.log("Closure Error:", err);
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
    { name: "REASON", selector: (row) => row.reasonForBuildRequest },
    { name: "QTY", selector: (row) => row.proposedBuildPlanQty },
    { name: "STATUS", selector: (row) => row.status,
      grow: 1.5,
  cell: (row) => (
    <span className="font-bold">
      {row.status}
    </span>
  ),
     },

    {
      name: "ACTIONS",
      width: "210px",
      cell: (row) => {

        // POST APPROVAL
        if (row.status === "SCM > FINANCE POST APPROVAL PENDING") {
          return (
            <div className="flex gap-3">
  <button
    onClick={() => openFinalPopup("APPROVE", row.orderId)}
    className="px-5 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
  >
    Approve
  </button>

  <button
    onClick={() => openFinalPopup("REJECT", row.orderId)}
    className="px-5 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
  >
    Reject
  </button>
</div>

          );
        }

        // 🔥 NEW Closure case
        if (row.status === "LOGISTIC > FINANCE CLOSURE PENDING") {
          return (
            <button
              className="px-5 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700"
              onClick={() => {
                setClosureOrderId(row.orderId);
                setShowClosurePopup(true);
              }}
            >
              Document Closure
            </button>
          );
        }

        // Default Approve/Reject
        return (
          <div className="flex gap-3">
            <button
              onClick={() => openPopup("approve", row.orderId)}
              className="px-5 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600"
            >
              Approve
            </button>
            <button
              onClick={() => openPopup("reject", row.orderId)}
              className="px-5 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600"
            >
              Reject
            </button>
          </div>
        );
      },
    },
  ];

  return (
    <div className="w-full relative text-black">
      
      <div className="flex justify-end mb-2 px-1 font-semibold text-black">
  Total Orders:&nbsp;
  {noDataFound ? 0 : (filteredCount > 0 ? filteredCount : totalOrders)}
</div>
      
      {/* {DATE} */}
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
          <option value="PROJECT TEAM > FINANCE PRE APPROVAL PENDING">PROJECT TEAM {'>'} FINANCE PRE APPROVAL PENDING</option>
          <option value="SCM > FINANCE POST APPROVAL PENDING">SCM {'>'} FINANCE POST APPROVAL PENDING</option>
          <option value="LOGISTIC > FINANCE CLOSURE PENDING">SCM {'>'} LOGISTIC {'>'} FINANCE CLOSURE PENDING</option>
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

      {/* APPROVE / REJECT POPUP */}
      {showPopup && (
  <div className="fixed inset-0 bg-black/40 flex justify-center items-center backdrop-blur-sm z-[9999]">
    <div className="bg-white shadow-2xl p-8 rounded-2xl border w-[450px]">

      <h2 className="text-2xl font-bold mb-4 text-center">
        {actionType === "approve" ? "Approve Reason" : "Reject Reason"}
      </h2>

      <p className="text-center font-semibold mb-4">
        Order ID: {selectedId}
      </p>

      <form onSubmit={handleReasonSubmit(submitApproveReject)}>
        
        <input
          type="text"
          placeholder="Enter Reason"
          {...registerReason("reason")}
          className={`w-full border px-4 py-3 rounded-lg text-black ${
            reasonErrors.reason ? "border-red-500" : ""
          }`}
        />

        {reasonErrors.reason && (
          <p className="text-red-500 text-sm mt-1">{reasonErrors.reason.message}</p>
        )}

        <div className="flex justify-between mt-6">
          <button
            type="button"
            onClick={() => {
              resetReason();
              setShowPopup(false);
            }}
            className="px-6 py-2 bg-gray-500 text-white rounded-lg"
          >
            ✖
          </button>

          <button
            type="submit"
            className={`px-6 py-2 text-white rounded-lg ${
              actionType === "approve" ? "bg-green-500" : "bg-red-500"
            }`}
          >
            Submit
          </button>
        </div>
      </form>

    </div>
  </div>
)}


      {/* FINAL REMARK POPUP */}
    {showFinalPopup && (
  <div className="fixed inset-0 bg-black/40 flex justify-center items-center backdrop-blur-sm z-[9999]">
    <div className="bg-white shadow-2xl p-8 rounded-2xl w-[450px]">

      <h2 className="text-2xl font-bold text-center mb-4">
        {actionType === "APPROVE" ? "Final Approve" : "Final Reject"}
      </h2>

      <p className="text-center font-semibold mb-3">
        Order ID: {finalOrderId}
      </p>

      <form onSubmit={handleFinalSubmit(submitFinal)}>

        <textarea
          placeholder="Enter Finance Reason"
          {...registerFinal("financeRemark")}
          className={`w-full border px-3 py-2 rounded-lg text-black h-20 ${
            finalErrors.financeRemark ? "border-red-500" : ""
          }`}
        />
        {finalErrors.financeRemark && (
          <p className="text-red-500 text-sm mt-1">{finalErrors.financeRemark.message}</p>
        )}

        <textarea
          placeholder="Enter Final Remark"
          {...registerFinal("finalRemark")}
          className={`w-full border px-3 py-2 rounded-lg text-black h-20 ${
            finalErrors.finalRemark ? "border-red-500" : ""
          }`}
        />
        {finalErrors.finalRemark && (
          <p className="text-red-500 text-sm mt-1">{finalErrors.finalRemark.message}</p>
        )}

        <div className="flex justify-between mt-6">

             <button
            type="button"
            onClick={() => {
              resetFinal();
              setShowFinalPopup(false);
            }}
            className="px-6 py-2 bg-gray-500 text-white rounded-lg"
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
)}



      {/*NEW CLOSURE POPUP */}
      {showClosurePopup && (
  <div className="fixed inset-0 bg-black/40 flex justify-center items-center backdrop-blur-sm z-[9999]">
    <div className="bg-white shadow-2xl p-8 rounded-2xl w-[460px]">

      <h2 className="text-2xl font-bold mb-2 text-center">DOCUMENT CLOSURE</h2>

      <p className="text-center font-semibold mb-4">
        Order ID: {closureOrderId}
      </p>

      <form onSubmit={handleClosureSubmit(submitClosureForm)}>

        <label className="text-sm font-semibold">Finance Document URL</label>
        <input
          type="text"
          {...registerClosure("financeApprovalDocumentUrl")}
          className={`border px-3 py-2 rounded-lg w-full ${
            closureErrors.financeApprovalDocumentUrl ? "border-red-500" : ""
          }`}
        />
        {closureErrors.financeApprovalDocumentUrl && (
          <p className="text-red-500 text-sm">{closureErrors.financeApprovalDocumentUrl.message}</p>
        )}

        <label className="text-sm font-semibold mt-3">Closure Status</label>
        <input
          type="text"
          {...registerClosure("financeClosureStatus")}
          className={`border px-3 py-2 rounded-lg w-full ${
            closureErrors.financeClosureStatus ? "border-red-500" : ""
          }`}
        />
        {closureErrors.financeClosureStatus && (
          <p className="text-red-500 text-sm">{closureErrors.financeClosureStatus.message}</p>
        )}

        <div className="flex justify-between mt-6">
         
          <button
            type="button"
            onClick={() => {
              resetClosure();
              setShowClosurePopup(false);
            }}
            className="px-6 py-2 bg-gray-500 text-white rounded-lg"
          >
            ✖
          </button>

           <button
            type="submit"
            className="px-6 py-2 bg-green-500 text-white rounded-lg"
          >
            Submit
          </button>

        </div>
      </form>

    </div>
  </div>
)}


    </div>
  );
}
