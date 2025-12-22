"use client";
import { useEffect, useState } from "react";
import ScmTable from "../../../service/scmTable";
import httpService from "../../../service/httpService";
import { useRouter } from "next/navigation";
import { getUsernameFromToken, removeToken } from "../../../service/cookieService";

export default function ScmTeamPage() {
  const [orders, setOrders] = useState([]);
  const [totalOrders, setTotalOrders] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [ordersPerPage, setOrdersPerPage] = useState(10);

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

  const [username, setUsername] = useState("");
  const router = useRouter();

   // FETCH ORIGINAL DATA
  const fetchOrders = async () => {
    try {
      const name = getUsernameFromToken();
      setUsername(name || "");

      const offset = (currentPage - 1) * ordersPerPage;

      const data = await httpService.get(
        `/api/v1/orders/scm/pending?offset=${offset}&limit=${ordersPerPage}`
      );

      setOrders(data.orders || []);
      setTotalOrders(data.ordersCount || 0);

      setFilteredData([]);
      setFilteredCount(0);
      setNoDataFound(false);

      setIsDateApplied(false);
      setIsSearchApplied(false);

    } catch (err) {
      console.log("scm Fetch Error:", err);
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
      `/api/v1/orders/scm/date-filter?startDate=${startDate}&endDate=${endDate}&page=${page}&size=${ordersPerPage}`
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

    setIsDateApplied(true);     // 🔥 ordering set
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
      `/api/v1/orders/scm/status-filter?status=${value}&page=${page}&size=${ordersPerPage}`
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
      `/api/v1/orders/scm/search?keyword=${text}&page=${page}&size=${ordersPerPage}`
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

  // NEW JIRA API
  const createJira = async (orderId, jiraPayload) => {
    try {
      await httpService.postWithAuth(
        `/api/v1/orders/scm/jira/details/${orderId}`,
        jiraPayload
      );

      fetchOrders();
    } catch (error) {
      console.error("Jira Create Error:", error);
    }
  };

  // OLD JIRA API
  const createOldJira = async (orderId, jiraPayload) => {
    try {
      await httpService.postWithAuth(
        `/api/v1/orders/scm/old/jira/details/${orderId}`,
        jiraPayload
      );

      fetchOrders();
    } catch (error) {
      console.error("Old Jira Create Error:", error);
    }
  };

  //  NOTIFY API CALL
const notifyRma = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/v1/orders/scm/notify-rma/${orderId}`,
      {}   // ❗ no body required
    );

    alert(res);   //  backend response text alert me show hoga
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("Notify Error:", error);
    alert("Notification failed!");
  }
};

  //  NOTIFY API CALL
const notifyPT = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/v1/orders/scm/notify-project-team/${orderId}`,
      {}   // ❗ no body required
    );

    alert(res);   //  backend response text alert me show hoga
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("Notify Error:", error);
    alert("Notification failed!");
  }
};
 
  // NOTIFY API CALL
const notifyLogistic = async (orderId) => {
  try {
    const res = await httpService.updateWithAuth(
      `/api/v1/orders/scm/dispatch/${orderId}`,
      {}   //no body required
    );

    alert(res);   // backend response text alert me show hoga
    fetchOrders(); // UI refresh
  } catch (error) {
    console.error("Notify Error:", error);
    alert("Notification failed!");
  }
};

 


  const handleLogout = () => {
    removeToken();
    router.push("/login");
  };

  return (
    <div className="min-h-screen w-full bg-[#e3f3ff] flex flex-col items-center py-10 relative">

      {/* USER BOX */}
      <div className="absolute top-5 right-6 flex items-center gap-5 bg-white shadow-md px-4 py-2 rounded-lg border border-[#cce7ff]">
        <span className="text-lg font-semibold text-[#003b66]">
          👤 {username || "User"}
        </span>

        <button
          onClick={handleLogout}
          className="bg-red-500 text-white px-4 py-1.5 rounded-md hover:bg-red-600 transition font-medium shadow"
        >
          Logout
        </button>
      </div>

      {/* Logo */}
      <div className="absolute top-4 left-6">
        <img src="/cyanconnode-logo.png" alt="Logo" className="w-60 opacity-90" />
      </div>

      {/* Heading */}
      <h1 className="text-4xl font-bold text-[#02A3EE] mt-5 mb-4 tracking-wide">
        SCM PENDING ORDERS
      </h1>

      {/* Table Box */}
      <div className="w-[95%] bg-white shadow-xl rounded-xl p-6 border border-[#d4e8ff]">
        <ScmTable
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
          createJira={createJira}
          createOldJira={createOldJira}   // 🔥 Added
          notifyRma={notifyRma}
          notifyPT={notifyPT}
      
          notifyLogistic={notifyLogistic}
          fetchOrders={fetchOrders}
        />
      </div>
    </div>
  );
}
