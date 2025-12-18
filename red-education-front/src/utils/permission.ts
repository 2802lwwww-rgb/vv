/**
 * 权限检查工具
 */

import { useUserStore } from '@/stores/user'

/**
 * 检查是否有指定角色
 */
export function hasRole(role: string | string[]): boolean {
    const userStore = useUserStore()
    if (!userStore.userInfo) return false

    if (Array.isArray(role)) {
        return role.includes(userStore.userInfo.role)
    }

    return userStore.userInfo.role === role
}

/**
 * 检查是否是管理员
 */
export function isAdmin(): boolean {
    return hasRole('SYS_ADMIN')
}

/**
 * 检查是否是内容管理员
 */
export function isContentAdmin(): boolean {
    return hasRole(['SYS_ADMIN', 'CONTENT_ADMIN'])
}

/**
 * 检查是否登录
 */
export function isLoggedIn(): boolean {
    const userStore = useUserStore()
    return userStore.isLoggedIn
}
