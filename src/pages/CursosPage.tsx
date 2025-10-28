import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import { Curso } from "../types/Curso";
import axios, { AxiosError } from "axios";

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

// Modal reutilizável
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

export default function CursosPage() {
  // lista de cursos
  const [cursos, setCursos] = useState<Curso[]>([]);

  // erro global
  const [erro, setErro] = useState<string | null>(null);

  // carregando lista
  const [carregando, setCarregando] = useState<boolean>(true);

  // modal e item em edição
  const [modalAberto, setModalAberto] = useState<boolean>(false);
  const [editando, setEditando] = useState<Curso | null>(null);

  // confirmação de exclusão
  const [cursoExcluir, setCursoExcluir] = useState<Curso | null>(null);

  // campos do formulário
  const [codigo, setCodigo] = useState<string>("");
  const [nome, setNome] = useState<string>("");
  const [cargaHoraria, setCargaHoraria] = useState<number>(0);
  const [descricao, setDescricao] = useState<string>("");

  // -----------------------------------------
  // Funções auxiliares de UI
  // -----------------------------------------

  function abrirNovo(): void {
    setEditando(null);
    setCodigo("");
    setNome("");
    setCargaHoraria(0);
    setDescricao("");
    setModalAberto(true);
  }

  function abrirEditar(curso: Curso): void {
    setEditando(curso);
    setCodigo(curso.codigo);
    setNome(curso.nome);
    setCargaHoraria(curso.cargaHoraria);
    setDescricao(descricao);
    setModalAberto(true);
  }

  // -----------------------------------------
  // Listar cursos (GET /cursos)
  // -----------------------------------------

  async function carregar(): Promise<void> {
    try {
      setCarregando(true);
      setErro(null);

      // backend: @GetMapping em CursoController
      // ResponseEntity<List<CursoDTO>> listarTodos()
      const resp = await api.get<Curso[]>("/cursos");

      const data = Array.isArray(resp.data) ? resp.data : [];
      setCursos(data);
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
            : "Erro ao carregar cursos.");
        setErro(msg);
      } else {
        setErro("Erro inesperado ao carregar cursos.");
      }
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    void carregar();
  }, []);

  // -----------------------------------------
  // Salvar (criar ou atualizar)
  // - criar: POST /cursos
  // - editar: PUT /cursos/{id}
  //
  // Seu CursoDTO no backend:
  // private Long id;
  // private String codigo;
  // private String nome;
  // private int cargaHoraria;
  // private String descricao;
  // -----------------------------------------

  async function salvar(e: React.FormEvent<HTMLFormElement>): Promise<void> {
    e.preventDefault();

    try {
      const payload = {
        codigo,
        nome,
        cargaHoraria,
        descricao,
      };

      if (editando) {
        // atualizar curso existente
        await api.put(`/cursos/${editando.id}`, payload);
      } else {
        // criar novo curso
        await api.post("/cursos", payload);
      }

      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao salvar curso.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao salvar curso.");
      }
    }
  }

  // -----------------------------------------
  // Excluir curso
  // backend: DELETE /cursos/{id}
  // -----------------------------------------

  async function confirmarExclusao(): Promise<void> {
    if (!cursoExcluir) return;

    try {
      await api.delete(`/cursos/${cursoExcluir.id}`);
      setCursoExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao excluir curso.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao excluir curso.");
      }
    }
  }

  // -----------------------------------------
  // Colunas da tabela
  // -----------------------------------------

  const columns: ColumnDef<Curso>[] = [
    { header: "ID", accessor: "id" },
    { header: "Código", accessor: "codigo" },
    { header: "Nome", accessor: "nome" },
    { header: "Carga Horária", accessor: "cargaHoraria" },
    { header: "Descrição", accessor: "descricao" },
  ];

  // -----------------------------------------
  // Render
  // -----------------------------------------

  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        {/* Header */}
        <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Cursos
            </h1>
            <p className="text-sm text-slate-500">
              Cadastro e manutenção de cursos (somente ADMIN)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNovo}
          >
            Novo Curso
          </button>
        </header>

        {/* erro global */}
        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        {/* tabela ou carregando */}
        {carregando ? (
          <div className="bg-white border border-slate-200 rounded-xl shadow p-6 text-center text-slate-500 text-sm">
            Carregando cursos...
          </div>
        ) : (
          <DataTable<Curso>
            data={cursos}
            columns={columns}
            onEdit={(c) => abrirEditar(c)}
            onDelete={(c) => setCursoExcluir(c)}
          />
        )}

        {/* modal de criar/editar curso */}
        <Modal
          open={modalAberto}
          title={editando ? "Editar Curso" : "Novo Curso"}
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
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Nome
              </label>
              <input
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Carga Horária (h)
              </label>
              <input
                value={cargaHoraria}
                onChange={(e) =>
                  setCargaHoraria(Number(e.target.value))
                }
                type="number"
                min={0}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Descrição
              </label>
              <textarea
                value={descricao}
                onChange={(e) => setDescricao(e.target.value)}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2 min-h-[80px] resize-y"
                placeholder="Ex.: Curso de Análise e Desenvolvimento de Sistemas com foco em backend Java."
                required
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

        {/* diálogo de confirmação de exclusão */}
        {cursoExcluir && (
          <ConfirmDialog
            title="Excluir curso"
            message={`Deseja excluir o curso "${cursoExcluir.nome}"?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setCursoExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
