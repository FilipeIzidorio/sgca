import { Routes, Route } from "react-router-dom";

import ProtectedRoute from "./components/ProtectedRoute";

import HomeRedirect from "./pages/HomeRedirect";

// Dashboards específicos
import AdminDashboardPage from "./pages/admin/AdminDashboardPage";
import ProfessorDashboardPage from "./pages/professor/ProfessorDashboardPage";
import AlunoDashboardPage from "./pages/aluno/AlunoDashboardPage";

// Páginas gerais de gestão acadêmica
import AlunosPage from "./pages/AlunosPage";
import UsuariosPage from "./pages/UsuariosPage";
import CursosPage from "./pages/CursosPage";
import DisciplinasPage from "./pages/DisciplinasPage";
import TurmasPage from "./pages/TurmasPage";
import AvaliacaoPage from "./pages/AvaliacaoPage";
import NotaPage from "./pages/NotaPage";
import MatriculaPage from "./pages/MatriculaPage";
import PresencasPage from "./pages/PresencasPage";

// Páginas do aluno (auto-consulta)
import MinhasNotasPage from "./pages/MinhasNotasPage";
import MinhasAvaliacoesPage from "./pages/MinhasAvaliacoesPage";
import MeuCursoPage from "./pages/MeuCursoPage";
import MinhasDisciplinasPage from "./pages/MinhasDisciplinasPage";

// Auth
import LoginPage from "./auth/LoginPage";

export default function App() {
  return (
    <Routes>
      {/* LOGIN é público */}
      <Route path="/login" element={<LoginPage />} />

      {/* ROTA RAIZ: redireciona pro dashboard certo conforme perfil */}
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <HomeRedirect />
          </ProtectedRoute>
        }
      />

      {/*
        ===========
        DASHBOARDS
        ===========
      */}

      {/* dashboard admin */}
      <Route
        path="/admin"
        element={
          <ProtectedRoute allowed={["ADMIN"]}>
            <AdminDashboardPage />
          </ProtectedRoute>
        }
      />

      {/* dashboard professor */}
      <Route
    path="/professor"
    element={
    <ProtectedRoute allowed={["PROFESSOR"]}>
      <ProfessorDashboardPage />
    </ProtectedRoute>
  }
/>


      {/* dashboard aluno */}
      <Route
        path="/aluno"
        element={
          <ProtectedRoute allowed={["ALUNO"]}>
            <AlunoDashboardPage />
          </ProtectedRoute>
        }
      />

      {/*
        ==========================
        ÁREA ADMIN
        ==========================
      */}
      <Route
        path="/usuarios"
        element={
          <ProtectedRoute allowed={["ADMIN"]}>
            <UsuariosPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/cursos"
        element={
          <ProtectedRoute allowed={["ADMIN"]}>
            <CursosPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/matriculas"
        element={
          <ProtectedRoute allowed={["ADMIN"]}>
            <MatriculaPage />
          </ProtectedRoute>
        }
      />

      {/*
        ==========================
        ÁREA ADMIN + PROFESSOR
        ==========================
      */}
      <Route
        path="/alunos"
        element={
          <ProtectedRoute allowed={["ADMIN", "PROFESSOR"]}>
            <AlunosPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/disciplinas"
        element={
          <ProtectedRoute allowed={["ADMIN", "PROFESSOR"]}>
            <DisciplinasPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/turmas"
        element={
          <ProtectedRoute allowed={["ADMIN", "PROFESSOR"]}>
            <TurmasPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/avaliacoes"
        element={
          <ProtectedRoute allowed={["ADMIN", "PROFESSOR"]}>
            <AvaliacaoPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/notas"
        element={
          <ProtectedRoute allowed={["ADMIN", "PROFESSOR"]}>
            <NotaPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/presencas"
        element={
          <ProtectedRoute allowed={["ADMIN", "PROFESSOR"]}>
            <PresencasPage />
          </ProtectedRoute>
        }
      />

      {/*
        ==========================
        ÁREA DO ALUNO
        ==========================
      */}
      <Route
        path="/minhas-notas"
        element={
          <ProtectedRoute allowed={["ALUNO"]}>
            <MinhasNotasPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/minhas-avaliacoes"
        element={
          <ProtectedRoute allowed={["ALUNO"]}>
            <MinhasAvaliacoesPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/meu-curso"
        element={
          <ProtectedRoute allowed={["ALUNO"]}>
            <MeuCursoPage />
          </ProtectedRoute>
        }
      />

      <Route
        path="/minhas-disciplinas"
        element={
          <ProtectedRoute allowed={["ALUNO"]}>
            <MinhasDisciplinasPage />
          </ProtectedRoute>
        }
      />

      {/* 404 */}
      <Route
        path="*"
        element={
          <div className="min-h-screen flex items-center justify-center bg-slate-100 p-6">
            <div className="text-center">
              <div className="text-4xl font-bold text-slate-700 mb-2">
                404
              </div>
              <p className="text-slate-500">Página não encontrada.</p>
              <a
                href="/"
                className="inline-block mt-4 text-primary-600 hover:underline text-sm font-medium"
              >
                Voltar para o painel
              </a>
            </div>
          </div>
        }
      />
    </Routes>
  );
}
