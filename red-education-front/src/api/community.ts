import request from '@/utils/request'

// 获取帖子列表
export function getPostList(params: any) {
    return request({
        url: '/community/posts',
        method: 'get',
        params
    })
}

// 获取我的帖子（包含所有审核状态）
export function getMyPosts(params: any) {
    return request({
        url: '/community/posts/my',
        method: 'get',
        params
    })
}

// 获取帖子详情
export function getPostDetail(id: number) {
    return request({
        url: `/community/posts/${id}`,
        method: 'get'
    })
}

// 发布帖子
export function createPost(data: {
    title: string
    content: string
    topic: string
    images?: string  // 多张图片用逗号分隔
}) {
    return request({
        url: '/community/posts',
        method: 'post',
        data
    })
}

// 删除帖子
export function deletePost(id: number) {
    return request({
        url: `/community/posts/${id}`,
        method: 'delete'
    })
}

// 点赞帖子
export function likePost(postId: number) {
    return request({
        url: `/community/posts/${postId}/like`,
        method: 'post'
    })
}

// 取消点赞
export function unlikePost(postId: number) {
    return request({
        url: `/community/posts/${postId}/unlike`,
        method: 'post'
    })
}

// 获取评论列表
export function getComments(postId: number, params: any) {
    return request({
        url: `/community/posts/${postId}/comments`,
        method: 'get',
        params
    })
}

// 发表评论
export function createComment(data: {
    postId: number
    content: string
    parentId?: number
}) {
    return request({
        url: '/community/comments',
        method: 'post',
        data
    })
}

// 删除评论
export function deleteComment(id: number) {
    return request({
        url: `/community/comments/${id}`,
        method: 'delete'
    })
}

// 获取热门话题
export function getHotTopics(topN: number = 10) {
    return request({
        url: '/community/posts/hot-topics',
        method: 'get',
        params: { topN }
    })
}
