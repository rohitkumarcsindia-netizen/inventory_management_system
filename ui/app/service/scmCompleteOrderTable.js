"use client";

import DataTable from "react-data-table-component";

export default function ScmCompleteOrderTable({
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
    { name: "Order ID", selector: (row) => row.orderId,
      cell: (row) => <span className="font-bold">{row.orderId}</span>
     },
    { name: "ORDER DATE", selector: (row) => row.createAt,
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
    { name: "INITIATOR", selector: (row) => row.users?.username },
    { name: "PRODUCT TYPE", selector: (row) => row.productType, grow: 1.1,
          cell: (row) => (
    <span
      dangerouslySetInnerHTML={{
        __html: highlightText(row.productType),
      }}
    />
  ),
     },
    { name: "QTY", selector: (row) => row.proposedBuildPlanQty },
    { name: "ACTION", selector: (row) => row.scmAction,
       grow: 1.5,
  cell: (row) => (
    <span className="font-bold">
      {row.scmAction}
    </span>
  ),
     },
    { name: "ACTION TIME", selector: (row) => row.scmActionTime || row.actionTime,
       grow: 1.5,
cell: (row) => {
    const { date, time } = formatOrderDateTime(row.scmActionTime);

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
    { name: "INITIATOR ID", selector: (row) => row.users?.userId },
    { name: "JIRA SUMMARY", selector: (row) => row.jiraSummary, grow: 1.1 }
  ];

  return (
    <div className="scale-[1] origin-top">

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
          <option value="JIRA_VERIFIED">JIRA_VERIFIED</option>
          <option value="JIRA FILLED">JIRA FILLED</option>
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

      {/* FIXED DATATABLE */}
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

        onChangePage={(page) => {
          console.log("Page changed:", page);
          setCurrentPage(page);
        }}

        onChangeRowsPerPage={(limit) => {
          setOrdersPerPage(limit);
          setCurrentPage(1);
        }}

       customStyles={{
  rows: {
    style: {
      borderBottom: "1px solid #e5e7eb",   // light grey divider
      paddingTop: "12px",
      paddingBottom: "12px",
    },
  },
  headRow: {
    style: {
      backgroundColor: "#e8f3ff",
      fontWeight: "bold",
      fontSize: "16px",
      borderBottom: "2px solid #c0d9ff",   // header bottom border
    },
  },
  cells: {
    style: {
      borderRight: "1px solid #7eb8dfff",    // vertical cell divider
      paddingLeft: "14px",
      paddingRight: "14px",
    },
  },
}}
      />
    </div>
  );
}
