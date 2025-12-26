"use client";

import { useState } from "react";
import DataTable from "react-data-table-component";
import httpService from "../service/httpService";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import AlertPopup from "../../components/layout/AlertPopup";


export default function RmaTable({
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
  refreshData
}) {

 

  // PASS popup
  const [popupOrderId, setPopupOrderId] = useState(null);

  // FAIL popup
  const [failPopupOrderId, setFailPopupOrderId] = useState(null);

  const [alertPopup, setAlertPopup] = useState({
  show: false,
  message: "",
  type: "success",
});

  //VALIDATION
  const passSchema = yup.object().shape({
  rmaComment: yup.string().required("RMA Comment is required"),
});

const failSchema = yup.object().shape({
  rmaComment: yup.string().required("RMA Fail Comment is required"),
});

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

  //REACT HOOK FORM
  const {
  register: registerPass,
  handleSubmit: handlePassSubmit,
  reset: resetPass,
  formState: { errors: passErrors }
} = useForm({
  resolver: yupResolver(passSchema),
  defaultValues: { rmaComment: "" }
});

const {
  register: registerFail,
  handleSubmit: handleFailSubmit,
  reset: resetFail,
  formState: { errors: failErrors }
} = useForm({
  resolver: yupResolver(failSchema),
  defaultValues: { rmaComment: "" }
});


  // PASS API hit
 const submitPass = async (data) => {
  try {
    if (!popupOrderId) return;

    const res = await httpService.postWithAuth(
      `/api/v1/orders/rma/passed/${popupOrderId}`,
      data
    );

    setAlertPopup({
  show: true,
  message: res || "Passed",
  type: "success",
});

    resetPass();         // form reset
    setPopupOrderId(null);
    if (refreshData) refreshData();

  } catch (err) {
    setAlertPopup({
  show: true,
  message: "Something went wrong",
  type: "success",
});
  }
};


  // FAIL API hit
  const submitFail = async (data) => {
  try {
    if (!failPopupOrderId) return;

    const res = await httpService.postWithAuth(
      `/api/v1/orders/rma/failed/${failPopupOrderId}`,
      data
    );

    setAlertPopup({
  show: true,
  message: res || "Failed",
  type: "success",
});

    resetFail();         // reset form
    setFailPopupOrderId(null);
    if (refreshData) refreshData();

  } catch (err) {
   setAlertPopup({
  show: true,
  message: res || "Something went wrong",
  type: "success",
});
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
      width: "200px",
      cell: (row) => (
        <div className="flex gap-2">
          <button
            className="px-5 py-2 bg-green-600 text-white rounded-lg"
            onClick={() => setPopupOrderId(row.orderId)}
          >
            QI PASS 
          </button>

          <button
            className="px-5 py-2 bg-red-500 text-white rounded-lg"
            onClick={() => setFailPopupOrderId(row.orderId)}
          >
            QI FAIL
          </button>
        </div>
      ),
    },
  ];

    const capitalizeWords = (text = "") =>
  text
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());

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
         <AlertPopup
      show={alertPopup.show}
      message={alertPopup.message}
      type={alertPopup.type}
      onClose={() => setAlertPopup({ ...alertPopup, show: false })}
    />
      </div>

      {/* PASS POPUP */}
    {popupOrderId && (
  <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-xl w-[600px] relative">

      <h2 className="text-2xl font-bold mb-3 text-cyan-700 text-center">
        Submit Comment
      </h2>

      <p className="text-gray-600 text-center mb-4">
        Order ID: <b>{popupOrderId}</b>
      </p>

      <form onSubmit={handlePassSubmit(submitPass)} className="flex flex-col gap-3">

        <textarea
          {...registerPass("rmaComment",{
            setValueAs: (value) => capitalizeWords(value),
          })}
          placeholder="Enter RMA Comment"
          className={`capitalize border p-2 rounded h-28 w-full text-black ${
            passErrors.rmaComment ? "border-red-500" : ""
          }`}
        />

        {passErrors.rmaComment && (
          <p className="text-red-500 text-sm">{passErrors.rmaComment.message}</p>
        )}

        <div className="flex justify-center gap-4 mt-5">
          <button
            type="button"
            onClick={() => {
              resetPass();
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
)}


      {/* FAIL POPUP */}
     {failPopupOrderId && (
  <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-xl w-[600px] relative">

      <h2 className="text-2xl font-bold mb-3 text-red-700 text-center">
        SUBMIT COMMENT
      </h2>

      <p className="text-gray-600 text-center mb-4">
        Order ID: <b>{failPopupOrderId}</b>
      </p>

      <form onSubmit={handleFailSubmit(submitFail)} className="flex flex-col gap-3">

        <textarea
          {...registerFail("rmaComment",{
            setValueAs: (value) => capitalizeWords(value),
          })}
          placeholder="Enter RMA Comment"
          className={`capitalize border p-2 rounded h-28 w-full text-black ${
            failErrors.rmaComment ? "border-red-500" : ""
          }`}
        />

        {failErrors.rmaComment && (
          <p className="text-red-500 text-sm">{failErrors.rmaComment.message}</p>
        )}

        <div className="flex justify-center gap-4 mt-5">
          <button
            type="button"
            onClick={() => {
              resetFail();
              setFailPopupOrderId(null);
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
)}


    </>
  );
}
