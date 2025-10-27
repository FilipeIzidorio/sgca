import { Box, Typography } from "@mui/material";

export default function NotFoundPage() {
  return (
    <Box sx={{ p: 2 }}>
      <Typography variant="h5" fontWeight={600}>
        404 - Página não encontrada
      </Typography>
      <Typography variant="body2" color="text.secondary">
        Verifique a URL.
      </Typography>
    </Box>
  );
}
