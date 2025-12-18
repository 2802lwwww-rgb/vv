import axios from 'axios'
import type { AxiosInstance, AxiosResponse, AxiosError } from 'axios'
import { ElMessage } from 'element-plus'

// 创建axios实例
const service: AxiosInstance = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,
    timeout: 15000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
    (config) => {
        // 从localStorage获取token
        const token = localStorage.getItem('token')
        if (token) {
            config.headers['Authorization'] = `Bearer ${token}`
        }
        return config
    },
    (error: AxiosError) => {
        console.error('Request error:', error)
        return Promise.reject(error)
    }
)

// 响应拦截器
service.interceptors.response.use(
    (response: AxiosResponse) => {
        // 如果是 blob 类型，直接返回
        if (response.config.responseType === 'blob' || response.data instanceof Blob) {
            return response.data
        }

        const res = response.data
        console.log('API Response:', response.config.url, res)

        // 如果返回的状态码不是200，则认为是错误
        if (res.code !== 200) {
            console.error('API Error Code:', res.code, res.message)
            ElMessage.error(res.message || '请求失败')

            // 401: 未登录或token过期
            if (res.code === 401) {
                console.warn('Handling 401 from response body')
                localStorage.removeItem('token')
                window.location.href = '/login'
            }

            return Promise.reject(new Error(res.message || 'Error'))
        } else {
            return res
        }
    },
    (error: AxiosError) => {
        console.error('Response error:', error)
        if (error.response) {
            console.error('Error Status:', error.response.status)
            console.error('Error Data:', error.response.data)

            switch (error.response.status) {
                case 401:
                    // 检查是否有 token，有 token 说明是登录过期，无 token 则是未登录
                    const hasToken = !!localStorage.getItem('token')
                    if (hasToken) {
                        ElMessage.error('登录已过期，请重新登录')
                    }
                    localStorage.removeItem('token')
                    // 如果不在登录页，则跳转到登录页
                    if (!window.location.pathname.includes('/login')) {
                        window.location.href = '/login'
                    }
                    break
                case 403:
                    ElMessage.error('没有权限访问该资源')
                    break
                case 404:
                    ElMessage.error('请求的资源不存在')
                    break
                case 500:
                    ElMessage.error('服务器错误')
                    break
                default:
                    ElMessage.error(error.message || '网络请求失败')
            }
        } else {
            ElMessage.error('网络连接失败，请检查网络')
        }

        return Promise.reject(error)
    }
)

export default service
