import { useAuth } from "../auth/AuthContext";
import { Usuario } from "../types/Usuario";
import AdminDashboardPage from "./admin/AdminDashboardPage";
import ProfessorDashboardPage from "./professor/ProfessorDashboardPage";
import AlunoDashboardPage from "./aluno/AlunoDashboardPage";

type Perfil = Usuario["perfil"];

export default function DashboardRouter() {
  const { user } = useAuth();
  const perfil: Perfil | undefined = user?.perfil;

  if (perfil === "ADMIN") return <AdminDashboardPage />;
  if (perfil === "PROFESSOR") return <ProfessorDashboardPage />;
  if (perfil === "ALUNO") return <AlunoDashboardPage />;

  // fallback seguro (se algo vier sem perfil)
  return <AdminDashboardPage />;
}
