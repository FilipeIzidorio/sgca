export interface Avaliacao {
  id: number;
  titulo: string;
  peso: number;
  tipo: "PROVA" | "TRABALHO" | "PARTICIPACAO";
  turmaId: number;
}
