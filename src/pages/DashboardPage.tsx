import { Card, CardContent, Grid, Typography } from "@mui/material";
import { useAuthStore } from "../auth/useAuthStore";

export default function DashboardPage() {
  const { user } = useAuthStore();

  return (
    <Grid container spacing={2} sx={{ p: 2 }}>
      <Grid item xs={12}>
        <Typography variant="h5" fontWeight={600}>
          Olá, {user?.nome} 👋
        </Typography>
        <Typography variant="body2" color="text.secondary">
          Perfil: {user?.perfil}
        </Typography>
      </Grid>

      <Grid item xs={12} md={4}>
        <Card>
          <CardContent>
            <Typography variant="subtitle2" color="text.secondary">
              Acesso rápido
            </Typography>
            <Typography variant="h6" fontWeight={600}>
              Cursos
            </Typography>
            <Typography variant="body2">
              Cadastre e gerencie cursos da instituição.
            </Typography>
          </CardContent>
        </Card>
      </Grid>

      <Grid item xs={12} md={4}>
        <Card>
          <CardContent>
            <Typography variant="subtitle2" color="text.secondary">
              Acesso rápido
            </Typography>
            <Typography variant="h6" fontWeight={600}>
              Turmas
            </Typography>
            <Typography variant="body2">
              Atribua professores, período e capacidade.
            </Typography>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
}
