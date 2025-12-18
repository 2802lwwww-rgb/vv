import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes: Array<RouteRecordRaw> = [
    {
        path: '/',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/auth/Login.vue'),
        meta: { title: '登录' }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('@/views/auth/Register.vue'),
        meta: { title: '注册' }
    },
    {
        path: '/forgot-password',
        name: 'ForgotPassword',
        component: () => import('@/views/auth/ForgotPassword.vue'),
        meta: { title: '忘记密码' }
    },
    {
        path: '/resources',
        name: 'Resources',
        component: () => import('@/views/resources/List.vue'),
        meta: { title: '红色资源' }
    },
    {
        path: '/resources/:id',
        name: 'ResourceDetail',
        component: () => import('@/views/resources/Detail.vue'),
        meta: { title: '资源详情' }
    },
    {
        path: '/resources/upload',
        name: 'ResourceUpload',
        component: () => import('@/views/resources/Upload.vue'),
        meta: { title: '上传资源', requiresAuth: true }
    },
    {
        path: '/courses',
        name: 'Courses',
        component: () => import('@/views/courses/List.vue'),
        meta: { title: '在线课程' }
    },
    {
        path: '/courses/:id',
        name: 'CourseDetail',
        component: () => import('@/views/courses/Detail.vue'),
        meta: { title: '课程详情', requiresAuth: true }
    },
    {
        path: '/courses/:id/study/:chapterId',
        name: 'CourseStudy',
        component: () => import('@/views/courses/Study.vue'),
        meta: { title: '在线学习', requiresAuth: true }
    },
    {
        path: '/exam',
        name: 'ExamList',
        component: () => import('@/views/exam/List.vue'),
        meta: { title: '在线考试', requiresAuth: true }
    },
    {
        path: '/exam/:id',
        name: 'ExamDetail',
        component: () => import('@/views/exam/Detail.vue'),
        meta: { title: '考试详情', requiresAuth: true }
    },
    {
        path: '/exam/:id/answer',
        name: 'ExamAnswer',
        component: () => import('@/views/exam/Answer.vue'),
        meta: { title: '在线答题', requiresAuth: true }
    },
    {
        path: '/exam/result/:id',
        name: 'ExamResult',
        component: () => import('@/views/exam/Result.vue'),
        meta: { title: '考试结果', requiresAuth: true }
    },
    {
        path: '/exam/wrong-questions',
        name: 'WrongQuestions',
        component: () => import('@/views/exam/WrongQuestions.vue'),
        meta: { title: '错题本', requiresAuth: true }
    },
    {
        path: '/community',
        name: 'Community',
        component: () => import('@/views/community/List.vue'),
        meta: { title: '互动社区' }
    },
    {
        path: '/community/:id',
        name: 'CommunityDetail',
        component: () => import('@/views/community/Detail.vue'),
        meta: { title: '帖子详情' }
    },
    {
        path: '/community/create',
        name: 'CommunityCreate',
        component: () => import('@/views/community/Create.vue'),
        meta: { title: '发布帖子', requiresAuth: true }
    },
    {
        path: '/points',
        name: 'PointsMall',
        component: () => import('@/views/points/List.vue'),
        meta: { title: '积分商城' }
    },
    {
        path: '/points/ranking',
        name: 'PointsRanking',
        component: () => import('@/views/points/Ranking.vue'),
        meta: { title: '积分排行榜' }
    },
    {
        path: '/points/:id',
        name: 'PointsDetail',
        component: () => import('@/views/points/Detail.vue'),
        meta: { title: '商品详情' }
    },
    {
        path: '/user',
        name: 'UserCenter',
        redirect: '/user/profile',
        meta: { requiresAuth: true },
        children: [
            {
                path: 'profile',
                name: 'UserProfile',
                component: () => import('@/views/user/Profile.vue'),
                meta: { title: '个人信息', requiresAuth: true }
            },
            {
                path: 'study',
                name: 'UserStudy',
                component: () => import('@/views/user/Study.vue'),
                meta: { title: '学习记录', requiresAuth: true }
            },
            {
                path: 'exams',
                name: 'UserExams',
                component: () => import('@/views/user/Exams.vue'),
                meta: { title: '我的成绩', requiresAuth: true }
            },
            {
                path: 'points',
                name: 'UserPoints',
                component: () => import('@/views/user/Points.vue'),
                meta: { title: '我的积分', requiresAuth: true }
            },
            {
                path: 'exchange-records',
                name: 'UserExchangeRecords',
                component: () => import('@/views/user/ExchangeRecords.vue'),
                meta: { title: '兑换记录', requiresAuth: true }
            },
            {
                path: 'my-posts',
                name: 'UserMyPosts',
                component: () => import('@/views/user/MyPosts.vue'),
                meta: { title: '我的帖子', requiresAuth: true }
            },
            {
                path: 'my-resources',
                name: 'UserMyResources',
                component: () => import('@/views/user/MyResources.vue'),
                meta: { title: '我的资源', requiresAuth: true }
            }
        ]
    },
    {
        path: '/admin',
        name: 'Admin',
        redirect: '/admin/dashboard',
        meta: { requiresAdmin: true },
        children: [
            {
                path: 'dashboard',
                name: 'AdminDashboard',
                component: () => import('@/views/admin/Dashboard.vue'),
                meta: { title: '数据概览', requiresAdmin: true }
            },
            {
                path: 'users',
                name: 'AdminUsers',
                component: () => import('@/views/admin/Users.vue'),
                meta: { title: '用户管理', requiresAdmin: true, requiresSysAdmin: true }
            },
            {
                path: 'resources',
                name: 'AdminResources',
                component: () => import('@/views/admin/resources/List.vue'),
                meta: { title: '资源管理', requiresAdmin: true }
            },
            {
                path: 'resources/upload',
                name: 'AdminResourcesUpload',
                component: () => import('@/views/admin/resources/Upload.vue'),
                meta: { title: '上传资源', requiresAdmin: true }
            },
            {
                path: 'resources/:id',
                name: 'AdminResourceDetail',
                component: () => import('@/views/admin/resources/Detail.vue'),
                meta: { title: '资源详情', requiresAdmin: true }
            },
            {
                path: 'resources/audit',
                name: 'AdminResourcesAudit',
                component: () => import('@/views/admin/resources/Audit.vue'),
                meta: { title: '资源审核', requiresAdmin: true }
            },
            {
                path: 'courses',
                name: 'AdminCourses',
                component: () => import('@/views/admin/Courses.vue'),
                meta: { title: '课程管理', requiresAdmin: true }
            },
            {
                path: 'questions',
                name: 'AdminQuestions',
                component: () => import('@/views/admin/Questions.vue'),
                meta: { title: '题库管理', requiresAdmin: true, requiresSysAdmin: true }
            },
            {
                path: 'exams',
                name: 'AdminExams',
                component: () => import('@/views/admin/Exams.vue'),
                meta: { title: '试卷管理', requiresAdmin: true, requiresSysAdmin: true }
            },
            {
                path: 'posts/audit',
                name: 'AdminPostsAudit',
                component: () => import('@/views/admin/posts/Audit.vue'),
                meta: { title: '内容审核', requiresAdmin: true }
            },
            {
                path: 'posts/detail/:id',
                name: 'AdminPostDetail',
                component: () => import('@/views/admin/posts/Detail.vue'),
                meta: { title: '帖子详情', requiresAdmin: true }
            },
            {
                path: 'products',
                name: 'AdminProducts',
                component: () => import('@/views/admin/Products.vue'),
                meta: { title: '商品管理', requiresAdmin: true }
            },
            {
                path: 'exchange',
                name: 'AdminExchange',
                component: () => import('@/views/admin/Exchange.vue'),
                meta: { title: '兑换管理', requiresAdmin: true }
            },
            {
                path: 'config',
                name: 'AdminConfig',
                component: () => import('@/views/admin/Config.vue'),
                meta: { title: '系统配置', requiresAdmin: true, requiresSysAdmin: true }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFound.vue'),
        meta: { title: '404' }
    }
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes
})

// 路由守卫
router.beforeEach((to, _from, next) => {
    // 设置页面标题
    document.title = `${to.meta.title || ''} - ${import.meta.env.VITE_APP_TITLE}`

    // 检查是否需要登录
    const token = localStorage.getItem('token')

    if (to.meta.requiresAuth && !token) {
        next('/login')
        return
    }

    // 检查是否需要管理员权限（SYS_ADMIN 或 CONTENT_ADMIN）
    if (to.meta.requiresAdmin) {
        const userRole = localStorage.getItem('userRole')
        if (userRole !== 'SYS_ADMIN' && userRole !== 'CONTENT_ADMIN') {
            ElMessage.error('权限不足，需要管理员权限')
            next('/')
            return
        }
    }

    // 检查是否需要系统管理员权限（仅 SYS_ADMIN）
    if (to.meta.requiresSysAdmin) {
        const userRole = localStorage.getItem('userRole')
        if (userRole !== 'SYS_ADMIN') {
            ElMessage.error('权限不足，需要系统管理员权限')
            next('/admin/dashboard')
            return
        }
    }

    next()
})

export default router
