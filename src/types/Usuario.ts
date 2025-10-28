export type PerfilUsuario = "ADMIN" | "PROFESSOR" | "ALUNO";

export interface Usuario {
  id: number;
  nome: string;
  email: string;
  perfil: PerfilUsuario;
}
