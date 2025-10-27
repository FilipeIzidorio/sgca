import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      main: "#1976d2", // azul padrão MUI
    },
    background: {
      default: "#f5f5f5",
    },
  },
  shape: {
    borderRadius: 10,
  },
});

export default theme;
