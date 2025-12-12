"use client";

import DataTable from "react-data-table-component";

export default function LogisticCompleteOrderTable({
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
  statusFilter,
  applyStatusFilter
}) {

    const displayedData =
  searchFilteredData?.length > 0
    ? searchFilteredData
    : filteredData?.length > 0
    ? filteredData
    : orders;

  const columns = [
    { name: "Order ID", selector: (row) => row.orderId,
      cell: (row) => <span className="font-bold">{row.orderId}</span>
     },
    { name: "ORDER DATE", selector: (row) => row.createAt },
    { name: "PROJECT", selector: (row) => row.project },
    { name: "INITIATOR", selector: (row) => row.initiator || "-" },
    { name: "PRODUCT TYPE", selector: (row) => row.productType, grow: 1.1 },
    { name: "QTY", selector: (row) => row.proposedBuildPlanQty },
    { name: "ACTION", selector: (row) => row.pdiAction,
       grow: 1.5,
  cell: (row) => (
    <span className="font-bold">
      {row.pdiAction}
    </span>
  ),
     },
    { name: "ACTION TIME", selector: (row) => row.actionTime,
       grow: 1.5,
  cell: (row) => (
    <span className="font-bold">
      {row.actionTime}
    </span>
  ),
     },
    { name: "ACTION BY", selector: (row) => row.actionBy?.userId || row.actionBy },

    { name: "COMMENT", selector: (row) => row.logisticsComment }
  ];

  return (
    <div className="scale-[1] origin-top">

     <div className="flex justify-end mb-2 px-1 font-semibold text-black">
  Total Orders:&nbsp;
  {noDataFound ? 0 : (filteredCount > 0 ? filteredCount : totalOrders)}
</div>

      <div className="flex justify-end gap-3 mb-3 text-black">

        {/* {DATE} */}
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

          {/* {STATUS} */}
        <select
          value={statusFilter}
          onChange={(e) => { setCurrentPage(1); applyStatusFilter(e.target.value); }}
          className="px-3 py-2 border rounded-md shadow-sm"
        >
          <option value="">STATUS</option>
          <option value="PDI PASS">PDI PASS</option>
          <option value="PDI FAIL">PDI FAIL</option>
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
      borderRight: "1px solid #7eb8dfff",    
      paddingLeft: "14px",
      paddingRight: "14px",
    },
  },
}}
      />
    </div>
  );
}
