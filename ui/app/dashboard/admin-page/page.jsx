"use client";

import { useEffect, useState } from "react";
import httpService from "../../service/httpService";
import AdminTable from "../../service/adminTable";
import { Cpu } from "lucide-react";
import { getUsernameFromToken, removeToken } from "../../service/cookieService";
import { useRouter } from "next/navigation";

// NORMALIZER
const normalizeRecord = (o) => ({
  orderId: o.orderId,
  createAt: o.createAt,
  project: o.project,
  initiator: o.initiator || "-",
  productType: o.productType,
  proposedBuildPlanQty: o.proposedBuildPlanQty,
  orderType: o.orderType,
  status: o.status,
 
});

export default function AdminCompletedOrders() {
  const [orders, setOrders] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [ordersPerPage, setOrdersPerPage] = useState(10);
  const [totalOrders, setTotalOrders] = useState(0);

  const [filteredData, setFilteredData] = useState([]);
  const [filteredCount, setFilteredCount] = useState(0);

  const [searchFilteredData, setSearchFilteredData] = useState([]);

  const [searchText, setSearchText] = useState("");
  const [statusFilter, setStatusFilter] = useState("");

  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [isDateApplied, setIsDateApplied] = useState(false);
  const [isSearchApplied, setIsSearchApplied] = useState(false);
  const [isStatusApplied, setIsStatusApplied] = useState(false);

  const [noDataFound, setNoDataFound] = useState(false);

  const router = useRouter();
  const [username, setUsername] = useState("");

  // LOAD ALL ADMIN ORDERS
  const fetchOrders = async () => {
    try {
      setUsername(getUsernameFromToken() || "");

      const offset = (currentPage - 1) * ordersPerPage;

      const data = await httpService.get(
        `/api/orders/admin/all-orders?offset=${offset}&limit=${ordersPerPage}`
      );

      const recs = data.records || data.orders || [];

      setOrders(recs.map(normalizeRecord));
      setTotalOrders(data.ordersCount || recs.length);

      setFilteredData([]);
      setSearchFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(false);
    } catch (err) {
      console.log("Admin Fetch Error:", err);
    }
  };

  useEffect(() => {
    if (isSearchApplied) return applySearchFilter(searchText);
    if (isStatusApplied) return applyStatusFilter(statusFilter);
    if (isDateApplied) return applyDateFilter();
    fetchOrders();
  }, [currentPage, ordersPerPage]);

  // DATE FILTER
  const applyDateFilter = async () => {
    if (!startDate || !endDate) return alert("Select both dates");

    const page = currentPage - 1;

    const data = await httpService.get(
      `/api/orders/admin/date-filter?startDate=${startDate}&endDate=${endDate}&page=${page}&size=${ordersPerPage}`
    );

    const records = (data.records || []).map(normalizeRecord);

    if (records.length === 0) {
      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(true);
    } else {
      setFilteredData(records);
      setFilteredCount(data.totalElements || 0);
      setNoDataFound(false);
    }

    setIsDateApplied(true);
    setIsSearchApplied(false);
    setIsStatusApplied(false);
  };

  // STATUS FILTER
  const applyStatusFilter = async (value) => {
    setStatusFilter(value);
    if (!value) return fetchOrders();

    const page = currentPage - 1;

    const data = await httpService.get(
      `/api/orders/admin/status-filter?status=${value}&page=${page}&size=${ordersPerPage}`
    );

    const records = (data.records || []).map(normalizeRecord);

    if (records.length === 0) {
      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(true);
    } else {
      setFilteredData(records);
      setFilteredCount(data.totalElements || 0);
      setNoDataFound(false);
    }

    setIsStatusApplied(true);
    setIsSearchApplied(false);
    setIsDateApplied(false);
  };

  // SEARCH FILTER
  const applySearchFilter = async (text) => {
    setSearchText(text);

    if (!text.trim()) {
      setSearchFilteredData([]);
      setIsSearchApplied(false);
      fetchOrders();
      return;
    }

    const page = currentPage - 1;

    const data = await httpService.get(
      `/api/orders/admin/search?keyword=${text}&page=${page}&size=${ordersPerPage}`
    );

    const records = (data.records || []).map(normalizeRecord);

    if (records.length === 0) {
      setSearchFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(true);
    } else {
      setSearchFilteredData(records);
      setFilteredCount(data.totalElements || 0);
      setNoDataFound(false);
    }

    setIsSearchApplied(true);
    setIsDateApplied(false);
  };

  return (
    <div className="min-h-screen w-full bg-[#e3f3ff] flex flex-col items-center py-10">

      <div className="flex items-center gap-3 mb-4 mt-5">
        <Cpu className="w-10 h-10 text-[#02A3EE]" />
        <h1 className="text-4xl font-bold text-[#02A3EE] tracking-wide ">
          ADMIN COMPLETED ORDERS
        </h1>
      </div>

      <div className="w-[95%] bg-white shadow-xl rounded-xl p-6 border border-[#d4e8ff]">
        <AdminTable
          orders={orders}
          totalOrders={totalOrders}
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          ordersPerPage={ordersPerPage}
          setOrdersPerPage={setOrdersPerPage}
          filteredData={filteredData}
          filteredCount={filteredCount}
          noDataFound={noDataFound}
          startDate={startDate}
          endDate={endDate}
          setStartDate={setStartDate}
          setEndDate={setEndDate}
          applyDateFilter={applyDateFilter}
          searchText={searchText}
          applySearchFilter={applySearchFilter}
          searchFilteredData={searchFilteredData}
          statusFilter={statusFilter}
          applyStatusFilter={applyStatusFilter}
        />
      </div>
    </div>
  );
}
