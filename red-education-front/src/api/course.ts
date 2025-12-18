import request from '@/utils/request'

// 获取课程列表
export function getCourseList(params: any) {
    return request({
        url: '/course/list',
        method: 'get',
        params
    })
}

// 获取课程详情
export function getCourseDetail(id: number) {
    return request({
        url: `/course/${id}`,
        method: 'get'
    })
}



// 保存学习进度
export function saveStudyProgress(data: {
    courseId: number
    chapterId: number
    progress: number
    duration: number
    lastPosition?: number
}) {
    return request({
        url: '/study/progress',
        method: 'post',
        data
    })
}

// 完成章节
export function completeChapter(courseId: number, chapterId: number) {
    return request({
        url: '/study/complete',
        method: 'post',
        data: { courseId, chapterId }
    })
}

// 获取学习记录
export function getStudyRecords(params: any) {
    return request({
        url: '/study/history',
        method: 'get',
        params
    })
}

// 获取学习统计
export function getStudyStats() {
    return request({
        url: '/study/stats',
        method: 'get'
    })
}

// 获取课程学习记录
export function getCourseStudy(courseId: number) {
    return request({
        url: `/study/record/${courseId}`,
        method: 'get'
    })
}

// 获取学习报告
export function getStudyReport() {
    return request({
        url: '/study/report',
        method: 'get'
    })
}
