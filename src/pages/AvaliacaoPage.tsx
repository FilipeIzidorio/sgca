import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import { Avaliacao } from "../types/Avaliacao";
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

type TipoAvaliacao = Avaliacao["tipo"];

export default function AvaliacaoPage() {
  const [avaliacoes, setAvaliacoes] = useState<Avaliacao[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  const [modalAberto, setModalAberto] = useState(false);
  const [editando, setEditando] = useState<Avaliacao | null>(null);
  const [avaliacaoExcluir, setAvaliacaoExcluir] = useState<Avaliacao | null>(null);

  const [titulo, setTitulo] = useState("");
  const [peso, setPeso] = useState<number>(0);
  const [tipo, setTipo] = useState<TipoAvaliacao>("PROVA");
  const [turmaId, setTurmaId] = useState<number>(0);

  function abrirNovo(): void {
    setEditando(null);
    setTitulo("");
    setPeso(0);
    setTipo("PROVA");
    setTurmaId(0);
    setModalAberto(true);
  }

  function abrirEditar(a: Avaliacao): void {
    setEditando(a);
    setTitulo(a.titulo);
    setPeso(a.peso);
    setTipo(a.tipo);
    setTurmaId(a.turmaId);
    setModalAberto(true);
  }

  async function carregar(): Promise<void> {
    try {
      const resp = await api.get<Avaliacao[]>("/avaliacoes");
      // se 204 noContent: resp.data pode estar vazio/undefined.
      // axios ainda dá data = ""? melhor garantir array.
      const data = Array.isArray(resp.data) ? resp.data : [];
      setAvaliacoes(data);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(
          e.response?.data?.erro ||
            "Erro ao carregar avaliações"
        );
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

    const payload: Omit<Avaliacao, "id"> = {
      titulo,
      peso,
      tipo,
      turmaId
    };

    try {
      if (editando) {
        await api.put<Avaliacao>(`/avaliacoes/${editando.id}`, payload);
      } else {
        await api.post<Avaliacao>("/avaliacoes", payload);
      }
      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(
          e.response?.data?.erro ||
            "Erro ao salvar avaliação"
        );
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  async function confirmarExclusao(): Promise<void> {
    if (!avaliacaoExcluir) return;
    try {
      await api.delete(`/avaliacoes/${avaliacaoExcluir.id}`);
      setAvaliacaoExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(
          e.response?.data?.erro ||
            "Erro ao excluir avaliação"
        );
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  const columns: ColumnDef<Avaliacao>[] = [
    { header: "ID", accessor: "id" },
    { header: "Título", accessor: "titulo" },
    { header: "Peso", accessor: "peso" },
    { header: "Tipo", accessor: "tipo" },
    { header: "Turma ID", accessor: "turmaId" }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        <header className="flex items-center justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Avaliações
            </h1>
            <p className="text-sm text-slate-500">
              Avaliações por turma (PROFESSOR)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNovo}
          >
            Nova Avaliação
          </button>
        </header>

        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        <DataTable<Avaliacao>
          data={avaliacoes}
          columns={columns}
          onEdit={(a) => abrirEditar(a)}
          onDelete={(a) => setAvaliacaoExcluir(a)}
        />

        <Modal
          open={modalAberto}
          title={editando ? "Editar Avaliação" : "Nova Avaliação"}
          onClose={() => setModalAberto(false)}
        >
          <form className="grid gap-4 text-sm" onSubmit={salvar}>
            <div>
              <label className="block text-slate-600 font-medium">
                Título
              </label>
              <input
                value={titulo}
                onChange={(e) => setTitulo(e.target.value)}
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Peso (%)
              </label>
              <input
                value={peso === 0 ? "" : peso}
                onChange={(e) => setPeso(Number(e.target.value))}
                type="number"
                min={0}
                max={100}
                step={0.1}
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Tipo
              </label>
              <select
                value={tipo}
                onChange={(e) =>
                  setTipo(e.target.value as TipoAvaliacao)
                }
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              >
                <option value="PROVA">PROVA</option>
                <option value="TRABALHO">TRABALHO</option>
                <option value="PARTICIPACAO">PARTICIPACAO</option>
              </select>
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Turma ID
              </label>
              <input
                value={turmaId === 0 ? "" : turmaId}
                onChange={(e) => setTurmaId(Number(e.target.value))}
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

        {avaliacaoExcluir && (
          <ConfirmDialog
            title="Excluir avaliação"
            message={`Deseja excluir a avaliação "${avaliacaoExcluir.titulo}"?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setAvaliacaoExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
