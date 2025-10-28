import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import { Nota } from "../types/Nota";
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

export default function NotaPage() {
  const [notas, setNotas] = useState<Nota[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  const [modalAberto, setModalAberto] = useState(false);
  const [editando, setEditando] = useState<Nota | null>(null);
  const [notaExcluir, setNotaExcluir] = useState<Nota | null>(null);

  // form
  const [avaliacaoId, setAvaliacaoId] = useState<number>(0);
  const [matriculaId, setMatriculaId] = useState<number>(0);
  const [valor, setValor] = useState<number>(0);
  const [data, setData] = useState("");

  function abrirNovo(): void {
    setEditando(null);
    setAvaliacaoId(0);
    setMatriculaId(0);
    setValor(0);
    setData("");
    setModalAberto(true);
  }

  function abrirEditar(n: Nota): void {
    setEditando(n);
    // para editar, só vamos alterar valor
    setAvaliacaoId(n.avaliacaoId);
    setMatriculaId(n.matriculaId);
    setValor(n.valor);
    setData(n.data);
    setModalAberto(true);
  }

  async function carregar(): Promise<void> {
    try {
      const resp = await api.get<Nota[]>("/notas");
      setNotas(resp.data);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao carregar notas");
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

    try {
      if (editando) {
        // PATCH /notas/{id}/valor  body: { valor: number }
        await api.patch(`/notas/${editando.id}/valor`, {
          valor
        });
      } else {
        // POST /notas
        const payload: Omit<Nota, "id"> = {
          avaliacaoId,
          matriculaId,
          valor,
          data
        };
        await api.post<Nota>("/notas", payload);
      }

      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao salvar nota");
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  async function confirmarExclusao(): Promise<void> {
    if (!notaExcluir) return;
    try {
      await api.delete(`/notas/${notaExcluir.id}`);
      setNotaExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao excluir nota");
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  const columns: ColumnDef<Nota>[] = [
    { header: "ID", accessor: "id" },
    { header: "Avaliação ID", accessor: "avaliacaoId" },
    { header: "Matrícula ID", accessor: "matriculaId" },
    { header: "Valor", accessor: "valor" },
    { header: "Data", accessor: "data" }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        <header className="flex items-center justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Notas
            </h1>
            <p className="text-sm text-slate-500">
              Lançamento e ajuste de notas (ADMIN / PROFESSOR)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNovo}
          >
            Nova Nota
          </button>
        </header>

        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        <DataTable<Nota>
          data={notas}
          columns={columns}
          onEdit={(n) => abrirEditar(n)}
          onDelete={(n) => setNotaExcluir(n)}
        />

        <Modal
          open={modalAberto}
          title={editando ? "Editar Nota" : "Nova Nota"}
          onClose={() => setModalAberto(false)}
        >
          <form className="grid gap-4 text-sm" onSubmit={salvar}>
            {!editando && (
              <>
                <div>
                  <label className="block text-slate-600 font-medium">
                    Avaliação ID
                  </label>
                  <input
                    value={avaliacaoId === 0 ? "" : avaliacaoId}
                    onChange={(e) =>
                      setAvaliacaoId(Number(e.target.value))
                    }
                    type="number"
                    min={1}
                    required
                    className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                  />
                </div>

                <div>
                  <label className="block text-slate-600 font-medium">
                    Matrícula ID
                  </label>
                  <input
                    value={matriculaId === 0 ? "" : matriculaId}
                    onChange={(e) =>
                      setMatriculaId(Number(e.target.value))
                    }
                    type="number"
                    min={1}
                    required
                    className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                  />
                </div>

                <div>
                  <label className="block text-slate-600 font-medium">
                    Data
                  </label>
                  <input
                    value={data}
                    onChange={(e) => setData(e.target.value)}
                    type="datetime-local"
                    required
                    className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                  />
                </div>
              </>
            )}

            <div>
              <label className="block text-slate-600 font-medium">
                Valor
              </label>
              <input
                value={valor === 0 ? "" : valor}
                onChange={(e) => setValor(Number(e.target.value))}
                type="number"
                min={0}
                max={10}
                step={0.1}
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

        {notaExcluir && (
          <ConfirmDialog
            title="Excluir nota"
            message={`Deseja excluir a nota ID ${notaExcluir.id}?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setNotaExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
