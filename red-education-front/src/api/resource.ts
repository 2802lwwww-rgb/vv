import request from '@/utils/request'

// 获取资源列表
export function getResourceList(params: any) {
    return request({
        url: '/resource/list',
        method: 'get',
        params
    })
}

// 获取资源详情
export function getResourceDetail(id: number) {
    return request({
        url: `/resource/${id}`,
        method: 'get'
    })
}

// 获取资源分类
export function getResourceCategories() {
    return request({
        url: '/resource/categories',
        method: 'get'
    })
}

// 上传资源
export function uploadResource(data: any) {
    return request({
        url: '/resource',
        method: 'post',
        data
    })
}

// 获取我的资源
export function getMyResources(params: any) {
    return request({
        url: '/resource/my',
        method: 'get',
        params
    })
}

// 下载资源
export function downloadResource(id: number) {
    return request({
        url: `/resource/${id}/download`,
        method: 'post'
    })
}
