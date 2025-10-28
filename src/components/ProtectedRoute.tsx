import { Navigate } from "react-router-dom";
import { useAuth } from "../auth/AuthContext";
import { Usuario } from "../types/Usuario";

type PerfilUsuario = Usuario["perfil"];

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowed?: PerfilUsuario[]; // Se não passar, qualquer usuário autenticado entra
}

export default function ProtectedRoute({ children, allowed }: ProtectedRouteProps) {
  const { isAuthenticated, user } = useAuth();

  // Se não estiver autenticado -> vai pro login
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  // Se for rota com permissão específica e o perfil não está permitido -> volta dashboard
  if (allowed && user && !allowed.includes(user.perfil)) {
    return <Navigate to="/" replace />;
  }

  return <>{children}</>;
}
