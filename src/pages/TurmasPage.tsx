import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import axios, { AxiosError } from "axios";

import { Turma, TurmaView } from "../types/Turma";
import { Disciplina } from "../types/Disciplina";
import { Usuario } from "../types/Usuario";

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

export default function TurmasPage() {
  // tabela renderiza essa lista (já enriquecida com nomes)
  const [turmasView, setTurmasView] = useState<TurmaView[]>([]);

  // guardamos as turmas "cruas" pra usar no formulário de edição
  const [turmasRaw, setTurmasRaw] = useState<Turma[]>([]);

  // estados globais
  const [carregando, setCarregando] = useState<boolean>(true);
  const [erro, setErro] = useState<string | null>(null);

  // modal criar/editar
  const [modalAberto, setModalAberto] = useState<boolean>(false);
  const [editando, setEditando] = useState<Turma | null>(null);

  // confirmação de exclusão
  const [turmaExcluir, setTurmaExcluir] = useState<TurmaView | null>(null);

  // campos do formulário (TurmaDTO)
  const [disciplinaId, setDisciplinaId] = useState<number | null>(null);
  const [periodo, setPeriodo] = useState<string>(""); // ex: "2025.1"
  const [professorId, setProfessorId] = useState<number | null>(null);
  const [capacidade, setCapacidade] = useState<number | null>(null);

  // cache de nomes pra evitar chamadas repetidas
  const disciplinaCacheRef = useState<Map<number, string>>(
    () => new Map()
  )[0];

  const professorCacheRef = useState<Map<number, string>>(
    () => new Map()
  )[0];

  // util: nome da disciplina pelo ID, evitando chamar se for nulo
  async function getDisciplinaNome(id: number | null | undefined): Promise<string> {
    if (!id || id <= 0) {
      return "";
    }

    if (disciplinaCacheRef.has(id)) {
      return disciplinaCacheRef.get(id) ?? "";
    }

    try {
      // GET /api/v1/disciplinas/{id}
      const resp = await api.get<Disciplina>(`/disciplinas/${id}`);
      const nome = resp.data?.nome ?? "";
      disciplinaCacheRef.set(id, nome);
      return nome;
    } catch {
      disciplinaCacheRef.set(id, "");
      return "";
    }
  }

  // util: nome do professor pelo ID, evitando chamar se for nulo
  async function getProfessorNome(id: number | null | undefined): Promise<string> {
    if (!id || id <= 0) {
      return "";
    }

    if (professorCacheRef.has(id)) {
      return professorCacheRef.get(id) ?? "";
    }

    try {
      // GET /api/v1/usuarios/{id}
      const resp = await api.get<Usuario>(`/usuarios/${id}`);
      const nome = resp.data?.nome ?? "";
      professorCacheRef.set(id, nome);
      return nome;
    } catch {
      professorCacheRef.set(id, "");
      return "";
    }
  }

  // carrega todas as turmas e enriquece (disciplinaNome, professorNome)
  async function carregar(): Promise<void> {
    try {
      setCarregando(true);
      setErro(null);

      // GET /api/v1/turmas
      const resp = await api.get<Turma[]>("/turmas");
      const turmas = Array.isArray(resp.data) ? resp.data : [];

      // pega ids únicos válidos (>0) pra cachear tudo antes
      const disciplinaIdsUnicos = [
        ...new Set(
          turmas
            .map((t) => t.disciplinaId)
            .filter((id): id is number => id !== null && id !== undefined && id > 0)
        ),
      ];

      const professorIdsUnicos = [
        ...new Set(
          turmas
            .map((t) => t.professorId)
            .filter((id): id is number => id !== null && id !== undefined && id > 0)
        ),
      ];

      // pré-carrega nomes de disciplinas e professores
      for (const did of disciplinaIdsUnicos) {
        if (!disciplinaCacheRef.has(did)) {
          await getDisciplinaNome(did);
        }
      }

      for (const pid of professorIdsUnicos) {
        if (!professorCacheRef.has(pid)) {
          await getProfessorNome(pid);
        }
      }

      // monta TurmaView final
      const enriquecida: TurmaView[] = turmas.map((t) => ({
        ...t,
        disciplinaNome:
          (t.disciplinaId && t.disciplinaId > 0
            ? disciplinaCacheRef.get(t.disciplinaId) ?? ""
            : ""),
        professorNome:
          (t.professorId && t.professorId > 0
            ? professorCacheRef.get(t.professorId) ?? ""
            : ""),
      }));

      setTurmasRaw(turmas);
      setTurmasView(enriquecida);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          (e.response?.status === 401
            ? "Sessão expirada. Faça login novamente."
            : e.response?.status === 403
            ? "Acesso negado. (ADMIN / PROFESSOR)"
            : "Erro ao carregar turmas.");
        setErro(msg);
      } else {
        setErro("Erro inesperado ao carregar turmas.");
      }
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    void carregar();
  }, []);

  // abrir modal para cadastrar nova turma
  function abrirNova(): void {
    setEditando(null);
    setDisciplinaId(null);
    setPeriodo("");
    setProfessorId(null);
    setCapacidade(null);
    setModalAberto(true);
  }

  // abrir modal para editar
  function abrirEditar(view: TurmaView): void {
    // procura a versão crua (Turma) pelo id
    const base: Turma | undefined = turmasRaw.find(
      (t) => t.id === view.id
    );

    const origem = base ?? {
      id: view.id,
      disciplinaId: view.disciplinaId,
      periodo: view.periodo,
      professorId: view.professorId,
      capacidade: view.capacidade,
    };

    setEditando(origem);
    setDisciplinaId(origem.disciplinaId ?? null);
    setPeriodo(origem.periodo ?? "");
    setProfessorId(origem.professorId ?? null);
    setCapacidade(
      origem.capacidade !== null && origem.capacidade !== undefined
        ? origem.capacidade
        : null
    );

    setModalAberto(true);
  }

  // salvar turma (POST novo ou PUT atualização)
  async function salvar(e: React.FormEvent<HTMLFormElement>): Promise<void> {
    e.preventDefault();

    // monta payload pro backend (TurmaDTO)
    // obs: professorId pode ser opcional. Se estiver null, não manda string "null"
    // obs: capacidade pode ser null; service aceita Integer, então null é ok
    const payload: {
      disciplinaId: number | null;
      periodo: string;
      professorId: number | null;
      capacidade: number | null;
    } = {
      disciplinaId,
      periodo,
      professorId,
      capacidade,
    };

    try {
      if (editando) {
        // PUT /turmas/{id}
        await api.put(`/turmas/${editando.id}`, payload);
      } else {
        // POST /turmas
        await api.post("/turmas", payload);
      }

      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao salvar turma.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao salvar turma.");
      }
    }
  }

  // excluir turma
  async function confirmarExclusao(): Promise<void> {
    if (!turmaExcluir) return;

    try {
      await api.delete(`/turmas/${turmaExcluir.id}`);
      setTurmaExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao excluir turma.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao excluir turma.");
      }
    }
  }

  // colunas da tabela: agora a gente mostra nomes em vez de só IDs
  const columns: ColumnDef<TurmaView>[] = [
    { header: "ID", accessor: "id" },
    { header: "Disciplina", accessor: "disciplinaNome" },
    { header: "Período", accessor: "periodo" },
    { header: "Professor", accessor: "professorNome" },
    { header: "Capacidade", accessor: "capacidade" },
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        {/* topo da página */}
        <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Turmas
            </h1>
            <p className="text-sm text-slate-500">
              Gerenciamento de turmas (ADMIN e PROFESSOR)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNova}
          >
            Nova Turma
          </button>
        </header>

        {/* erro global */}
        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        {/* tabela */}
        {carregando ? (
          <div className="bg-white border border-slate-200 rounded-xl shadow p-6 text-center text-slate-500 text-sm">
            Carregando turmas...
          </div>
        ) : (
          <DataTable<TurmaView>
            data={turmasView}
            columns={columns}
            onEdit={(t) => abrirEditar(t)}
            onDelete={(t) => setTurmaExcluir(t)}
          />
        )}

        {/* modal criar/editar */}
        <Modal
          open={modalAberto}
          title={editando ? "Editar Turma" : "Nova Turma"}
          onClose={() => setModalAberto(false)}
        >
          <form className="grid gap-4 text-sm" onSubmit={salvar}>
            {/* disciplinaId */}
            <div>
              <label className="block text-slate-600 font-medium">
                ID da Disciplina
              </label>
              <input
                value={disciplinaId ?? ""}
                onChange={(e) => {
                  const v = e.target.value;
                  setDisciplinaId(v === "" ? null : Number(v));
                }}
                type="number"
                min={1}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Ex: 12"
                required
              />
              <p className="text-xs text-slate-400 mt-1">
                A tabela vai mostrar o nome da disciplina.
              </p>
            </div>

            {/* período */}
            <div>
              <label className="block text-slate-600 font-medium">
                Período
              </label>
              <input
                value={periodo}
                onChange={(e) => setPeriodo(e.target.value)}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Ex: 2025.1"
                required
              />
            </div>

            {/* professorId */}
            <div>
              <label className="block text-slate-600 font-medium">
                ID do Professor
              </label>
              <input
                value={professorId ?? ""}
                onChange={(e) => {
                  const v = e.target.value;
                  setProfessorId(v === "" ? null : Number(v));
                }}
                type="number"
                min={1}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Ex: 7"
              />
              <p className="text-xs text-slate-400 mt-1">
                Opcional. A tabela vai mostrar o nome do professor.
              </p>
            </div>

            {/* capacidade */}
            <div>
              <label className="block text-slate-600 font-medium">
                Capacidade
              </label>
              <input
                value={capacidade ?? ""}
                onChange={(e) => {
                  const v = e.target.value;
                  setCapacidade(v === "" ? null : Number(v));
                }}
                type="number"
                min={0}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="Ex: 40"
              />
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
                {editando ? "Salvar Alterações" : "Cadastrar"}
              </button>
            </div>
          </form>
        </Modal>

        {/* confirmar exclusão */}
        {turmaExcluir && (
          <ConfirmDialog
            title="Excluir turma"
            message={`Deseja excluir a turma "${turmaExcluir.periodo}" da disciplina "${turmaExcluir.disciplinaNome}"?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setTurmaExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
