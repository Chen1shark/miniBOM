// src/utils/request.js
import axios from 'axios'

const isDev = import.meta.env.MODE === 'development'

const instance = axios.create({
  baseURL: isDev ? '/' : 'https://your-api.com', // 后端 API 地址
  timeout: 5000,
})

// 请求拦截器：附带 token
instance.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

export default instance
