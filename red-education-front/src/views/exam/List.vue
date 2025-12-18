<template>
  <MainLayout>
    <div class="exam-page">
      <!-- 页面头部 - 游戏化设计 -->
      <div class="page-header gamified">
        <div class="header-bg">
          <div class="bg-gradient"></div>
          <div class="stars-bg">
            <div v-for="i in 30" :key="i" class="star" :style="getStarStyle(i)"></div>
          </div>
        </div>
        <div class="header-content">
          <div class="header-badge">
            <span>🎮</span>
            <span>闯关挑战</span>
          </div>
          <h1 class="page-title">
            <span class="text-gradient">红色学习之旅</span>
          </h1>
          <p class="page-desc">完成每个关卡，解锁新的学习挑战</p>
          
          <!-- 视图切换 -->
          <div class="view-toggle">
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'map' }"
              @click="viewMode = 'map'"
            >
              <span>🗺️</span> 闯关地图
            </button>
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'list' }"
              @click="viewMode = 'list'"
            >
              <span>📋</span> 列表模式
            </button>
          </div>
        </div>
      </div>

      <div class="page-container">
        <!-- 闯关地图模式 -->
        <div v-if="viewMode === 'map'" class="map-view">
          <ExamMap :exams="exams" @select="goToDetail" />
          <div class="map-actions">
            <el-button class="wrong-btn" size="large" @click="$router.push('/exam/wrong-questions')">
              <el-icon><Warning /></el-icon>
              我的错题本
            </el-button>
          </div>
        </div>

        <!-- 列表模式 -->
        <div v-else class="list-view">
          <!-- 筛选区域 -->
          <div class="filter-section glass-filter">
            <el-input
              v-model="filters.keyword"
              placeholder="搜索考试..."
              size="large"
              class="search-input"
              clearable
              @keyup.enter="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-button class="wrong-btn-inline" @click="$router.push('/exam/wrong-questions')">
              <el-icon><Warning /></el-icon>
              错题本
            </el-button>
          </div>

          <!-- 考试列表 -->
          <div class="exams-grid" v-loading="loading">
            <div 
              v-for="(exam, index) in exams" 
              :key="exam.id" 
              class="exam-card-wrapper"
            >
              <div 
                class="exam-card animate-fade-in-up"
                :style="{ animationDelay: `${index * 60}ms` }"
                @click="goToDetail(exam.id)"
                @mouseenter="onCardHover($event)"
                @mouseleave="onCardLeave($event)"
                @mousemove="onCardMove($event)"
              >

                <div class="exam-header">
                  <div class="exam-icon">
                    <el-icon :size="28"><EditPen /></el-icon>
                    <div class="icon-ring"></div>
                  </div>
                  <el-tag 
                    :type="getStatusType(exam.status)" 
                    effect="dark"
                    size="small"
                    class="status-tag"
                  >
                    <span class="status-dot"></span>
                    {{ getStatusText(exam.status) }}
                  </el-tag>
                </div>

                <h3 class="exam-title">{{ exam.name }}</h3>

                <div class="exam-info">
                  <div class="info-item">
                    <el-icon><Document /></el-icon>
                    <span class="info-value">{{ exam.questionCount }}</span>
                    <span class="info-label">题</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Clock /></el-icon>
                    <span class="info-value">{{ exam.duration }}</span>
                    <span class="info-label">分钟</span>
                  </div>
                  <div class="info-item">
                    <el-icon><Trophy /></el-icon>
                    <span class="info-value">{{ exam.passScore }}</span>
                    <span class="info-label">及格分</span>
                  </div>
                </div>

                <div class="exam-footer">
                  <span class="exam-date">
                    <el-icon><Calendar /></el-icon>
                    {{ formatDate(exam.createTime) }}
                  </span>
                  <el-button 
                    :type="exam.hasJoined ? 'success' : 'primary'" 
                    size="small"
                    class="action-btn"
                    @click.stop="goToDetail(exam.id)"
                  >
                    <el-icon v-if="!exam.hasJoined"><CaretRight /></el-icon>
                    {{ exam.hasJoined ? '查看成绩' : '参加考试' }}
                  </el-button>
                </div>
                <div class="card-shine"></div>
              </div>
            </div>
          </div>

          <el-empty v-if="exams.length === 0 && !loading" description="暂无考试" />

          <!-- 分页 -->
          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :current-page="filters.page"
              :page-size="filters.pageSize"
              :total="total"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, Document, Clock, Trophy, Calendar, Warning, EditPen, CaretRight } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import ExamMap from '@/components/creative/ExamMap.vue'
import { getExamList } from '@/api/exam'
import dayjs from 'dayjs'

interface Exam {
  id: number
  name: string
  status: number
  questionCount: number
  duration: number
  passScore: number
  createTime: string
  hasJoined: boolean
}

const router = useRouter()
const loading = ref(false)
const exams = ref<Exam[]>([])
const total = ref(0)
const viewMode = ref<'map' | 'list'>('map')

const filters = reactive({
  page: 1,
  pageSize: 12,
  keyword: ''
})

// 星星样式
const getStarStyle = (index: number) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${1 + Math.random() * 3}px`,
    height: `${1 + Math.random() * 3}px`,
    animationDelay: `${index * 0.1}s`,
    animationDuration: `${2 + Math.random() * 3}s`
  }
}

// 3D 卡片交互
const onCardHover = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  card.style.transition = 'transform 0.1s ease-out'
}

const onCardLeave = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  card.style.transform = 'perspective(1000px) rotateX(0) rotateY(0) scale(1)'
  card.style.transition = 'transform 0.5s ease-out'
}

const onCardMove = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  const rect = card.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2
  const rotateX = (y - centerY) / 20
  const rotateY = (centerX - x) / 20
  
  card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.02)`
}

const fetchExams = async () => {
  loading.value = true
  try {
    const params = {
      current: filters.page,
      size: filters.pageSize,
      keyword: filters.keyword || undefined
    }
    const res = await getExamList(params)
    if (res.data) {
      exams.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取考试列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.page = 1
  fetchExams()
}

const handlePageChange = (page: number) => {
  filters.page = page
  fetchExams()
}

const goToDetail = (id: number) => {
  router.push(`/exam/${id}`)
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD')

const getStatusType = (status: number) => {
  const types: Record<number, string> = { 0: 'info', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status: number) => {
  const texts: Record<number, string> = { 0: '未开始', 1: '进行中', 2: '已结束' }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchExams()
})
</script>

<style scoped lang="scss">
.exam-page {
  min-height: calc(100vh - var(--header-height));
  background: linear-gradient(180deg, #0f0f23 0%, #1a1a3a 100%);
}

// ============================================
// 页面头部 - 游戏化设计
// ============================================
.page-header.gamified {
  position: relative;
  padding: 80px 20px;
  text-align: center;
  overflow: hidden;

  .header-bg {
    position: absolute;
    inset: 0;
    z-index: 0;

    .bg-gradient {
      position: absolute;
      inset: 0;
      background: linear-gradient(135deg, #1a0a2e 0%, #16213e 50%, #0f3460 100%);
    }

    .stars-bg {
      position: absolute;
      inset: 0;
      overflow: hidden;
      
      .star {
        position: absolute;
        background: #fff;
        border-radius: 50%;
        animation: twinkle 3s ease-in-out infinite;
      }
    }
  }

  .header-content {
    position: relative;
    z-index: 1;
    max-width: 800px;
    margin: 0 auto;
  }

  .header-badge {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    background: rgba(139, 92, 246, 0.3);
    backdrop-filter: blur(10px);
    padding: 10px 24px;
    border-radius: 50px;
    font-size: 14px;
    color: #fcd34d;
    margin-bottom: 20px;
    border: 1px solid rgba(139, 92, 246, 0.4);
  }

  .page-title {
    font-size: clamp(36px, 6vw, 56px);
    font-weight: 800;
    margin-bottom: 16px;

    .text-gradient {
      background: linear-gradient(135deg, #fcd34d 0%, #f97316 50%, #ec4899 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .page-desc {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 32px;
  }
}

// 视图切换
.view-toggle {
  display: inline-flex;
  gap: 12px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);

  .toggle-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: transparent;
    border: none;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.6);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      color: rgba(255, 255, 255, 0.9);
    }

    &.active {
      background: linear-gradient(135deg, #8b5cf6 0%, #6d28d9 100%);
      color: #fff;
      box-shadow: 0 4px 20px rgba(139, 92, 246, 0.4);
    }

    span {
      font-size: 16px;
    }
  }
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

// 地图视图
.map-view {
  .map-actions {
    display: flex;
    justify-content: center;
    margin-top: 32px;
  }
}

// 列表视图
.list-view {
  // 样式继承
}

// 错题本按钮
.wrong-btn {
  background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%) !important;
  border: none !important;
  color: white !important;
  font-weight: 600;
  padding: 14px 28px;
  border-radius: 50px;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 30px rgba(245, 158, 11, 0.4);
  }
}

.wrong-btn-inline {
  background: rgba(255, 255, 255, 0.1) !important;
  border: 1px solid rgba(255, 255, 255, 0.2) !important;
  color: rgba(255, 255, 255, 0.8) !important;
  border-radius: 14px;
  margin-left: 12px;

  &:hover {
    background: rgba(255, 255, 255, 0.2) !important;
    color: #fff !important;
  }
}

// ============================================
// 筛选区域 - 玻璃效果
// ============================================
.filter-section {
  margin-bottom: 40px;

  &.glass-filter {
    display: inline-block;
    background: rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    border-radius: 20px;
    padding: 16px 24px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
  }

  .search-input {
    width: 350px;

    :deep(.el-input__wrapper) {
      border-radius: 14px;
      box-shadow: none;
      border: 1px solid var(--color-border);
      background: white;

      &:hover, &.is-focus {
        border-color: #8B5CF6;
        box-shadow: 0 0 0 3px rgba(139, 92, 246, 0.1);
      }
    }
  }
}

// ============================================
// 考试网格 - 3D卡片
// ============================================
.exams-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
  margin-bottom: 40px;

  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 576px) {
    grid-template-columns: 1fr;
  }
}

.exam-card-wrapper {
  perspective: 1000px;
}

.exam-card {
  position: relative;
  background: white;
  border-radius: 24px;
  padding: 28px;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  transform-style: preserve-3d;
  will-change: transform;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden;

  &:hover {
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.12);

    .exam-icon .icon-ring {
      transform: scale(1.3);
      opacity: 0.3;
    }

    .card-shine {
      opacity: 1;
      transform: translateX(200%);
    }
  }



  .exam-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 20px;

    .exam-icon {
      position: relative;
      width: 56px;
      height: 56px;
      background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      box-shadow: 0 8px 20px rgba(139, 92, 246, 0.3);

      .icon-ring {
        position: absolute;
        inset: -6px;
        border: 2px solid #8B5CF6;
        border-radius: 20px;
        opacity: 0.2;
        transition: all 0.4s ease;
      }
    }

    .status-tag {
      display: flex;
      align-items: center;
      gap: 6px;
      border-radius: 20px;
      padding: 6px 14px;
      font-weight: 500;

      .status-dot {
        width: 6px;
        height: 6px;
        border-radius: 50%;
        background: currentColor;
        animation: pulse 2s ease-in-out infinite;
      }
    }
  }

  .exam-title {
    font-size: 18px;
    font-weight: 700;
    color: var(--color-text-primary);
    margin-bottom: 20px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .exam-info {
    display: flex;
    gap: 12px;
    margin-bottom: 24px;
    flex-wrap: wrap;

    .info-item {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: var(--color-text-muted);
      padding: 8px 14px;
      background: linear-gradient(135deg, #F5F3FF 0%, #EDE9FE 100%);
      border-radius: 12px;
      transition: all 0.3s ease;

      .el-icon {
        color: #8B5CF6;
      }

      .info-value {
        font-weight: 700;
        color: var(--color-text-primary);
      }

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(139, 92, 246, 0.15);
      }
    }
  }

  .exam-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 20px;
    border-top: 1px solid var(--color-border-light);

    .exam-date {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 13px;
      color: var(--color-text-muted);
    }

    .action-btn {
      font-weight: 600;
      border-radius: 20px;
      padding: 8px 20px;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 20px rgba(0, 0, 0, 0.15);
      }
    }
  }

  .card-shine {
    position: absolute;
    top: 0;
    left: -100%;
    width: 50%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
    opacity: 0;
    transition: opacity 0.3s, transform 0.6s;
    pointer-events: none;
  }
}

// ============================================
// 分页
// ============================================
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}

// ============================================
// 动画
// ============================================
.animate-fade-in-up {
  opacity: 0;
  animation: fadeInUp 0.6s ease forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes breathe {
  0%, 100% {
    transform: translateX(-50%) scale(1);
    opacity: 0.2;
  }
  50% {
    transform: translateX(-50%) scale(1.1);
    opacity: 0.4;
  }
}

@keyframes ringPulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.15;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.05);
    opacity: 0.3;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.3);
  }
}
</style>
