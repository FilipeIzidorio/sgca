import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import api from "../../api/api";
import { Turma } from "../../types/Turma";
import { Avaliacao } from "../../types/Avaliacao";
import { Presenca } from "../../types/Presenca";
import { Nota } from "../../types/Nota";
import axios, { AxiosError } from "axios";
import { Link } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";

interface ApiError { erro?: string }

interface Stats {
  turmas: number;
  avaliacoes: number;
  presencas: number;
  notas: number;
}

export default function ProfessorDashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<Stats>({ turmas: 0, avaliacoes: 0, presencas: 0, notas: 0 });
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar(): Promise<void> {
      try {
        // Se no futuro você tiver endpoints filtrando por professor, troque para:
        // /turmas/professor/{professorId} etc.
        const [t, a, p, n] = await Promise.all([
          api.get<Turma[]>("/turmas").catch((e) => e),
          api.get<Avaliacao[]>("/avaliacoes").catch((e) => e),
          api.get<Presenca[]>("/presencas").catch((e) => e),
          api.get<Nota[]>("/notas").catch((e) => e),
        ]);
        const len = (x: unknown): number => (axios.isAxiosError(x) ? 0 : Array.isArray((x as {data: unknown}).data) ? (x as {data: unknown[]}).data.length : 0);
        setStats({ turmas: len(t), avaliacoes: len(a), presencas: len(p), notas: len(n) });
      } catch (err) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro ?? "Erro ao carregar painel do professor");
      }
    }
    void carregar();
  }, []);

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 p-6 max-w-7xl mx-auto space-y-6">
        <header>
          <h1 className="text-2xl font-bold text-slate-800">Painel do Professor</h1>
          <p className="text-slate-500">Olá, {user?.nome}. Gerencie suas turmas e avaliações.</p>
        </header>

        {erro && <div className="bg-red-100 text-red-700 text-sm rounded p-2">{erro}</div>}

        <div className="grid md:grid-cols-4 gap-4">
          <Card title="Minhas Turmas" value={stats.turmas} to="/turmas" />
          <Card title="Avaliações" value={stats.avaliacoes} to="/avaliacoes" />
          <Card title="Presenças" value={stats.presencas} to="/presencas" />
          <Card title="Notas" value={stats.notas} to="/notas" />
        </div>

        <div className="bg-white border border-slate-200 rounded-xl shadow p-5">
          <h2 className="font-semibold text-slate-800 mb-3">Ações rápidas</h2>
          <div className="flex flex-wrap gap-2 text-sm">
            <Link className="px-3 py-2 rounded-lg bg-primary-500 text-white hover:bg-primary-600" to="/avaliacoes">Nova Avaliação</Link>
            <Link className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50" to="/presencas">Lançar Presença</Link>
            <Link className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50" to="/notas">Lançar Nota</Link>
            <Link className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50" to="/alunos">Ver Alunos</Link>
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
