import { useEffect, useState } from "react";
import Navbar from "../../components/Navbar";
import api from "../../api/api";
import { Nota } from "../../types/Nota";
import { Avaliacao } from "../../types/Avaliacao";
import { Disciplina } from "../../types/Disciplina";
import { Curso } from "../../types/Curso";
import { AxiosError, AxiosResponse } from "axios";
import { Link } from "react-router-dom";
import { useAuth } from "../../auth/AuthContext";

interface ApiError {
  erro?: string;
}

interface Stats {
  notas: number;
  avaliacoes: number;
  disciplinas: number;
  curso: number; // 0 ou 1
}

// type guard: verifica se o valor é uma resposta AxiosResponse<T>
function isAxiosResponse<T>(
  value: unknown
): value is AxiosResponse<T> {
  return (
    typeof value === "object" &&
    value !== null &&
    "data" in value &&
    "status" in value
  );
}

// devolve o tamanho de um array dentro de uma resposta axios (ou 0 se foi erro)
function getArrayCount<T>(value: unknown): number {
  if (isAxiosResponse<T[]>(value)) {
    return Array.isArray(value.data) ? value.data.length : 0;
  }
  return 0;
}

// devolve 1 se veio pelo menos 1 curso, senão 0
function hasAtLeastOneCurso(value: unknown): number {
  if (isAxiosResponse<Curso[]>(value)) {
    return Array.isArray(value.data) && value.data.length > 0 ? 1 : 0;
  }
  return 0;
}

export default function AlunoDashboardPage() {
  const { user } = useAuth();
  const [stats, setStats] = useState<Stats>({
    notas: 0,
    avaliacoes: 0,
    disciplinas: 0,
    curso: 0,
  });
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar(): Promise<void> {
      try {
        // TODO: trocar para endpoints específicos do aluno logado quando disponíveis.
        // Ex: /notas/matricula/{matriculaIdDoAluno}
        const [nResp, aResp, dResp, cResp] = await Promise.all([
          api.get<Nota[]>("/notas").catch((e) => e),
          api.get<Avaliacao[]>("/avaliacoes").catch((e) => e),
          api.get<Disciplina[]>("/disciplinas").catch((e) => e),
          api.get<Curso[]>("/cursos").catch((e) => e),
        ]);

        const novasStats: Stats = {
          notas: getArrayCount<Nota>(nResp),
          avaliacoes: getArrayCount<Avaliacao>(aResp),
          disciplinas: getArrayCount<Disciplina>(dResp),
          curso: hasAtLeastOneCurso(cResp),
        };

        setStats(novasStats);
      } catch (err) {
        const e = err as AxiosError<ApiError>;
        setErro(
          e.response?.data?.erro ??
            "Erro ao carregar painel do aluno"
        );
      }
    }

    void carregar();
  }, []);

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 p-6 max-w-7xl mx-auto space-y-6">
        <header>
          <h1 className="text-2xl font-bold text-slate-800">
            Meu Painel
          </h1>
          <p className="text-slate-500">
            Olá, {user?.nome}. Aqui você acompanha seu desempenho.
          </p>
        </header>

        {erro && (
          <div className="bg-red-100 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        <div className="grid md:grid-cols-4 gap-4">
          <Card
            title="Minhas Notas"
            value={stats.notas}
            to="/minhas-notas"
          />
          <Card
            title="Minhas Avaliações"
            value={stats.avaliacoes}
            to="/minhas-avaliacoes"
          />
          <Card
            title="Minhas Disciplinas"
            value={stats.disciplinas}
            to="/minhas-disciplinas"
          />
          <Card
            title="Meu Curso"
            value={stats.curso}
            to="/meu-curso"
          />
        </div>

        <div className="bg-white border border-slate-200 rounded-xl shadow p-5">
          <h2 className="font-semibold text-slate-800 mb-3">
            Acesso rápido
          </h2>
          <div className="flex flex-wrap gap-2 text-sm">
            <Link
              className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50"
              to="/minhas-notas"
            >
              Ver Notas
            </Link>
            <Link
              className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50"
              to="/minhas-avaliacoes"
            >
              Ver Avaliações
            </Link>
            <Link
              className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50"
              to="/minhas-disciplinas"
            >
              Ver Disciplinas
            </Link>
            <Link
              className="px-3 py-2 rounded-lg border border-slate-300 hover:bg-slate-50"
              to="/meu-curso"
            >
              Ver Curso
            </Link>
          </div>
        </div>
      </main>
    </div>
  );
}

function Card(props: { title: string; value: number; to: string }) {
  const { title, value, to } = props;
  return (
    <Link
      to={to}
      className="block bg-white border border-slate-200 rounded-xl shadow p-5 hover:shadow-md transition"
    >
      <div className="text-sm text-slate-600">{title}</div>
      <div className="text-3xl font-semibold mt-2">{value}</div>
    </Link>
  );
}
