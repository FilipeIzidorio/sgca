/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{ts,tsx}"],
  theme: {
    extend: {
      colors: {
        primary: {
          500: "#2563eb",
          600: "#1e40af"
        }
      }
    }
  },
  plugins: []
}
