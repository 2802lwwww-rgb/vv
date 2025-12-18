/**
 * Token 管理工具
 */

const TOKEN_KEY = 'token'
const REFRESH_TOKEN_KEY = 'refresh_token'

/**
 * 获取Token
 */
export function getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY)
}

/**
 * 设置Token
 */
export function setToken(token: string): void {
    localStorage.setItem(TOKEN_KEY, token)
}

/**
 * 移除Token
 */
export function removeToken(): void {
    localStorage.removeItem(TOKEN_KEY)
}

/**
 * 获取刷新Token
 */
export function getRefreshToken(): string | null {
    return localStorage.getItem(REFRESH_TOKEN_KEY)
}

/**
 * 设置刷新Token
 */
export function setRefreshToken(token: string): void {
    localStorage.setItem(REFRESH_TOKEN_KEY, token)
}

/**
 * 移除刷新Token
 */
export function removeRefreshToken(): void {
    localStorage.removeItem(REFRESH_TOKEN_KEY)
}

/**
 * 清除所有Token
 */
export function clearTokens(): void {
    removeToken()
    removeRefreshToken()
}

/**
 * 检查Token是否存在
 */
export function hasToken(): boolean {
    return !!getToken()
}
