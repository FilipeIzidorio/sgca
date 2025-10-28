import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/api";
import { Nota } from "../types/Nota";
import axios, { AxiosError } from "axios";
import { useAuth } from "../auth/AuthContext";

interface ApiError {
  erro?: string;
}

export default function MinhasNotasPage() {
  const { user } = useAuth();
  const [notas, setNotas] = useState<Nota[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar(): Promise<void> {
      try {
        // TODO: substituir matriculaId pelo dado real do usuário logado
        // Exemplo futuro:
        // const resp = await api.get<Nota[]>(`/notas/matricula/${minhaMatriculaId}`);
        const resp = await api.get<Nota[]>("/notas"); // fallback até ter endpoint específico
        setNotas(resp.data);
      } catch (err) {
        if (axios.isAxiosError(err)) {
          const e = err as AxiosError<ApiError>;
          setErro(e.response?.data?.erro || "Erro ao carregar suas notas");
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
            Minhas Notas
          </h1>
          <p className="text-sm text-slate-500">
            Consultar notas lançadas | Aluno: {user?.nome}
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
                <th className="py-2 pr-4">Avaliação</th>
                <th className="py-2 pr-4">Matrícula</th>
                <th className="py-2 pr-4">Valor</th>
                <th className="py-2 pr-4">Data</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {notas.map((n) => (
                <tr key={n.id} className="border-b last:border-none">
                  <td className="py-2 pr-4">{n.avaliacaoId}</td>
                  <td className="py-2 pr-4">{n.matriculaId}</td>
                  <td className="py-2 pr-4 font-medium">{n.valor}</td>
                  <td className="py-2 pr-4">{n.data}</td>
                </tr>
              ))}

              {notas.length === 0 && (
                <tr>
                  <td
                    colSpan={4}
                    className="py-6 text-center text-slate-400 text-sm"
                  >
                    Nenhuma nota disponível ainda.
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
