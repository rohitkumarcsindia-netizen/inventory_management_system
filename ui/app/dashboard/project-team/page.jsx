"use client";

import { useEffect, useState } from "react";
import httpService from "../../service/httpService";
import OrdersTable from "../../service/ordersTable";
import { useRouter } from "next/navigation";
import { Cpu } from "lucide-react";
import { getUsernameFromToken, removeToken } from "../../service/cookieService";

export default function GetOrders() {
  const [orders, setOrders] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalOrders, setTotalOrders] = useState(0);
  const [ordersPerPage, setOrdersPerPage] = useState(10);

  const [filteredData, setFilteredData] = useState([]);
  const [filteredCount, setFilteredCount] = useState(0);

  const [searchFilteredData, setSearchFilteredData] = useState([]);
  const [noDataFound, setNoDataFound] = useState(false);


  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");

  const [statusFilter, setStatusFilter] = useState("");
  const [searchText, setSearchText] = useState("");

  const [isDateApplied, setIsDateApplied] = useState(false);
  const [isStatusApplied, setIsStatusApplied] = useState(false);
  const [isSearchApplied, setIsSearchApplied] = useState(false);

  const router = useRouter();
  const [username, setUsername] = useState("");

  // FETCH ORDERS DEFAULT
  const fetchOrders = async () => {
    try {
      const offset = (currentPage - 1) * ordersPerPage;

      const data = await httpService.get(
        `/api/v1/orders/project/page?offset=${offset}&limit=${ordersPerPage}`
      );

      setOrders(data.orders || []);
      setTotalOrders(data.ordersCount || 0);

      setFilteredData([]);
      setFilteredCount(0);
      setSearchFilteredData([]);
      setIsStatusApplied(false);
      setIsSearchApplied(false);
      setIsDateApplied(false);
      setNoDataFound(false);
    } catch (e) {
      console.log(e);
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
      `/api/v1/orders/project/date-filter?startDate=${startDate}&endDate=${endDate}&page=${page}&size=${ordersPerPage}`
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
    setIsDateApplied(true);
    setIsStatusApplied(false);
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
      `/api/v1/orders/project/status-filter?status=${value}&page=${page}&size=${ordersPerPage}`
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

  // SEARCH FILTER API
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
      `/api/v1/orders/project/search?keyword=${text}&page=${page}&size=${ordersPerPage}`
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
  
    //  NOTIFY LOCATION SCM API CALL
const notifyLocScm = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/v1/orders/project/notify-scm-location-details/${orderId}`,
      {}   // ❗ no body required
    );

    alert(res);   // 👈 backend response text alert me show hoga
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("Notify Error:", error);
    alert("Notification failed!");
  }
};

  // LOGOUT
  const handleLogout = () => {
    removeToken();
    setUsername("");
    router.push("/login");
  };

  return (
    <div className="min-h-screen w-full bg-[#e3f3ff] flex flex-col items-center py-10 relative">

      {/* TOP USER */}
      <div className="absolute top-5 right-6 flex items-center gap-5 bg-white shadow-md px-4 py-2 rounded-lg">
        <span className="text-lg font-semibold text-[#003b66]">👤 {username || "User"}</span>
        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-4 py-1.5 rounded-md"
        >
          Logout
        </button>
      </div>

      {/* LOGO */}
      <div className="absolute top-4 left-6">
        <img src="/cyanconnode-logo.png" className="w-60 opacity-90" />
      </div>

      {/* HEADING */}
      <div className="flex items-center gap-3 mt-5 mb-4">
        <Cpu className="w-10 h-10 text-[#02A3EE]" />
        <h1 className="text-4xl font-bold text-[#02A3EE]">ORDER LIST</h1>
      </div>

      {/* TABLE */}
      <div className="w-[95%] bg-white shadow-xl rounded-xl p-6 border border-[#d4e8ff]">
        <OrdersTable
          orders={orders}
          filteredData={filteredData}
          searchFilteredData={searchFilteredData}
          totalOrders={totalOrders}
          filteredCount={filteredCount}
          noDataFound={noDataFound}
          currentPage={currentPage}
          setCurrentPage={setCurrentPage}
          ordersPerPage={ordersPerPage}
          setOrdersPerPage={setOrdersPerPage}
          searchText={searchText}
          applySearchFilter={applySearchFilter}
          startDate={startDate}
          endDate={endDate}
          setStartDate={setStartDate}
          setEndDate={setEndDate}
          applyDateFilter={applyDateFilter}
          statusFilter={statusFilter}
          applyStatusFilter={applyStatusFilter}
          
          notifyLocScm={notifyLocScm}
          fetchOrders={fetchOrders}
        />
      </div>

      {/* CREATE NEW ORDER */}
      <button
        onClick={() => router.push("/orderInitiation")}
        className="fixed bottom-8 left-8 bg-[#02A3EE] text-white px-6 py-4 rounded-2xl shadow-xl"
      >
        Create New Order
        <div className="text-sm opacity-70">Start Order Request</div>
      </button>
    </div>
  );
}
