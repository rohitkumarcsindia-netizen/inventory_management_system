"use client";

import DataTable from "react-data-table-component";

export default function AdminTable({
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
  applyStatusFilter,
}) {

  const displayedData =
    searchFilteredData?.length > 0
      ? searchFilteredData
      : filteredData?.length > 0
      ? filteredData
      : orders;

  const columns = [
    { name: "Order ID", selector: row => row.orderId, cell: row => <b>{row.orderId}</b> },
    { name: "ORDER DATE", selector: row => row.createAt },
    { name: "PROJECT", selector: row => row.project },
    { name: "INITIATOR", selector: row => row.initiator },
    { name: "PRODUCT TYPE", selector: row => row.productType },
    { name: "QTY", selector: row => row.proposedBuildPlanQty },
    { name: "ORDER TYPE", selector: row => row.orderType },
    { name: "STATUS", selector: row => row.status, grow:1.5,
      cell: row => <b>{row.status}</b>
    },
  ];

  return (
    <div>

      <div className="flex justify-end mb-2 font-semibold text-black">
        Total Orders: {noDataFound ? 0 : (filteredCount > 0 ? filteredCount : totalOrders)}
      </div>

      {/* FILTER BAR */}
      <div className="flex justify-end gap-3 mb-3 text-black">

        <span className="font-bold">From:</span>
        <input type="date" value={startDate} onChange={e=>setStartDate(e.target.value)}
          className="px-3 py-2 border rounded-md shadow-sm" />

        <span className="font-bold">To:</span>
        <input type="date" value={endDate} onChange={e=>setEndDate(e.target.value)}
          className="px-3 py-2 border rounded-md shadow-sm" />

        <button onClick={() => { setCurrentPage(1); applyDateFilter(); }}
          className="bg-cyan-500 text-white px-3 rounded-md">
          Search
        </button>

        {/* STATUS */}
        <select value={statusFilter}
          onChange={e => { setCurrentPage(1); applyStatusFilter(e.target.value); }}
          className="px-3 py-2 border rounded-md shadow-sm">

         <option value="">Status</option>
          <option value="PROJECT TEAM PENDING">PROJECT TEAM PENDING</option>
          <option value="PROJECT TEAM > FINANCE PRE APPROVAL PENDING">PROJECT TEAM {'>'} FINANCE PRE APPROVAL PENDING</option>
          <option value="FINANCE TEAM REJECTED">Finance TEAM Rejected</option>
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
          <option value="PROJECT TEAM NOTIFY > SCM LOCATION DETAILS">PROJECT TEAM NOTIFY {'>'} SCM LOCATION DETAILS</option>
          <option value="SCM > FINANCE POST APPROVAL PENDING">SCM {'>'} FINANCE POST APPROVAL PENDING</option>
          <option value="FINANCE > SCM PLAN TO DISPATCH">FINANCE {'>'} SCM PLAN TO DISPATCH</option>
          <option value="SCM > LOGISTIC PENDING">SCM {'>'} LOGISTIC PENDING</option>
          <option value="DELIVERY PENDING">DELIVERY PENDING</option>
          <option value="PDI PENDING">PDI PENDING</option>
          <option value="LOGISTIC > FINANCE CLOSURE PENDING">LOGISTIC {'>'} FINANCE CLOSURE PENDING</option>
          <option value="FINANCE CLOSURE DONE > SCM CLOSURE PENDING">FINANCE CLOSURE DONE {'>'} SCM CLOSURE PENDING</option>
          <option value="COMPLETED">COMPLETED</option>
        </select>

        {/* SEARCH */}
        <div className="relative">
          <input
            type="text"
            placeholder="Search orders..."
            value={searchText}
            onChange={(e) => { setCurrentPage(1); applySearchFilter(e.target.value); }}
            className="px-4 py-2 border rounded-md w-64 shadow-sm pr-8"
          />
          {searchText && (
            <button onClick={() => applySearchFilter("")}
              className="absolute right-2 top-2 text-gray-600">
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
        onChangePage={(p) => setCurrentPage(p)}
        onChangeRowsPerPage={(limit) => { setOrdersPerPage(limit); setCurrentPage(1); }}
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
