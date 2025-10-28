import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import api from "../../api/api";
import { Aluno } from "../../types/Aluno";
import { Curso } from "../../types/Curso";
import { Turma } from "../../types/Turma";
import { Usuario } from "../../types/Usuario";
import axios, { AxiosError } from "axios";
import { Link } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";

interface ApiError { erro?: string }

interface Stats {
  alunos: number;
  cursos: number;
  turmas: number;
  usuarios: number;
}

export default function AdminDashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<Stats>({ alunos: 0, cursos: 0, turmas: 0, usuarios: 0 });
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar(): Promise<void> {
      try {
        const [al, cu, tu, us] = await Promise.all([
          api.get<Aluno[]>("/alunos").catch((e) => e),
          api.get<Curso[]>("/cursos").catch((e) => e),
          api.get<Turma[]>("/turmas").catch((e) => e),
          api.get<Usuario[]>("/usuarios").catch((e) => e),
        ]);

        const len = (x: unknown): number => (axios.isAxiosError(x) ? 0 : Array.isArray((x as {data: unknown}).data) ? (x as {data: unknown[]}).data.length : 0);
        setStats({ alunos: len(al), cursos: len(cu), turmas: len(tu), usuarios: len(us) });
      } catch (err) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro ?? "Erro ao carregar dashboard do administrador");
      }
    }
    void carregar();
  }, []);

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 p-6 max-w-7xl mx-auto space-y-6">
        <header>
          <h1 className="text-2xl font-bold text-slate-800">Painel do Administrador</h1>
          <p className="text-slate-500">Bem-vindo, {user?.nome}. Gerencie todo o sistema.</p>
        </header>

        {erro && <div className="bg-red-100 text-red-700 text-sm rounded p-2">{erro}</div>}

        <div className="grid md:grid-cols-4 gap-4">
          <Card title="Alunos" value={stats.alunos} to="/alunos" />
          <Card title="Cursos" value={stats.cursos} to="/cursos" />
          <Card title="Turmas" value={stats.turmas} to="/turmas" />
          <Card title="Usuários" value={stats.usuarios} to="/usuarios" />
        </div>

        <div className="bg-white border border-slate-200 rounded-xl shadow p-5">
          <h2 className="font-semibold text-slate-800 mb-3">Ações rápidas</h2>
          <div className="flex flex-wrap gap-2 text-sm">
            <Link className="px-3 py-2 rounded-lg bg-primary-500 text-white hover:bg-primary-600" to="/usuarios">Cadastrar Usuário</Link>
            <Link className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50" to="/cursos">Novo Curso</Link>
            <Link className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50" to="/turmas">Nova Turma</Link>
            <Link className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50" to="/matriculas">Nova Matrícula</Link>
          </div>
        </div>
      </main>
    </div>
  );
}

function Card({ title, value, to }: { title: string; value: number; to: string }) {
  return (
    <Link to={to} className="block bg-white border border-slate-200 rounded-xl shadow p-5 hover:shadow-md transition">
      <div className="text-sm text-slate-600">{title}</div>
      <div className="text-3xl font-semibold mt-2">{value}</div>
    </Link>
  );
}
