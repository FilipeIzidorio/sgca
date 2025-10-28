import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import { Presenca } from "../types/Presenca";
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

export default function PresencasPage() {
  const [presencas, setPresencas] = useState<Presenca[]>([]);
  const [erro, setErro] = useState<string | null>(null);

  const [modalAberto, setModalAberto] = useState(false);
  const [editando, setEditando] = useState<Presenca | null>(null);
  const [presencaExcluir, setPresencaExcluir] = useState<Presenca | null>(null);

  // form
  const [turmaId, setTurmaId] = useState<number>(0);
  const [matriculaId, setMatriculaId] = useState<number>(0);
  const [dataAula, setDataAula] = useState("");
  const [presente, setPresente] = useState<boolean>(true);

  function abrirNovo(): void {
    setEditando(null);
    setTurmaId(0);
    setMatriculaId(0);
    setDataAula("");
    setPresente(true);
    setModalAberto(true);
  }

  function abrirEditar(p: Presenca): void {
    setEditando(p);
    setTurmaId(p.turmaId);
    setMatriculaId(p.matriculaId);
    setDataAula(p.dataAula);
    setPresente(p.presente);
    setModalAberto(true);
  }

  async function carregar(): Promise<void> {
    try {
      const resp = await api.get<Presenca[]>("/presencas");
      setPresencas(resp.data);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao carregar presenças");
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
        // PATCH /presencas/{id} body: { presente: boolean }
        await api.patch(`/presencas/${editando.id}`, {
          presente
        });
      } else {
        // POST /presencas
        const payload: Omit<Presenca, "id"> = {
          turmaId,
          matriculaId,
          dataAula,
          presente
        };
        await api.post<Presenca>("/presencas", payload);
      }

      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao salvar presença");
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  async function confirmarExclusao(): Promise<void> {
    if (!presencaExcluir) return;
    try {
      await api.delete(`/presencas/${presencaExcluir.id}`);
      setPresencaExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        setErro(e.response?.data?.erro || "Erro ao excluir presença");
      } else {
        setErro("Erro inesperado");
      }
    }
  }

  const columns: ColumnDef<Presenca>[] = [
    { header: "ID", accessor: "id" },
    { header: "Turma ID", accessor: "turmaId" },
    { header: "Matrícula ID", accessor: "matriculaId" },
    { header: "Data Aula", accessor: "dataAula" },
    { header: "Presente", accessor: "presente" }
  ];

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        <header className="flex items-center justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Presenças
            </h1>
            <p className="text-sm text-slate-500">
              Frequência em aula (ADMIN / PROFESSOR)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNovo}
          >
            Nova Presença
          </button>
        </header>

        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        <DataTable<Presenca>
          data={presencas}
          columns={columns}
          onEdit={(p) => abrirEditar(p)}
          onDelete={(p) => setPresencaExcluir(p)}
        />

        <Modal
          open={modalAberto}
          title={editando ? "Editar Presença" : "Nova Presença"}
          onClose={() => setModalAberto(false)}
        >
          <form className="grid gap-4 text-sm" onSubmit={salvar}>
            {!editando && (
              <>
                <div>
                  <label className="block text-slate-600 font-medium">
                    Turma ID
                  </label>
                  <input
                    value={turmaId === 0 ? "" : turmaId}
                    onChange={(e) =>
                      setTurmaId(Number(e.target.value))
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
                    Data da Aula
                  </label>
                  <input
                    value={dataAula}
                    onChange={(e) => setDataAula(e.target.value)}
                    type="date"
                    required
                    className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                  />
                </div>
              </>
            )}

            <div>
              <label className="block text-slate-600 font-medium">
                Presente?
              </label>
              <select
                value={presente ? "true" : "false"}
                onChange={(e) =>
                  setPresente(e.target.value === "true")
                }
                required
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
              >
                <option value="true">Presente</option>
                <option value="false">Faltou</option>
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
                {editando ? "Salvar Presença" : "Cadastrar"}
              </button>
            </div>
          </form>
        </Modal>

        {presencaExcluir && (
          <ConfirmDialog
            title="Excluir presença"
            message={`Deseja excluir o registro de presença ID ${presencaExcluir.id}?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setPresencaExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
