import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { useAuthStore } from "./useAuthStore";
import {
  Box,
  Button,
  Card,
  CardContent,
  TextField,
  Typography,
  Alert,
} from "@mui/material";

export default function LoginPage() {
  const navigate = useNavigate();
  const loginStore = useAuthStore();
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [erro, setErro] = useState<string | null>(null);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setErro(null);

    try {
      const res = await api.post("/auth/login", { email, senha });

      // backend responde:
      // {
      //   mensagem: "...",
      //   usuario: { id, nome, email, perfil },
      //   token: "jwt..."
      // }

      const { token, usuario } = res.data;

      loginStore.login(token, {
        id: usuario.id,
        nome: usuario.nome,
        email: usuario.email,
        perfil: usuario.perfil,
      });

      navigate("/dashboard");
    } catch (err: any) {
      setErro(
        err?.response?.data?.erro ||
          "Falha no login. Verifique suas credenciais."
      );
    }
  }

  return (
    <Box
      sx={{
        display: "grid",
        placeItems: "center",
        minHeight: "100vh",
        bgcolor: "#f5f5f5",
        p: 2,
      }}
    >
      <Card sx={{ width: "100%", maxWidth: 380 }}>
        <CardContent>
          <Typography variant="h5" mb={1} fontWeight={600} textAlign="center">
            SGCA
          </Typography>
          <Typography
            variant="body2"
            color="text.secondary"
            textAlign="center"
            mb={3}
          >
            Acesse com seu e-mail institucional
          </Typography>

          {erro && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {erro}
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <TextField
              label="E-mail"
              type="email"
              value={email}
              fullWidth
              required
              onChange={(e) => setEmail(e.target.value)}
              sx={{ mb: 2 }}
            />
            <TextField
              label="Senha"
              type="password"
              value={senha}
              fullWidth
              required
              onChange={(e) => setSenha(e.target.value)}
              sx={{ mb: 3 }}
            />
            <Button type="submit" fullWidth variant="contained" size="large">
              Entrar
            </Button>
          </form>
        </CardContent>
      </Card>
    </Box>
  );
}
