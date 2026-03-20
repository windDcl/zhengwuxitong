import axios from 'axios'

const http = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 8000
})

http.interceptors.request.use((config) => {
  const token = localStorage.getItem('admin_token')
  if (token) {
    config.headers.Authorization = token
  }
  return config
})

export default http
