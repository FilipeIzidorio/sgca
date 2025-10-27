import { Navigate } from "react-router-dom";
import { useAuthStore } from "./useAuthStore";

export default function RequireAuth({ children }: { children: JSX.Element }) {
  const { token } = useAuthStore();
  if (!token) {
    return <Navigate to="/login" replace />;
  }
  return children;
}
