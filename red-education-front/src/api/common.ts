import request from '@/utils/request'

// 获取公共配置
export function getPublicConfig() {
    return request({
        url: '/public/config',
        method: 'get'
    })
}

// 获取统计数据
export function getStatistics() {
    return request({
        url: '/statistics/overview',
        method: 'get'
    })
}
