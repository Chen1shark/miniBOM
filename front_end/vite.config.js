// vite.config.js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteMockServe } from 'vite-plugin-mock'
import path from 'path'

export default defineConfig(({ command }) => {
  return {
    plugins: [
      vue(),
      viteMockServe({
        mockPath: 'src/mock',
        localEnabled: command === 'serve', // 👈 关键是这里用了 command
      }),
    ],
    resolve: {
      alias: {
        '@': path.resolve(__dirname, 'src'), // 👈 添加别名
      },
    },
  }
})
