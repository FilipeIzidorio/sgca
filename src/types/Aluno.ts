export type StatusAluno = "ATIVO" | "INATIVO" | "FORMADO";

export interface Aluno {
  id: number;
  nome: string;
  email: string;
  cpf: string;
  dataNascimento: string; // vem como "2000-05-15" (LocalDate -> string)
  status: StatusAluno;
}
