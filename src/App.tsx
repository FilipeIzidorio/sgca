import { BrowserRouter, Routes, Route } from "react-router-dom";
import LoginPage from "./auth/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import CursosPage from "./pages/CursosPage";
import TurmasPage from "./pages/TurmasPage";
import NotFoundPage from "./pages/NotFoundPage";
import RequireAuth from "./auth/RequireAuth";
import AppNavbar from "./components/AppNavbar";
import { Box } from "@mui/material";

function AppShell({ children }: { children: React.ReactNode }) {
  return (
    <>
      <AppNavbar />
      <Box sx={{ maxWidth: 1200, mx: "auto" }}>{children}</Box>
    </>
  );
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* público */}
        <Route path="/login" element={<LoginPage />} />

        {/* privado */}
        <Route
          path="/dashboard"
          element={
            <RequireAuth>
              <AppShell>
                <DashboardPage />
              </AppShell>
            </RequireAuth>
          }
        />
        <Route
          path="/cursos"
          element={
            <RequireAuth>
              <AppShell>
                <CursosPage />
              </AppShell>
            </RequireAuth>
          }
        />
        <Route
          path="/turmas"
          element={
            <RequireAuth>
              <AppShell>
                <TurmasPage />
              </AppShell>
            </RequireAuth>
          }
        />

        {/* default */}
        <Route path="*" element={<NotFoundPage />} />
      </Routes>
    </BrowserRouter>
  );
}
