<template>
  <AdminLayout>
    <div class="dashboard-page">
      <!-- 欢迎区域 -->
      <div class="welcome-section">
        <div class="welcome-text">
          <h1>欢迎回来，{{ userStore.userInfo?.nickname || '管理员' }}</h1>
          <p>今天是 {{ currentDate }}，祝您工作顺利！</p>
        </div>
        <div class="quick-actions">
          <el-button type="primary" @click="$router.push('/admin/posts/audit')">
            <el-icon><Bell /></el-icon>
            待审核 {{ stats.pendingPosts + stats.pendingResources }}
          </el-button>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card users">
          <div class="stat-icon"><el-icon :size="28"><User /></el-icon></div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.totalUsers }}</span>
            <span class="stat-label">总用户</span>
          </div>
          <div class="stat-trend" :class="{ up: stats.userGrowthRate >= 0, down: stats.userGrowthRate < 0 }">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ formatGrowthRate(stats.userGrowthRate) }}</span>
          </div>
        </div>

        <div class="stat-card resources">
          <div class="stat-icon"><el-icon :size="28"><Document /></el-icon></div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.totalResources }}</span>
            <span class="stat-label">资源数</span>
          </div>
          <div class="stat-trend" :class="{ up: stats.resourceGrowthRate >= 0, down: stats.resourceGrowthRate < 0 }">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ formatGrowthRate(stats.resourceGrowthRate) }}</span>
          </div>
        </div>

        <div class="stat-card courses">
          <div class="stat-icon"><el-icon :size="28"><Reading /></el-icon></div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.totalCourses }}</span>
            <span class="stat-label">课程数</span>
          </div>
          <div class="stat-trend" :class="{ up: stats.courseGrowthRate >= 0, down: stats.courseGrowthRate < 0 }">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ formatGrowthRate(stats.courseGrowthRate) }}</span>
          </div>
        </div>

        <div class="stat-card posts">
          <div class="stat-icon"><el-icon :size="28"><ChatLineSquare /></el-icon></div>
          <div class="stat-content">
            <span class="stat-value">{{ stats.totalPosts }}</span>
            <span class="stat-label">帖子数</span>
          </div>
          <div class="stat-trend" :class="{ up: stats.postGrowthRate >= 0, down: stats.postGrowthRate < 0 }">
            <el-icon><TrendCharts /></el-icon>
            <span>{{ formatGrowthRate(stats.postGrowthRate) }}</span>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div class="charts-grid">
        <div class="chart-card">
          <div class="chart-header">
            <h3>用户增长趋势</h3>
            <el-radio-group v-model="chartPeriod" size="small">
              <el-radio-button label="week">本周</el-radio-button>
              <el-radio-button label="month">本月</el-radio-button>
            </el-radio-group>
          </div>
          <div ref="userChartRef" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>内容分布</h3>
          </div>
          <div ref="contentChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 新增图表 -->
      <div class="charts-grid-extended">
        <div class="chart-card">
          <div class="chart-header">
            <h3>学习活跃度趋势</h3>
          </div>
          <div ref="studyActivityChartRef" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>考试通过率</h3>
          </div>
          <div ref="examPassRateChartRef" class="chart-container"></div>
        </div>

        <div class="chart-card">
          <div class="chart-header">
            <h3>热门资源/课程 Top 10</h3>
          </div>
          <div ref="popularItemsChartRef" class="chart-container"></div>
        </div>
      </div>

      <!-- 待办事项 & 系统信息 -->
      <div class="bottom-grid">
        <div class="pending-card">
          <div class="card-header">
            <h3>待处理事项</h3>
            <el-button text type="primary" @click="$router.push('/admin/posts/audit')">查看全部</el-button>
          </div>
          <div class="pending-list">
            <div class="pending-item" @click="$router.push('/admin/posts/audit')">
              <div class="item-icon posts"><el-icon><ChatLineSquare /></el-icon></div>
              <div class="item-info">
                <span class="item-title">待审核帖子</span>
                <span class="item-desc">需要您审核的社区帖子</span>
              </div>
              <el-badge :value="stats.pendingPosts" :max="99" type="warning" />
            </div>
            <div class="pending-item" @click="$router.push('/admin/resources/audit')">
              <div class="item-icon resources"><el-icon><Document /></el-icon></div>
              <div class="item-info">
                <span class="item-title">待审核资源</span>
                <span class="item-desc">需要您审核的上传资源</span>
              </div>
              <el-badge :value="stats.pendingResources" :max="99" type="warning" />
            </div>
          </div>
        </div>

        <div class="system-card">
          <div class="card-header">
            <h3>系统信息</h3>
          </div>
          <div class="system-info">
            <div class="info-row">
              <span class="label">系统版本</span>
              <span class="value">v1.0.0</span>
            </div>
            <div class="info-row">
              <span class="label">注册用户</span>
              <span class="value">{{ stats.totalUsers }}</span>
            </div>
            <div class="info-row">
              <span class="label">内容总数</span>
              <span class="value">{{ stats.totalResources + stats.totalCourses + stats.totalPosts }}</span>
            </div>
            <div class="info-row">
              <span class="label">试卷数量</span>
              <span class="value">{{ stats.totalExams }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { User, Document, Reading, ChatLineSquare, Bell, TrendCharts } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { useUserStore } from '@/stores/user'
import { getDashboardStats } from '@/api/admin'
import * as echarts from 'echarts'

const userStore = useUserStore()
const userChartRef = ref<HTMLElement>()
const contentChartRef = ref<HTMLElement>()
const studyActivityChartRef = ref<HTMLElement>()
const examPassRateChartRef = ref<HTMLElement>()
const popularItemsChartRef = ref<HTMLElement>()
const chartPeriod = ref('month')

const currentDate = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const stats = ref({
  totalUsers: 0, totalResources: 0, totalCourses: 0, totalPosts: 0, totalExams: 0,
  pendingPosts: 0, pendingResources: 0, onlineUsers: 0, todayVisits: 0, userGrowthData: [0, 0, 0, 0, 0, 0],
  userGrowthRate: 0, resourceGrowthRate: 0, courseGrowthRate: 0, postGrowthRate: 0,
  studyActivityData: [], examPassRate: null, popularItems: []
})

// 格式化增长率显示
const formatGrowthRate = (rate: number) => {
  if (rate === 0) return '0%'
  return rate > 0 ? `+${rate}%` : `${rate}%`
}

const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    if (res.data) {
      stats.value = {
        totalUsers: res.data.totalUsers || 0, totalResources: res.data.totalResources || 0,
        totalCourses: res.data.totalCourses || 0, totalPosts: res.data.totalPosts || 0,
        totalExams: res.data.totalExams || 0, pendingPosts: res.data.pendingPosts || 0,
        pendingResources: res.data.pendingResources || 0, onlineUsers: res.data.onlineUsers || 0,
        todayVisits: res.data.todayVisits || 0, userGrowthData: res.data.userGrowthData || [0, 0, 0, 0, 0, 0],
        userGrowthRate: res.data.userGrowthRate || 0, resourceGrowthRate: res.data.resourceGrowthRate || 0,
        courseGrowthRate: res.data.courseGrowthRate || 0, postGrowthRate: res.data.postGrowthRate || 0,
        studyActivityData: res.data.studyActivityData || [], examPassRate: res.data.examPassRate || { totalExams: 0, passCount: 0, failCount: 0, passRate: 0 },
        popularItems: res.data.popularItems || []
      }
    }
  } catch (error) { ElMessage.error('获取统计数据失败') }
}

const initUserChart = () => {
  if (!userChartRef.value) return
  const chart = echarts.init(userChartRef.value)
  const today = new Date()
  const months = []
  for (let i = 5; i >= 0; i--) { const d = new Date(today.getFullYear(), today.getMonth() - i, 1); months.push(`${d.getMonth() + 1}月`) }
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: months, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { color: '#6b7280' } },
    yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f3f4f6' } }, axisLabel: { color: '#6b7280' } },
    series: [{ data: stats.value.userGrowthData, type: 'line', smooth: true, lineStyle: { color: '#D64541', width: 3 }, itemStyle: { color: '#D64541' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(214, 69, 65, 0.3)' }, { offset: 1, color: 'rgba(214, 69, 65, 0)' }] } } }]
  })
}

const initContentChart = () => {
  if (!contentChartRef.value) return
  const chart = echarts.init(contentChartRef.value)
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center' },
    series: [{ type: 'pie', radius: ['45%', '70%'], center: ['35%', '50%'], label: { show: false },
      data: [
        { value: stats.value.totalResources, name: '资源', itemStyle: { color: '#FFD93D' } },
        { value: stats.value.totalCourses, name: '课程', itemStyle: { color: '#D64541' } },
        { value: stats.value.totalPosts, name: '帖子', itemStyle: { color: '#22C55E' } },
        { value: stats.value.totalExams, name: '试卷', itemStyle: { color: '#8B5CF6' } }
      ]
    }]
  })
}

const initStudyActivityChart = () => {
  if (!studyActivityChartRef.value) return
  const chart = echarts.init(studyActivityChartRef.value)
  const dates = stats.value.studyActivityData.map((item: any) => item.date)
  const minutes = stats.value.studyActivityData.map((item: any) => item.minutes)
  chart.setOption({
    tooltip: { trigger: 'axis', formatter: '{b}: {c} 分钟' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: dates, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { color: '#6b7280', rotate: 45 } },
    yAxis: { type: 'value', name: '分钟', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f3f4f6' } }, axisLabel: { color: '#6b7280' } },
    series: [{ data: minutes, type: 'line', smooth: true, lineStyle: { color: '#22C55E', width: 3 }, itemStyle: { color: '#22C55E' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(34, 197, 94, 0.3)' }, { offset: 1, color: 'rgba(34, 197, 94, 0)' }] } } }]
  })
}

const initExamPassRateChart = () => {
  if (!examPassRateChartRef.value) return
  const chart = echarts.init(examPassRateChartRef.value)
  const passRate = stats.value.examPassRate
  chart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} 次 ({d}%)' },
    legend: { orient: 'vertical', right: '5%', top: 'center' },
    series: [{ type: 'pie', radius: ['45%', '70%'], center: ['35%', '50%'], label: { show: true, formatter: '{b}: {d}%' },
      data: [
        { value: passRate.passCount, name: '及格', itemStyle: { color: '#22C55E' } },
        { value: passRate.failCount, name: '不及格', itemStyle: { color: '#EF4444' } }
      ]
    }]
  })
}

const initPopularItemsChart = () => {
  if (!popularItemsChartRef.value) return
  const chart = echarts.init(popularItemsChartRef.value)
  const items = stats.value.popularItems.slice(0, 10)
  const titles = items.map((item: any) => item.title.length > 15 ? item.title.substring(0, 15) + '...' : item.title).reverse()
  const counts = items.map((item: any) => item.count).reverse()
  const colors = items.map((item: any) => item.type === 'resource' ? '#FFD93D' : '#8B5CF6').reverse()
  chart.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '20%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f3f4f6' } }, axisLabel: { color: '#6b7280' } },
    yAxis: { type: 'category', data: titles, axisLine: { lineStyle: { color: '#e5e7eb' } }, axisLabel: { color: '#6b7280' } },
    series: [{ type: 'bar', data: counts, itemStyle: { color: (params: any) => colors[params.dataIndex] }, barWidth: '60%' }]
  })
}

onMounted(async () => { await fetchStats(); await nextTick(); initUserChart(); initContentChart(); initStudyActivityChart(); initExamPassRateChart(); initPopularItemsChart() })
</script>

<style scoped lang="scss">
// 欢迎区块
.welcome-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, #D64541 0%, #FF6B6B 100%);
  border-radius: 16px;
  padding: 24px 32px;
  margin-bottom: 24px;
  color: white;

  h1 { font-size: 24px; margin: 0 0 8px; }
  p { margin: 0; opacity: 0.9; }

  .quick-actions {
    .el-button {
      background: white !important;
      color: #D64541 !important;
      border: none !important;
      font-weight: 600;
      padding: 12px 20px;
      font-size: 15px;
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

      &:hover {
        background: #f3f4f6 !important;
      }
    }
  }
}

// 统计卡片
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 24px;

  @media (max-width: 1200px) { grid-template-columns: repeat(2, 1fr); }
  @media (max-width: 576px) { grid-template-columns: 1fr; }
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .stat-icon {
    width: 56px; height: 56px; border-radius: 12px;
    display: flex; align-items: center; justify-content: center; color: white;
  }

  &.users .stat-icon { background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%); }
  &.resources .stat-icon { background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%); }
  &.courses .stat-icon { background: linear-gradient(135deg, #D64541 0%, #FF6B6B 100%); }
  &.posts .stat-icon { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); }

  .stat-content {
    flex: 1;
    .stat-value { display: block; font-size: 28px; font-weight: 700; color: #1f2937; }
    .stat-label { font-size: 14px; color: #6b7280; }
  }

  .stat-trend {
    display: flex; align-items: center; gap: 4px;
    font-size: 13px; color: #22C55E;
    &.down { color: #EF4444; }
  }
}

// 图表区块
.charts-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 24px;

  @media (max-width: 992px) { grid-template-columns: 1fr; }
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .chart-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
    h3 { margin: 0; font-size: 16px; font-weight: 600; color: #1f2937; }
  }

  .chart-container { height: 280px; }
}

// 底部区块
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;

  @media (max-width: 992px) { grid-template-columns: 1fr; }
}

.pending-card, .system-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

  .card-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
    h3 { margin: 0; font-size: 16px; font-weight: 600; color: #1f2937; }
  }
}

.pending-list {
  .pending-item {
    display: flex; align-items: center; gap: 16px; padding: 16px;
    border-radius: 12px; cursor: pointer; transition: all 0.2s;

    &:hover { background: #f9fafb; }

    .item-icon {
      width: 44px; height: 44px; border-radius: 10px;
      display: flex; align-items: center; justify-content: center; color: white;
      &.posts { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); }
      &.resources { background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%); }
    }

    .item-info {
      flex: 1;
      .item-title { display: block; font-weight: 500; color: #1f2937; }
      .item-desc { font-size: 13px; color: #9ca3af; }
    }
  }
}

.system-info {
  .info-row {
    display: flex; justify-content: space-between; padding: 12px 0;
    border-bottom: 1px solid #f3f4f6;
    &:last-child { border: none; }
    .label { color: #6b7280; }
    .value { font-weight: 500; color: #1f2937; }
  }
}

// 扩展图表区块
.charts-grid-extended {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
  margin-bottom: 24px;

  @media (min-width: 1400px) { grid-template-columns: repeat(3, 1fr); }
  @media (max-width: 1400px) { grid-template-columns: 1fr; }
}
</style>
