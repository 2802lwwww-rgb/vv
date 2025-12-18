<template>
  <MainLayout>
    <div class="wrong-questions-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="title-icon">📚</span>
            错题本
          </h1>
          <p class="page-desc">温故而知新，巩固薄弱知识点</p>
        </div>
      </div>

      <div class="page-container">
        <!-- 统计卡片 -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon blue"><el-icon :size="28"><Document /></el-icon></div>
            <div class="stat-info">
              <span class="stat-value">{{ stats.total }}</span>
              <span class="stat-label">总错题</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon red"><el-icon :size="28"><Warning /></el-icon></div>
            <div class="stat-info">
              <span class="stat-value">{{ stats.unmastered }}</span>
              <span class="stat-label">未掌握</span>
            </div>
          </div>
          <div class="stat-card">
            <div class="stat-icon green"><el-icon :size="28"><SuccessFilled /></el-icon></div>
            <div class="stat-info">
              <span class="stat-value">{{ stats.mastered }}</span>
              <span class="stat-label">已掌握</span>
            </div>
          </div>
        </div>

        <!-- 筛选栏 -->
        <div class="filter-bar">
          <div class="filter-tabs">
            <span 
              class="tab-item" 
              :class="{ active: filterStatus === '' }"
              @click="filterStatus = ''; handleFilter()"
            >全部</span>
            <span 
              class="tab-item" 
              :class="{ active: filterStatus === 0 }"
              @click="filterStatus = 0; handleFilter()"
            >未掌握</span>
            <span 
              class="tab-item" 
              :class="{ active: filterStatus === 1 }"
              @click="filterStatus = 1; handleFilter()"
            >已掌握</span>
          </div>
          <el-button type="primary" @click="fetchWrongQuestions">
            <el-icon><Refresh /></el-icon> 刷新
          </el-button>
        </div>

        <!-- 错题列表 -->
        <div class="questions-list" v-loading="loading">
          <el-empty v-if="questions.length === 0 && !loading" description="暂无错题记录，继续加油！" />

          <div
            v-for="(item, index) in questions"
            :key="item.id"
            class="question-card"
            :class="{ mastered: item.isMastered === 1 }"
          >
            <div class="question-header">
              <div class="question-meta">
                <span class="question-number"># {{ index + 1 }}</span>
                <el-tag :type="getQuestionTypeColor(item.type)" effect="dark" size="small">
                  {{ getQuestionType(item.type) }}
                </el-tag>
                <el-tag type="warning" size="small">错 {{ item.wrongCount || 1 }}次</el-tag>
                <el-tag v-if="item.isMastered === 1" type="success" size="small">已掌握</el-tag>
                <el-tag v-else type="danger" size="small">未掌握</el-tag>
              </div>
              <div class="question-actions">
                <el-button
                  v-if="item.isMastered === 0"
                  type="success"
                  size="small"
                  plain
                  @click="handleMarkMastered(item.id)"
                >
                  <el-icon><Check /></el-icon> 标记已掌握
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  text
                  @click="handleDelete(item.id)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>

            <div class="question-content">
              <p class="question-title">{{ item.content }}</p>

              <div v-if="item.type !== 'FILL_BLANK'" class="options-list">
                <div
                  v-for="option in parseOptions(item)"
                  :key="option.key"
                  class="option-item"
                  :class="{
                    correct: option.key === item.correctAnswer,
                    wrong: option.key === item.userAnswer && option.key !== item.correctAnswer
                  }"
                >
                  <span class="option-key">{{ option.key }}</span>
                  <span class="option-value">{{ option.value }}</span>
                  <span v-if="option.key === item.correctAnswer" class="option-badge correct">正确答案</span>
                  <span v-if="option.key === item.userAnswer && option.key !== item.correctAnswer" class="option-badge wrong">你的答案</span>
                </div>
              </div>

              <div v-else class="answer-comparison">
                <p><strong>正确答案：</strong>{{ item.correctAnswer }}</p>
                <p><strong>你的答案：</strong>{{ item.userAnswer || '未作答' }}</p>
              </div>

              <div v-if="item.explanation" class="explanation">
                <h4><el-icon><InfoFilled /></el-icon> 题目解析</h4>
                <p>{{ item.explanation }}</p>
              </div>
            </div>
          </div>
        </div>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            v-model:current-page="currentPage"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Warning, SuccessFilled, Refresh, Check, Delete, InfoFilled } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getWrongQuestions, markWrongQuestionMastered, deleteWrongQuestion } from '@/api/exam'

const loading = ref(false)
const questions = ref<any[]>([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const filterStatus = ref<number | string>('')

const stats = computed(() => ({
  total: total.value,
  unmastered: questions.value.filter(q => q.isMastered === 0).length,
  mastered: questions.value.filter(q => q.isMastered === 1).length
}))

const fetchWrongQuestions = async () => {
  loading.value = true
  try {
    const params: any = { current: currentPage.value, size: pageSize.value }
    if (filterStatus.value !== '') params.isMastered = filterStatus.value
    const res = await getWrongQuestions(params)
    if (res.data) { questions.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch (error) {
    ElMessage.error('获取错题列表失败')
  } finally {
    loading.value = false
  }
}

const parseOptions = (item: any) => {
  const options = []
  if (item.optionA) options.push({ key: 'A', value: item.optionA })
  if (item.optionB) options.push({ key: 'B', value: item.optionB })
  if (item.optionC) options.push({ key: 'C', value: item.optionC })
  if (item.optionD) options.push({ key: 'D', value: item.optionD })
  return options
}

const handleMarkMastered = async (id: number) => {
  try { await markWrongQuestionMastered(id); ElMessage.success('已标记为掌握'); fetchWrongQuestions() }
  catch (error) { ElMessage.error('操作失败') }
}

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确定删除这道错题记录吗？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }).then(async () => {
    try { await deleteWrongQuestion(id); ElMessage.success('删除成功'); fetchWrongQuestions() }
    catch (error) { ElMessage.error('删除失败') }
  })
}

const handleFilter = () => { currentPage.value = 1; fetchWrongQuestions() }
const handlePageChange = () => fetchWrongQuestions()
const handleSizeChange = () => { currentPage.value = 1; fetchWrongQuestions() }

const getQuestionType = (type: string) => ({ 'SINGLE_CHOICE': '单选', 'MULTIPLE': '多选', 'JUDGE': '判断', 'FILL_BLANK': '填空' }[type] || '未知')
const getQuestionTypeColor = (type: string) => ({ 'SINGLE_CHOICE': 'primary', 'MULTIPLE': 'warning', 'JUDGE': 'info', 'FILL_BLANK': 'success' }[type] || '')

onMounted(() => fetchWrongQuestions())
</script>

<style scoped lang="scss">
.wrong-questions-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
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

// 统计卡片
.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;

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

  .stat-icon {
    width: 56px;
    height: 56px;
    border-radius: var(--radius-lg);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    &.blue { background: linear-gradient(135deg, #3B82F6 0%, #2563EB 100%); }
    &.red { background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%); }
    &.green { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); }
  }

  .stat-info {
    .stat-value { display: block; font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: var(--color-text-primary); }
    .stat-label { font-size: var(--font-size-sm); color: var(--color-text-muted); }
  }
}

// 筛选栏
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 16px 24px;
  margin-bottom: 24px;
  border: 1px solid var(--color-border);

  .filter-tabs {
    display: flex;
    gap: 8px;

    .tab-item {
      padding: 8px 16px;
      border-radius: var(--radius-md);
      cursor: pointer;
      font-size: var(--font-size-sm);
      color: var(--color-text-secondary);
      transition: all var(--transition-fast);

      &:hover { background: var(--color-bg-tertiary); }
      &.active { background: var(--color-primary); color: white; }
    }
  }
}

// 错题卡片
.questions-list {
  .question-card {
    background: var(--color-bg-primary);
    border-radius: var(--radius-xl);
    padding: 24px;
    margin-bottom: 20px;
    border: 1px solid var(--color-border);
    transition: all var(--transition-base);

    &:hover { box-shadow: var(--shadow-md); }
    &.mastered { opacity: 0.7; }

    .question-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 20px;

      @media (max-width: 576px) { flex-direction: column; gap: 12px; }

      .question-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        flex-wrap: wrap;
        .question-number { font-weight: var(--font-weight-bold); color: var(--color-primary); }
      }

      .question-actions { display: flex; gap: 8px; }
    }

    .question-content {
      .question-title {
        font-size: var(--font-size-base);
        line-height: var(--line-height-relaxed);
        color: var(--color-text-primary);
        font-weight: var(--font-weight-medium);
        margin-bottom: 20px;
      }

      .options-list {
        margin-bottom: 20px;

        .option-item {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 14px 16px;
          border: 1px solid var(--color-border-light);
          border-radius: var(--radius-lg);
          margin-bottom: 10px;
          transition: all var(--transition-fast);

          .option-key {
            width: 28px;
            height: 28px;
            background: var(--color-bg-tertiary);
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: var(--font-weight-bold);
            font-size: var(--font-size-sm);
            flex-shrink: 0;
          }
          .option-value { flex: 1; }
          .option-badge {
            font-size: var(--font-size-xs);
            padding: 2px 8px;
            border-radius: var(--radius-sm);
            &.correct { background: rgba(34, 197, 94, 0.1); color: #22C55E; }
            &.wrong { background: rgba(239, 68, 68, 0.1); color: #EF4444; }
          }

          &.correct { border-color: #22C55E; background: rgba(34, 197, 94, 0.05); .option-key { background: #22C55E; color: white; } }
          &.wrong { border-color: #EF4444; background: rgba(239, 68, 68, 0.05); .option-key { background: #EF4444; color: white; } }
        }
      }

      .answer-comparison {
        padding: 16px;
        background: var(--color-bg-tertiary);
        border-radius: var(--radius-lg);
        margin-bottom: 20px;
        p { margin-bottom: 8px; &:last-child { margin-bottom: 0; } }
      }

      .explanation {
        padding: 16px;
        background: rgba(139, 92, 246, 0.05);
        border-left: 3px solid #8B5CF6;
        border-radius: var(--radius-lg);

        h4 {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: var(--font-size-sm);
          color: #8B5CF6;
          margin-bottom: 8px;
        }
        p { color: var(--color-text-secondary); line-height: var(--line-height-relaxed); }
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 32px;
}
</style>
