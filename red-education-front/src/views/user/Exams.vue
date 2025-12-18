<template>
  <MainLayout>
    <div class="exams-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="title-icon">🏆</span>
            我的成绩
          </h1>
          <p class="page-desc">查看考试记录，追踪学习效果</p>
        </div>
      </div>

      <div class="page-container">
        <div class="section-card" v-loading="loading">
          <div class="records-list">
            <div 
              v-for="(record, index) in records" 
              :key="record.id" 
              class="record-item animate-fade-in-up"
              :style="{ animationDelay: `${index * 50}ms` }"
              @click="viewResult(record.id)"
            >
              <div class="record-icon" :class="record.score >= record.passScore ? 'pass' : 'fail'">
                <el-icon :size="24" v-if="record.score >= record.passScore"><Check /></el-icon>
                <el-icon :size="24" v-else><Close /></el-icon>
              </div>
              <div class="record-info">
                <h4 class="exam-title">{{ record.examTitle }}</h4>
                <div class="exam-meta">
                  <span><el-icon><Clock /></el-icon> {{ formatDate(record.submitTime) }}</span>
                </div>
              </div>
              <div class="score-info">
                <span class="score" :class="record.score >= record.passScore ? 'pass' : 'fail'">
                  {{ record.score }}
                </span>
                <span class="score-label">分</span>
              </div>
              <el-button type="primary" class="detail-btn" @click.stop="viewResult(record.id)">
                查看详情
              </el-button>
            </div>
            <el-empty v-if="records.length === 0 && !loading" description="暂无考试记录" />
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Close, Clock } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getExamRecords } from '@/api/exam'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const records = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getExamRecords({ page: page.value, pageSize: pageSize.value })
    if (res.data) {
      records.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取成绩列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage: number) => {
  page.value = newPage
  fetchRecords()
}

const viewResult = (recordId: number) => {
  router.push(`/exam/result/${recordId}`)
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')

onMounted(() => {
  fetchRecords()
})
</script>

<style scoped lang="scss">
.exams-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
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
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 32px;
  border: 1px solid var(--color-border);
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
      border-color: #8B5CF6;
      box-shadow: var(--shadow-md);
    }

    @media (max-width: 768px) {
      flex-wrap: wrap;
    }

    .record-icon {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      flex-shrink: 0;

      &.pass { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); }
      &.fail { background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%); }
    }

    .record-info {
      flex: 1;
      min-width: 0;

      .exam-title {
        font-size: var(--font-size-base);
        font-weight: var(--font-weight-bold);
        color: var(--color-text-primary);
        margin-bottom: 8px;
      }

      .exam-meta {
        display: flex;
        gap: 16px;
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }

    .score-info {
      text-align: center;
      flex-shrink: 0;

      .score {
        font-size: 32px;
        font-weight: var(--font-weight-bold);

        &.pass { color: var(--color-success); }
        &.fail { color: var(--color-error); }
      }

      .score-label {
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);
        margin-left: 4px;
      }
    }

    .detail-btn {
      flex-shrink: 0;
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.animate-fade-in-up {
  opacity: 0;
  animation: fadeInUp 0.5s ease forwards;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
