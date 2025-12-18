import request from '@/utils/request'

// 获取用户列表
export function getUserList(params: any) {
    return request({
        url: '/admin/user/list',
        method: 'get',
        params
    })
}

// 更新用户角色
export function updateUserRole(userId: number, role: string) {
    return request({
        url: '/admin/user/role',
        method: 'put',
        data: { userId, role }
    })
}

// 更新用户状态
export function updateUserStatus(id: number, status: number) {
    return request({
        url: `/admin/user/${id}/status/${status}`,
        method: 'put'
    })
}

// 重置用户密码
export function resetUserPassword(id: number) {
    return request({
        url: `/admin/user/${id}/password`,
        method: 'put',
        params: { newPassword: '123456' }
    })
}

// 获取资源列表
export function getAdminResourceList(params: any) {
    return request({
        url: '/admin/resource/list',
        method: 'get',
        params
    })
}

// 创建资源
export function createResource(data: any) {
    return request({
        url: '/admin/resources',
        method: 'post',
        data
    })
}

// 管理员上传资源（自动通过）
export function adminUploadResource(data: any) {
    return request({
        url: '/admin/resource/upload',
        method: 'post',
        data
    })
}

// 获取资源详情（管理员）
export function getAdminResourceDetail(id: number) {
    return request({
        url: `/admin/resource/${id}`,
        method: 'get'
    })
}

// 更新资源
export function updateResource(id: number, data: any) {
    return request({
        url: `/admin/resource/${id}`,
        method: 'put',
        data
    })
}

// 删除资源
export function deleteResource(id: number) {
    return request({
        url: `/admin/resource/${id}`,
        method: 'delete'
    })
}

// 审核资源
export function auditResource(id: number, status: string, reason?: string) {
    return request({
        url: `/admin/resource/audit/${id}`,
        method: 'post',
        data: { status, reason }
    })
}

// 获取课程列表
export function getAdminCourseList(params: any) {
    return request({
        url: '/admin/courses',
        method: 'get',
        params
    })
}

// 创建课程
export function createCourse(data: any) {
    return request({
        url: '/admin/courses',
        method: 'post',
        data
    })
}

// 更新课程
export function updateCourse(id: number, data: any) {
    return request({
        url: `/admin/courses/${id}`,
        method: 'put',
        data
    })
}

// 删除课程
export function deleteCourse(id: number) {
    return request({
        url: `/admin/courses/${id}`,
        method: 'delete'
    })
}

// 获取试题列表
export function getQuestionList(params: any) {
    return request({
        url: '/admin/questions',
        method: 'get',
        params
    })
}

// 创建试题
export function createQuestion(data: any) {
    return request({
        url: '/admin/questions',
        method: 'post',
        data
    })
}

// 更新试题
export function updateQuestion(id: number, data: any) {
    return request({
        url: `/admin/questions/${id}`,
        method: 'put',
        data
    })
}

// 删除试题
export function deleteQuestion(id: number) {
    return request({
        url: `/admin/questions/${id}`,
        method: 'delete'
    })
}

// 获取试卷列表
export function getAdminExamList(params: any) {
    return request({
        url: '/admin/exams',
        method: 'get',
        params
    })
}

// 创建试卷
export function createExam(data: any) {
    return request({
        url: '/admin/exams',
        method: 'post',
        data
    })
}

// 更新试卷
export function updateExam(id: number, data: any) {
    return request({
        url: `/admin/exams/${id}`,
        method: 'put',
        data
    })
}

// 删除试卷
export function deleteExam(id: number) {
    return request({
        url: `/admin/exams/${id}`,
        method: 'delete'
    })
}

// 更新试卷状态
export function updateExamStatus(id: number, status: number) {
    return request({
        url: `/admin/exams/${id}/status`,
        method: 'put',
        params: { status }
    })
}

// 获取试卷题目列表
export function getExamQuestions(id: number) {
    return request({
        url: `/admin/exams/${id}/questions`,
        method: 'get'
    })
}

// 设置试卷题目
export function setExamQuestions(id: number, questionIds: number[]) {
    return request({
        url: `/admin/exams/${id}/questions`,
        method: 'put',
        data: questionIds
    })
}

// 获取待审核帖子列表
export function getPendingPosts(params: any) {
    return request({
        url: '/admin/post/pending',
        method: 'get',
        params
    })
}

// 审核帖子
export function auditPost(id: number, status: string, reason?: string) {
    // 后端 status: 1-通过, 2-驳回
    const statusMap: Record<string, number> = {
        'APPROVED': 1,
        'REJECTED': 2
    }
    return request({
        url: `/admin/post/audit/${id}`,
        method: 'post',
        data: {
            status: statusMap[status] || 1,
            auditComment: reason || ''
        }
    })
}

// 获取商品列表
export function getAdminGoodsList(params: any) {
    return request({
        url: '/admin/goods/list',
        method: 'get',
        params
    })
}

// 创建商品
export function createGoods(data: any) {
    return request({
        url: '/admin/goods',
        method: 'post',
        data
    })
}

// 更新商品
export function updateGoods(data: any) {
    return request({
        url: '/admin/goods',
        method: 'put',
        data
    })
}

// 删除商品
export function deleteGoods(id: number) {
    return request({
        url: `/admin/goods/${id}`,
        method: 'delete'
    })
}

// 获取系统配置
export function getSystemConfig() {
    return request({
        url: '/admin/config/list',
        method: 'get'
    })
}

// 批量更新系统配置
export function updateSystemConfig(data: any) {
    return request({
        url: '/admin/config/batch',
        method: 'post',
        data
    })
}

// 获取仪表盘统计数据
export function getDashboardStats() {
    return request({
        url: '/admin/statistics/dashboard',
        method: 'get'
    })
}

// 获取所有兑换记录（管理员）
export function getExchangeRecords(params: any) {
    return request({
        url: '/exchange/admin/list',
        method: 'get',
        params
    })
}

// 核销取件码
export function verifyPickupCode(pickupCode: string) {
    return request({
        url: `/exchange/verify/${pickupCode}`,
        method: 'post'
    })
}

// 更新兑换状态
export function updateExchangeStatus(id: number, status: number) {
    return request({
        url: `/exchange/${id}/status/${status}`,
        method: 'put'
    })
}
