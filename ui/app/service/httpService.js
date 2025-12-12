import axios from "axios";
import { getToken } from "./cookieService";

const baseUrl = "http://localhost:5828";



const httpService = {
  get: async (path) => {
    try {
      const token = getToken();
      const response = await axios.get(`${baseUrl}${path}`,{
        headers: {
          "Content-Type": "application/json",
          Authorization: token ? `Bearer ${token}` : "",
        },
      });
      return response.data;
    } catch (error) {
      console.error("GET Error:", error);
      throw error;
    }
  },

  postWithAuth: async (path, body) => {
    try {
      const token = getToken();
      const response = await axios.post(`${baseUrl}${path}`, body, {
        headers: {
          "Content-Type": "application/json",
          Authorization: token ? `Bearer ${token}` : "",
        },
      });
      return response.data;
    } catch (error) {
      console.error("POST Error:", error);
      throw error;
    }
  },

  postWithoutAuth: async (path, body) => {
    try {
      const response = await axios.post(`${baseUrl}${path}`, body);
      return response.data;
    } catch (error) {
      console.error("POST Error:", error);
      throw error;
    }
  },

 updateWithAuth: async (path, body) => {
  try {
    const token = getToken();
    const response = await axios.put(`${baseUrl}${path}`, body, {
      headers: {
        "Content-Type": "application/json",
        Authorization: token ? `Bearer ${token}` : "",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Update Error:", error);
    throw error;
  }
},

 deleteWithAuth: async (path) => {
  try {
    const token = getToken();
    const response = await axios.delete(`${baseUrl}${path}`, {
      headers: {
        "Content-Type": "application/json",
        Authorization: token ? `Bearer ${token}` : "",
      },
    });
    return response.data;
  } catch (error) {
    console.error("Delete Error:", error);
    throw error;
  }
},
  
};

export default httpService;
