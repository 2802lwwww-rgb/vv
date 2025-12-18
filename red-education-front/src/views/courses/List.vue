<template>
  <MainLayout>
    <div class="courses-page">
      <!-- 页面头部 - 电影风格 -->
      <div class="page-header cinematic">
        <div class="header-bg">
          <div class="bg-gradient"></div>
          <div class="spotlight spotlight-1"></div>
          <div class="spotlight spotlight-2"></div>
        </div>
        <div class="header-content">
          <div class="header-badge">
            <span class="badge-icon">🎬</span>
            <span>沉浸式学习体验</span>
          </div>
          <h1 class="page-title">
            <span class="text-highlight">精品</span>
            <span class="text-gradient">视频课程</span>
          </h1>
          <p class="page-desc">探索红色历史，重温革命岁月</p>
        </div>
      </div>

      <div class="page-container">
        <!-- 筛选区域 -->
        <div class="filter-section">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索你想学习的课程..."
            size="large"
            class="search-input"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-select v-model="filters.orderBy" placeholder="排序方式" size="large" class="sort-select" @change="handleSearch">
            <el-option label="最新发布" value="createTime" />
            <el-option label="最多学习" value="studyCount" />
          </el-select>
        </div>

        <!-- Netflix风格课程海报网格 -->
        <div class="courses-poster-grid" v-loading="loading">
          <div 
            v-for="(course, index) in courses" 
            :key="course.id" 
            class="poster-card"
            :style="{ animationDelay: `${index * 80}ms` }"
            @click="goToDetail(course.id)"
            @mouseenter="activeCard = course.id"
            @mouseleave="activeCard = null"
          >
            <!-- 海报封面 -->
            <div class="poster-image">
              <img :src="getImageUrl(course.coverImage)" :alt="course.title" />
              <!-- 悬停时的光晕边框 -->
              <div class="poster-glow"></div>
              <!-- 播放图标 -->
              <div class="play-overlay">
                <div class="play-button">
                  <el-icon :size="32"><VideoPlay /></el-icon>
                </div>
              </div>
              <!-- 章节数标签 -->
              <div class="chapter-tag" v-if="course.chapterCount">
                {{ course.chapterCount }}个章节
              </div>
            </div>
            
            <!-- 海报信息 -->
            <div class="poster-info">
              <h3 class="poster-title">{{ course.title }}</h3>
              <p class="poster-desc">{{ course.description }}</p>
              <div class="poster-meta">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ course.studyCount || 0 }} 人学习
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ formatDate(course.createTime) }}
                </span>
              </div>
            </div>
            
            <!-- 悬停时展开的详情卡片 -->
            <transition name="expand">
              <div v-if="activeCard === course.id" class="expand-card">
                <div class="expand-content">
                  <h4 class="expand-title">{{ course.title }}</h4>
                  <p class="expand-desc">{{ course.description }}</p>
                  <div class="expand-stats">
                    <span><el-icon><User /></el-icon> {{ course.studyCount || 0 }} 人学习</span>
                    <span><el-icon><Collection /></el-icon> {{ course.chapterCount || 0 }} 章节</span>
                  </div>
                  <el-button type="primary" class="expand-btn" @click.stop="goToDetail(course.id)">
                    <el-icon><CaretRight /></el-icon>
                    立即学习
                  </el-button>
                </div>
              </div>
            </transition>
          </div>
        </div>

        <el-empty v-if="courses.length === 0 && !loading" description="暂无课程" />

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
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Search, User, Clock, CaretRight, VideoPlay, Collection } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

interface Course {
  id: number
  title: string
  description: string
  coverImage: string
  studyCount: number
  chapterCount: number
  createTime: string
}

const router = useRouter()
const loading = ref(false)
const courses = ref<Course[]>([])
const total = ref(0)
const activeCard = ref<number | null>(null)

const filters = reactive({
  page: 1,
  pageSize: 12,
  keyword: '',
  orderBy: 'createTime'
})

const fetchCourses = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/course/list', {
      params: {
        current: filters.page,
        size: filters.pageSize,
        keyword: filters.keyword || undefined,
        orderBy: filters.orderBy
      }
    })
    if (res.code === 200) {
      courses.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取课程列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.page = 1
  fetchCourses()
}

const handlePageChange = (page: number) => {
  filters.page = page
  fetchCourses()
}

const goToDetail = (id: number) => {
  router.push(`/courses/${id}`)
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD')

const getImageUrl = (url: string) => {
  if (!url) return 'https://picsum.photos/400/180?random=' + Math.random()
  if (url.startsWith('http')) return url
  return `${import.meta.env.VITE_API_BASE_URL}${url}`
}

onMounted(() => {
  fetchCourses()
})
</script>

<style scoped lang="scss">
.courses-page {
  min-height: calc(100vh - var(--header-height));
  background: linear-gradient(180deg, #0f0f1a 0%, #1a1a2e 100%);
}

// ============================================
// 页面头部 - 电影风格
// ============================================
.page-header.cinematic {
  position: relative;
  padding: 100px 20px 80px;
  text-align: center;
  overflow: hidden;

  .header-bg {
    position: absolute;
    inset: 0;
    z-index: 0;

    .bg-gradient {
      position: absolute;
      inset: 0;
      background: linear-gradient(135deg, #1a0a0a 0%, #2a0a1a 50%, #0a0a1a 100%);
    }

    .spotlight {
      position: absolute;
      border-radius: 50%;
      filter: blur(80px);
      animation: spotlightMove 8s ease-in-out infinite;

      &.spotlight-1 {
        width: 400px;
        height: 400px;
        top: -100px;
        left: 20%;
        background: radial-gradient(circle, rgba(225, 29, 72, 0.4) 0%, transparent 70%);
      }

      &.spotlight-2 {
        width: 300px;
        height: 300px;
        bottom: -50px;
        right: 15%;
        background: radial-gradient(circle, rgba(252, 211, 77, 0.3) 0%, transparent 70%);
        animation-delay: -4s;
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
    background: rgba(225, 29, 72, 0.2);
    backdrop-filter: blur(10px);
    padding: 10px 24px;
    border-radius: 50px;
    font-size: 14px;
    color: #fcd34d;
    margin-bottom: 24px;
    border: 1px solid rgba(225, 29, 72, 0.3);

    .badge-icon {
      font-size: 18px;
    }
  }

  .page-title {
    font-size: clamp(40px, 7vw, 64px);
    font-weight: 800;
    margin-bottom: 20px;
    line-height: 1.2;

    .text-highlight {
      color: #fff;
      margin-right: 10px;
    }

    .text-gradient {
      background: linear-gradient(135deg, #fcd34d 0%, #f97316 50%, #e11d48 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .page-desc {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.7);
    letter-spacing: 2px;
  }
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px 80px;
}

// ============================================
// 筛选区域
// ============================================
.filter-section {
  display: flex;
  gap: 16px;
  margin-bottom: 50px;
  padding: 20px 28px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);

  .search-input {
    width: 400px;

    :deep(.el-input__wrapper) {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 14px;
      box-shadow: none;
      color: #fff;

      .el-input__inner {
        color: #fff;
        &::placeholder {
          color: rgba(255, 255, 255, 0.5);
        }
      }

      .el-input__prefix {
        color: rgba(255, 255, 255, 0.5);
      }

      &:hover, &.is-focus {
        border-color: #e11d48;
        box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.2);
      }
    }
  }

  .sort-select {
    width: 160px;

    :deep(.el-select__wrapper) {
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 14px;
      box-shadow: none;

      .el-select__placeholder, .el-select__selected-item {
        color: rgba(255, 255, 255, 0.8);
      }

      &:hover, &.is-focused {
        border-color: #e11d48;
        box-shadow: 0 0 0 3px rgba(225, 29, 72, 0.2);
      }
    }
  }
}

// ============================================
// Netflix风格海报网格
// ============================================
.courses-poster-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  margin-bottom: 60px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(3, 1fr);
  }

  @media (max-width: 900px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 576px) {
    grid-template-columns: 1fr;
    gap: 20px;
  }
}

// 海报卡片
.poster-card {
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  background: #1a1a2e;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  opacity: 0;
  animation: posterFadeIn 0.6s ease forwards;

  &:hover {
    transform: scale(1.05) translateY(-10px);
    z-index: 10;

    .poster-glow {
      opacity: 1;
    }

    .play-overlay {
      opacity: 1;
    }

    .poster-image img {
      transform: scale(1.1);
    }
  }

  .poster-image {
    position: relative;
    aspect-ratio: 2/3;
    overflow: hidden;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }

    // 发光边框
    .poster-glow {
      position: absolute;
      inset: -3px;
      background: linear-gradient(135deg, #e11d48, #fcd34d, #f97316, #e11d48);
      background-size: 300% 300%;
      border-radius: inherit;
      z-index: -1;
      opacity: 0;
      transition: opacity 0.4s ease;
      animation: glowRotate 3s linear infinite;
    }

    // 播放遮罩
    .play-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(180deg, transparent 40%, rgba(0, 0, 0, 0.9) 100%);
      display: flex;
      align-items: center;
      justify-content: center;
      opacity: 0;
      transition: opacity 0.3s ease;

      .play-button {
        width: 60px;
        height: 60px;
        background: rgba(225, 29, 72, 0.9);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        box-shadow: 0 0 30px rgba(225, 29, 72, 0.6);
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.1);
        }
      }
    }

    // 章节标签
    .chapter-tag {
      position: absolute;
      top: 12px;
      right: 12px;
      background: rgba(0, 0, 0, 0.7);
      backdrop-filter: blur(10px);
      color: #fcd34d;
      padding: 6px 12px;
      border-radius: 20px;
      font-size: 12px;
      font-weight: 600;
      border: 1px solid rgba(252, 211, 77, 0.3);
    }
  }

  // 海报信息
  .poster-info {
    padding: 16px;
    background: linear-gradient(180deg, #1a1a2e 0%, #0f0f1a 100%);

    .poster-title {
      font-size: 16px;
      font-weight: 700;
      color: #fff;
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .poster-desc {
      font-size: 13px;
      color: rgba(255, 255, 255, 0.6);
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      margin-bottom: 12px;
      min-height: 39px;
    }

    .poster-meta {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: rgba(255, 255, 255, 0.5);

        .el-icon {
          color: #e11d48;
          font-size: 14px;
        }
      }
    }
  }
}

// 展开卡片
.expand-card {
  position: absolute;
  inset: 0;
  background: linear-gradient(180deg, rgba(26, 26, 46, 0.95) 0%, rgba(15, 15, 26, 0.98) 100%);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 20;

  .expand-content {
    padding: 24px;
    text-align: center;

    .expand-title {
      font-size: 18px;
      font-weight: 700;
      color: #fff;
      margin-bottom: 12px;
    }

    .expand-desc {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.7);
      line-height: 1.6;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .expand-stats {
      display: flex;
      justify-content: center;
      gap: 20px;
      margin-bottom: 20px;

      span {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 13px;
        color: rgba(255, 255, 255, 0.6);

        .el-icon {
          color: #fcd34d;
        }
      }
    }

    .expand-btn {
      background: linear-gradient(135deg, #e11d48 0%, #9f1239 100%);
      border: none;
      padding: 12px 32px;
      border-radius: 30px;
      font-weight: 600;
      font-size: 14px;
      transition: all 0.3s ease;

      &:hover {
        transform: scale(1.05);
        box-shadow: 0 10px 30px rgba(225, 29, 72, 0.5);
      }
    }
  }
}

// 展开动画
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
}

.expand-enter-from,
.expand-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

// ============================================
// 分页
// ============================================
.pagination-wrapper {
  display: flex;
  justify-content: center;
  padding: 20px 0;

  :deep(.el-pagination) {
    --el-pagination-bg-color: rgba(255, 255, 255, 0.1);
    --el-pagination-text-color: rgba(255, 255, 255, 0.7);
    --el-pagination-button-disabled-bg-color: rgba(255, 255, 255, 0.05);

    .el-pager li {
      background: rgba(255, 255, 255, 0.1);
      color: rgba(255, 255, 255, 0.7);
      border-radius: 8px;

      &.is-active {
        background: linear-gradient(135deg, #e11d48, #f97316);
        color: #fff;
      }
    }

    .btn-prev, .btn-next {
      background: rgba(255, 255, 255, 0.1);
      color: rgba(255, 255, 255, 0.7);
      border-radius: 8px;
    }
  }
}

// ============================================
// 空状态
// ============================================
:deep(.el-empty) {
  .el-empty__description {
    color: rgba(255, 255, 255, 0.5);
  }
}

// ============================================
// 动画
// ============================================
@keyframes posterFadeIn {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes glowRotate {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes spotlightMove {
  0%, 100% {
    transform: translate(0, 0);
  }
  25% {
    transform: translate(30px, 20px);
  }
  50% {
    transform: translate(-20px, 30px);
  }
  75% {
    transform: translate(20px, -20px);
  }
}
</style>
