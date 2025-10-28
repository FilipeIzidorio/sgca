import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { useAuthStore } from "./useAuthStorePage";
import { Usuario } from "../types/Usuario";
import axios, { AxiosError } from "axios";

interface LoginResponse {
  mensagem: string;
  usuario: UsuarioPayload;
  token: string;
}

interface UsuarioPayload {
  id: number;
  nome: string;
  email: string;
  perfil: Usuario["perfil"];
}

interface ApiErrorData {
  erro?: string;
}

// decide rota inicial de acordo com o perfil
function getHomeRoute(perfil: Usuario["perfil"]): string {
  switch (perfil) {
    case "ADMIN":
      return "/admin";
    case "PROFESSOR":
      return "/professor";
    case "ALUNO":
      return "/aluno";
    default:
      return "/login";
  }
}

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuthStore();

  const [email, setEmail] = useState<string>("");
  const [senha, setSenha] = useState<string>("");

  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  async function handleSubmit(ev: React.FormEvent<HTMLFormElement>): Promise<void> {
    ev.preventDefault();
    setSubmitting(true);
    setErrorMsg(null);

    try {
      // backend: POST /api/v1/auth/login
      const resp = await api.post<LoginResponse>("/auth/login", {
        email,
        senha,
      });

      // guarda token + user globalmente (zustand)
      login({
        token: resp.data.token,
        user: {
          id: resp.data.usuario.id,
          nome: resp.data.usuario.nome,
          email: resp.data.usuario.email,
          perfil: resp.data.usuario.perfil,
        },
      });

      // decide rota baseada no perfil
      const destino = getHomeRoute(resp.data.usuario.perfil);
      navigate(destino, { replace: true });
    } catch (err) {
      let msg = "Erro ao fazer login.";
      if (axios.isAxiosError(err)) {
        const e = err as AxiosError<ApiErrorData>;
        if (e.response?.data?.erro) {
          msg = e.response.data.erro;
        } else if (e.response?.status === 401) {
          msg = "E-mail ou senha inválidos.";
        }
      }
      setErrorMsg(msg);
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-100 px-4">
      <div className="w-full max-w-sm bg-white rounded-xl shadow border border-slate-200 p-6">
        <h1 className="text-xl font-semibold text-slate-800 text-center">
          SGCA
        </h1>
        <p className="text-center text-slate-500 text-sm mb-6">
          Acesse sua conta
        </p>

        {errorMsg && (
          <div className="mb-4 bg-red-100 text-red-700 text-sm rounded p-2 border border-red-200">
            {errorMsg}
          </div>
        )}

        <form className="space-y-4" onSubmit={handleSubmit}>
          <div className="flex flex-col gap-1">
            <label
              htmlFor="email"
              className="text-xs font-medium text-slate-600"
            >
              E-mail
            </label>
            <input
              id="email"
              type="email"
              className="rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              placeholder="seu.email@exemplo.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              disabled={submitting}
              required
            />
          </div>

          <div className="flex flex-col gap-1">
            <label
              htmlFor="senha"
              className="text-xs font-medium text-slate-600"
            >
              Senha
            </label>
            <input
              id="senha"
              type="password"
              className="rounded-lg border border-slate-300 px-3 py-2 text-sm outline-none focus:ring-2 focus:ring-primary-500 focus:border-primary-500"
              placeholder="••••••••"
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              disabled={submitting}
              required
            />
          </div>

          <button
            type="submit"
            disabled={submitting}
            className="w-full rounded-lg bg-primary-600 text-white text-sm font-semibold py-2 hover:bg-primary-700 disabled:opacity-50 disabled:cursor-not-allowed transition"
          >
            {submitting ? "Entrando..." : "Entrar"}
          </button>
        </form>
      </div>
    </div>
  );
}
