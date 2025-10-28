export interface Matricula {
  id: number;
  alunoId: number;
  turmaId: number;
  data: string; // LocalDate ISO
  situacao: "ATIVA" | "TRANCADA" | "CANCELADA" | "CONCLUIDA";
}
// Matricula que o front EXIBE (enriquecida)
export interface MatriculaView extends Matricula {
  alunoNome: string;
  turmaPeriodo: string;
}