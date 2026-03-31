import axios from 'axios'

const baseURL = import.meta.env.VITE_API_BASE_URL || '/api'

const http = axios.create({
  baseURL,
  timeout: 30000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

export default http
