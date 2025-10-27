import { useEffect, useState } from "react";
import api from "../api/api";
import { Typography, Box, CircularProgress } from "@mui/material";
import { DataTable } from "../components/DataTable";

interface TurmaListItem {
  id: number;
  periodo: string;
  capacidade: number | null;
  disciplinaId: number;
  professorId: number | null;
  disciplinaNome?: string;
  professorNome?: string;
}

export default function TurmasPage() {
  const [turmas, setTurmas] = useState<TurmaListItem[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchTurmas() {
      try {
        const res = await api.get("/turmas");
        setTurmas(res.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    fetchTurmas();
  }, []);

  if (loading) {
    return (
      <Box sx={{ p: 2 }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ p: 2 }}>
      <Typography variant="h6" mb={2} fontWeight={600}>
        Turmas
      </Typography>

      <DataTable
        rows={turmas}
        columns={[
          { key: "id", header: "ID" },
          { key: "periodo", header: "Período" },
          {
            key: "disciplinaNome",
            header: "Disciplina",
            render: (row) => row.disciplinaNome || `#${row.disciplinaId}`,
          },
          {
            key: "professorNome",
            header: "Professor",
            render: (row) => row.professorNome || (row.professorId ? `#${row.professorId}` : "-"),
          },
          { key: "capacidade", header: "Capacidade" },
        ]}
      />
    </Box>
  );
}
