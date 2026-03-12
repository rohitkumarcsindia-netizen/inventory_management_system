import { removeToken } from "./cookieService";

export const logoutUser = () => {
  removeToken();
  localStorage.removeItem("department");
  window.location.replace("/");
};