<template>
  <MainLayout>
    <div class="study-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="title-icon">📊</span>
            学习记录
          </h1>
          <p class="page-desc">追踪学习进度，见证成长轨迹</p>
        </div>
      </div>

      <div class="page-container">
        <!-- 核心指标卡片 -->
        <div class="stats-grid" v-loading="reportLoading">
          <div class="stat-card">
            <div class="stat-icon red">
              <el-icon :size="28"><Timer /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ formatDuration(report.totalStudyTime) }}</span>
              <span class="stat-label">总学习时长</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon gold">
              <el-icon :size="28"><Trophy /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ report.totalPoints }}</span>
              <span class="stat-label">获得积分</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon green">
              <el-icon :size="28"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ report.completedCourses }}</span>
              <span class="stat-label">已完成课程</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon purple">
              <el-icon :size="28"><Calendar /></el-icon>
            </div>
            <div class="stat-info">
              <span class="stat-value">{{ report.studyDays }}天</span>
              <span class="stat-label">累计学习天数</span>
            </div>
          </div>
        </div>

        <!-- 图表和概览 -->
        <div class="chart-layout">
          <div class="section-card chart-card">
            <h3 class="card-title">近30天学习趋势</h3>
            <div ref="chartRef" class="chart-container"></div>
          </div>
          <div class="section-card overview-card">
            <h3 class="card-title">学习概览</h3>
            <div class="overview-list">
              <div class="overview-item">
                <span class="label">课程完成率</span>
                <el-progress :percentage="report.courseCompletionRate" :stroke-width="10" :color="'#D64541'" />
              </div>
              <div class="overview-item">
                <span class="label">学习中课程</span>
                <span class="value">{{ report.inProgressCourses }} 门</span>
              </div>
              <div class="overview-item">
                <span class="label">日均学习时长</span>
                <span class="value">{{ report.avgDailyStudyTime }} 分钟</span>
              </div>
              <div class="overview-item">
                <span class="label">学习勤奋度</span>
                <el-rate :model-value="diligenceScore" disabled text-color="#FFD93D" />
              </div>
            </div>
          </div>
        </div>

        <!-- 学习历史 -->
        <div class="section-card records-section">
          <div class="section-header">
            <h3 class="card-title">
              <el-icon><Document /></el-icon>
              学习历史
            </h3>
            <el-input
              v-model="keyword"
              placeholder="搜索课程..."
              size="large"
              class="search-input"
              clearable
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>

          <div class="records-list" v-loading="loading">
            <div
              v-for="record in records"
              :key="record.id"
              class="record-item"
              @click="continueCourse(record.courseId)"
            >
              <div class="record-cover">
                <img :src="getFullUrl(record.courseCover)" :alt="record.courseTitle" />
              </div>
              <div class="record-info">
                <h4 class="record-title">{{ record.courseTitle }}</h4>
                <div class="record-progress">
                  <el-progress :percentage="record.progress || 0" :stroke-width="8" :show-text="false" color="#D64541" />
                  <span class="progress-text">{{ record.completedChapters }}/{{ record.totalChapters }} 章节</span>
                </div>
                <div class="record-meta">
                  <span><el-icon><Clock /></el-icon> {{ record.totalDuration }}分钟</span>
                  <span><el-icon><Calendar /></el-icon> {{ formatDate(record.lastStudyTime) }}</span>
                </div>
              </div>
              <el-button type="primary" class="continue-btn" @click.stop="continueCourse(record.courseId)">
                继续学习
              </el-button>
            </div>
            <el-empty v-if="records.length === 0 && !loading" description="暂无学习记录" />
          </div>

          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="page"
              :page-size="pageSize"
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
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Clock, CircleCheck, Search, Calendar, Timer, Trophy, Document } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getStudyRecords, getStudyReport } from '@/api/course'
import dayjs from 'dayjs'
import * as echarts from 'echarts'

const router = useRouter()

// 获取完整URL（添加API基础路径）
const getFullUrl = (url: string) => {
  if (!url) return 'https://picsum.photos/200/120?random=1'
  if (url.startsWith('http')) return url
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${url}`
}
const loading = ref(false)
const reportLoading = ref(false)
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)
const records = ref<any[]>([])
const chartRef = ref<HTMLElement | null>(null)
let chartInstance: echarts.ECharts | null = null

const report = ref({
  totalStudyTime: 0,
  completedCourses: 0,
  studyDays: 0,
  studyDates: [] as string[],
  dailyStudyTime: [] as number[],
  courseCompletionRate: 0,
  totalPoints: 0,
  inProgressCourses: 0,
  avgDailyStudyTime: 0,
  diligenceScore: 0
})

// 使用后端返回的勤奋度分数
const diligenceScore = computed(() => {
  return report.value.diligenceScore || 0
})

const formatDuration = (minutes: number) => {
  if (minutes < 60) return `${minutes}分钟`
  const hours = Math.floor(minutes / 60)
  const mins = minutes % 60
  return mins > 0 ? `${hours}小时${mins}分` : `${hours}小时`
}

const initChart = () => {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  const option = {
    tooltip: { trigger: 'axis', formatter: '{b}<br />学习时长: {c} 分钟' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', boundaryGap: false, data: report.value.studyDates },
    yAxis: { type: 'value', name: '时长(分钟)' },
    series: [{
      name: '学习时长',
      type: 'line',
      smooth: true,
      lineStyle: { width: 3, color: '#D64541' },
      showSymbol: false,
      areaStyle: {
        opacity: 0.3,
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(214, 69, 65, 0.5)' },
          { offset: 1, color: 'rgba(214, 69, 65, 0.05)' }
        ])
      },
      data: report.value.dailyStudyTime
    }]
  }
  chartInstance.setOption(option)
}

const fetchReport = async () => {
  reportLoading.value = true
  try {
    const res = await getStudyReport()
    if (res.data) {
      report.value = res.data
      setTimeout(() => initChart(), 100)
    }
  } catch (error) {
    console.error('获取学习报告失败', error)
  } finally {
    reportLoading.value = false
  }
}

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getStudyRecords({})
    if (res.data) {
      const allRecords = res.data || []
      total.value = allRecords.length
      let filteredRecords = allRecords
      if (keyword.value) {
        const k = keyword.value.toLowerCase()
        filteredRecords = allRecords.filter((r: any) => r.courseTitle?.toLowerCase().includes(k))
        total.value = filteredRecords.length
      }
      const start = (page.value - 1) * pageSize.value
      records.value = filteredRecords.slice(start, start + pageSize.value)
    }
  } catch (error) {
    ElMessage.error('获取学习记录失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => { page.value = 1; fetchRecords() }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchRecords() }
const continueCourse = (courseId: number) => router.push(`/courses/${courseId}`)
const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')
const handleResize = () => chartInstance?.resize()

onMounted(() => {
  fetchReport()
  fetchRecords()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
})
</script>

<style scoped lang="scss">
.study-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: var(--color-primary-gradient);
  padding: 60px 20px;
  text-align: center;
  color: white;

  .page-title {
    font-size: var(--font-size-4xl);
    font-weight: var(--font-weight-bold);
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    .title-icon { font-size: 40px; }
  }

  .page-desc { font-size: var(--font-size-lg); opacity: 0.9; }
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

// 统计卡片
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 32px;

  @media (max-width: 992px) { grid-template-columns: repeat(2, 1fr); }
  @media (max-width: 576px) { grid-template-columns: 1fr; }
}

.stat-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  border: 1px solid var(--color-border);
  transition: all var(--transition-base);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
  }

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: var(--radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;

    &.red { background: var(--color-primary-gradient); }
    &.gold { background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%); }
    &.green { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); }
    &.purple { background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%); }
  }

  .stat-info {
    .stat-value {
      display: block;
      font-size: var(--font-size-xl);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
    }
    .stat-label {
      font-size: var(--font-size-sm);
      color: var(--color-text-muted);
    }
  }
}

// 图表布局
.chart-layout {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 24px;
  margin-bottom: 32px;

  @media (max-width: 992px) { grid-template-columns: 1fr; }
}

.section-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-border);

  .card-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: var(--font-size-base);
    font-weight: var(--font-weight-bold);
    color: var(--color-text-primary);
    margin: 0 0 20px 0;
  }
}

.chart-card .chart-container { height: 280px; }

.overview-card {
  .overview-list {
    .overview-item {
      margin-bottom: 20px;
      &:last-child { margin-bottom: 0; }
      .label {
        display: block;
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);
        margin-bottom: 8px;
      }
      .value {
        font-size: var(--font-size-lg);
        font-weight: var(--font-weight-bold);
        color: var(--color-text-primary);
      }
    }
  }
}

// 学习记录
.records-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;

    @media (max-width: 576px) { flex-direction: column; gap: 16px; }

    .search-input {
      width: 280px;
      :deep(.el-input__wrapper) { border-radius: var(--radius-lg); }
    }
  }
}

.records-list {
  .record-item {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 20px;
    border-radius: var(--radius-lg);
    border: 1px solid var(--color-border-light);
    margin-bottom: 16px;
    cursor: pointer;
    transition: all var(--transition-base);

    &:hover {
      border-color: var(--color-primary);
      box-shadow: var(--shadow-md);
    }

    @media (max-width: 768px) {
      flex-direction: column;
      text-align: center;
    }

    .record-cover {
      width: 160px;
      height: 100px;
      border-radius: var(--radius-md);
      overflow: hidden;
      flex-shrink: 0;

      img { width: 100%; height: 100%; object-fit: cover; }
    }

    .record-info {
      flex: 1;
      min-width: 0;

      .record-title {
        font-size: var(--font-size-base);
        font-weight: var(--font-weight-bold);
        color: var(--color-text-primary);
        margin-bottom: 12px;
      }

      .record-progress {
        margin-bottom: 12px;
        .progress-text {
          font-size: var(--font-size-xs);
          color: var(--color-text-muted);
          margin-top: 4px;
        }
      }

      .record-meta {
        display: flex;
        gap: 20px;
        font-size: var(--font-size-xs);
        color: var(--color-text-muted);

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }

    .continue-btn { flex-shrink: 0; }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
