<template>
  <MainLayout>
    <div class="result-page" v-loading="loading">
      <div class="page-container" v-if="result">
        <!-- 结果卡片 -->
        <div class="result-card" :class="result.score >= result.passScore ? 'pass' : 'fail'">
          <div class="result-icon">
            <el-icon v-if="result.score >= result.passScore"><CircleCheckFilled /></el-icon>
            <el-icon v-else><CircleCloseFilled /></el-icon>
          </div>
          <h1 class="result-title">{{ result.score >= result.passScore ? '🎉 恭喜通过' : '😢 未能通过' }}</h1>
          <p class="exam-name">{{ result.examTitle }}</p>

          <div class="score-display">
            <div class="score-ring">
              <svg viewBox="0 0 120 120">
                <circle class="bg" cx="60" cy="60" r="52" />
                <circle 
                  class="progress" 
                  cx="60" cy="60" r="52" 
                  :stroke-dasharray="`${(result.score / result.totalScore) * 327} 327`"
                />
              </svg>
              <div class="score-text">
                <span class="score">{{ result.score }}</span>
                <span class="total">/ {{ result.totalScore }}</span>
              </div>
            </div>
          </div>

          <div class="stats-grid">
            <div class="stat-item">
              <span class="label">及格分</span>
              <span class="value">{{ result.passScore }}</span>
            </div>
            <div class="stat-item">
              <span class="label">用时</span>
              <span class="value">{{ formatDuration(result.duration) }}</span>
            </div>
            <div class="stat-item correct">
              <span class="label">正确</span>
              <span class="value">{{ result.correctCount }}题</span>
            </div>
            <div class="stat-item wrong">
              <span class="label">错误</span>
              <span class="value">{{ result.wrongCount }}题</span>
            </div>
          </div>

          <div class="submit-time">
            <el-icon><Clock /></el-icon>
            提交时间：{{ formatDate(result.submitTime) }}
          </div>

          <div class="actions">
            <el-button size="large" type="primary" @click="$router.push('/exam')">
              返回考试列表
            </el-button>
            <el-button size="large" @click="$router.push(`/exam/${result.examId}`)">
              再考一次
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { CircleCheckFilled, CircleCloseFilled, Clock } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getExamResult, getExamDetail } from '@/api/exam'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const result = ref<any>(null)

const fetchResult = async () => {
  const id = route.params.id
  if (!id) return
  loading.value = true
  try {
    const res = await getExamResult(Number(id))
    if (res.data) {
      const scoreData = res.data
      let examInfo: any = {}
      if (scoreData.examId) {
        try {
          const examRes = await getExamDetail(scoreData.examId)
          if (examRes.data) {
            examInfo = { examTitle: examRes.data.name, passScore: examRes.data.passScore, totalScore: examRes.data.totalScore }
          }
        } catch (e) { console.error('获取试卷详情失败', e) }
      }
      result.value = { ...scoreData, ...examInfo, submitTime: scoreData.createTime, passScore: examInfo.passScore || 60, totalScore: examInfo.totalScore || 100 }
    }
  } catch (error) {
    ElMessage.error('获取成绩失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')
const formatDuration = (seconds: number) => {
  if (!seconds) return '0秒'
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return mins ? `${mins}分${secs}秒` : `${secs}秒`
}

onMounted(() => fetchResult())
</script>

<style scoped lang="scss">
.result-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
  padding: 60px 20px;
}

.page-container {
  max-width: 600px;
  margin: 0 auto;
}

.result-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-2xl);
  padding: 48px;
  text-align: center;
  border: 2px solid transparent;

  &.pass {
    border-color: rgba(34, 197, 94, 0.3);
    .result-icon { color: #22C55E; }
    .score-ring .progress { stroke: #22C55E; }
  }

  &.fail {
    border-color: rgba(239, 68, 68, 0.3);
    .result-icon { color: #EF4444; }
    .score-ring .progress { stroke: #EF4444; }
  }

  .result-icon {
    font-size: 80px;
    margin-bottom: 16px;
  }

  .result-title {
    font-size: var(--font-size-2xl);
    font-weight: var(--font-weight-bold);
    margin-bottom: 8px;
    color: var(--color-text-primary);
  }

  .exam-name {
    font-size: var(--font-size-base);
    color: var(--color-text-muted);
    margin-bottom: 32px;
  }
}

.score-display {
  margin-bottom: 32px;

  .score-ring {
    position: relative;
    width: 160px;
    height: 160px;
    margin: 0 auto;

    svg {
      transform: rotate(-90deg);
      width: 100%;
      height: 100%;
    }

    circle {
      fill: none;
      stroke-width: 10;
      stroke-linecap: round;

      &.bg { stroke: var(--color-border-light); }
      &.progress { transition: stroke-dasharray 1s ease; }
    }

    .score-text {
      position: absolute;
      inset: 0;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      .score {
        font-size: 48px;
        font-weight: var(--font-weight-bold);
        color: var(--color-text-primary);
        line-height: 1;
      }

      .total {
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);
      }
    }
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;

  @media (max-width: 576px) { grid-template-columns: repeat(2, 1fr); }

  .stat-item {
    background: var(--color-bg-tertiary);
    border-radius: var(--radius-lg);
    padding: 16px;

    .label {
      display: block;
      font-size: var(--font-size-xs);
      color: var(--color-text-muted);
      margin-bottom: 4px;
    }

    .value {
      font-size: var(--font-size-lg);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
    }

    &.correct .value { color: #22C55E; }
    &.wrong .value { color: #EF4444; }
  }
}

.submit-time {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: var(--font-size-sm);
  color: var(--color-text-muted);
  margin-bottom: 32px;
}

.actions {
  display: flex;
  justify-content: center;
  gap: 16px;

  @media (max-width: 576px) { flex-direction: column; }
}
</style>
