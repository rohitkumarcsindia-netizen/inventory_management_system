"use client";

import { useEffect, useState } from "react";
import httpService from "../../../service/httpService";
import ScmCompleteOrderTable from "../../../service/scmCompleteOrderTable";
import { Cpu } from "lucide-react";
import { getUsernameFromToken, removeToken } from "../../../service/cookieService";
import { useRouter } from "next/navigation";

export default function ScmCompleteOrders() {
  const [orders, setOrders] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalOrders, setTotalOrders] = useState(0);
  const [ordersPerPage, setOrdersPerPage] = useState(10);

  const [filteredData, setFilteredData] = useState([]);
  const [searchFilteredData, setSearchFilteredData] = useState([]);

  const [filteredCount, setFilteredCount] = useState(0);
  const [noDataFound, setNoDataFound] = useState(false);


  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [statusFilter, setStatusFilter] = useState("");

  const [searchText, setSearchText] = useState("");

  const [isDateApplied, setIsDateApplied] = useState(false);
  const [isStatusApplied, setIsStatusApplied] = useState(false);
  const [isSearchApplied, setIsSearchApplied] = useState(false);

  const [username, setUsername] = useState("");
  const router = useRouter();

  const normalizeFinanceRecord = (rec) => {
  const o = rec.order || rec;

  let approved = rec.approvedBy || o.approvedBy;
  let approvedUserId = null;

  if (typeof approved === "number") {
    approvedUserId = approved;
  }

  if (approved && typeof approved === "object") {
    approvedUserId = approved.userId;
  }

  return {
    orderId: o.orderId,
    createAt: o.createAt,
    project: o.project,
    initiator: o.initiator,
    productType: o.productType,
    proposedBuildPlanQty: o.proposedBuildPlanQty,
    scmAction: rec.scmAction || o.scmAction,
    scmActionTime: rec.actionTime || o.scmActionTime,
    jiraSummary: rec.jiraSummary || o.jiraSummary,
    approvedByUserId: approvedUserId,
    users: o.users
  };
};

  // 🔹 FETCH DEFAULT DATA
  const fetchOrders = async () => {
    try {
      const offset = (currentPage - 1) * ordersPerPage;

      const data = await httpService.get(
        `/api/v1/orders/scm/complete?offset=${offset}&limit=${ordersPerPage}`
      );

      const recs = data.records || data.orders || [];

      const normalized = recs.map(normalizeFinanceRecord);
      setOrders(normalized);

      setTotalOrders(data.ordersCount || data.totalElements || recs.length);

      setFilteredData([]);
      setFilteredCount(0);
      setSearchFilteredData([]);
      setNoDataFound(false);

      setIsStatusApplied(false);
      setIsDateApplied(false);
      setIsSearchApplied(false);

    } catch (e) {
      console.log("Fetch error", e);
    }
  };

  useEffect(() => {
    setUsername(getUsernameFromToken() || "");

    if (isSearchApplied) return applySearchFilter(searchText);
    if (isStatusApplied) return applyStatusFilter(statusFilter);
    if (isDateApplied) return applyDateFilter();

    fetchOrders();
  }, [currentPage, ordersPerPage]);

  // 🔹 STATUS FILTER
  const applyStatusFilter = async (value) => {
    setStatusFilter(value);

    if (!value) return fetchOrders();

    const page = currentPage - 1;

    const data = await httpService.get(
      `/api/v1/orders/scm/complete/status-filter?status=${value}&page=${page}&size=${ordersPerPage}`
    );

    const records = (data.records || []).map(normalizeFinanceRecord);

    if (records.length === 0) {
      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(true);
    } else {
      setFilteredData(records);
      setFilteredCount(data.totalElements || 0);
      setNoDataFound(false);
    }

    setSearchFilteredData([]);
    setIsStatusApplied(true);
    setIsDateApplied(false);
    setIsSearchApplied(false);
  };

  // 🔹 DATE FILTER
  const applyDateFilter = async () => {
    if (!startDate || !endDate) return alert("Select both dates");

    const page = currentPage - 1;
    const data = await httpService.get(
      `/api/v1/orders/scm/complete/date-filter?startDate=${startDate}&endDate=${endDate}&page=${page}&size=${ordersPerPage}`
    );

    const records = (data.records || []).map(normalizeFinanceRecord);

    if (records.length === 0) {
      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(true);
    } else {
      setFilteredData(records);
      setFilteredCount(data.totalElements || 0);
      setNoDataFound(false);
    }

    setSearchFilteredData([]);
    setIsDateApplied(true);
    setIsStatusApplied(false);
    setIsSearchApplied(false);
  };

  // 🔹 SEARCH FILTER
  const applySearchFilter = async (text) => {
    setSearchText(text);

    if (!text.trim()) return fetchOrders();

    const page = currentPage - 1;
    const data = await httpService.get(
      `/api/v1/orders/scm/complete/search?keyword=${text}&page=${page}&size=${ordersPerPage}`
    );

    const records = (data.records || []).map(normalizeFinanceRecord);

    if (records.length === 0) {
      setSearchFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(true);
    } else {
      setSearchFilteredData(records);
      setFilteredCount(data.totalElements || 0);
      setNoDataFound(false);
    }

    setFilteredData([]);
    setIsSearchApplied(true);
    setIsStatusApplied(false);
    setIsDateApplied(false);
  };

  useEffect(() => {
    if (!searchText.trim()) {
      setNoDataFound(false);
      fetchOrders();
    }
  }, [searchText]);

  const handleLogout = () => {
    removeToken();
    router.push("/login");
  };

  return (
    <div className="min-h-screen w-full bg-[#e3f3ff] flex flex-col items-center py-10 relative">

      {/* TOP RIGHT */}
      <div className="absolute top-5 right-6 flex items-center gap-5 bg-white shadow-md px-5 py-2 rounded-lg border border-[#cce7ff]">
        <span className="text-lg font-semibold text-[#003b66]">👤 {username}</span>

        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-4 py-1.5 rounded-md hover:bg-red-600 transition font-medium"
        >
          Logout
        </button>
      </div>

      <div className="absolute top-4 left-6">
        <img src="/cyanconnode-logo.png" className="w-60 opacity-90" />
      </div>

      <div className="flex items-center gap-3 mb-4 mt-5">
        <Cpu className="w-10 h-10 text-[#02A3EE]" />
        <h1 className="text-4xl font-bold text-[#02A3EE] tracking-wide ">
          SCM COMPLETED ORDERS
        </h1>
      </div>

      <div className="w-[95%] bg-white shadow-xl rounded-xl p-6 border border-[#d4e8ff]">
        <ScmCompleteOrderTable
          orders={orders}
          totalOrders={totalOrders}
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          ordersPerPage={ordersPerPage}
          setOrdersPerPage={setOrdersPerPage}
          filteredCount={filteredCount}
          filteredData={filteredData}
          searchFilteredData={searchFilteredData}
          noDataFound={noDataFound}
          searchText={searchText}
          applySearchFilter={applySearchFilter}
          startDate={startDate}
          endDate={endDate}
          setStartDate={setStartDate}
          setEndDate={setEndDate}
          applyDateFilter={applyDateFilter}
          statusFilter={statusFilter}
          applyStatusFilter={applyStatusFilter}
         
        />
      </div>
    </div>
  );
}
