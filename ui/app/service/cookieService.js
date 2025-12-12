"use client";

import Cookies from "js-cookie";
import jwt from "jsonwebtoken";

export const setToken = (token) => {
  Cookies.set("token", token, {
    expires: 1 / 24, 
    sameSite: "strict",
  });
};

export const getToken = () => {
  return Cookies.get("token");
};

export const removeToken = () => {
  Cookies.remove("token");
};

export const isTokenValid = (token) => {
  try {
    if (!token) return false;

    const decoded = jwt.decode(token);

    if (!decoded || !decoded.exp) return false;

    const currentTime = Math.floor(Date.now() / 1000);
    return decoded.exp > currentTime;
  } catch (error) {
    console.error("Invalid token:", error);
    return false;
  }
};

// Extract Username from Token
export const getUsernameFromToken = () => {
  const token = Cookies.get("token");
  if (!token) return null;

  const decoded = jwt.decode(token);
  return decoded?.username || 
  decoded?.sub ||
  null;
};
