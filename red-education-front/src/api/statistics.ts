import request from '@/utils/request'

// 获取公开统计数据
export function getPublicStatistics() {
    return request({
        url: '/statistics/public',
        method: 'get'
    })
}
