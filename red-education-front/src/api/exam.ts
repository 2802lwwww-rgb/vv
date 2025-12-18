import request from '@/utils/request'

// 获取考试列表
export function getExamList(params: any) {
    return request({
        url: '/exam/list',
        method: 'get',
        params
    })
}

// 获取考试详情
export function getExamDetail(id: number) {
    return request({
        url: `/exam/${id}`,
        method: 'get'
    })
}


export function submitExam(data: {
    examId: number
    answers: any
    duration?: number
}) {
    return request({
        url: '/exam/submit',
        method: 'post',
        data
    })
}

// 获取考试记录（我的成绩）
export function getExamRecords(params: any) {
    return request({
        url: '/exam/score/my',
        method: 'get',
        params
    })
}

// 获取考试结果
export function getExamResult(recordId: number) {
    return request({
        url: `/exam/score/${recordId}`,
        method: 'get'
    })
}

// 获取错题本
export function getWrongQuestions(params: any) {
    return request({
        url: '/exam/wrong/list',
        method: 'get',
        params
    })
}

// 标记错题为已掌握
export function markWrongQuestionMastered(id: number) {
    return request({
        url: `/exam/wrong/${id}/master`,
        method: 'put'
    })
}

// 删除错题记录
export function deleteWrongQuestion(id: number) {
    return request({
        url: `/exam/wrong/${id}`,
        method: 'delete'
    })
}

// 重做错题
export function redoWrongQuestion(data: { questionId: number; answer: string }) {
    return request({
        url: '/exam/wrong/redo',
        method: 'post',
        data
    })
}

// 获取试卷排行榜
export function getExamLeaderboard(examId: number, topN: number = 10) {
    return request({
        url: `/exam/${examId}/leaderboard`,
        method: 'get',
        params: { topN }
    })
}
