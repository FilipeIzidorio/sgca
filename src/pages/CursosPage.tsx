import { useEffect, useState } from "react";
import api from "../api/api";
import { CursoDTO } from "../types/Curso";
import { Typography, Box, CircularProgress } from "@mui/material";
import { DataTable } from "../components/DataTable";

export default function CursosPage() {
  const [cursos, setCursos] = useState<CursoDTO[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    async function fetchCursos() {
      try {
        const res = await api.get("/cursos");
        setCursos(res.data);
      } catch (err) {
        console.error(err);
      } finally {
        setLoading(false);
      }
    }
    fetchCursos();
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
        Cursos
      </Typography>

      <DataTable
        rows={cursos}
        columns={[
          { key: "id", header: "ID" },
          { key: "codigo", header: "Código" },
          { key: "nome", header: "Nome" },
          { key: "cargaHoraria", header: "Carga Horária (h)" },
          { key: "descricao", header: "Descrição" },
        ]}
      />
    </Box>
  );
}
