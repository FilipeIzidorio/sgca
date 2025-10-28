import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/api";
import { Avaliacao } from "../types/Avaliacao";
import axios, { AxiosError } from "axios";
import { useAuth } from "../auth/AuthContext";

interface ApiError {
  erro?: string;
}

export default function MinhasAvaliacoesPage() {
  const { user } = useAuth();
  const [avaliacoes, setAvaliacoes] = useState<Avaliacao[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar(): Promise<void> {
      try {
        // TODO: idealmente filtrar por turmas em que o aluno está matriculado
        const resp = await api.get<Avaliacao[]>("/avaliacoes");
        const data = Array.isArray(resp.data) ? resp.data : [];
        setAvaliacoes(data);
      } catch (err) {
        if (axios.isAxiosError(err)) {
          const e = err as AxiosError<ApiError>;
          setErro(
            e.response?.data?.erro ||
              "Erro ao carregar suas avaliações"
          );
        } else {
          setErro("Erro inesperado");
        }
      }
    }

    void carregar();
  }, []);

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 max-w-4xl mx-auto p-6 space-y-6">
        <header>
          <h1 className="text-xl font-semibold text-slate-800">
            Minhas Avaliações
          </h1>
          <p className="text-sm text-slate-500">
            Avaliações previstas nas suas turmas | {user?.nome}
          </p>
        </header>

        {erro && (
          <div className="bg-red-100 text-red-700 text-sm rounded p-2 border border-red-200">
            {erro}
          </div>
        )}

        <div className="bg-white border border-slate-200 rounded-xl shadow p-4 text-sm overflow-x-auto">
          <table className="min-w-full text-left">
            <thead className="text-slate-500 font-medium border-b text-xs uppercase">
              <tr>
                <th className="py-2 pr-4">Título</th>
                <th className="py-2 pr-4">Tipo</th>
                <th className="py-2 pr-4">Peso (%)</th>
                <th className="py-2 pr-4">Turma</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {avaliacoes.map((a) => (
                <tr key={a.id} className="border-b last:border-none">
                  <td className="py-2 pr-4 font-medium">{a.titulo}</td>
                  <td className="py-2 pr-4">{a.tipo}</td>
                  <td className="py-2 pr-4">{a.peso}</td>
                  <td className="py-2 pr-4">{a.turmaId}</td>
                </tr>
              ))}

              {avaliacoes.length === 0 && (
                <tr>
                  <td
                    colSpan={4}
                    className="py-6 text-center text-slate-400 text-sm"
                  >
                    Nenhuma avaliação cadastrada para você ainda.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
}
