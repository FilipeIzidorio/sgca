export interface Presenca {
  id: number;
  turmaId: number;
  matriculaId: number;
  dataAula: string; // LocalDate ISO
  presente: boolean;
}
