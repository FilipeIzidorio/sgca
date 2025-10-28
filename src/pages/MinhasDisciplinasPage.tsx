import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/api";
import { Disciplina } from "../types/Disciplina";
import axios, { AxiosError } from "axios";
import { useAuth } from "../auth/AuthContext";

interface ApiError {
  erro?: string;
}

export default function MinhasDisciplinasPage() {
  const { user } = useAuth();
  const [disciplinas, setDisciplinas] = useState<Disciplina[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar(): Promise<void> {
      try {
        // TODO: substituir pelo ID real do curso do aluno logado
        // ex: const resp = await api.get<Disciplina[]>(`/disciplinas/curso/${cursoIdDoAluno}`);
        const resp = await api.get<Disciplina[]>("/disciplinas");
        setDisciplinas(resp.data);
      } catch (err) {
        if (axios.isAxiosError(err)) {
          const e = err as AxiosError<ApiError>;
          setErro(
            e.response?.data?.erro ||
              "Erro ao carregar suas disciplinas"
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
            Minhas Disciplinas
          </h1>
          <p className="text-sm text-slate-500">
            Disciplinas associadas ao seu curso | {user?.nome}
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
                <th className="py-2 pr-4">Código</th>
                <th className="py-2 pr-4">Nome</th>
                <th className="py-2 pr-4">Carga Horária</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {disciplinas.map((d) => (
                <tr key={d.id} className="border-b last:border-none">
                  <td className="py-2 pr-4 font-medium">
                    {d.codigo}
                  </td>
                  <td className="py-2 pr-4">{d.nome}</td>
                  <td className="py-2 pr-4">{d.cargaHoraria}h</td>
                </tr>
              ))}

              {disciplinas.length === 0 && (
                <tr>
                  <td
                    colSpan={3}
                    className="py-6 text-center text-slate-400 text-sm"
                  >
                    Nenhuma disciplina encontrada.
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
