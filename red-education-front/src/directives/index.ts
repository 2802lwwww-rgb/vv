/**
 * 全局指令注册
 */

import type { App } from 'vue'
import permission from './permission'

export function setupDirectives(app: App) {
    // 注册权限指令
    app.directive('permission', permission)
}

export default setupDirectives
