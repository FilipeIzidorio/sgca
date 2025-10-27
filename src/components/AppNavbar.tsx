import { AppBar, Toolbar, Typography, Box, Button } from "@mui/material";
import { useAuthStore } from "../auth/useAuthStore";
import { Link as RouterLink } from "react-router-dom";

export default function AppNavbar() {
  const { user, logout } = useAuthStore();

  return (
    <AppBar position="static" sx={{ mb: 3 }}>
      <Toolbar sx={{ display: "flex", gap: 2 }}>
        <Typography
          variant="h6"
          component={RouterLink}
          to="/dashboard"
          sx={{ color: "white", textDecoration: "none", flexGrow: 1 }}
        >
          SGCA
        </Typography>

        <Button color="inherit" component={RouterLink} to="/cursos">
          Cursos
        </Button>

        <Button color="inherit" component={RouterLink} to="/turmas">
          Turmas
        </Button>

        <Box sx={{ flexGrow: 0 }}>
          <Typography variant="body2" sx={{ lineHeight: 1.2 }}>
            {user?.nome}
          </Typography>
          <Typography variant="caption">{user?.perfil}</Typography>
        </Box>

        <Button
          color="inherit"
          onClick={() => {
            logout();
            window.location.href = "/login";
          }}
        >
          Sair
        </Button>
      </Toolbar>
    </AppBar>
  );
}
