import axios from 'axios'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

request.interceptors.request.use(config => {
  if (!config.url.startsWith('/auth/')) {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
  }
  return config
}, error => Promise.reject(error))

request.interceptors.response.use(
  response => {
    const body = response.data
    if (body && body.code !== undefined && body.code !== 200) {
      return Promise.reject(new Error(body.message || '请求失败'))
    }
    return body && body.data !== undefined ? body.data : body
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      router.push('/login')
    }
    const msg = error.response?.data?.message || error.message || '网络错误'
    return Promise.reject(new Error(msg))
  }
)

export default request
