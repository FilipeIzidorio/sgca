export interface TurmaDTO {
  id?: number;
  disciplinaId: number;
  periodo: string;
  professorId?: number | null;
  capacidade?: number | null;
}
