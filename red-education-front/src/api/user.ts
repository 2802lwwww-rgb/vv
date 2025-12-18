import request from '@/utils/request'

export interface LoginData {
    usernameOrEmail: string
    password: string
}

export interface RegisterData {
    username: string
    password: string
    email: string
    phone?: string
    confirmPassword?: string
}

// 用户登录
export function login(data: LoginData) {
    return request({
        url: '/user/login',
        method: 'post',
        data
    })
}

// 用户注册
export function register(data: RegisterData) {
    return request({
        url: '/user/register',
        method: 'post',
        data
    })
}

// 获取用户信息
export function getUserInfo() {
    return request({
        url: '/user/info',
        method: 'get'
    })
}

// 更新用户信息
export function updateUserInfo(data: any) {
    return request({
        url: '/user/info',
        method: 'put',
        data
    })
}

// 修改密码
export function updatePassword(data: { oldPassword: string; newPassword: string }) {
    return request({
        url: '/user/password',
        method: 'put',
        data
    })
}

// 发送重置密码验证码
export function sendResetCode(data: { email: string }) {
    return request({
        url: '/user/reset-code',
        method: 'post',
        data
    })
}

// 重置密码
export function resetPassword(data: { email: string; code: string; newPassword: string }) {
    return request({
        url: '/user/reset-password',
        method: 'post',
        data
    })
}
