<template>
  <MainLayout>
    <div class="exam-detail-page" v-loading="loading">
      <div class="page-container" v-if="exam">
        <!-- 返回按钮 -->
        <el-button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回列表
        </el-button>

        <!-- 考试信息头部 -->
        <div class="exam-hero">
          <div class="hero-content">
            <el-tag :type="getStatusType(exam.status)" effect="dark" size="large" class="status-tag">
              {{ getStatusText(exam.status) }}
            </el-tag>
            <h1 class="exam-title">{{ exam.name }}</h1>
            <div class="exam-stats">
              <div class="stat-item">
                <el-icon><Document /></el-icon>
                <span>{{ exam.questionCount }}题</span>
              </div>
              <div class="stat-item">
                <el-icon><Clock /></el-icon>
                <span>{{ exam.duration }}分钟</span>
              </div>
              <div class="stat-item">
                <el-icon><Trophy /></el-icon>
                <span>满分{{ exam.totalScore }}分</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 标签页 -->
        <div class="tabs-section">
          <div class="tabs-header">
            <div 
              class="tab-item" 
              :class="{ active: activeTab === 'info' }"
              @click="activeTab = 'info'"
            >
              考试信息
            </div>
            <div 
              v-if="userStore.isLoggedIn"
              class="tab-item" 
              :class="{ active: activeTab === 'history' }"
              @click="activeTab = 'history'"
            >
              我的成绩
            </div>
            <div 
              class="tab-item" 
              :class="{ active: activeTab === 'leaderboard' }"
              @click="activeTab = 'leaderboard'"
            >
              排行榜
            </div>
          </div>

          <!-- 考试信息内容 -->
          <div class="tab-content" v-show="activeTab === 'info'">
            <div class="info-grid">
              <div class="info-card">
                <div class="info-item">
                  <span class="label">及格分数</span>
                  <span class="value">{{ exam.passScore }}分</span>
                </div>
                <div class="info-item">
                  <span class="label">创建时间</span>
                  <span class="value">{{ formatDate(exam.createTime) }}</span>
                </div>
              </div>

              <div class="rules-card">
                <h3><el-icon><Warning /></el-icon> 考试规则</h3>
                <ul class="rules-list">
                  <li>考试时长{{ exam.duration }}分钟，请合理安排答题时间</li>
                  <li>考试期间不得切换窗口，否则系统将自动交卷</li>
                  <li>答题过程中答案会自动保存，请放心答题</li>
                  <li>考试结束后可查看成绩和答题解析</li>
                </ul>
              </div>
            </div>

            <el-button
              type="primary"
              size="large"
              class="start-btn"
              @click="handleStartExam"
              :loading="starting"
            >
              <el-icon><VideoPlay /></el-icon>
              开始考试
            </el-button>
          </div>

          <!-- 我的成绩 -->
          <div class="tab-content" v-show="activeTab === 'history'">
            <div class="records-list" v-if="records.length > 0">
              <div 
                v-for="record in records" 
                :key="record.id" 
                class="record-item"
                @click="viewResult(record.id)"
              >
                <div class="record-score" :class="record.score >= exam.passScore ? 'pass' : 'fail'">
                  {{ record.score }}
                </div>
                <div class="record-info">
                  <el-tag :type="record.score >= exam.passScore ? 'success' : 'danger'" size="small">
                    {{ record.score >= exam.passScore ? '及格' : '不及格' }}
                  </el-tag>
                  <span class="record-time">{{ formatDate(record.createTime) }}</span>
                </div>
                <el-button type="primary" text>查看详情</el-button>
              </div>
            </div>
            <el-empty v-else description="暂无考试记录" />
          </div>

          <!-- 排行榜 -->
          <div class="tab-content" v-show="activeTab === 'leaderboard'" v-loading="leaderboardLoading">
            <div class="leaderboard-header">
              <h3>🏆 考试排行榜</h3>
              <el-select v-model="topN" @change="fetchLeaderboard" size="small">
                <el-option label="前10名" :value="10" />
                <el-option label="前20名" :value="20" />
                <el-option label="前50名" :value="50" />
              </el-select>
            </div>

            <div class="leaderboard-list" v-if="leaderboard.length > 0">
              <div 
                v-for="item in leaderboard" 
                :key="item.userId" 
                class="leaderboard-item"
                :class="{ 'current-user': item.userId === userStore.userInfo?.id }"
              >
                <div class="rank">
                  <span v-if="item.rank === 1" class="medal">🥇</span>
                  <span v-else-if="item.rank === 2" class="medal">🥈</span>
                  <span v-else-if="item.rank === 3" class="medal">🥉</span>
                  <span v-else class="rank-num">#{{ item.rank }}</span>
                </div>
                <el-avatar :size="40" :src="getAvatarUrl(item.avatar)" class="user-avatar">
                  {{ item.nickname?.charAt(0) || item.username?.charAt(0) }}
                </el-avatar>
                <div class="user-info">
                  <span class="username">
                    {{ item.nickname || item.username }}
                    <el-tag v-if="item.userId === userStore.userInfo?.id" type="success" size="small">我</el-tag>
                  </span>
                  <span class="submit-time">{{ formatDate(item.submitTime) }}</span>
                </div>
                <div class="score-col">
                  <span class="score">{{ item.score }}分</span>
                  <span class="duration">{{ formatDuration(item.duration) }}</span>
                </div>
              </div>
            </div>
            <el-empty v-else description="暂无排行数据" />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Document, Clock, Trophy, Warning, VideoPlay } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getExamDetail, getExamRecords, getExamLeaderboard } from '@/api/exam'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const starting = ref(false)
const exam = ref<any>(null)
const records = ref<any[]>([])
const activeTab = ref('info')
const leaderboard = ref<any[]>([])
const leaderboardLoading = ref(false)
const topN = ref(10)

const getAvatarUrl = (avatar: string | undefined) => {
  if (!avatar) return undefined
  if (avatar.startsWith('http')) return avatar
  return `${import.meta.env.VITE_API_BASE_URL}${avatar}`
}

const examId = computed(() => Number(route.params.id))

const fetchExamDetail = async () => {
  loading.value = true
  try {
    const res = await getExamDetail(examId.value)
    if (res.data) {
      exam.value = res.data
      if (userStore.isLoggedIn) fetchRecords()
      fetchLeaderboard()
    }
  } catch (error) {
    ElMessage.error('获取考试详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const fetchRecords = async () => {
  try {
    const res = await getExamRecords({ examId: examId.value })
    if (res.data) records.value = res.data.records || []
  } catch (error) {
    console.error('获取历史成绩失败', error)
  }
}

const fetchLeaderboard = async () => {
  leaderboardLoading.value = true
  try {
    const res = await getExamLeaderboard(examId.value, topN.value)
    if (res.data) leaderboard.value = res.data
  } catch (error) {
    console.error('获取排行榜失败', error)
  } finally {
    leaderboardLoading.value = false
  }
}

const handleStartExam = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  ElMessageBox.confirm('确认开始考试吗？考试期间请勿切换窗口。', '提示', {
    confirmButtonText: '开始考试',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    router.push(`/exam/${examId.value}/answer`)
  })
}

const viewResult = (recordId: number) => router.push(`/exam/result/${recordId}`)
const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')
const formatDuration = (seconds: number) => {
  if (!seconds) return '0秒'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return mins ? `${mins}分${secs}秒` : `${secs}秒`
}
const getStatusType = (status: number) => ({ 0: 'info', 1: 'success', 2: 'danger' }[status] || 'info')
const getStatusText = (status: number) => ({ 0: '未开始', 1: '进行中', 2: '已结束' }[status] || '未知')

onMounted(() => fetchExamDetail())
</script>

<style scoped lang="scss">
.exam-detail-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
  padding: 40px 20px;
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 24px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  &:hover { border-color: var(--color-primary); color: var(--color-primary); }
}

// 考试头部
.exam-hero {
  background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
  border-radius: var(--radius-xl);
  padding: 40px;
  color: white;
  margin-bottom: 24px;

  .status-tag { margin-bottom: 16px; }

  .exam-title {
    font-size: var(--font-size-3xl);
    font-weight: var(--font-weight-bold);
    margin-bottom: 20px;
  }

  .exam-stats {
    display: flex;
    gap: 32px;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: var(--font-size-base);
      opacity: 0.9;
    }
  }
}

// 标签页
.tabs-section {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  border: 1px solid var(--color-border);
  overflow: hidden;

  .tabs-header {
    display: flex;
    border-bottom: 1px solid var(--color-border-light);

    .tab-item {
      flex: 1;
      padding: 16px;
      text-align: center;
      cursor: pointer;
      transition: all var(--transition-fast);
      font-weight: var(--font-weight-medium);
      color: var(--color-text-secondary);

      &:hover { color: var(--color-primary); }

      &.active {
        color: var(--color-primary);
        border-bottom: 2px solid var(--color-primary);
      }
    }
  }

  .tab-content { padding: 32px; }
}

// 信息卡片
.info-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  margin-bottom: 32px;

  @media (max-width: 768px) { grid-template-columns: 1fr; }
}

.info-card {
  background: var(--color-bg-tertiary);
  border-radius: var(--radius-lg);
  padding: 24px;

  .info-item {
    display: flex;
    justify-content: space-between;
    margin-bottom: 16px;
    &:last-child { margin-bottom: 0; }
    .label { color: var(--color-text-muted); }
    .value { font-weight: var(--font-weight-bold); color: var(--color-text-primary); }
  }
}

.rules-card {
  background: rgba(139, 92, 246, 0.05);
  border-radius: var(--radius-lg);
  padding: 24px;
  border: 1px solid rgba(139, 92, 246, 0.2);

  h3 {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: var(--font-size-base);
    color: #8B5CF6;
    margin-bottom: 16px;
  }

  .rules-list {
    padding-left: 20px;
    li {
      margin-bottom: 8px;
      color: var(--color-text-secondary);
      line-height: var(--line-height-relaxed);
    }
  }
}

.start-btn {
  width: 100%;
  height: 56px;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
  border: none;
}

// 成绩记录
.records-list {
  .record-item {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 20px;
    border-radius: var(--radius-lg);
    border: 1px solid var(--color-border-light);
    margin-bottom: 12px;
    cursor: pointer;
    transition: all var(--transition-base);

    &:hover { border-color: #8B5CF6; box-shadow: var(--shadow-sm); }

    .record-score {
      font-size: 32px;
      font-weight: var(--font-weight-bold);
      &.pass { color: var(--color-success); }
      &.fail { color: var(--color-error); }
    }

    .record-info {
      flex: 1;
      .record-time { margin-left: 12px; font-size: var(--font-size-sm); color: var(--color-text-muted); }
    }
  }
}

// 排行榜
.leaderboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  h3 { font-size: var(--font-size-lg); margin: 0; }
}

.leaderboard-list {
  .leaderboard-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: var(--radius-lg);
    border: 1px solid var(--color-border-light);
    margin-bottom: 12px;
    transition: all var(--transition-base);

    &:hover { transform: translateY(-2px); box-shadow: var(--shadow-md); }
    &.current-user { background: rgba(139, 92, 246, 0.1); border-color: #8B5CF6; }

    .rank {
      width: 40px;
      text-align: center;
      .medal { font-size: 28px; }
      .rank-num { font-size: var(--font-size-lg); font-weight: var(--font-weight-bold); color: var(--color-text-muted); }
    }

    .user-avatar {
      background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
      color: white;
    }

    .user-info {
      flex: 1;
      .username { display: flex; align-items: center; gap: 8px; font-weight: var(--font-weight-medium); }
      .submit-time { font-size: var(--font-size-xs); color: var(--color-text-muted); display: block; margin-top: 4px; }
    }

    .score-col {
      text-align: right;
      .score { display: block; font-size: var(--font-size-lg); font-weight: var(--font-weight-bold); color: #8B5CF6; }
      .duration { font-size: var(--font-size-xs); color: var(--color-text-muted); }
    }
  }
}
</style>
