import { create } from "zustand";

interface AuthUser {
  id: number;
  nome: string;
  email: string;
  perfil: "ADMIN" | "PROFESSOR" | "ALUNO";
}

interface AuthState {
  user: AuthUser | null;
  token: string | null;
  login: (data: { user: AuthUser; token: string }) => void;
  logout: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  token: null,

  login: ({ user, token }) => {
    localStorage.setItem("sgca_token", token);
    localStorage.setItem("sgca_user", JSON.stringify(user));
    set({ user, token });
  },

  logout: () => {
    localStorage.removeItem("sgca_token");
    localStorage.removeItem("sgca_user");
    set({ user: null, token: null });
  }
}));

/** Hidrata o Zustand com dados do localStorage (mantém login ao recarregar a página) */
export function initAuthStoreFromStorage() {
  const token = localStorage.getItem("sgca_token");
  const userRaw = localStorage.getItem("sgca_user");

  if (token && userRaw) {
    try {
      const user = JSON.parse(userRaw);
      useAuthStore.setState({ token, user });
    } catch {
      localStorage.removeItem("sgca_token");
      localStorage.removeItem("sgca_user");
    }
  }
}
