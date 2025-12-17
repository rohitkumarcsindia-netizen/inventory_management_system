"use client";

import DataTable from "react-data-table-component";

export default function ProjectAndProductControlTable({ orders }) {
  const columns = [
    {
      name: "ID",
      selector: row => row.id,
      cell: row => <b>{row.id}</b>,
    },
    {
      name: "PROJECT",
      selector: row => row.projectType,
    },
    {
      name: "PRODUCT TYPE",
      selector: row => row.productType,
    },
  
    {
      name: "CREATED BY",
      selector: row => row.createdBy?.username || "-",
    },
      {
      name: "ACTION BY",
      selector: row => row.updatedBy?.userId || "-",
    },
  ];

  return (
    <>
      <div className="flex justify-end mb-2 text-black font-semibold">
        Total: {orders.length}
      </div>

      <DataTable
        columns={columns}
        data={orders}
        highlightOnHover
        fixedHeader
        fixedHeaderScrollHeight="500px"
        pagination
        customStyles={{
          headRow: {
            style: {
              backgroundColor: "#e8f3ff",
              fontWeight: "bold",
            },
          },
        }}
      />
    </>
  );
}
