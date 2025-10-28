import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import axios, { AxiosError } from "axios";

import { Matricula, MatriculaView } from "../types/Matricula";
import { Aluno } from "../types/Aluno";
import { Turma } from "../types/Turma";

interface ApiError {
  erro?: string;
  message?: string;
}

interface ModalProps {
  open: boolean;
  title: string;
  children: React.ReactNode;
  onClose: () => void;
}

// modal reutilizável (mesmo padrão das outras telas)
function Modal({ open, title, children, onClose }: ModalProps) {
  if (!open) return null;
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
      <div className="bg-white w-full max-w-lg rounded-xl shadow-lg border border-slate-200 p-6">
        <div className="flex items-start justify-between mb-4">
          <h2 className="text-lg font-semibold text-slate-800">{title}</h2>
          <button
            className="text-slate-500 hover:text-slate-700 text-sm"
            onClick={onClose}
          >
            Fechar
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

export default function MatriculasPage() {
  // lista enriquecida pronta pra exibir
  const [matriculas, setMatriculas] = useState<MatriculaView[]>([]);

  const [erro, setErro] = useState<string | null>(null);
  const [carregando, setCarregando] = useState<boolean>(true);

  // para deletar
  const [matriculaExcluir, setMatriculaExcluir] = useState<MatriculaView | null>(null);

  // modal de criar nova matrícula
  const [modalAberto, setModalAberto] = useState<boolean>(false);

  // campos do form de criação
  const [alunoIdInput, setAlunoIdInput] = useState<number>(0);
  const [turmaIdInput, setTurmaIdInput] = useState<number>(0);
  const [dataInput, setDataInput] = useState<string>("");
  const [situacaoInput, setSituacaoInput] = useState<string>("ATIVA");

  // -------------------------------------------------
  // Helpers: buscar nome do aluno e período da turma
  // -------------------------------------------------

  // caches in-memory pra evitar requisições duplicadas
  const alunoCacheRef = useState<Map<number, string>>(() => new Map())[0];
  const turmaCacheRef = useState<Map<number, string>>(() => new Map())[0];

  async function getAlunoNome(alunoId: number): Promise<string> {
    // já conhecido?
    const cached = alunoCacheRef.get(alunoId);
    if (cached) return cached;

    // chama backend: GET /api/v1/alunos/{id}
    // controller AlunoController.buscar(@PathVariable Long id)
    const resp = await api.get<Aluno>(`/alunos/${alunoId}`);
    const nome = resp.data.nome ?? "";

    alunoCacheRef.set(alunoId, nome);
    return nome;
  }

  async function getTurmaPeriodo(turmaId: number): Promise<string> {
    // cache?
    const cached = turmaCacheRef.get(turmaId);
    if (cached) return cached;

    // chama backend: GET /api/v1/turmas/{id}
    // controller TurmaController.buscar(@PathVariable Long id)
    const resp = await api.get<Turma>(`/turmas/${turmaId}`);
    const periodo = resp.data.periodo ?? "";

    turmaCacheRef.set(turmaId, periodo);
    return periodo;
  }

  // -------------------------------------------------
  // Carregar todas as matrículas e enriquecer
  // GET /api/v1/matriculas
  // -------------------------------------------------

  async function carregar(): Promise<void> {
    try {
      setErro(null);
      setCarregando(true);

      // pega as matrículas cruas
      const resp = await api.get<Matricula[]>("/matriculas");
      const base = Array.isArray(resp.data) ? resp.data : [];

      // precisamos enriquecer cada item com alunoNome e turmaPeriodo
      // estratégia:
      // 1. extrair todos os alunoIds únicos
      // 2. extrair todos os turmaIds únicos
      // 3. buscar detalhes faltantes (usando cache pra não repetir)
      // 4. montar MatriculaView[]

      // carrega nomes de alunos que ainda não estão no cache
      const alunoIdsUnicos = [...new Set(base.map((m) => m.alunoId))];
      for (const aid of alunoIdsUnicos) {
        if (!alunoCacheRef.has(aid)) {
          await getAlunoNome(aid);
        }
      }

      // carrega períodos de turmas que ainda não estão no cache
      const turmaIdsUnicos = [...new Set(base.map((m) => m.turmaId))];
      for (const tid of turmaIdsUnicos) {
        if (!turmaCacheRef.has(tid)) {
          await getTurmaPeriodo(tid);
        }
      }

      // agora montamos a lista final
      const enriquecida: MatriculaView[] = base.map((m) => ({
        ...m,
        alunoNome: alunoCacheRef.get(m.alunoId) ?? "",
        turmaPeriodo: turmaCacheRef.get(m.turmaId) ?? "",
      }));

      setMatriculas(enriquecida);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          (e.response?.status === 401
            ? "Sessão expirada. Faça login novamente."
            : e.response?.status === 403
            ? "Acesso negado."
            : "Erro ao carregar matrículas.");
        setErro(msg);
      } else {
        setErro("Erro inesperado ao carregar matrículas.");
      }
    } finally {
        setCarregando(false);
    }
  }

  useEffect(() => {
    void carregar();
  }, []);

  // -------------------------------------------------
  // Criar matrícula
  // POST /api/v1/matriculas
  // body esperado: { alunoId, turmaId, data, situacao }
  // -------------------------------------------------
  async function salvarNovaMatricula(e: React.FormEvent<HTMLFormElement>): Promise<void> {
    e.preventDefault();

    try {
      setErro(null);

      const payload = {
        alunoId: alunoIdInput,
        turmaId: turmaIdInput,
        data: dataInput,
        situacao: situacaoInput,
      };

      await api.post("/matriculas", payload);

      setModalAberto(false);
      // limpa form
      setAlunoIdInput(0);
      setTurmaIdInput(0);
      setDataInput("");
      setSituacaoInput("ATIVA");

      // recarrega tabela enriquecida
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao criar matrícula.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao criar matrícula.");
      }
    }
  }

  // -------------------------------------------------
  // Excluir matrícula
  // DELETE /api/v1/matriculas/{id}
  // -------------------------------------------------
  async function confirmarExclusao(): Promise<void> {
    if (!matriculaExcluir) return;
    try {
      await api.delete(`/matriculas/${matriculaExcluir.id}`);
      setMatriculaExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao excluir matrícula.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao excluir matrícula.");
      }
    }
  }

  // -------------------------------------------------
  // Colunas da tabela
  // Agora temos alunoNome e turmaPeriodo enriquecidos no front
  // -------------------------------------------------
  const columns: ColumnDef<MatriculaView>[] = [
    { header: "ID", accessor: "id" },
    { header: "Aluno (Nome)", accessor: "alunoNome" },
    { header: "Turma (Período)", accessor: "turmaPeriodo" },
    { header: "Data Matrícula", accessor: "data" },
    { header: "Situação", accessor: "situacao" },
  ];

  // -------------------------------------------------
  // Render
  // -------------------------------------------------
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        {/* HEADER */}
        <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Matrículas
            </h1>
            <p className="text-sm text-slate-500">
              Controle de vínculo aluno ↔ turma (ADMIN)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={() => setModalAberto(true)}
          >
            Nova Matrícula
          </button>
        </header>

        {/* ERRO GLOBAL */}
        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        {/* TABELA / LOADING */}
        {carregando ? (
          <div className="bg-white border border-slate-200 rounded-xl shadow p-6 text-center text-slate-500 text-sm">
            Carregando matrículas...
          </div>
        ) : (
          <DataTable<MatriculaView>
            data={matriculas}
            columns={columns}
            // edição completa de matrícula não implementada aqui porque envolve trocar aluno/turma
            onEdit={undefined}
            onDelete={(m) => setMatriculaExcluir(m)}
          />
        )}

        {/* MODAL CRIAR MATRÍCULA */}
        <Modal
          open={modalAberto}
          title="Nova Matrícula"
          onClose={() => setModalAberto(false)}
        >
          <form className="grid gap-4 text-sm" onSubmit={salvarNovaMatricula}>
            <div>
              <label className="block text-slate-600 font-medium">
                ID do Aluno
              </label>
              <input
                value={alunoIdInput === 0 ? "" : alunoIdInput}
                onChange={(e) => setAlunoIdInput(Number(e.target.value))}
                type="number"
                min={1}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Ex: 10"
                required
              />
              <p className="text-xs text-slate-400 mt-1">
                Vamos buscar o nome automaticamente.
              </p>
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                ID da Turma
              </label>
              <input
                value={turmaIdInput === 0 ? "" : turmaIdInput}
                onChange={(e) => setTurmaIdInput(Number(e.target.value))}
                type="number"
                min={1}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Ex: 5"
                required
              />
              <p className="text-xs text-slate-400 mt-1">
                Vamos buscar o período automaticamente.
              </p>
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Data da Matrícula
              </label>
              <input
                value={dataInput}
                onChange={(e) => setDataInput(e.target.value)}
                type="date"
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Situação
              </label>
              <select
                value={situacaoInput}
                onChange={(e) => setSituacaoInput(e.target.value)}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              >
                <option value="ATIVA">ATIVA</option>
                <option value="TRANCADA">TRANCADA</option>
                <option value="CANCELADA">CANCELADA</option>
              </select>
            </div>

            <div className="flex justify-end gap-2 pt-4">
              <button
                type="button"
                className="rounded-lg border border-slate-300 px-4 py-2 font-medium text-slate-600 hover:bg-slate-100"
                onClick={() => setModalAberto(false)}
              >
                Cancelar
              </button>

              <button className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 font-medium">
                Salvar Matrícula
              </button>
            </div>
          </form>
        </Modal>

        {/* CONFIRMAR EXCLUSÃO */}
        {matriculaExcluir && (
          <ConfirmDialog
            title="Excluir matrícula"
            message={`Deseja excluir a matrícula #${matriculaExcluir.id} de ${matriculaExcluir.alunoNome} (${matriculaExcluir.turmaPeriodo})?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setMatriculaExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
