import { useAuth } from "../auth/AuthContext";
import { Link } from "react-router-dom";
import { LogOut } from "lucide-react";
import { Usuario } from "../types/Usuario";

type PerfilUsuario = Usuario["perfil"];

function isAdmin(perfil: PerfilUsuario | null): boolean {
  return perfil === "ADMIN";
}

function isProf(perfil: PerfilUsuario | null): boolean {
  return perfil === "PROFESSOR";
}

function isAluno(perfil: PerfilUsuario | null): boolean {
  return perfil === "ALUNO";
}

export default function Navbar() {
  const { user, logout } = useAuth();
  const perfil: PerfilUsuario | null = user ? user.perfil : null;

  return (
    <header className="bg-white border-b border-slate-200 px-6 py-3 flex items-center justify-between shadow-sm">
      {/* Left side */}
      <div className="flex items-center gap-4 flex-wrap">
        <span className="text-primary-600 font-bold text-lg">
          SGCA
        </span>

        <nav className="flex flex-wrap gap-x-4 gap-y-2 text-sm text-slate-600">
          {/* Todos os perfis autenticados */}
          <Link to="/" className="hover:text-primary-600 font-medium">
            Dashboard
          </Link>

          {/* ADMIN e PROFESSOR enxergam /alunos */}
          {(isAdmin(perfil) || isProf(perfil)) && (
            <Link
              to="/alunos"
              className="hover:text-primary-600 font-medium"
            >
              Alunos
            </Link>
          )}

          {/* ADMIN */}
          {isAdmin(perfil) && (
            <>
              <Link
                to="/usuarios"
                className="hover:text-primary-600 font-medium"
              >
                Usuários
              </Link>

              <Link
                to="/cursos"
                className="hover:text-primary-600 font-medium"
              >
                Cursos
              </Link>

              <Link
                to="/matriculas"
                className="hover:text-primary-600 font-medium"
              >
                Matrículas
              </Link>
            </>
          )}

          {/* ADMIN e PROFESSOR */}
          {(isAdmin(perfil) || isProf(perfil)) && (
            <>
              <Link
                to="/disciplinas"
                className="hover:text-primary-600 font-medium"
              >
                Disciplinas
              </Link>

              <Link
                to="/turmas"
                className="hover:text-primary-600 font-medium"
              >
                Turmas
              </Link>

              <Link
                to="/avaliacoes"
                className="hover:text-primary-600 font-medium"
              >
                Avaliações
              </Link>

              <Link
                to="/notas"
                className="hover:text-primary-600 font-medium"
              >
                Notas
              </Link>

              <Link
                to="/presencas"
                className="hover:text-primary-600 font-medium"
              >
                Presenças
              </Link>
            </>
          )}

          {/* ALUNO */}
          {isAluno(perfil) && (
            <>
              <Link
                to="/minhas-notas"
                className="hover:text-primary-600 font-medium"
              >
                Minhas Notas
              </Link>

              <Link
                to="/minhas-avaliacoes"
                className="hover:text-primary-600 font-medium"
              >
                Minhas Avaliações
              </Link>

              <Link
                to="/meu-curso"
                className="hover:text-primary-600 font-medium"
              >
                Meu Curso
              </Link>

              <Link
                to="/minhas-disciplinas"
                className="hover:text-primary-600 font-medium"
              >
                Minhas Disciplinas
              </Link>
            </>
          )}
        </nav>
      </div>

      {/* Right side (usuário + logout) */}
      <div className="flex items-center gap-3">
        {user && (
          <div className="text-right text-xs leading-tight">
            <div className="font-semibold text-slate-700">{user.nome}</div>
            <div className="text-slate-400 uppercase">{user.perfil}</div>
          </div>
        )}

        <button
          onClick={logout}
          className="flex items-center gap-1 text-xs text-red-600 hover:text-red-700 transition"
        >
          <LogOut size={16} />
          <span>Sair</span>
        </button>
      </div>
    </header>
  );
}
