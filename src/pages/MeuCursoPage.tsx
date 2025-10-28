import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import api from "../api/api";
import { Curso } from "../types/Curso";
import axios, { AxiosError } from "axios";
import { useAuth } from "../auth/AuthContext";

interface ApiError {
  erro?: string;
}

export default function MeuCursoPage() {
  const { user } = useAuth();
  const [curso, setCurso] = useState<Curso | null>(null);
  const [erro, setErro] = useState<string | null>(null);

  useEffect(() => {
    async function carregar(): Promise<void> {
      try {
        // TODO: descobrir qual curso o aluno está
        // Exemplo futuro: GET /alunos/{idAluno}/curso
        // Por enquanto, só pega lista e escolhe o primeiro como mock
        const resp = await api.get<Curso[]>("/cursos");
        const lista = resp.data;
        setCurso(lista.length > 0 ? lista[0] : null);
      } catch (err) {
        if (axios.isAxiosError(err)) {
          const e = err as AxiosError<ApiError>;
          setErro(
            e.response?.data?.erro ||
              "Erro ao carregar seu curso"
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

      <main className="flex-1 max-w-2xl mx-auto p-6 space-y-6">
        <header>
          <h1 className="text-xl font-semibold text-slate-800">
            Meu Curso
          </h1>
          <p className="text-sm text-slate-500">
            Informações do seu curso | {user?.nome}
          </p>
        </header>

        {erro && (
          <div className="bg-red-100 text-red-700 text-sm rounded p-2 border border-red-200">
            {erro}
          </div>
        )}

        {!curso ? (
          <div className="bg-white border border-slate-200 rounded-xl shadow p-4 text-slate-500 text-sm">
            Nenhum curso associado encontrado.
          </div>
        ) : (
          <div className="bg-white border border-slate-200 rounded-xl shadow p-4 space-y-3 text-sm">
            <div className="flex justify-between">
              <span className="text-slate-500">Nome:</span>
              <span className="font-medium text-slate-800">
                {curso.nome}
              </span>
            </div>

            <div className="flex justify-between">
              <span className="text-slate-500">Código:</span>
              <span className="font-medium text-slate-800">
                {curso.codigo}
              </span>
            </div>

            <div className="flex justify-between">
              <span className="text-slate-500">Carga Horária:</span>
              <span className="font-medium text-slate-800">
                {curso.cargaHoraria} horas
              </span>
            </div>

            <div>
              <div className="text-slate-500 mb-1">Descrição:</div>
              <div className="text-slate-700 leading-relaxed">
                {curso.descricao || "Sem descrição"}
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
