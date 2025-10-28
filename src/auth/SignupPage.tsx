import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import axios, { AxiosError } from "axios";

interface ApiError {
  message?: string;
}

export default function SignupPage() {
  const navigate = useNavigate();

  const [nome, setNome] = useState("");
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState<string | null>(null);
  const [okMsg, setOkMsg] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setErro(null);
    setOkMsg(null);

    try {
      await api.post("/auth/signup", { nome, email, senha });
      setOkMsg("Usuário criado com sucesso! Faça login.");
      setNome("");
      setEmail("");
      setSenha("");
      setTimeout(() => navigate("/login"), 2000);
    } catch (err) {
      if (axios.isAxiosError(err)) {
        const axiosError = err as AxiosError<ApiError>;
        setErro(axiosError.response?.data?.message || "Erro ao cadastrar");
      } else {
        setErro("Erro inesperado. Tente novamente.");
      }
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center bg-slate-100 p-4">
      <div className="w-full max-w-sm bg-white rounded-xl shadow p-6">
        <h1 className="text-2xl font-semibold text-center text-primary-600 mb-4">
          Criar Conta
        </h1>

        {erro && (
          <div className="bg-red-100 text-red-700 text-sm rounded p-2 mb-3">
            {erro}
          </div>
        )}
        {okMsg && (
          <div className="bg-green-100 text-green-700 text-sm rounded p-2 mb-3">
            {okMsg}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm text-slate-700">Nome</label>
            <input
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              className="mt-1 w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm text-slate-700">Email</label>
            <input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              type="email"
              className="mt-1 w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary-500"
              required
            />
          </div>

          <div>
            <label className="block text-sm text-slate-700">Senha</label>
            <input
              value={senha}
              onChange={(e) => setSenha(e.target.value)}
              type="password"
              className="mt-1 w-full border border-slate-300 rounded-lg px-3 py-2 text-sm focus:ring-2 focus:ring-primary-500"
              required
            />
          </div>

          <button className="w-full bg-primary-500 hover:bg-primary-600 text-white py-2 rounded-lg text-sm font-medium transition">
            Cadastrar
          </button>
        </form>

        <p className="text-center text-xs text-slate-500 mt-4">
          Já possui conta?{" "}
          <button
            onClick={() => navigate("/login")}
            className="text-primary-600 hover:underline"
          >
            Fazer login
          </button>
        </p>
      </div>
    </div>
  );
}
