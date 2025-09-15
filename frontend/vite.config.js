// vite.config.js
// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    open: true,
    historyApiFallback: true, // 👈 важливо для React Router
  },
  css: {
    preprocessorOptions: {
      scss: {
        includePaths: ['src/styles'],
      },
    },
  },
});