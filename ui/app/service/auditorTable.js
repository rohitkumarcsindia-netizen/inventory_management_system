"use client";

import DataTable from "react-data-table-component";

export default function AuditorTable({
  orders,
  totalOrders,
  currentPage,
  setCurrentPage,
  ordersPerPage,
  setOrdersPerPage,
  filteredData,
  searchFilteredData,
  filteredCount,
  noDataFound,
  searchText,
  applySearchFilter,
  startDate,
  endDate,
  setStartDate,
  setEndDate,
  applyDateFilter,
  statusFilter,
  applyStatusFilter
}) {

  const displayedData =
    searchFilteredData.length > 0
      ? searchFilteredData
      : filteredData.length > 0
      ? filteredData
      : orders;

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
    { name: "ORDER ID", selector: row => row.orderId || "-", 
      cell: (row) => <span className="font-bold">{row.orderId}</span>,
     },
    { name: "ORDER DATE", selector: row => row.createAt || "-", 
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
    { name: "PROJECT", selector: row => row.project || "-",
          cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.project),
      }}
    />
  ),
     },
    { name: "PRODUCT TYPE", selector: row => row.productType || "-", grow: 1.1,
          cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.productType),
      }}
    />
  ),
     },
    { name: "QTY", selector: row => row.proposedBuildPlanQty || "-" },
    { name: "ORDER TYPE", selector: row => row.orderType || "-",
       grow: 1.5,
     },
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
    { name: "APPROVED BY", selector: row => row.users?.username || "-" },
    { name: "REASON", selector: row => row.reasonForBuildRequest || "-" }
  ];

   const ExpandedOrderDetails = ({ data }) => {
  const Field = ({ label, value, type = "text" }) => {
  let formattedValue = value || "-";

  if (type === "date" && value) {
    const { date } = formatOrderDateTime(value);
    formattedValue = date;   // ✅ 19-Dec-25
  }

  return (
    <div>
      <label className="text-xs font-semibold text-gray-600">
        {label}
      </label>
      <input
        type="text"
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
          <Field label="PM's Remarks" value={data.pmsRemarks} />
        

      </div>
    </div>
  );
};

  return (
    <div className="w-full">

      <div className="flex justify-end mb-2 px-1 text-black font-semibold text-sm">
        Total Orders:&nbsp;
        {noDataFound ? 0 : (filteredCount > 0 ? filteredCount : totalOrders)}
      </div>

      <div className="flex justify-end gap-3 mb-3 text-black">

        {/* {DATE} */}
        <label className="font-bold">From:</label>
        <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} className="px-3 py-2 border rounded-md shadow-sm" />
        <label className="font-bold">To:</label>
        <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} className="px-3 py-2 border rounded-md shadow-sm" />

        <button onClick={() => { setCurrentPage(1); applyDateFilter(); }} className="bg-cyan-500 text-white px-3 rounded-md">
          Search
        </button>

        {/* {STATUS} */}
        <select
          value={statusFilter}
          onChange={(e) => { setCurrentPage(1); applyStatusFilter(e.target.value); }}
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

      {/* {SEARCH} */}
        <div className="relative">
          <input
            type="text"
            placeholder="Search orders..."
            value={searchText}
            onChange={(e) => { setCurrentPage(1); applySearchFilter(e.target.value); }}
            className="px-4 py-2 border rounded-md w-64 shadow-sm pr-8"
          />
          {searchText && (
            <button onClick={() => applySearchFilter("")} className="absolute right-2 top-2 text-gray-500 hover:text-red-500 text-lg">
              ✖
            </button>
          )}
        </div>
      </div>

      {/* Custom No-Data Message */}
      {noDataFound && (
        <div className="text-center text-red-500 font-bold py-5">
          No Records Found
        </div>
      )}

      <DataTable
        columns={columns}
        data={!noDataFound ? displayedData : []}
        highlightOnHover
        fixedHeader
        fixedHeaderScrollHeight="500px"
        pagination
        paginationServer
        paginationPerPage={ordersPerPage}
        paginationTotalRows={filteredCount > 0 ? filteredCount : totalOrders}
         expandableRows 
        expandableRowsComponent={ExpandedOrderDetails} 
        expandableRowsHideExpander={false} 
        paginationDefaultPage={currentPage}
        onChangePage={setCurrentPage}
        onChangeRowsPerPage={(newPerPage) => {
          setOrdersPerPage(newPerPage);
          setCurrentPage(1);
        }}

        noDataComponent={<div></div>}  // hides default text

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
  );
}
