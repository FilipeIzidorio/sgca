import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";
import DataTable, { ColumnDef } from "../components/DataTable";
import ConfirmDialog from "../components/ConfirmDialog";
import api from "../api/api";
import { Usuario } from "../types/Usuario";
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

// Modal simples reutilizável
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

type Perfil = Usuario["perfil"];

export default function UsuariosPage() {
  // lista de usuários carregada do backend
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);

  // estado de erro geral
  const [erro, setErro] = useState<string | null>(null);

  // estado de carregamento
  const [carregando, setCarregando] = useState<boolean>(true);

  // modal/crud
  const [modalAberto, setModalAberto] = useState<boolean>(false);
  const [editando, setEditando] = useState<Usuario | null>(null);

  // confirmação de exclusão
  const [usuarioExcluir, setUsuarioExcluir] = useState<Usuario | null>(null);

  // campos de formulário
  const [nome, setNome] = useState<string>("");
  const [email, setEmail] = useState<string>("");
  const [perfil, setPerfil] = useState<Perfil>("ALUNO");
  const [senha, setSenha] = useState<string>("");
  const [confirmarSenha, setConfirmarSenha] = useState<string>("");

  // -----------------------------------------
  // Helpers de UI
  // -----------------------------------------

  function abrirNovo(): void {
    setEditando(null);
    setNome("");
    setEmail("");
    setPerfil("ALUNO");
    setSenha("");
    setConfirmarSenha("");
    setModalAberto(true);
  }

  function abrirEditar(u: Usuario): void {
    setEditando(u);
    setNome(u.nome);
    setEmail(u.email);
    setPerfil(u.perfil);
    setSenha("");
    setConfirmarSenha("");
    setModalAberto(true);
  }

  // -----------------------------------------
  // Carregar lista de usuários (GET /usuarios)
  // rota do backend:
  // @GetMapping -> public ResponseEntity<List<UsuarioDTO>> listar()
  // -----------------------------------------
  async function carregar(): Promise<void> {
    try {
      setCarregando(true);
      setErro(null);

      // GET /api/v1/usuarios
      // O axios `api` já injeta Authorization: Bearer <token> via interceptor.
      const resp = await api.get<Usuario[]>("/usuarios");

      // garante que é array
      const data = Array.isArray(resp.data) ? resp.data : [];
      setUsuarios(data);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          (e.response?.status === 401
            ? "Sessão expirada. Faça login novamente."
            : e.response?.status === 403
            ? "Acesso negado. Apenas ADMIN pode listar usuários."
            : "Erro ao carregar usuários.");
        setErro(msg);
      } else {
        setErro("Erro inesperado ao carregar usuários.");
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
  // - criar: POST /auth/signup (AuthController)
  // - editar: PUT /usuarios/{id} (UsuarioController)
  // -----------------------------------------
  async function salvar(e: React.FormEvent<HTMLFormElement>): Promise<void> {
    e.preventDefault();

    try {
      if (editando) {
        // Atualizar usuário existente
        // backend: PUT /api/v1/usuarios/{id}
        // body esperado no controller:
        // nome, email, senha, confirmarSenha, perfil
        const payload: {
          nome: string;
          email: string;
          perfil: string;
          senha?: string;
          confirmarSenha?: string;
        } = {
          nome,
          email,
          perfil,
        };

        if (senha.trim() !== "") {
          payload.senha = senha;
          payload.confirmarSenha = confirmarSenha;
        }

        await api.put(`/usuarios/${editando.id}`, payload);
      } else {
        // Criar novo usuário
        // backend: POST /api/v1/auth/signup
        // precisa: nome, email, senha, confirmarSenha, perfil
        await api.post("/auth/signup", {
          nome,
          email,
          senha,
          confirmarSenha,
          perfil,
        });
      }

      // se deu tudo certo:
      setModalAberto(false);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao salvar usuário.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao salvar usuário.");
      }
    }
  }

  // -----------------------------------------
  // Excluir usuário
  // backend: DELETE /api/v1/usuarios/{id}
  // -----------------------------------------
  async function confirmarExclusao(): Promise<void> {
    if (!usuarioExcluir) return;

    try {
      await api.delete(`/usuarios/${usuarioExcluir.id}`);
      setUsuarioExcluir(null);
      await carregar();
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiError>;
        const msg =
          e.response?.data?.erro ||
          e.response?.data?.message ||
          "Erro ao excluir usuário.";
        setErro(msg);
      } else {
        setErro("Erro inesperado ao excluir usuário.");
      }
    }
  }

  // -----------------------------------------
  // Definição das colunas da tabela
  // -----------------------------------------
  const columns: ColumnDef<Usuario>[] = [
    { header: "ID", accessor: "id" },
    { header: "Nome", accessor: "nome" },
    { header: "Email", accessor: "email" },
    { header: "Perfil", accessor: "perfil" },
  ];

  // -----------------------------------------
  // Render
  // -----------------------------------------
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />

      <main className="flex-1 p-4 max-w-7xl mx-auto space-y-6">
        {/* HEADER */}
        <header className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <h1 className="text-xl font-semibold text-slate-800">
              Usuários
            </h1>
            <p className="text-sm text-slate-500">
              Gerenciamento de contas do sistema (ADMIN)
            </p>
          </div>

          <button
            className="rounded-lg bg-primary-500 hover:bg-primary-600 text-white px-4 py-2 text-sm font-medium"
            onClick={abrirNovo}
          >
            Novo Usuário
          </button>
        </header>

        {/* ERRO GLOBAL */}
        {erro && (
          <div className="bg-red-100 border border-red-200 text-red-700 text-sm rounded p-2">
            {erro}
          </div>
        )}

        {/* TABELA OU CARREGANDO */}
        {carregando ? (
          <div className="bg-white border border-slate-200 rounded-xl shadow p-6 text-center text-slate-500 text-sm">
            Carregando usuários...
          </div>
        ) : (
          <DataTable<Usuario>
            data={usuarios}
            columns={columns}
            onEdit={(u) => abrirEditar(u)}
            onDelete={(u) => setUsuarioExcluir(u)}
          />
        )}

        {/* MODAL CADASTRO / EDIÇÃO */}
        <Modal
          open={modalAberto}
          title={editando ? "Editar Usuário" : "Novo Usuário"}
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
                Perfil
              </label>
              <select
                value={perfil}
                onChange={(e) =>
                  setPerfil(e.target.value as Perfil)
                }
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                required
              >
                <option value="ADMIN">ADMIN</option>
                <option value="PROFESSOR">PROFESSOR</option>
                <option value="ALUNO">ALUNO</option>
              </select>
            </div>

            <div className="border-t border-slate-200 pt-4">
              <label className="block text-slate-600 font-medium">
                Senha{" "}
                {editando && (
                  <span className="text-xs text-slate-400">
                    (deixe vazio para não alterar)
                  </span>
                )}
              </label>
              <input
                value={senha}
                onChange={(e) => setSenha(e.target.value)}
                type="password"
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder={
                  editando ? "Nova senha (opcional)" : "Senha"
                }
                required={!editando} // obrigatório só no cadastro
              />
            </div>

            <div>
              <label className="block text-slate-600 font-medium">
                Confirmar Senha
              </label>
              <input
                value={confirmarSenha}
                onChange={(e) => setConfirmarSenha(e.target.value)}
                type="password"
                className="mt-1 w-full rounded-lg border border-slate-300 px-3 py-2"
                placeholder={
                  editando
                    ? "Confirmar (se alterar senha)"
                    : "Confirmar senha"
                }
                required={!editando} // obrigatório só no cadastro
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
                {editando
                  ? "Salvar Alterações"
                  : "Cadastrar"}
              </button>
            </div>
          </form>
        </Modal>

        {/* CONFIRM DIALOG EXCLUSÃO */}
        {usuarioExcluir && (
          <ConfirmDialog
            title="Excluir usuário"
            message={`Deseja excluir o usuário "${usuarioExcluir.nome}"?`}
            confirmLabel="Excluir"
            cancelLabel="Cancelar"
            onConfirm={confirmarExclusao}
            onCancel={() => setUsuarioExcluir(null)}
          />
        )}
      </main>
    </div>
  );
}
