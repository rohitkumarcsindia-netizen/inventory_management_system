"use client";

import { useEffect, useState } from "react";
import FinanceTable from "../../../service/financeTable";
import httpService from "../../../service/httpService";
import { getUsernameFromToken, removeToken } from "../../../service/cookieService";
import { useRouter } from "next/navigation";

export default function FinanceTeamPage() {
  const [orders, setOrders] = useState([]);
  const [totalOrders, setTotalOrders] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [ordersPerPage, setOrdersPerPage] = useState(10);
  const [username, setUsername] = useState("");

  const [filteredData, setFilteredData] = useState([]);
  const [filteredCount, setFilteredCount] = useState(0);

  const [isDateApplied, setIsDateApplied] = useState(false);  // NEW
  const [isSearchApplied, setIsSearchApplied] = useState(false);
  const [isStatusApplied, setIsStatusApplied] = useState(false);

  const [statusFilter, setStatusFilter] = useState("");

  const [searchText, setSearchText] = useState("");
  const [searchFilteredData, setSearchFilteredData] = useState([]);

  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");



  const [noDataFound, setNoDataFound] = useState(false);

  const router = useRouter();

  // FETCH ORIGINAL DATA
  const fetchOrders = async () => {
    try {
      const name = getUsernameFromToken();
      setUsername(name || "");

      const offset = (currentPage - 1) * ordersPerPage;

      const data = await httpService.get(
        `/api/v1/orders/finance/pending?offset=${offset}&limit=${ordersPerPage}`
      );

      setOrders(data.orders || []);
      setTotalOrders(data.ordersCount || 0);

      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(false);

      setIsDateApplied(false);
      setIsSearchApplied(false);

    } catch (err) {
      console.log("Finance Fetch Error:", err);
    }
  };

      useEffect(() => {
    setUsername(getUsernameFromToken() || "");

    if (isSearchApplied) {
      applySearchFilter(searchText);
      return;
    }

    if (isStatusApplied) {
      applyStatusFilter(statusFilter);
      return;
    }

    if (isDateApplied) {
      applyDateFilter();
      return;
    }

    fetchOrders();
  }, [currentPage, ordersPerPage]);

  // DATE FILTER API
  const applyDateFilter = async () => {
    if (!startDate || !endDate) return alert("Select both dates");

    const page = currentPage - 1;

    const data = await httpService.get(
      `/api/v1/orders/finance/date-filter?startDate=${startDate}&endDate=${endDate}&page=${page}&size=${ordersPerPage}`
    );

    const records = data.records || [];

    if (records.length === 0) {
      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(true);
    } else {
      setFilteredData(records);
      setFilteredCount(data.totalElements || 0);
      setNoDataFound(false);
    }

    setIsDateApplied(true);     // ordering set
    setIsSearchApplied(false);
  };

  // STATUS FILTER API
  const applyStatusFilter = async (value) => {
    setStatusFilter(value);

    if (!value) {
      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(false);
      setIsStatusApplied(false);
      fetchOrders();
      return;
    }

    const page = currentPage - 1;
    const data = await httpService.get(
      `/api/v1/orders/finance/status-filter?status=${value}&page=${page}&size=${ordersPerPage}`
    );

    const records = data.records || [];

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

  // SEARCH API
  const applySearchFilter = async (text) => {
    setSearchText(text);

    if (!text.trim()) {
      setNoDataFound(false);
      setIsSearchApplied(false);
      setSearchFilteredData([]);
      fetchOrders();
      return;
    }

    const page = currentPage - 1;
    const data = await httpService.get(
      `/api/v1/orders/finance/search?keyword=${text}&page=${page}&size=${ordersPerPage}`
    );

    const records = data.records || [];

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

  // RESET WHEN SEARCH CLEARS
  useEffect(() => {
    if (!searchText.trim()) {
      setNoDataFound(false);
      setIsSearchApplied(false);
      setSearchFilteredData([]);
      fetchOrders();
    }
  }, [searchText]);


  

  // LOGOUT
  const handleLogout = () => {
    removeToken();
    setUsername("");
    router.push("/");
  };

  return (
    <div className="min-h-screen w-full bg-[#e3f3ff] flex flex-col items-center py-10 relative">

      {/* USER + LOGOUT */}
      <div className="absolute top-5 right-6 flex items-center gap-5 bg-white shadow-md px-5 py-2 rounded-lg border border-[#cce7ff]">
        <span className="text-lg font-semibold text-[#003b66]">👤 {username || "User"}</span>
        <button onClick={handleLogout} className="bg-red-500 text-white px-4 py-1.5 rounded-md hover:bg-red-600">
          Logout
        </button>
      </div>

      {/* LOGO */}
      <div className="absolute top-4 left-6">
        <img src="/cyanconnode-logo.png" className="w-60 opacity-90" />
      </div>

      <h1 className="text-4xl font-bold text-[#02A3EE] mt-5 mb-4">
        FINANCE PENDING ORDERS
      </h1>

      {/* TABLE */}
      <div className="w-[95%] bg-white shadow-xl rounded-xl p-6 border border-[#d4e8ff]">
        <FinanceTable
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
          setSearchText={setSearchText}
          searchFilteredData={searchFilteredData}
          setSearchFilteredData={setSearchFilteredData}
          applySearchFilter={applySearchFilter}
          isStatusApplied={isStatusApplied}
          setIsDateApplied={setIsDateApplied}
          statusFilter={statusFilter}
          applyStatusFilter={applyStatusFilter}
          fetchOrders={fetchOrders}
        />
      </div>
    </div>
  );
}
