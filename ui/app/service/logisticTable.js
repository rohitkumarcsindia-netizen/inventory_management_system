"use client";

import { useState } from "react";
import DataTable from "react-data-table-component";
import httpService from "../service/httpService";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

export default function LogisticTable({
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
  

  // Shipping Popup State
  const [shippingPopupId, setShippingPopupId] = useState(null);

  // Shipping Data
  const [shippingDetails, setShippingDetails] = useState({
    dispatchDate: "",
    shippingMode: "",
    logisticsComment: "",
    deliveredStatus: "",
    courierName: "",
    serialNumbers: "",
    trackingNumber: "",
    expectedDeliveryDate: "",
    shipmentDocumentUrl: "",
  });

  // NEW Delivered Popup State
  const [deliveredPopupId, setDeliveredPopupId] = useState(null);

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

  // ---------- NEW: PDI Popup ----------
  const [pdiPopup, setPdiPopup] = useState({
    id: null,
    type: null, // pass / fail
  });







// SHIPPING VALIDATION 
const shippingSchema = yup.object().shape({
  dispatchDate: yup.string().required("Dispatch Date is required"),
  shippingMode: yup.string().required("Shipping mode is required"),
  logisticsComment: yup.string().required("Logistics comment is required"),
  deliveredStatus: yup.string().required("Delivered status is required"),
  courierName: yup.string().required("Courier name is required"),
  serialNumbers: yup.string().required("Serial numbers are required"),
  trackingNumber: yup.string().required("Tracking number is required"),
  expectedDeliveryDate: yup.string().required("Expected delivery date is required"),
  shipmentDocumentUrl: yup.string().required("Document URL is required"),
});

const {
  register: registerShipping,
  handleSubmit: handleShippingSubmit,
  reset: resetShipping,
  formState: { errors: shippingErrors },
} = useForm({
  resolver: yupResolver(shippingSchema),
});

const shippingLabels = {
  dispatchDate: "Dispatch Date",
  shippingMode: "Shipping Mode",
  logisticsComment: "Logistics Comment",
  deliveredStatus: "Delivered Status",
  courierName: "Courier Name",
  serialNumbers: "Serial Numbers",
  trackingNumber: "Tracking Number",
  expectedDeliveryDate: "Expected Delivery Date",
  shipmentDocumentUrl: "Shipment Document URL",
};

//  DELIVERED VALIDATION
const deliveredSchema = yup.object().shape({
  deliveredStatus: yup.string().required("Delivered status is required"),
  logisticsComment: yup.string().required("Logistics comment is required"),
  actualDeliveryDate: yup.string().required("Delivery date is required"),
});
 
const {
  register: registerDelivered,
  handleSubmit: handleDeliveredSubmit,
  reset: resetDelivered,
  formState: { errors: deliveredErrors },
} = useForm({
  resolver: yupResolver(deliveredSchema),
});

// PDI VALIDATION
const pdiSchema = yup.object().shape({
  logisticsPdiComment: yup.string().required("Comment is required"),
});

const {
  register: registerPdi,
  handleSubmit: handlePdiSubmit,
  reset: resetPdi,
  formState: { errors: pdiErrors },
} = useForm({
  resolver: yupResolver(pdiSchema),
});


  // Submit Shipping
  const submitShipping = async (data) => {
  try {
    if (!shippingPopupId) return;

    const res = await httpService.postWithAuth(
      `/api/v1/orders/logistic/shipping-details/${shippingPopupId}`,
      data
    );

    alert(res?.message || "Shipping details submitted!");

    resetShipping();
    setShippingPopupId(null);
    fetchOrders();

  } catch (err) {
    console.error("Shipping API ERROR:", err);
    alert("Failed to submit shipping details!");
  }
};

  // Submit Delivered
 const submitDelivered = async (data) => {
  try {
    if (!deliveredPopupId) return;

    const res = await httpService.updateWithAuth(
      `/api/v1/orders/logistic/delivery-details/${deliveredPopupId}`,
      data
    );

    alert(res?.message || "Delivery updated!");

    resetDelivered();
    setDeliveredPopupId(null);
    fetchOrders();

  } catch (err) {
    console.error("Delivered API ERROR:", err);
    alert("Failed to update delivery!");
  }
};


  // ---------- NEW: Submit PDI API ----------
  const submitPDI = async (data) => {
  try {
    if (!pdiPopup.id || !pdiPopup.type) return;

    const endpoint =
      pdiPopup.type === "pass"
        ? `/api/v1/orders/logistic/pdi-pass/${pdiPopup.id}`
        : `/api/v1/orders/logistic/pdi-fail/${pdiPopup.id}`;

    const res = await httpService.updateWithAuth(endpoint, data);

    alert(res?.message || "PDI updated successfully!");

    resetPdi();
    setPdiPopup({ id: null, type: null });
    fetchOrders();

  } catch (err) {
    console.error("PDI API ERROR:", err);
    alert("Failed to update PDI!");
  }
};


      // table data
  const displayedData =
  searchFilteredData?.length > 0
    ? searchFilteredData
    : filteredData?.length > 0
    ? filteredData
    : orders;

    const formatOrderDateTime = (dateString) => {
  if (!dateString) return { date: "-", time: "-" };

  const date = new Date(dateString);

  // DATE PART → 19-Dec-25
  const day = String(date.getDate()).padStart(2, "0");
  const month = date.toLocaleString("en-US", { month: "short" });
  const year = String(date.getFullYear()).slice(-2);

  // TIME PART → 12:34 PM
  const time = date.toLocaleString("en-US", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: true,
  });

  return {
    date: `${day}-${month}-${year}`,
    time,
  };
};

  // Table Columns
  const columns = [
    {
      name: "ORDER ID",
      selector: (row) => row.orderId,
      cell: (row) => <span className="font-bold">{row.orderId}</span>,
    },
    { name: "ORDER DATE", selector: (row) => row.createAt, sortable: true,
      cell: (row) => {
    const { date, time } = formatOrderDateTime(row.createAt);

    return (
      <div className="leading-tight">
        <div className="font-medium text-black">
          {date}
        </div>
        <div className="text-xs text-gray-500">
          {time}
        </div>
      </div>
    );
  },
     },
    { name: "PROJECT", selector: (row) => row.project,
             cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.project),
      }}
    />
  ),
     },
    {
      name: "INITIATOR",
      selector: (row) => row.users?.username || row.initiator,
    },
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
    { name: "STATUS", selector: (row) => row.status,
       grow: 1.5,
  cell: (row) => (
    <span className="font-bold">
      {row.status}
    </span>
  ),
     },
    { name: "REASON", selector: (row) => row.reasonForBuildRequest },

    {
      name: "ACTION",
      width: "190px",
      cell: (row) => {
        if (row.status === "DELIVERY PENDING") {
          return (
            <button
              onClick={() => setDeliveredPopupId(row.orderId)}
              className="px-5 py-2 bg-yellow-600 text-white rounded-lg hover:bg-yellow-700"
            >
              Delivered
            </button>
          );
        }

        if (row.status === "PDI PENDING") {
          return (
            <div className="flex gap-3">
              <button
                className="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 flex items-center justify-center"
                onClick={() => setPdiPopup({ id: row.orderId, type: "pass" })}
              >
                PDI Pass
              </button>

              <button
                className="px-4 py-2 bg-red-500 text-white rounded-lg hover:bg-red-600 flex items-center justify-center"
                onClick={() => setPdiPopup({ id: row.orderId, type: "fail" })}
              >
                PDI Fail
              </button>
            </div>
          );
        }

        return (
          <button
            className="px-5 py-2 bg-cyan-600 text-white rounded-lg"
            onClick={() => setShippingPopupId(row.orderId)}
          >
            Shipping
          </button>
        );
      },
    },
  ];

  const capitalizeWords = (text = "") =>
  text
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());

  return (
    <>
      {/* TABLE */}
      <div className="w-full">
     
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
          <option value="SCM > LOGISTIC PENDING">SCM {'>'} LOGISTIC PENDING</option>
          <option value="DELIVERY PENDING">DELIVERY PENDING</option>
          <option value="PDI PENDING">PDI PENDING</option>
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

      {/* SHIPPING POPUP */}
     {shippingPopupId && (
  <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-2xl w-[650px]">
      
      <h2 className="text-2xl font-bold text-center text-cyan-700 mb-2">
        SHIPPING DETAILS
      </h2>

      <p className="text-center text-lg text-gray-600 mb-4">
        Order ID: <b>{shippingPopupId}</b>
      </p>

      <form onSubmit={handleShippingSubmit(submitShipping)}>

        <div className="grid grid-cols-2 gap-3 text-black">

          {Object.keys(shippingDetails).map((field) => {

            const isDateField =
              field === "dispatchDate" || field === "expectedDeliveryDate";

            return (
              <div key={field}>
                <label className="text-sm font-semibold">
                  {shippingLabels[field]}
                </label>

                <input
                  type={isDateField ? "date" : "text"}
                  className={`capitalize border px-2 py-2 rounded w-full ${
                    shippingErrors[field] ? "border-red-500" : ""
                  }`}
                  {...registerShipping(field,{
                    setValueAs: (value) => capitalizeWords(value),
                  })}
                  defaultValue={shippingDetails[field]}
                />

                {shippingErrors[field] && (
                  <p className="text-red-500 text-xs mt-1">
                    {shippingErrors[field].message}
                  </p>
                )}
              </div>
            );
          })}

        </div>

        <div className="flex justify-center gap-4 mt-6">
          <button
            type="button"
            onClick={() => {
              resetShipping();
              setShippingPopupId(null);
            }}
            className="px-6 py-2 bg-red-500 text-white rounded-lg"
          >
            ✖
          </button>

          <button
            type="submit"
            className="px-6 py-2 bg-cyan-600 text-white rounded-lg"
          >
            Submit
          </button>
        </div>

      </form>

    </div>
  </div>
)}


      {/* DELIVERED POPUP */}
     {deliveredPopupId && (
  <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-2xl w-[550px]">
      
      <h2 className="text-2xl font-bold text-center text-yellow-700 mb-2">
        DELIVERY CONFIRMATION
      </h2>

      <p className="text-center text-lg text-gray-600 mb-4">
        Order ID: <b>{deliveredPopupId}</b>
      </p>

      <form onSubmit={handleDeliveredSubmit(submitDelivered)}>

        <div className="grid grid-cols-1 gap-3 text-black">

          {/* Delivered Status */}
          <div>
            <label className="text-sm font-semibold">Delivered Status</label>
            <input
              type="text"
              {...registerDelivered("deliveredStatus",{
                setValueAs: (value) => capitalizeWords(value),
              })}
              className={`capitalize border px-2 py-2 rounded w-full ${
                deliveredErrors.deliveredStatus ? "border-red-500" : ""
              }`}
            />
            {deliveredErrors.deliveredStatus && (
              <p className="text-red-500 text-xs mt-1">
                {deliveredErrors.deliveredStatus.message}
              </p>
            )}
          </div>

          {/* Logistics Comment */}
          <div>
            <label className="text-sm font-semibold">Logistics Comment</label>
            <input
              type="text"
              {...registerDelivered("logisticsComment",{
                setValueAs: (value) => capitalizeWords(value),
              })}
              className={`capitalize border px-2 py-2 rounded w-full ${
                deliveredErrors.logisticsComment ? "border-red-500" : ""
              }`}
            />
            {deliveredErrors.logisticsComment && (
              <p className="text-red-500 text-xs mt-1">
                {deliveredErrors.logisticsComment.message}
              </p>
            )}
          </div>

          {/* Actual Delivery Date */}
          <div>
            <label className="text-sm font-semibold">Actual Delivery Date</label>
            <input
              type="datetime-local"
              {...registerDelivered("actualDeliveryDate")}
              className={`border px-2 py-2 rounded w-full ${
                deliveredErrors.actualDeliveryDate ? "border-red-500" : ""
              }`}
            />
            {deliveredErrors.actualDeliveryDate && (
              <p className="text-red-500 text-xs mt-1">
                {deliveredErrors.actualDeliveryDate.message}
              </p>
            )}
          </div>

        </div>

        <div className="flex justify-center gap-4 mt-6">
          <button
            type="button"
            onClick={() => {
              resetDelivered();
              setDeliveredPopupId(null);
            }}
            className="px-6 py-2 bg-red-500 text-white rounded-lg"
          >
            ✖
          </button>

          <button
            type="submit"
            className="px-6 py-2 bg-yellow-600 text-white rounded-lg"
          >
            OK
          </button>
        </div>

      </form>

    </div>
  </div>
)}


      {/* ---------- NEW: PDI POPUP ---------- */}
      {pdiPopup.id && (
  <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-2xl w-[550px]">

      <h2 className="text-2xl font-bold text-center text-blue-700 mb-2">
        {pdiPopup.type === "pass" ? "PDI Pass Confirmation" : "PDI Fail Confirmation"}
      </h2>

      <p className="text-center text-lg text-gray-600 mb-4">
        Order ID: <b>{pdiPopup.id}</b>
      </p>

      <form onSubmit={handlePdiSubmit(submitPDI)}>

        <div className="grid grid-cols-1 gap-3 text-black">
          <div>
            <label className="text-sm font-semibold">Logistics PDI Comment</label>

            <input
              type="text"
              {...registerPdi("logisticsPdiComment")}
              className={`border px-2 py-2 rounded w-full ${
                pdiErrors.logisticsPdiComment ? "border-red-500" : ""
              }`}
            />

            {pdiErrors.logisticsPdiComment && (
              <p className="text-red-500 text-xs mt-1">
                {pdiErrors.logisticsPdiComment.message}
              </p>
            )}
          </div>
        </div>

        <div className="flex justify-center gap-4 mt-6">
          <button
            type="button"
            onClick={() => {
              resetPdi();
              setPdiPopup({ id: null, type: null });
            }}
            className="px-6 py-2 bg-red-500 text-white rounded-lg"
          >
            ✖
          </button>

          <button
            type="submit"
            className="px-6 py-2 bg-blue-600 text-white rounded-lg"
          >
            OK
          </button>
        </div>

      </form>

    </div>
  </div>
)}

    </>
  );
}
