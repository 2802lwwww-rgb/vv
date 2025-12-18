import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface UserInfo {
    id: number
    username: string
    nickname: string
    email: string
    phone: string
    avatar: string
    role: string
    points: number
    status: number
    createTime?: string
}

export const useUserStore = defineStore('user', () => {
    const token = ref<string>(localStorage.getItem('token') || '')
    const userInfo = ref<UserInfo | null>(null)

    const isLoggedIn = computed(() => !!token.value)
    const isAdmin = computed(() => userInfo.value?.role === 'SYS_ADMIN')
    const isContentAdmin = computed(() =>
        userInfo.value?.role === 'CONTENT_ADMIN' || userInfo.value?.role === 'SYS_ADMIN'
    )

    // 初始化 - 从localStorage恢复用户信息
    function init() {
        const savedUserInfo = localStorage.getItem('userInfo')
        if (savedUserInfo) {
            try {
                userInfo.value = JSON.parse(savedUserInfo)
            } catch (e) {
                console.error('Failed to parse user info from localStorage', e)
            }
        }
    }

    // 登录 - 设置Token
    function setToken(newToken: string) {
        token.value = newToken
        localStorage.setItem('token', newToken)
    }

    // 设置用户信息
    function setUserInfo(info: UserInfo) {
        userInfo.value = info
        localStorage.setItem('userInfo', JSON.stringify(info))
        localStorage.setItem('userRole', info.role)
    }

    // 更新用户信息（部分更新）
    function updateUserInfo(updates: Partial<UserInfo>) {
        if (userInfo.value) {
            userInfo.value = { ...userInfo.value, ...updates }
            localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        }
    }

    // 更新积分
    function updatePoints(points: number) {
        if (userInfo.value) {
            userInfo.value.points = points
            localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        }
    }

    // 登出
    function logout() {
        token.value = ''
        userInfo.value = null
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('userRole')
    }

    // 检查权限
    function hasRole(role: string | string[]): boolean {
        if (!userInfo.value) return false
        if (Array.isArray(role)) {
            return role.includes(userInfo.value.role)
        }
        return userInfo.value.role === role
    }

    // 初始化store
    init()

    return {
        token,
        userInfo,
        isLoggedIn,
        isAdmin,
        isContentAdmin,
        setToken,
        setUserInfo,
        updateUserInfo,
        updatePoints,
        logout,
        hasRole
    }
})
