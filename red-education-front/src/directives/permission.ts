/**
 * 权限指令
 * 用法: v-permission="['SYS_ADMIN']" 或 v-permission="'SYS_ADMIN'"
 */

import type { Directive, DirectiveBinding } from 'vue'
import { useUserStore } from '@/stores/user'

export const permission: Directive = {
    mounted(el: HTMLElement, binding: DirectiveBinding) {
        const { value } = binding
        const userStore = useUserStore()

        if (value) {
            const requiredRoles = Array.isArray(value) ? value : [value]
            const hasPermission = userStore.userInfo && requiredRoles.includes(userStore.userInfo.role)

            if (!hasPermission) {
                // 移除元素
                el.parentNode?.removeChild(el)
            }
        } else {
            throw new Error('需要指定权限角色！例如: v-permission="[\'SYS_ADMIN\']"')
        }
    }
}

export default permission
