import axios from "axios";
import { useAuthStore } from "../auth/useAuthStore";

const api = axios.create({
  baseURL: "http://localhost:8081/api/v1", // ajuste se sua porta mudar
  headers: {
    "Content-Type": "application/json",
  },
});

// adiciona Authorization: Bearer <token> em toda request
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
