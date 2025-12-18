<template>
  <MainLayout>
    <div class="points-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="points-display">
            <div class="points-icon">
              <el-icon :size="48"><Trophy /></el-icon>
            </div>
            <div class="points-info">
              <span class="points-label">当前积分</span>
              <span class="points-value">{{ balance }}</span>
            </div>
          </div>
          <p class="page-desc">通过学习和互动获取积分，兑换精美礼品</p>
        </div>
      </div>

      <div class="page-container">
        <div class="content-layout">
          <!-- 左侧主内容 -->
          <div class="main-content">
            <!-- 积分明细 -->
            <div class="section-card">
              <div class="section-header">
                <h3 class="section-title">
                  <el-icon><List /></el-icon>
                  积分明细
                </h3>
              </div>
              <div class="history-list" v-loading="loading">
                <div 
                  v-for="(item, index) in history" 
                  :key="index" 
                  class="history-item"
                >
                  <div class="item-left">
                    <div class="item-icon" :class="item.points > 0 ? 'income' : 'expense'">
                      <el-icon v-if="item.points > 0"><Plus /></el-icon>
                      <el-icon v-else><Minus /></el-icon>
                    </div>
                    <div class="item-info">
                      <span class="item-desc">{{ item.description || item.type }}</span>
                      <span class="item-time">{{ formatDate(item.createTime) }}</span>
                    </div>
                  </div>
                  <span class="item-points" :class="item.points > 0 ? 'income' : 'expense'">
                    {{ item.points > 0 ? '+' : '' }}{{ item.points }}
                  </span>
                </div>
                <el-empty v-if="history.length === 0 && !loading" description="暂无积分记录" />
              </div>
              <div class="pagination-wrapper" v-if="total > 0">
                <el-pagination
                  background
                  layout="prev, pager, next"
                  :current-page="page"
                  :page-size="pageSize"
                  :total="total"
                  small
                  @current-change="handlePageChange"
                />
              </div>
            </div>
          </div>

          <!-- 右侧边栏 -->
          <div class="sidebar">
            <!-- 积分规则 -->
            <div class="section-card rules-card">
              <h3 class="section-title">
                <el-icon><InfoFilled /></el-icon>
                获取规则
              </h3>
              <div class="rules-list">
                <div class="rule-item" v-for="rule in pointsRules" :key="rule.action">
                  <span class="rule-action">{{ rule.action }}</span>
                  <span class="rule-points">+{{ rule.points }}</span>
                </div>
              </div>
            </div>

            <!-- 快捷操作 -->
            <div class="section-card actions-card">
              <h3 class="section-title">
                <el-icon><Promotion /></el-icon>
                快捷操作
              </h3>
              <div class="quick-actions">
                <el-button class="action-btn" @click="$router.push('/points/ranking')">
                  <el-icon><Trophy /></el-icon>
                  积分排行
                </el-button>
                <el-button class="action-btn" @click="$router.push('/courses')">
                  <el-icon><Reading /></el-icon>
                  去学习
                </el-button>
                <el-button class="action-btn" @click="$router.push('/exam')">
                  <el-icon><EditPen /></el-icon>
                  去考试
                </el-button>
                <el-button class="action-btn" @click="$router.push('/community')">
                  <el-icon><ChatDotRound /></el-icon>
                  发帖子
                </el-button>
                <el-button class="action-btn" @click="$router.push('/user/exchange-records')">
                  <el-icon><ShoppingCart /></el-icon>
                  兑换记录
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { 
  Trophy, List, Plus, Minus, InfoFilled, Promotion, 
  Reading, EditPen, ChatDotRound, ShoppingCart 
} from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getPointsBalance, getPointsHistory } from '@/api/points'
import dayjs from 'dayjs'

const loading = ref(false)
const balance = ref(0)
const history = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 积分规则 - 与后端实际逻辑匹配
const pointsRules = [
  { action: '完成课程', points: 10 },
  { action: '考试及格', points: 15 },
  { action: '考试优秀', points: 30 },
  { action: '考试满分', points: 50 },
  { action: '资源审核通过', points: 5 },
  { action: '发表评论', points: 2 }
]

const fetchBalance = async () => {
  try {
    const res = await getPointsBalance()
    if (res.data) {
      balance.value = res.data.points || 0
    }
  } catch (error) {
    console.error('获取积分失败', error)
  }
}

const fetchHistory = async () => {
  loading.value = true
  try {
    const res = await getPointsHistory({ current: page.value, size: pageSize.value })
    if (res.data) {
      history.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取积分明细失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage: number) => {
  page.value = newPage
  fetchHistory()
}

const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')

onMounted(() => {
  fetchBalance()
  fetchHistory()
})
</script>

<style scoped lang="scss">
.points-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

// 页面头部
.page-header {
  background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
  padding: 60px 20px;
  text-align: center;
  color: var(--color-secondary);

  .header-content {
    max-width: 600px;
    margin: 0 auto;
  }

  .points-display {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    margin-bottom: 16px;

    .points-icon {
      width: 80px;
      height: 80px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .points-info {
      text-align: left;

      .points-label {
        display: block;
        font-size: var(--font-size-sm);
        opacity: 0.85;
        margin-bottom: 4px;
      }

      .points-value {
        font-size: 48px;
        font-weight: var(--font-weight-bold);
        line-height: 1;
      }
    }
  }

  .page-desc {
    font-size: var(--font-size-base);
    opacity: 0.85;
  }
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
}

// 内容布局
.content-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 24px;

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

// 卡片样式
.section-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-border);

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
  }

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: var(--font-size-base);
    font-weight: var(--font-weight-bold);
    color: var(--color-text-primary);
    margin: 0 0 20px 0;
  }
}

// 积分明细
.history-list {
  .history-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 0;
    border-bottom: 1px solid var(--color-border-light);

    &:last-child {
      border-bottom: none;
    }

    .item-left {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .item-icon {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;

      &.income {
        background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%);
      }

      &.expense {
        background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
      }
    }

    .item-info {
      display: flex;
      flex-direction: column;

      .item-desc {
        font-size: var(--font-size-sm);
        color: var(--color-text-primary);
        font-weight: var(--font-weight-medium);
      }

      .item-time {
        font-size: var(--font-size-xs);
        color: var(--color-text-muted);
      }
    }

    .item-points {
      font-size: var(--font-size-lg);
      font-weight: var(--font-weight-bold);

      &.income {
        color: var(--color-success);
      }

      &.expense {
        color: var(--color-error);
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

// 侧边栏
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;

  @media (max-width: 768px) {
    display: none;
  }
}

// 规则卡片
.rules-card {
  .rules-list {
    .rule-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid var(--color-border-light);

      &:last-child {
        border-bottom: none;
      }

      .rule-action {
        font-size: var(--font-size-sm);
        color: var(--color-text-secondary);
      }

      .rule-points {
        font-size: var(--font-size-sm);
        font-weight: var(--font-weight-bold);
        color: var(--color-success);
        background: rgba(34, 197, 94, 0.1);
        padding: 4px 10px;
        border-radius: var(--radius-full);
      }
    }
  }
}

// 快捷操作
.actions-card {
  .quick-actions {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 12px;

    .action-btn {
      height: auto;
      padding: 16px 12px;
      flex-direction: column;
      gap: 8px;
      border-radius: var(--radius-lg);
      border: 1px solid var(--color-border);
      background: var(--color-bg-tertiary);
      color: var(--color-text-primary);

      &:hover {
        border-color: var(--color-warning);
        color: var(--color-warning);
        background: rgba(255, 217, 61, 0.05);
      }

      .el-icon {
        font-size: 20px;
      }
    }
  }
}
</style>
