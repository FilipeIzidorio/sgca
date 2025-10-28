import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/api";
import { Aluno } from "../types/Aluno";
import { Curso } from "../types/Curso";
import { Turma } from "../types/Turma";
import axios, { AxiosError } from "axios";

interface ApiError {
  erro?: string;
}

interface Stats {
  totalAlunos: number;
  totalCursos: number;
  totalTurmas: number;
}

export default function DashboardPage() {
  const [stats, setStats] = useState<Stats>({
    totalAlunos: 0,
    totalCursos: 0,
    totalTurmas: 0
  });

  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregarResumo(): Promise<void> {
      try {
        const [alunosRes, cursosRes, turmasRes] = await Promise.all([
          api.get<Aluno[]>("/alunos").catch((e) => e),
          api.get<Curso[]>("/cursos").catch((e) => e),
          api.get<Turma[]>("/turmas").catch((e) => e)
        ]);

        const safeLen = <T,>(respOrErr: unknown): number => {
          if (axios.isAxiosError(respOrErr)) return 0;
          const ok = respOrErr as { data: T[] };
          return Array.isArray(ok.data) ? ok.data.length : 0;
        };

        setStats({
          totalAlunos: safeLen<Aluno>(alunosRes),
          totalCursos: safeLen<Curso>(cursosRes),
          totalTurmas: safeLen<Turma>(turmasRes)
        });
      } catch (err) {
        if (axios.isAxiosError(err)) {
          const e = err as AxiosError<ApiError>;
          setErro(e.response?.data?.erro || "Erro ao carregar dashboard");
        } else {
          setErro("Erro inesperado");
        }
      }
    }

    void carregarResumo();
  }, []);

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 p-6 max-w-7xl mx-auto space-y-6">
        <div>
          <h1 className="text-2xl font-bold text-slate-800">Dashboard</h1>
          <p className="text-slate-500">
            Visão geral do sistema acadêmico
          </p>
        </div>

        {erro && (
          <div className="bg-red-100 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        <div className="grid md:grid-cols-3 gap-4">
          <CardResumo titulo="Alunos cadastrados" valor={stats.totalAlunos} />
          <CardResumo titulo="Cursos ativos" valor={stats.totalCursos} />
          <CardResumo titulo="Turmas em andamento" valor={stats.totalTurmas} />
        </div>

        <div className="bg-white border border-slate-200 rounded-xl shadow p-5">
          <h2 className="text-lg font-semibold text-slate-800 mb-2">
            Bem-vindo ao SGCA
          </h2>
          <p className="text-slate-600 text-sm leading-relaxed">
            Acesse o menu superior para gerenciar alunos, usuários,
            cursos, disciplinas, turmas, avaliações, notas, matrículas
            e presenças. Esta tela mostra um resumo geral das
            informações acadêmicas.
          </p>
        </div>
      </main>
    </div>
  );
}

function CardResumo({ titulo, valor }: { titulo: string; valor: number }) {
  return (
    <div className="bg-white border border-slate-200 rounded-xl shadow p-5">
      <div className="text-sm font-medium text-slate-600">{titulo}</div>
      <div className="text-3xl font-semibold text-primary-600 mt-2">
        {valor}
      </div>
    </div>
  );
}
