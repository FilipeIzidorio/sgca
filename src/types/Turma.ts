// Essa é a estrutura crua que o backend já retorna (TurmaDTO)
export interface Turma {
  id: number;
  disciplinaId: number;
  periodo: string;         // ex: "2025.1"
  professorId: number;
  capacidade: number | null;
}

// Essa é a versão enriquecida que vamos exibir na tabela
export interface TurmaView extends Turma {
  disciplinaNome: string;
  professorNome: string;
}
