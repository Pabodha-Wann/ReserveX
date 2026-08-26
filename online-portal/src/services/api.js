
import axios from "axios";// for sending HTTP requests

const BASE_URL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

const api = axios.create({
  baseURL: BASE_URL,// custom instance of axios with a base URL
  headers: {
    "Content-Type": "application/json",
  },
  timeout: 10000,
});

let getAccessToken = null;

// Call this from AuthContext when the app loads
export const setTokenProvider = (provider) => {
  getAccessToken = provider;
};

api.interceptors.request.use(async (config) => { // interceptor to add the JWT token to the Authorization header of each request
  if (getAccessToken) {
    try {
      const token = await getAccessToken();
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
    } catch (error) {
      console.error('Error fetching Auth0 token for API request', error);
    }
  }
  return config;
});
export default api;
