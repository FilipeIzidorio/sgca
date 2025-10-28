import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import { Disciplina } from "../types/Disciplina";
import axios, { AxiosError } from "axios";

interface ApiError {
  erro?: string;
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

export default function DisciplinasPage() {
  const [disciplinas, setDisciplinas] = useState<Disciplina[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  const [modalAberto, setModalAberto] = useState(false);
  const [editando, setEditando] = useState<Disciplina | null>(null);
  const [disciplinaExcluir, setDisciplinaExcluir] = useState<Disciplina | null>(null);

  // form
  const [codigo, setCodigo] = useState("");
  const [nome, setNome] = useState("");
  const [cargaHoraria, setCargaHoraria] = useState<number>(0);
  const [cursoId, setCursoId] = useState<number>(0);

  function abrirNovo(): void {
    setEditando(null);
    setCodigo("");
    setNome("");
    setCargaHoraria(0);
    setCursoId(0);
    setModalAberto(true);
  }

  function abrirEditar(d: Disciplina): void {
    setEditando(d);
    setCodigo(d.codigo);
    setNome(d.nome);
    setCargaHoraria(d.cargaHoraria);
    setCursoId(d.cursoId);
    setModalAberto(true);
  }

  async function carregar(): Promise<void> {
    try {
      const resp = await api.get<Disciplina[]>("/disciplinas");
      setDisciplinas(resp.data);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao carregar disciplinas");
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  useEffect(() => {
    void carregar();
  }, []);

  async function salvar(e: React.FormEvent<HTMLFormElement>): Promise<void> {
    e.preventDefault();

    const payload: Omit<Disciplina, "id"> = {
      codigo,
      nome,
      cargaHoraria,
      cursoId
    };

    try {
      if (editando) {
        await api.put<Disciplina>(`/disciplinas/${editando.id}`, payload);
      } else {
        await api.post<Disciplina>("/disciplinas", payload);
      }
      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao salvar disciplina");
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  async function confirmarExclusao(): Promise<void> {
    if (!disciplinaExcluir) return;
    try {
      await api.delete(`/disciplinas/${disciplinaExcluir.id}`);
      setDisciplinaExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao excluir disciplina");
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  const columns: ColumnDef<Disciplina>[] = [
    { header: "ID", accessor: "id" },
    { header: "Código", accessor: "codigo" },
    { header: "Nome", accessor: "nome" },
    { header: "Carga Horária", accessor: "cargaHoraria" },
    { header: "Curso ID", accessor: "cursoId" }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        <header className="flex items-center justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Disciplinas
            </h1>
            <p className="text-sm text-slate-500">
              Disciplinas ofertadas (ADMIN / PROFESSOR)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNovo}
          >
            Nova Disciplina
          </button>
        </header>

        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        <DataTable<Disciplina>
          data={disciplinas}
          columns={columns}
          onEdit={(d) => abrirEditar(d)}
          onDelete={(d) => setDisciplinaExcluir(d)}
        />

        <Modal
          open={modalAberto}
          title={editando ? "Editar Disciplina" : "Nova Disciplina"}
          onClose={() => setModalAberto(false)}
        >
          <form className="grid gap-4 text-sm" onSubmit={salvar}>
            <div>
              <label className="block text-slate-600 font-medium">
                Código
              </label>
              <input
                value={codigo}
                onChange={(e) => setCodigo(e.target.value)}
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Nome
              </label>
              <input
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Carga Horária
              </label>
              <input
                value={cargaHoraria === 0 ? "" : cargaHoraria}
                onChange={(e) =>
                  setCargaHoraria(Number(e.target.value))
                }
                type="number"
                min={0}
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Curso ID
              </label>
              <input
                value={cursoId === 0 ? "" : cursoId}
                onChange={(e) => setCursoId(Number(e.target.value))}
                type="number"
                min={1}
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
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

        {disciplinaExcluir && (
          <ConfirmDialog
            title="Excluir disciplina"
            message={`Deseja excluir a disciplina "${disciplinaExcluir.nome}"?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setDisciplinaExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
