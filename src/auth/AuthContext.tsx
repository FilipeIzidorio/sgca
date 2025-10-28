import { createContext, useContext } from "react";
import { useAuthStore } from "./useAuthStorePage";

interface AuthContextType {
  user: ReturnType<typeof useAuthStore>["user"];
  token: string | null;
  isAuthenticated: boolean;
  hasRole: (role: string) => boolean;
  logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const { user, token, logout } = useAuthStore();

  const value: AuthContextType = {
    user,
    token,
    isAuthenticated: !!token,
    hasRole: (role: string) => user?.perfil === role,
    logout
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth deve ser usado dentro de <AuthProvider />");
  return ctx;
}
