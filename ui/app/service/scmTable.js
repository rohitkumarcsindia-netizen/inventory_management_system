"use client";
import { useState } from "react";
import DataTable from "react-data-table-component";
import httpService from "../service/httpService";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { useEffect } from "react";
import * as yup from "yup";

export default function ScmTable({
  orders,
  createJira,
  createOldJira,
  currentPage,
  setCurrentPage,
   notifyRma, 
   notifyPT, 
   notifyLogistic,
   fetchOrders,
   
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
  totalOrders,
}) {
  

  // Popup for new/old jira
  const [popupOrderId, setPopupOrderId] = useState(null);
  const [isOld, setIsOld] = useState(false);

  //  NEW POPUP FOR JIRA CLOSURE
  const [closurePopupId, setClosurePopupId] = useState(null);

  

  // Jira details popup form
  const [jiraData, setJiraData] = useState({
    jiraTicketNumber: "",
    jiraSummary: "",
    jiraStatus: "",
    scmComments: "",
  });

  // Closure popup fields
  const [closureData, setClosureData] = useState({
    jiraStatus: "",
    scmComments: "",
  });

  const handleInputChange = (e) => {
    setJiraData({ ...jiraData, [e.target.name]: e.target.value });
  };

  const handleClosureChange = (e) => {
    setClosureData({ ...closureData, [e.target.name]: e.target.value });
  };

  //  VALIDATION NEW/OLD POPUP
  const jiraSchema = yup.object().shape({
  jiraTicketNumber: yup.string().required("Jira Ticket Number is required"),
  jiraSummary: yup.string().required("Jira Summary is required"),
  jiraStatus: yup.string().required("Jira Status is required"),
  scmComments: yup.string().required("SCM Comments are required"),
});

  //  VALIDATION CLOSURE POPUP
const closureSchema = yup.object().shape({
  jiraStatus: yup.string().required("Jira Status is required"),
  scmComments: yup.string().required("SCM Comments are required"),
});

  
  // REACT FORM NEW/OLD POPUP
  const {
  register,
  handleSubmit,
  reset,
  formState: { errors }
} = useForm({
  resolver: yupResolver(jiraSchema),
});

// REACT FORM CLOSURE POPUP
const {
  register: registerClosure,
  handleSubmit: submitClosureForm,
  reset: resetClosure,
  formState: { errors: closureErrors },
} = useForm({
  resolver: yupResolver(closureSchema),
});
 
// JIRA POPUP RESET
  useEffect(() => {
    if (popupOrderId) {
      reset({
        jiraTicketNumber: "",
        jiraSummary: "",
        jiraStatus: "",
        scmComments: ""
      });
    }
  }, [popupOrderId]);

  //  CLOSURE POPUP RESET
  useEffect(() => {
    if (closurePopupId) {
      resetClosure({
        jiraStatus: "",
        scmComments: ""
      });
    }
  }, [closurePopupId]);

  // SUBMIT NEW / OLD JIRA
  const submitJiraDetails = async (data) => {
  if (isOld) {
    await createOldJira(popupOrderId, data);
  } else {
    await createJira(popupOrderId, data);
  }

  alert("Jira Details Submitted!");
  reset();
  setPopupOrderId(null);
  setIsOld(false);
};


  //  SUBMIT CLOSURE API HIT (PUT)
 const submitClosure = async (data) => {
  await httpService.updateWithAuth(
    `/api/orders/scm/jira-ticket-closure/${closurePopupId}`,
    data
  );

  alert("Closure Submitted");
  resetClosure();
  setClosurePopupId(null);
  fetchOrders();
};


      // NOTIFY AMISP API CALL
const notifyAmisp = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/orders/scm/notify-amisp/${orderId}`,
      {}   // ❗ no body required
    );

    alert(res);   // 👈 backend response text alert me show hoga
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("Notify Error:", error);
    alert("Notification failed!");
  }
};

 // NOTIFY FINANCE APPROVAL API CALL
const financeApproval = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/orders/scm/approval-request/${orderId}`,
      {}   // ❗ no body required
    );

    alert(res);   // 👈 backend response text alert me show hoga
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("Notify Error:", error);
    alert("Notification failed!");
  }
};

   //------------FINAL CLOSURE ORDER COMPLETED-----------
    const orderCompleted = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/orders/scm/completed/${orderId}`,
      {}   // ❗ no body required
    );

    alert(res);   // 👈 backend response text alert me show hoga
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("completed Error:", error);
    alert("Notification failed!");
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
      selector: (row) => row.orderId, grow: 0.5,
      cell: (row) => <span className="font-bold">{row.orderId}</span>,
    },
    { name: "ORDER DATE", selector: (row) => row.createAt },
    { name: "PROJECT", selector: (row) => row.project, grow: 0.5, },
    { name: "INITIATOR", selector: (row) => row.users?.username || row.initiator, grow: 0.5 },
    { name: "PRODUCT TYPE", selector: (row) => row.productType, grow: 0.7 },
    { name: "Qty", selector: (row) => row.proposedBuildPlanQty, grow: 0.1 },
    { name: "REASON", selector: (row) => row.reasonForBuildRequest, wrap: true },
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
      width: "200px",
      cell: (row) => {
        // Case 1: CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING → Show Jira Closure
        if (row.status === "CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING") {
          return (
            <button
              onClick={() => setClosurePopupId(row.orderId)}
              className="px-5 py-2 bg-yellow-600 text-white rounded-lg"
            >
              Generate Prod-back and Closure
            </button>
          );
        }

        //  Case 2: SYRMA PROD/TEST DONE > SCM ACTION PENDING 
        //   || SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING → Show Notify Button
       if (row.status === "SYRMA PROD/TEST DONE > SCM ACTION PENDING" || 
        row.status === "SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING") {
  return (
    <button
      onClick={() => notifyRma(row.orderId)}   //  FUNCTION CALL
      className="px-5 py-2 bg-purple-600 text-white rounded-lg"
    >
      Notify To RMA
    </button>
  );
}
        //  Case 3: RMA QC PASS > SCM ORDER RELEASE PENDINGG → Show Notify Button
       if (row.status === "RMA QC PASS > SCM ORDER RELEASE PENDING") {
  return (
    <button
      onClick={() => notifyPT(row.orderId)}   // FUNCTION CALL
      className="px-5 py-2 bg-blue-600 text-white rounded-lg"
    >
      Notify To Project Team
    </button>
  );
}
        //Case 4: PROJECT TEAM > SCM READY FOR DISPATCH → Show Notify Button
       if (row.status === "PROJECT TEAM > SCM READY FOR DISPATCH") {
  return (
    <button
      onClick={() => notifyAmisp(row.orderId)}   //  FUNCTION CALL
      className="px-5 py-2 bg-green-600 text-white rounded-lg"
    >
      Notify To AMISP
    </button>
  );
}
        //Case 5: PROJECT TEAM NOTIFY > SCM LOCATION DETAILS → Show Notify Button
       if (row.status === "PROJECT TEAM NOTIFY > SCM LOCATION DETAILS") {
  return (
    <button
      onClick={() => financeApproval(row.orderId)}   //  FUNCTION CALL
      className="px-5 py-2 bg-orange-600 text-white rounded-lg"
    >
      Sent For Finance Approval
    </button>
  );
}

// //         //Case 6: FINANCE > SCM PLAN TO DISPATCH  → Show Notify Button
       if (row.status === "FINANCE > SCM PLAN TO DISPATCH") {
  return (
    <button
      onClick={() => notifyLogistic(row.orderId)}   //  FUNCTION CALL
      className="px-5 py-2 bg-blue-600 text-white rounded-lg"
    >
      Notify To Logistic Team
    </button>
  );
}

// //         //Case 7: FINANCE CLOSURE DONE > SCM CLOSURE PENDING  → Show Notify Button
       if (row.status === "FINANCE CLOSURE DONE > SCM CLOSURE PENDING") {
  return (
    <button
      onClick={() => orderCompleted(row.orderId)}   //  FUNCTION CALL
      className="px-5 py-2 bg-blue-600 text-white rounded-lg"
    >
      Final Closure
    </button>
  );
}


        //  Default → Show New / Old Buttons
        return (
          <div className="flex gap-3">
            <button
              onClick={() => {
                setPopupOrderId(row.orderId);
                setIsOld(false);
              }}
              className="px-5 py-2 bg-cyan-600 text-white rounded-lg"
            >
              New
            </button>

            <button
              onClick={() => {
                setPopupOrderId(row.orderId);
                setIsOld(true);
              }}
              className="px-5 py-2 bg-gray-500 text-white rounded-lg"
            >
              Old
            </button>
          </div>
        );
      },
    },
  ];

  return (
    <>
     <div className="scale-[1] origin-top">
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
         <option value="">STATUS</option>

  <option value="PROJECT TEAM > SCM PENDING">PROJECT TEAM {'>'} SCM PENDING</option>

  <option value="FINANCE APPROVED > SCM PENDING">FINANCE APPROVED {'>'} SCM PENDING</option>

  <option value="CLOUD CREATED CERTIFICATE > SCM PROD-BACK CREATION PENDING">
    CLOUD CREATED CERTIFICATE {'>'} SCM PROD-BACK CREATION PENDING
  </option>

  <option value="SYRMA PROD/TEST DONE > SCM ACTION PENDING">
    SYRMA PROD/TEST DONE {'>'} SCM ACTION PENDING
  </option>

  <option value="RMA QC PASS > SCM ORDER RELEASE PENDING">
    RMA QC PASS {'>'} SCM ORDER RELEASE PENDING
  </option>

  <option value="SYRMA RE-PROD/TEST DONE > SCM ACTION PENDING">
    SYRMA RE-PROD/TEST DONE {'>'} SCM ACTION PENDING
  </option>

  <option value="PROJECT TEAM > SCM READY FOR DISPATCH">
    PROJECT TEAM {'>'} SCM READY FOR DISPATCH
  </option>

  <option value="PROJECT TEAM NOTIFY > SCM LOCATION DETAILS">
    PROJECT TEAM NOTIFY {'>'} SCM LOCATION DETAILS
  </option>

  <option value="FINANCE > SCM PLAN TO DISPATCH">
    FINANCE {'>'} SCM PLAN TO DISPATCH
  </option>

  <option value="FINANCE CLOSURE DONE > SCM CLOSURE PENDING">
    FINANCE CLOSURE DONE {'>'} SCM CLOSURE PENDING
  </option>
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
        pagination
        paginationServer
        paginationTotalRows={filteredCount > 0 ? filteredCount : totalOrders}
        paginationPerPage={ordersPerPage}
        paginationRowsPerPageOptions={[10, 20, 30, 50]}
        onChangeRowsPerPage={(newLimit) => {
          setOrdersPerPage(newLimit);
          setCurrentPage(1);
        }}
        onChangePage={(newPage) => setCurrentPage(newPage)}
        fixedHeader
        highlightOnHover
        fixedHeaderScrollHeight="500px"

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

      {/* ============================= POPUP - NEW/OLD JIRA ============================= */}
      {popupOrderId && (
        <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-xl w-[800px] relative">
            <h2 className="text-2xl font-bold mb-3 text-cyan-700 text-center">
              {isOld ? "UPDATE PROD-BACK GENERATION JIRA DETAILS" : 
              "UPDATE NEW CERTIFICATE CREATION JIRA DETAILS"}
            </h2>

            <p className="text-gray-600 text-center mb-4">
              Order ID: <b>{popupOrderId}</b>
            </p>

            <div className="flex flex-col gap-3">
              <input
  {...register("jiraTicketNumber")}
  placeholder="Jira Ticket Number"
  className={`border p-2 rounded text-black ${
    errors.jiraTicketNumber ? "border-red-500" : ""
  }`}
/>
<p className="text-red-500 text-sm">{errors.jiraTicketNumber?.message}</p>

<textarea
  {...register("jiraSummary")}
  placeholder="Jira Summary"
  className={`border p-2 rounded h-20 text-black ${
    errors.jiraSummary ? "border-red-500" : ""
  }`}
/>
<p className="text-red-500 text-sm">{errors.jiraSummary?.message}</p>

<input
  {...register("jiraStatus")}
  placeholder="Jira Status"
  className={`border p-2 rounded text-black ${
    errors.jiraStatus ? "border-red-500" : ""
  }`}
/>
<p className="text-red-500 text-sm">{errors.jiraStatus?.message}</p>

<textarea
  {...register("scmComments")}
  placeholder="SCM Comments"
  className={`border p-2 rounded h-20 text-black ${
    errors.scmComments ? "border-red-500" : ""
  }`}
/>
<p className="text-red-500 text-sm">{errors.scmComments?.message}</p>

            </div>

            <div className="flex justify-center gap-4 mt-5">
              <button
               onClick={() => {
  reset();                          // first reset form
  setTimeout(() => {
    setPopupOrderId(null);          // then close popup
    setIsOld(false);
  }, 0);                            // 0 ms delay ensures reset applies
}}
                className="px-6 py-2 bg-red-500 text-white rounded-lg"
              >
                ✖
              </button>

              <button
                onClick={handleSubmit(submitJiraDetails)}
                className="px-6 py-2 bg-green-600 text-white rounded-lg"
              >
                Submit
              </button>
            </div>
          </div>
        </div>
      )}

      {/* ============================= POPUP - JIRA CLOSURE ============================= */}
      {closurePopupId && (
        <div className="fixed inset-0 bg-black/30 backdrop-blur-sm flex justify-center items-center z-50">
          <div className="bg-white p-6 rounded-lg shadow-xl w-[800px] relative">
            <h2 className="text-2xl font-bold mb-3 text-yellow-700 text-center">
              JIRA TICKET CLOSURE
            </h2>

            <p className="text-gray-600 text-center mb-4">
              Order ID: <b>{closurePopupId}</b>
            </p>

            <div className="flex flex-col gap-3">
              <input
  {...registerClosure("jiraStatus")}
  placeholder="Jira Status"
  className={`border p-2 rounded text-black ${
    closureErrors.jiraStatus ? "border-red-500" : ""
  }`}
/>
<p className="text-red-500 text-sm">{closureErrors.jiraStatus?.message}</p>

<textarea
  {...registerClosure("scmComments")}
  placeholder="SCM Comments"
  className={`border p-2 rounded h-20 text-black ${
    closureErrors.scmComments ? "border-red-500" : ""
  }`}
/>
<p className="text-red-500 text-sm">{closureErrors.scmComments?.message}</p>

            </div>

            <div className="flex justify-center gap-4 mt-5">
              <button
                onClick={() => {
  resetClosure();                   // reset closure form
  setTimeout(() => {
    setClosurePopupId(null);        // close popup
  }, 0);
}}
                className="px-6 py-2 bg-red-500 text-white rounded-lg"
              >
                ✖
              </button>

              <button
                onClick={submitClosureForm(submitClosure)}
                className="px-6 py-2 bg-yellow-600 text-white rounded-lg"
              >
                Submit Closure
              </button>
            </div>
          </div>
        </div>
      )}
      </div>
    </>
  );
}
