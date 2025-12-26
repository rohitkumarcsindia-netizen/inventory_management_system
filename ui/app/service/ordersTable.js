"use client";

import DataTable from "react-data-table-component";
import { useState } from "react";
import httpService from "../service/httpService";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";
import AlertPopup from "../../components/layout/AlertPopup";



export default function OrdersTable({
  orders,
  filteredData,
  searchFilteredData,
  totalOrders,
  filteredCount,
  noDataFound,
  currentPage,
  setCurrentPage,
  ordersPerPage,
  setOrdersPerPage,
  searchText,
  applySearchFilter,
  startDate,
  endDate,
  setStartDate,
  setEndDate,
  applyDateFilter,
  statusFilter,
  applyStatusFilter,
  fetchOrders
}) {

  
const [editPopup, setEditPopup] = useState(false);
const [selectedOrder, setSelectedOrder] = useState(null);
const [editForm, setEditForm] = useState(null);
const [isEditMode, setIsEditMode] = useState(false);

const [showPostPopup, setShowPostPopup] = useState(false);
  const [selectedOrderId, setSelectedOrderId] = useState(null);

  const [pdiMode, setPdiMode] = useState("POST"); // PRE or POST

  //Notify to AMISP POPUP
  const [popupOrderId, setPopupOrderId] = useState(null);

  
//Send Location ProjectTeam to Scm Team
const [locationPopupOrderId, setLocationPopupOrderId] = useState(null);

 // ---------- NEW: PDI Popup ----------
  const [pdiPopup, setPdiPopup] = useState({
    id: null,
    type: null, // pass / fail
  });

  const [alertPopup, setAlertPopup] = useState({
  show: false,
  message: "",
  type: "success",
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

  const fieldLabels = {
  projectTeamComment: "Project Team Comment",
  serialNumbers: "Serial Numbers",
  documentUrl: "Document URL",
  dispatchDetails: "Dispatch Details",
  pdiLocation: "PDI Location"
};  

//  AMISP VALIDATION
const amispSchema = yup.object().shape({
  amispEmailId: yup
    .string()
    .email("Enter a valid email")
    .required("Email is required"),
});

// AMISP REACT HOOK FORM
const {
  register: registerAmisp,
  handleSubmit: handleAmispSubmit,
  reset: resetAmisp,
  formState: { errors: amispErrors },
} = useForm({
  resolver: yupResolver(amispSchema),
});

// PRE / POST PDI VALIDATION SCHEMA
const pdiSchema = yup.object().shape({
  projectTeamComment: yup.string().required("Project Team Comment is required"),
  serialNumbers: yup.string().required("Serial Numbers are required"),
  documentUrl: yup.string().required("Document URL is required"),
  dispatchDetails: yup.string().required("Dispatch Details are required"),
  pdiLocation: yup.string().required("PDI Location is required"),
});

// PRE / POST PDI - REACT HOOK FORM
const {
  register: registerPdi,
  handleSubmit: handlePdiSubmit,
  reset: resetPdi,
  formState: { errors: pdiErrors },
} = useForm({
  resolver: yupResolver(pdiSchema),
  defaultValues: {
    projectTeamComment: "",
    serialNumbers: "",
    documentUrl: "",
    dispatchDetails: "",
    pdiLocation: "",
  },
});

// LOCATION VALIDATION SCHEMA
const locationSchema = yup.object().shape({
  locationDetails: yup
    .string()
    .required("Location is required"),
});

// LOCATION FORM - REACT HOOK FORM
const {
  register: registerLocation,
  handleSubmit: handleLocationSubmit,
  reset: resetLocation,
  formState: { errors: locationErrors },
} = useForm({
  resolver: yupResolver(locationSchema),
  defaultValues: {
    locationDetails: "",
  },
});

//  -----------PDI PASS/FAIL SCHEMA-------
const pdiPassFailSchema = yup.object().shape({
  pdiComment: yup.string().required("PDI Comment is required"),
});

//  -----------PDI PASS/FAIL REACT HOOK FORM-------
const {
  register: registerPdiResult,
  handleSubmit: handlePdiResultSubmit,
  reset: resetPdiResult,
  formState: { errors: pdiResultErrors },
} = useForm({
  resolver: yupResolver(pdiPassFailSchema),
});



   //------POST/PRE PDI SUBMIT---------
  const handlePostSubmit = async (data) => {
  try {
    const apiUrl =
      pdiMode === "PRE"
        ? `/api/v1/orders/project/pri-delivery-pdi/${selectedOrderId}`
        : `/api/v1/orders/project/post-delivery-pdi/${selectedOrderId}`;

    const res = await httpService.updateWithAuth(apiUrl, data);

    setAlertPopup({
  show: true,
  message: res || "submitted successfully!",
  type: "success",
});

    setShowPostPopup(false);
    resetPdi(); 
    fetchOrders();

  } catch (error) {
    console.error(error);

      setAlertPopup({
  show: true,
  message: "Error submitting PDI data",
  type: "success",
});
  }
};


    // -----DELETE SAVE DATA--------
const handleDeleteOrder = async () => {

  try {
   const res = await httpService.deleteWithAuth(
      `/api/v1/orders/project/delete/${selectedOrder.orderId}`
    );

    setAlertPopup({
  show: true,
  message: res || "Order deleted successfully!",
  type: "success",
});

    // popup close + data clear
    setEditPopup(false);
    setSelectedOrder(null);

    // table refresh (agar parent se function aa raha ho)
    setCurrentPage(1); // optional
    fetchOrders(); 

  } catch (error) {
    console.error("Delete Order Error:", error);

        setAlertPopup({
  show: true,
  message: res?.message || "Failed to delete order ❌",
  type: "success",
});
  }
};

    //------------SUBMIT SAVE DATA------
  const handleSubmitOrder = async () => {
  if (!editForm) return;

  try {
    const res = await httpService.updateWithAuth(
      `/api/v1/orders/project/submit/${selectedOrder.orderId}`,editForm   //  popup ka poora data body me
    );

       setAlertPopup({
  show: true,
  message: res || "Order Submitted successfully",
  type: "success",
});
    

    // popup close + reset
    setEditPopup(false);
    setSelectedOrder(null);
    setEditForm(null);

    // table refresh (agar available)
    setCurrentPage(1);
    fetchOrders();

  } catch (error) {
    console.error("Submit Order Error:", error);

       setAlertPopup({
  show: true,
  message: "Failed to SUbmit order ❌",
  type: "success",
});
  }
};

    //--------EDIT SAVE DATA--------
    const handleUpdateOrder = async () => {
  if (!editForm || !selectedOrder?.orderId) return;

  try {
    const res = await httpService.updateWithAuth(
      `/api/v1/orders/project/update/${selectedOrder.orderId}`, // ✅ FIX
      editForm
    );

       setAlertPopup({
  show: true,
  message: res || "Order updated successfully",
  type: "success",
});

    setEditPopup(false);
    setSelectedOrder(null);
    setEditForm(null);
    setIsEditMode(false);

    fetchOrders();
  } catch (error) {
    console.error("Update Order Error:", error);
     setAlertPopup({
  show: true,
  message: res?.message || "Order updated failed!",
  type: "success",
});
  }
};


      //  NOTIFY AMISP API CALL
const notifyAmisp = async (data) => {
  try {
    if (!popupOrderId) return;

    const res = await httpService.postWithAuth(
      `/api/v1/orders/project/convey-amisp/${popupOrderId}`,
      data
    );

     setAlertPopup({
  show: true,
  message: res || "Email sent to AMISP successfully",
  type: "success",
});

    resetAmisp();        // form reset
    setPopupOrderId(null);
    fetchOrders();

  } catch (error) {
    console.error("Notify Error:", error);
     setAlertPopup({
  show: true,
  message: res || "Notification failed!",
  type: "success",
});
  }
};


        //  NOTIFY SCM API CALL
const notifyScm = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/v1/orders/project/notify-scm/${orderId}`,
      {}   // ❗ no body required
    );

     setAlertPopup({
  show: true,
  message: res || "Notification Sent for SCM!",
  type: "success",
});
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("Notify Error:", error);
     setAlertPopup({
  show: true,
  message: res || "Notification failed!",
  type: "success",
});
  }
};

  // SEND LOCATION TO scm
  const sendLocationToScm = async (data) => {
  try {
    if (!locationPopupOrderId) return;

    const res = await httpService.updateWithAuth(
      `/api/v1/orders/project/notify-scm-location-details/${locationPopupOrderId}`,
      data
    );

     setAlertPopup({
  show: true,
  message: res?.message || "Sent Locatio  for SCM",
  type: "success",
});

    resetLocation();
    setLocationPopupOrderId(null);
    fetchOrders();

  } catch (err) {
    console.error("Location API Error:", err);
     setAlertPopup({
  show: true,
  message: res.message || "Error sending location!",
  type: "success",
});
  }
};



// ---------- NEW: Submit PDI API ----------
  const submitPDI = async (data) => {
  try {
    if (!pdiPopup.id || !pdiPopup.type) return;

    const endpoint =
      pdiPopup.type === "pass"
        ? `/api/v1/orders/project/pdi-pass/${pdiPopup.id}`
        : `/api/v1/orders/project/pdi-fail/${pdiPopup.id}`;

    const res = await httpService.updateWithAuth(endpoint, data);


      setAlertPopup({
  show: true,
  message: res || "PDI updated successfully!",
  type: "success",
});
    resetPdi();
    setPdiPopup({ id: null, type: null });
    fetchOrders();

  } catch (err) {
    console.error("PDI API ERROR:", err);

     setAlertPopup({
  show: true,
  message: res.message || "Failed to update PDI!",
  type: "success",
});
  }
};



  const finalData =
    searchFilteredData.length > 0
      ? searchFilteredData
      : filteredData.length > 0
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
          { name: "ORDER ID", selector: (row) => row.orderId, sortable: true, grow: 0.9,
            cell: (row) => <span className="font-bold">{row.orderId}</span>,
          },
          { name: "ORDER DATE", selector: (row) => row.createAt, sortable: true, grow: 1.3,
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
          { name: "EXPECTED DATE", selector: (row) => row.expectedOrderDate, sortable: true, grow: 1.4,
            cell: (row) => {
    const { date } = formatOrderDateTime(row.createAt);

    return (
        <div className="font-medium text-black">
          {date}
        </div>
    );
  },
           },
          { name: "PROJECT", selector: (row) => row.project, grow: 0.7,
             cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.project),
      }}
    />
  ),
           },
          // { name: "INITIATOR", selector: (row) => row.users?.username || row.initiator, grow: 0.7 },
          { name: "PRODUCT TYPE", selector: (row) => row.productType, grow: 1.1,
             cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.productType),
      }}
    />
  ),
           },
          { name: "ORDER TYPE", selector: (row) => row.orderType, grow: 1,
             cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.orderType),
      }}
    />
  ),
          },
          { name: "QTY", selector: (row) => row.proposedBuildPlanQty, grow: 0.1 },
          {
  name: "STATUS",
  selector: (row) => row.status,
  grow: 1.4,
  cell: (row) => (
    <span className="font-bold">
      {row.status}
    </span>
  ),
},
          {
      name: "ACTION",
      width: "200px",
      cell: (row) => {
        // Case 1: SCM Notify > PROJECT TEAM BUILD IS READY → Show Jira Closure
        if (row.status === "SCM NOTIFY > PROJECT TEAM BUILD IS READY") {
          return (
            <button
              onClick={() => setPopupOrderId(row.orderId)}
              className="px-5 py-2 bg-yellow-600 text-white rounded-lg"
            >
              Notify To AMISP
            </button>
          );
        };

        // Case 2: PROJECT TEAM > PROJECT TEAM READY FOR DISPATCH 
        if (row.status === "PROJECT TEAM > PROJECT TEAM READY FOR DISPATCH") {
          return (
            <button
              onClick={() => notifyScm(row.orderId)}
              className="px-5 py-2 bg-blue-600 text-white rounded-lg"
            >
              Notify To SCM
            </button>
          );
        };

        // Case 3: SCM NOTIFY > AMISP READY FOR DISPATCH → Show Send Location SCM
        if (row.status === "SCM NOTIFY > AMISP READY FOR DISPATCH") {
          return (
            <button
              onClick={() => setLocationPopupOrderId(row.orderId)}   
              className="px-5 py-2 bg-yellow-600 text-white rounded-lg"
            >
              Send Location To SCM
            </button>
          );
        }

        // Case 4: PROJECT TEAM PENDING → Show Jira Closure
        if (row.status === "PROJECT TEAM PENDING") {
          return (
            <button
               onClick={() => {
        setSelectedOrder(row);
         setEditForm({
          orderId: row.orderId,
    expectedOrderDate: row.expectedOrderDate || "",
    project: row.project || "",
    productType: row.productType || "",
    proposedBuildPlanQty: row.proposedBuildPlanQty || "",
    reasonForBuildRequest: row.reasonForBuildRequest || "",
    orderType: row.orderType || "",
    pmsRemarks: row.pmsRemarks || "",
  });
        setIsEditMode(false);
        setEditPopup(true);
      }}
              className="px-5 py-2 bg-green-600 text-white rounded-lg"
            >
              Edit Order
            </button>
          );
        };

        // Case 5: PROJECT TEAM NOTIFY > AMISP PDI TYPE PENDING 
        if (row.status === "PROJECT TEAM NOTIFY > AMISP PDI TYPE PENDING") {
          return(
                        <div className="flex gap-2">
          <button
  onClick={() => {
    setSelectedOrderId(row.orderId);
    setPdiMode("POST");
    setShowPostPopup(true);
  }}
  className="px-5 py-2 bg-orange-500 text-white rounded-lg"
>
  POST PDI
</button>

            <button
  onClick={() => {
    setSelectedOrderId(row.orderId);
    setPdiMode("PRE");
    setShowPostPopup(true);
  }}
  className="px-5 py-2 bg-blue-600 text-white rounded-lg"
>
  PRE PDI
</button>

          </div>
          );
        };

  // Case 6: COMPLETED
    if (row.status === "COMPLETED") {
    return (
      <span className="px-3 py-2 inline-block bg-green-500 text-green-800 font-semibold rounded-md">
        Order Completed
      </span>
    );
  }
  // Case 6: FINANCE TEAM REJECTED
    if (row.status === "FINANCE TEAM REJECTED") {
    return (
      <span className="px-3 py-2 inline-block bg-red-500 text-red-800 font-semibold rounded-md">
        REJECTED
      </span>
    );
  }

  //Case 7: PDI PENDING
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

         // DEFAULT — No button
    return (
      <span className="text-balck-500 font-bold">
        Waiting for Action
      </span>
    );

      }
    },
        ]

    const capitalizeWords = (text = "") =>
  text
    .toLowerCase()
    .replace(/\b\w/g, (char) => char.toUpperCase());

  const ExpandedOrderDetails = ({ data }) => {
  const Field = ({ label, value, type = "text" }) => {
  const formattedValue =
    type === "date" && value
      ? value.split("T")[0] // 👈 YYYY-MM-DD
      : value || "-";

  return (
    <div>
      <label className="text-xs font-semibold text-gray-600">
        {label}
      </label>
      <input
        type={type}
        value={formattedValue}
        readOnly
        className="w-full mt-1 px-3 py-2 rounded-md border 
                   bg-gray-100 text-gray-800 text-sm 
                   cursor-not-allowed"
      />
    </div>
  );
};


  return (
    <div className="p-5 bg-gray-50 rounded-lg border border-gray-200">
      <div className="grid grid-cols-2 gap-4">

        <Field label="Order ID" value={data.orderId} />
        <Field label="Order Type" value={data.orderType} />
        <Field label="Product Type" value={data.productType} />

        <Field
          label="Expected Date"
          value={data.expectedOrderDate}
          type="date"
        />
        <Field
          label="Created At"
          value={data.createAt}
          type="date"
        />

        <Field
          label="Quantity"
          value={data.proposedBuildPlanQty}
        />

        <Field label="Project" value={data.project} />
        <Field label="Reason" value={data.reasonForBuildRequest} />
        <Field
          label="Initiator"
          value={data.users?.username || data.initiator}
        />

          <Field label="Status" value={data.status} />
        

      </div>
    </div>
  );
};

const handleCancelEdit = () => {
  setEditPopup(false);
  setSelectedOrder(null);
  setEditForm(null);     // ✅ ADD THIS
  setIsEditMode(false); // ✅ ADD THIS
};


  return (
    <div className="w-full">

      {/* TOTAL ORDERS */}
      <div className="flex justify-end mb-2 px-1 text-black font-semibold text-sm">
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
          <option value="PROJECT TEAM PENDING">PROJECT TEAM PENDING</option>
          <option value="PROJECT TEAM > FINANCE PRE APPROVAL PENDING">PROJECT TEAM {'>'} FINANCE PRE APPROVAL PENDING</option>
          <option value="PROJECT TEAM > SCM PENDING">PROJECT TEAM {'>'} SCM PENDING</option>
          <option value="FINANCE TEAM REJECTED">FINANCE TEAM REJECTED</option>
          <option value="FINANCE APPROVED > SCM PENDING">FINANCE APPROVED {'>'} SCM PENDING</option>
          <option value="SCM CREATED TICKET > CLOUD PENDING">
SCM CREATED TICKET {'>'} CLOUD PENDING</option>
          <option value="CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING">CLOUD CREATED CERTIFICATE {'>'} SCM PROD-BACK CREATION PENDING</option>
          <option value="SCM JIRA TICKET CLOSURE > SYRMA PENDING">
SCM JIRA TICKET CLOSURE {'>'} SYRMA PENDING</option>
          <option value="SYRMA PROD/TEST DONE > SCM ACTION PENDING">SYRMA PROD/TEST DONE {'>'} SCM ACTION PENDING</option>
          <option value="SCM NOTIFY > RMA QC PENDING">SCM NOTIFY {'>'} RMA QC PENDING</option>
          <option value="RRMA QC PASS > SCM ORDER RELEASE PENDING">RMA QC PASS {'>'} SCM ORDER RELEASE PENDING</option>
          <option value="RMA QC FAIL > SYRMA RE-PROD/TEST PENDING">RMA QC FAIL {'>'} SYRMA RE-PROD/TEST PENDING</option>
          <option value="SCM NOTIFY > PROJECT TEAM BUILD IS READY">SCM NOTIFY {'>'} PROJECT TEAM BUILD IS READY</option>
          <option value="PROJECT TEAM NOTIFY > AMISP PDI TYPE PENDING">PROJECT TEAM NOTIFY {'>'} AMISP PDI TYPE PENDING</option>
          <option value="PROJECT TEAM > PROJECT TEAM READY FOR DISPATCH">PROJECT TEAM {'>'} PROJECT TEAM READY FOR DISPATCH</option>
          <option value="PROJECT TEAM > SCM READY FOR DISPATCH">PROJECT TEAM {'>'} SCM READY FOR DISPATCH</option>
          <option value="SCM NOTIFY > AMISP READY FOR DISPATCH">SCM NOTIFY {'>'} AMISP READY FOR DISPATCH</option>
          <option value="PROJECT TEAM > NOTIFY SCM LOCATION DETAILS">PROJECT TEAM {'>'} NOTIFY SCM LOCATION DETAILS</option>
          <option value="SCM > FINANCE POST APPROVAL PENDING">SCM {'>'} FINANCE POST APPROVAL PENDING</option>
          <option value="FINANCE > SCM PLAN TO DISPATCH">FINANCE {'>'} SCM PLAN TO DISPATCH</option>
          <option value="SCM > LOGISTIC PENDING">SCM {'>'} LOGISTIC PENDING</option>
          <option value="DELIVERY PENDING">DELIVERY PENDING</option>
          <option value="PDI PENDING">PDI PENDING</option>
          <option value="LOGISTIC > FINANCE CLOSURE PENDING">LOGISTIC {'>'} FINANCE CLOSURE PENDING</option>
          <option value="PROJECT TEAM > FINANCE CLOSURE PENDING">PROJECT TEAM {'>'} FINANCE CLOSURE PENDING</option>
          <option value="FINANCE CLOSURE DONE > SCM CLOSURE PENDING">FINANCE CLOSURE DONE {'>'} SCM CLOSURE PENDING</option>
          <option value="COMPLETED">COMPLETED</option>
         
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
        data={!noDataFound ? finalData : []}
        highlightOnHover
        fixedHeader
        fixedHeaderScrollHeight="500px"
        pagination
        paginationServer
        paginationPerPage={ordersPerPage}
        paginationTotalRows={noDataFound ? 0 : (filteredCount > 0 ? filteredCount : totalOrders)}
        expandableRows 
        expandableRowsComponent={ExpandedOrderDetails} 
        expandableRowsHideExpander={false} 

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

    {editPopup && selectedOrder && (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">

    <div className="bg-white w-[900px] rounded-xl p-6 shadow-2xl text-black">

      <h2 className="text-xl font-bold mb-4 text-cyan-700">
        Edit Order – #{selectedOrder.orderId}
      </h2>

      {/* FORM */}
      <div className="grid grid-cols-2 gap-4">

        <div>
          <label className="text-sm font-semibold">Expected Date</label>
          <input
            type="date"
            value={editForm.expectedOrderDate}
            onChange={(e) =>
    setEditForm({ ...editForm, expectedOrderDate: e.target.value })
  }
            readOnly={!isEditMode}
            
            className="w-full border px-3 py-2 rounded bg-gray-100"
          />
        </div>

        <div>
          <label className="text-sm font-semibold">Project</label>
          <input
            value={editForm.project}
             onChange={(e) =>
    setEditForm({ ...editForm, project: e.target.value })
  }
            readOnly={!isEditMode}
            className="w-full border px-3 py-2 rounded bg-gray-100"
          />
        </div>

        <div>
          <label className="text-sm font-semibold">Product Type</label>
          <input
            value={editForm.productType}
             onChange={(e) =>
    setEditForm({ ...editForm, productType: e.target.value })
  }
            readOnly={!isEditMode}
            className="w-full border px-3 py-2 rounded bg-gray-100"
          />
        </div>

        <div>
          <label className="text-sm font-semibold">Proposed Build Plan Qty</label>
          <input
            value={editForm.proposedBuildPlanQty}
          onChange={(e) =>
    setEditForm({ ...editForm, proposedBuildPlanQty: e.target.value })
  }
            readOnly={!isEditMode}
            className="w-full border px-3 py-2 rounded bg-gray-100"
          />
        </div>

        <div className="col-span-2">
          <label className="text-sm font-semibold">Reason for Build Request</label>
          <textarea
            value={editForm.reasonForBuildRequest || ""}
            onChange={(e) =>
    setEditForm({ ...editForm, reasonForBuildRequest: e.target.value })
  }
            readOnly={!isEditMode}
            className="w-full border px-3 py-2 rounded bg-gray-100"
          />
        </div>

        <div>
          <label className="text-sm font-semibold">Order Type</label>
          <input
            value={editForm.orderType}
            onChange={(e) =>
    setEditForm({ ...editForm, orderType: e.target.value })
  }
            readOnly={!isEditMode}
            className="w-full border px-3 py-2 rounded bg-gray-100"
          />
        </div>

        <div>
          <label className="text-sm font-semibold">PM Remarks</label>
          <textarea
            value={editForm.pmsRemarks || ""}
            onChange={(e) =>
    setEditForm({ ...editForm, pmsRemarks: e.target.value })
  }
            readOnly={!isEditMode}
            className="w-full border px-3 py-2 rounded bg-gray-100"
          />
        </div>

      </div>

      {/* BUTTONS */}
      <div className="flex justify-end gap-4 mt-6">

        <button
    onClick={handleCancelEdit}
    className="px-5 py-2 bg-gray-500 text-white rounded-lg hover:bg-gray-600"
  >
    ✖
  </button>

        {!isEditMode ? (
    //  EDIT MODE OFF
    <button
      onClick={() => setIsEditMode(true)}
      className="px-5 py-2 bg-yellow-500 text-white rounded-lg"
    >
      Edit
    </button>
  ) : (
    //  UPDATE MODE ON
    <button
      onClick={handleUpdateOrder}
      className="px-5 py-2 bg-green-600 text-white rounded-lg"
    >
      Update
    </button>
  )}

        <button 
        onClick={handleDeleteOrder}
        className="px-5 py-2 bg-red-600 text-white rounded-lg"
        >
          Delete
        </button>

        <button
          onClick={handleSubmitOrder}
          className="px-5 py-2 bg-cyan-600 text-white rounded-lg"
        >
          Submit
        </button>

      </div>

    </div>
  </div>
)}

      {/* AMISP EMAIL POPUP */}
{popupOrderId && (
  <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-xl w-[600px] relative">

      <h2 className="text-2xl font-bold mb-3 text-cyan-700 text-center">
        NOTIFY TO AMISP
      </h2>

      <p className="text-gray-600 text-center mb-4">
        <b>Order ID:</b> <b>{popupOrderId}</b>
      </p>

      <form onSubmit={handleAmispSubmit(notifyAmisp)}>
        
        <input
          placeholder="Enter AMISP Email ID"
          {...registerAmisp("amispEmailId")}
          className={`border p-2 rounded h-10 w-full text-black ${
            amispErrors.amispEmailId ? "border-red-500" : ""
          }`}
        />

        {amispErrors.amispEmailId && (
          <p className="text-red-500 text-sm mt-1">
            {amispErrors.amispEmailId.message}
          </p>
        )}

        <div className="flex justify-center gap-4 mt-5">
          <button
            type="button"
            onClick={() => {
              resetAmisp();
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


   {/* POST / PRE PDI POPUP */}
{showPostPopup && (
  <div className="fixed inset-0 flex items-center justify-center backdrop-blur-md bg-white/10 z-50">

    <div className="bg-white p-6 rounded-xl shadow-2xl w-[600px] border border-blue-200">
      <h2 className="text-xl font-semibold text-blue-600 mb-4 text-center">
        AMISP {pdiMode === "PRE" ? "PRE" : "POST"} DELIVERY DATA
      </h2>

      <form onSubmit={handlePdiSubmit(handlePostSubmit)}>

        <p className="text-gray-600 text-center mb-4">
          <b>Order ID:</b> <b>{selectedOrderId}</b>
        </p>

        {/* Dynamic Fields */}
        {Object.keys(fieldLabels).map((key) => (
          <div key={key}>
            <label className="block font-semibold text-black text-sm mb-1">
              {fieldLabels[key]}
            </label>

            <input
              type="text"
              {...registerPdi(key,{
                setValueAs: (value) => capitalizeWords(value)
              })}
              className={`capitalize w-full px-3 py-2 rounded-md text-black border ${
                pdiErrors[key] ? "border-red-500" : "border-gray-400"
              }`}
            />

            {pdiErrors[key] && (
              <p className="text-red-500 text-xs mt-1">
                {pdiErrors[key].message}
              </p>
            )}
          </div>
        ))}

        {/* Buttons */}
        <div className="flex justify-end gap-3 mt-5">
          <button
            type="button"
            onClick={() => {
              resetPdi();
              setShowPostPopup(false);
            }}
            className="px-4 py-2 bg-gray-500 text-white rounded-md"
          >
            ✖
          </button>

          <button
            type="submit"
            className="px-4 py-2 bg-green-600 text-white rounded-md"
          >
            Submit
          </button>
        </div>

      </form>
    </div>
  </div>
)}


     {/* SEND LOCATION TO SCM POPUP */}
{locationPopupOrderId && (
  <div className="fixed inset-0 bg-black/40 backdrop-blur-sm flex justify-center items-center z-50">
    <div className="bg-white p-6 rounded-lg shadow-xl w-[600px] relative">

      <h2 className="text-2xl font-bold mb-3 text-cyan-700 text-center">
        SEND LOCATION TO SCM TEAM
      </h2>

      <p className="text-gray-600 text-center mb-4">
        <b>Order ID:</b> <b>{locationPopupOrderId}</b>
      </p>

      <form onSubmit={handleLocationSubmit(sendLocationToScm)}>

        <textarea
          placeholder="Enter Location"
          {...registerLocation("locationDetails",{
            setValueAs: (value) => capitalizeWords(value)
          })}
          className={`capitalize border p-2 rounded h-28 w-full text-black ${
            locationErrors.locationDetails ? "border-red-500" : ""
          }`}
        />

        {locationErrors.locationDetails && (
          <p className="text-red-500 text-sm mt-1">
            {locationErrors.locationDetails.message}
          </p>
        )}

        <div className="flex justify-center gap-4 mt-5">
          <button
            type="button"
            onClick={() => {
              resetLocation();
              setLocationPopupOrderId(null);
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

      <form onSubmit={handlePdiResultSubmit(submitPDI)}>

        <div className="grid grid-cols-1 gap-3 text-black">
          <div>
            <label className="text-sm font-semibold">Project Team PDI Comment</label>

            <input
              type="text"
              {...registerPdiResult("pdiComment")}
              className={`border px-2 py-2 rounded w-full ${
                pdiErrors.pdiComment ? "border-red-500" : ""
              }`}
            />

            {pdiResultErrors.pdiComment && (
              <p className="text-red-500 text-xs mt-1">
                {pdiResultErrors.pdiComment.message}
              </p>
            )}
          </div>
        </div>

        <div className="flex justify-center gap-4 mt-6">
          <button
            type="button"
            onClick={() => {
              resetPdiResult();
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

<AlertPopup
      show={alertPopup.show}
      message={alertPopup.message}
      type={alertPopup.type}
      onClose={() => setAlertPopup({ ...alertPopup, show: false })}
    />
    </div>
  );
}
