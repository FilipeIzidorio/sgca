import { createTheme } from "@mui/material/styles";


const theme = createTheme({
palette: {
primary: { main: "#1976d2" }, // azul padrão MUI
secondary: { main: "#009688" }, // verde água
background: { default: "#f5f5f5" },
},
typography: {
fontFamily: 'Roboto, Arial, sans-serif',
h5: { fontWeight: 600 },
},
shape: {
borderRadius: 10,
},
components: {
MuiButton: {
styleOverrides: {
root: {
borderRadius: 8,
textTransform: 'none',
fontWeight: 500,
},
},
},
},
});


export default theme;