import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { Usuario } from "../types/Usuario";

function getHomeRoute(perfil: Usuario["perfil"]): string {
  switch (perfil) {
    case "ADMIN":
      return "/admin";
    case "PROFESSOR":
      return "/professor";
    case "ALUNO":
      return "/aluno";
    default:
      return "/login";
  }
}

export default function HomeRedirect() {
  const { isAuthenticated, user } = useAuth();

  if (!isAuthenticated || !user) {
    return <Navigate to="/login" replace />;
  }

  const destino = getHomeRoute(user.perfil);
  return <Navigate to={destino} replace />;
}
