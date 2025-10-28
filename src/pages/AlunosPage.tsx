import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import axios, { AxiosError } from "axios";
import { Aluno, StatusAluno } from "../types/Aluno";

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

// Modal genérico reutilizável
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

export default function AlunosPage() {
  // lista de alunos carregados do backend
  const [alunos, setAlunos] = useState<Aluno[]>([]);

  // loading global da listagem
  const [carregando, setCarregando] = useState<boolean>(true);

  // erro global (carregar / salvar / deletar)
  const [erro, setErro] = useState<string | null>(null);

  // modal de cadastro/edição
  const [modalAberto, setModalAberto] = useState<boolean>(false);

  // aluno sendo editado (null = criando)
  const [editando, setEditando] = useState<Aluno | null>(null);

  // confirmação de exclusão
  const [alunoExcluir, setAlunoExcluir] = useState<Aluno | null>(null);

  // campos do formulário
  const [nome, setNome] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [cpf, setCpf] = useState<string>("");
  const [dataNascimento, setDataNascimento] = useState<string>("");
  const [status, setStatus] = useState<StatusAluno>("ATIVO");

  // -------------------------------------------------
  // Funções auxiliares da UI
  // -------------------------------------------------

  function abrirNovo(): void {
    setEditando(null);
    setNome("");
    setEmail("");
    setCpf("");
    setDataNascimento("");
    setStatus("ATIVO");
    setModalAberto(true);
  }

  function abrirEditar(a: Aluno): void {
    setEditando(a);
    setNome(a.nome);
    setEmail(a.email);
    setCpf(a.cpf);
    setDataNascimento(a.dataNascimento); // já vem no formato yyyy-MM-dd
    setStatus(a.status);
    setModalAberto(true);
  }

  // -------------------------------------------------
  // Carregar lista de alunos
  // GET /api/v1/alunos
  // -------------------------------------------------
  async function carregar(): Promise<void> {
    try {
      setCarregando(true);
      setErro(null);

      const resp = await api.get<Aluno[]>("/alunos");
      const data = Array.isArray(resp.data) ? resp.data : [];
      setAlunos(data);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          (e.response?.status === 401
            ? "Sessão expirada. Faça login novamente."
            : e.response?.status === 403
            ? "Acesso negado. Apenas ADMIN pode gerenciar alunos."
            : "Erro ao carregar alunos.");
        setErro(msg);
      } else {
        setErro("Erro inesperado ao carregar alunos.");
      }
    } finally {
      setCarregando(false);
    }
  }

  useEffect(() => {
    void carregar();
  }, []);

  // -------------------------------------------------
  // Salvar (criar ou atualizar aluno)
  //
  // Criar: POST /api/v1/alunos
  // Atualizar: PUT /api/v1/alunos/{id}
  //
  // Corpo aceito pelo controller é AlunoDTO:
  // { id?, nome, email, cpf, dataNascimento(yyyy-MM-dd), status }
  // -------------------------------------------------
  async function salvar(e: React.FormEvent<HTMLFormElement>): Promise<void> {
    e.preventDefault();

    const payload = {
      nome,
      email,
      cpf,
      dataNascimento,
      status,
    };

    try {
      if (editando) {
        // atualizar aluno
        await api.put(`/alunos/${editando.id}`, payload);
      } else {
        // criar aluno
        await api.post("/alunos", payload);
      }

      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao salvar aluno.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao salvar aluno.");
      }
    }
  }

  // -------------------------------------------------
  // Excluir aluno
  // DELETE /api/v1/alunos/{id}
  // -------------------------------------------------
  async function confirmarExclusao(): Promise<void> {
    if (!alunoExcluir) return;

    try {
      await api.delete(`/alunos/${alunoExcluir.id}`);
      setAlunoExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao excluir aluno.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao excluir aluno.");
      }
    }
  }

  // -------------------------------------------------
  // Colunas da tabela
  // -------------------------------------------------
  const columns: ColumnDef<Aluno>[] = [
    { header: "ID", accessor: "id" },
    { header: "Nome", accessor: "nome" },
    { header: "Email", accessor: "email" },
    { header: "CPF", accessor: "cpf" },
    { header: "Nascimento", accessor: "dataNascimento" },
    { header: "Status", accessor: "status" },
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
              Alunos
            </h1>
            <p className="text-sm text-slate-500">
              Gerenciamento de alunos (somente ADMIN)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNovo}
          >
            Novo Aluno
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
            Carregando alunos...
          </div>
        ) : (
          <DataTable<Aluno>
            data={alunos}
            columns={columns}
            onEdit={(a) => abrirEditar(a)}
            onDelete={(a) => setAlunoExcluir(a)}
          />
        )}

        {/* MODAL CADASTRO/EDIÇÃO */}
        <Modal
          open={modalAberto}
          title={editando ? "Editar Aluno" : "Novo Aluno"}
          onClose={() => setModalAberto(false)}
        >
          <form className="grid gap-4 text-sm" onSubmit={salvar}>
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
                Email
              </label>
              <input
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                type="email"
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                CPF
              </label>
              <input
                value={cpf}
                onChange={(e) => setCpf(e.target.value)}
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder="123.456.789-00"
                required
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Data de Nascimento
              </label>
              <input
                value={dataNascimento}
                onChange={(e) => setDataNascimento(e.target.value)}
                type="date"
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Status
              </label>
              <select
                value={status}
                onChange={(e) =>
                  setStatus(e.target.value as StatusAluno)
                }
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              >
                <option value="ATIVO">ATIVO</option>
                <option value="INATIVO">INATIVO</option>
                <option value="FORMADO">FORMADO</option>
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
                {editando ? "Salvar Alterações" : "Cadastrar"}
              </button>
            </div>
          </form>
        </Modal>

        {/* CONFIRMAR EXCLUSÃO */}
        {alunoExcluir && (
          <ConfirmDialog
            title="Excluir aluno"
            message={`Deseja excluir o aluno "${alunoExcluir.nome}"?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setAlunoExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
