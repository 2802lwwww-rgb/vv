<template>
  <MainLayout>
    <div class="resources-page">
      <!-- 页面头部 - 现代化设计 -->
      <div class="page-header">
        <div class="header-bg">
          <div class="bg-pattern"></div>
          <div class="bg-glow"></div>
          <div class="floating-shapes">
            <div class="shape shape-1"></div>
            <div class="shape shape-2"></div>
            <div class="shape shape-3"></div>
          </div>
        </div>
        <div class="header-content">
          <div class="header-badge">
            <span>📚</span>
            <span>探索红色文献</span>
          </div>
          <h1 class="page-title">
            <span class="text-gradient">红色资源</span>
          </h1>
          <p class="page-desc">探索丰富的红色文献资料</p>
        </div>
        <div class="header-rings">
          <div class="ring ring-1"></div>
          <div class="ring ring-2"></div>
        </div>
      </div>

      <div class="page-container">
        <!-- 分类标签筛选 -->
        <div class="category-tabs">
          <div 
            class="category-tab" 
            :class="{ active: !filters.categoryId }"
            @click="handleCategoryChange(null)"
          >
            全部
          </div>
          <div 
            v-for="category in categories" 
            :key="category.id"
            class="category-tab"
            :class="{ active: filters.categoryId === category.id }"
            @click="handleCategoryChange(category.id)"
          >
            {{ category.name }}
          </div>
        </div>

        <!-- 筛选区域 - 玻璃效果 -->
        <div class="filter-section glass-filter">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索资源..."
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
            <el-option label="最新上传" value="createTime" />
            <el-option label="最多下载" value="downloadCount" />
            <el-option label="最多浏览" value="viewCount" />
          </el-select>
          
          <!-- 视图切换 -->
          <div class="view-toggle">
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'grid' }"
              @click="viewMode = 'grid'"
              title="卡片模式"
            >
              📊
            </button>
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'bookshelf' }"
              @click="viewMode = 'bookshelf'"
              title="书架模式"
            >
              📚
            </button>
          </div>
          
          <el-button 
            type="primary" 
            size="large" 
            class="upload-btn"
            @click="goToUpload"
          >
            <el-icon><Upload /></el-icon>
            上传资源
          </el-button>
        </div>

        <!-- 书架模式 -->
        <div v-if="viewMode === 'bookshelf'" v-loading="loading">
          <Bookshelf 
            :resources="resources" 
            :books-per-row="5"
            @select="goToDetail($event.id)"
          />
        </div>

        <!-- 卡片模式 -->
        <div class="resources-grid" v-loading="loading" v-if="viewMode === 'grid'">
          <div 
            v-for="(resource, index) in resources" 
            :key="resource.id" 
            class="resource-card-wrapper"
          >
            <div 
              class="resource-card animate-fade-in-up"
              :style="{ animationDelay: `${index * 60}ms` }"
              @click="goToDetail(resource.id)"
            >
              <!-- 封面图片区域 -->
              <div class="resource-cover">
                <img 
                  :src="getImageUrl(resource.coverImage)" 
                  :alt="resource.title"
                  class="cover-image"
                />
                <div class="cover-overlay"></div>
                <el-tag type="danger" effect="dark" size="small" class="type-tag">
                  {{ getCategoryName(resource.categoryId) }}
                </el-tag>
              </div>

              <!-- 内容区域 -->
              <div class="resource-content">
                <h3 class="resource-title">{{ resource.title }}</h3>
                <p class="resource-desc">{{ resource.description }}</p>

                <div class="resource-meta">
                  <div class="meta-item">
                    <el-icon><View /></el-icon>
                    <span>{{ resource.viewCount || 0 }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Download /></el-icon>
                    <span>{{ resource.downloadCount || 0 }}</span>
                  </div>
                  <div class="meta-item">
                    <el-icon><Calendar /></el-icon>
                    <span>{{ formatDate(resource.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-if="resources.length === 0 && !loading" description="暂无资源" />

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
import { Search, View, Download, Calendar, Upload } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import Bookshelf from '@/components/creative/Bookshelf.vue'
import request from '@/utils/request'
import dayjs from 'dayjs'

interface Resource {
  id: number
  title: string
  description: string
  fileUrl: string
  coverImage: string
  categoryId: number
  viewCount: number
  downloadCount: number
  createTime: string
}

interface Category {
  id: number
  name: string
}

const router = useRouter()
const loading = ref(false)
const resources = ref<Resource[]>([])
const categories = ref<Category[]>([])
const total = ref(0)
const viewMode = ref<'grid' | 'bookshelf'>('grid')

const filters = reactive({
  page: 1,
  pageSize: 12,
  keyword: '',
  orderBy: 'createTime',
  categoryId: null as number | null
})

// 获取分类列表
const fetchCategories = async () => {
  try {
    const res: any = await request.get('/resource/categories')
    if (res.code === 200) {
      categories.value = res.data || []
    }
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

const fetchResources = async () => {
  loading.value = true
  try {
    const res: any = await request.get('/resource/list', {
      params: {
        current: filters.page,
        size: filters.pageSize,
        keyword: filters.keyword || undefined,
        orderBy: filters.orderBy,
        categoryId: filters.categoryId || undefined
      }
    })
    if (res.code === 200) {
      resources.value = res.data.records || []
      total.value = res.data.total || 0
      // 调试：查看返回的数据结构
      console.log('Resources data:', res.data.records)
      if (res.data.records && res.data.records.length > 0) {
        console.log('First resource coverImage:', res.data.records[0].coverImage)
      }
    }
  } catch (error) {
    ElMessage.error('获取资源列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  filters.page = 1
  fetchResources()
}

const handleCategoryChange = (categoryId: number | null) => {
  filters.categoryId = categoryId
  filters.page = 1
  fetchResources()
}

const handlePageChange = (page: number) => {
  filters.page = page
  fetchResources()
}

const goToDetail = (id: number) => {
  router.push(`/resources/${id}`)
}

const goToUpload = () => {
  router.push('/resources/upload')
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD')

const getCategoryName = (categoryId: number) => {
  const category = categories.value.find(c => c.id === categoryId)
  return category ? category.name : '未分类'
}

const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  // 使用 API base URL 前缀来正确路由图片请求
  return `${import.meta.env.VITE_API_BASE_URL}${url}`
}

onMounted(() => {
  fetchCategories()
  fetchResources()
})
</script>

<style scoped lang="scss">
.resources-page {
  min-height: calc(100vh - var(--header-height));
  background: linear-gradient(180deg, #FEF2F2 0%, var(--color-bg-secondary) 100%);
}

// ============================================
// 页面头部 - 现代化设计
// ============================================
.page-header {
  position: relative;
  padding: 80px 20px;
  text-align: center;
  overflow: hidden;

  .header-bg {
    position: absolute;
    inset: 0;
    z-index: 0;

    .bg-pattern {
      position: absolute;
      inset: 0;
      background: linear-gradient(135deg, #DC2626 0%, #991B1B 100%);
    }

    .bg-glow {
      position: absolute;
      top: -30%;
      left: 50%;
      transform: translateX(-50%);
      width: 800px;
      height: 800px;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, transparent 70%);
      animation: breathe 4s ease-in-out infinite;
    }

    .floating-shapes {
      position: absolute;
      inset: 0;
      pointer-events: none;

      .shape {
        position: absolute;
        border-radius: 50%;
        background: rgba(255, 255, 255, 0.1);

        &.shape-1 {
          width: 100px;
          height: 100px;
          top: 20%;
          left: 10%;
          animation: float 8s ease-in-out infinite;
        }

        &.shape-2 {
          width: 60px;
          height: 60px;
          top: 60%;
          right: 15%;
          animation: float 6s ease-in-out infinite 1s;
        }

        &.shape-3 {
          width: 80px;
          height: 80px;
          bottom: 20%;
          left: 25%;
          animation: float 7s ease-in-out infinite 0.5s;
        }
      }
    }
  }

  .header-rings {
    position: absolute;
    inset: 0;
    pointer-events: none;
    overflow: hidden;

    .ring {
      position: absolute;
      border: 2px solid rgba(255, 255, 255, 0.15);
      border-radius: 50%;

      &.ring-1 {
        width: 400px;
        height: 400px;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        animation: ringPulse 4s ease-in-out infinite;
      }

      &.ring-2 {
        width: 600px;
        height: 600px;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        animation: ringPulse 4s ease-in-out infinite 0.5s;
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
    background: rgba(255, 255, 255, 0.2);
    backdrop-filter: blur(10px);
    padding: 8px 20px;
    border-radius: 50px;
    font-size: 14px;
    color: white;
    margin-bottom: 20px;
    border: 1px solid rgba(255, 255, 255, 0.2);
  }

  .page-title {
    font-size: clamp(36px, 6vw, 56px);
    font-weight: 800;
    margin-bottom: 16px;
    color: white;

    .text-gradient {
      background: linear-gradient(135deg, #FFFFFF 0%, #FCD34D 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .page-desc {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.9);
    margin-bottom: 28px;
  }
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

// ============================================
// 分类标签
// ============================================
.category-tabs {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 24px;

  .category-tab {
    padding: 10px 24px;
    background: white;
    border: 2px solid transparent;
    border-radius: 50px;
    font-size: 14px;
    font-weight: 500;
    color: var(--color-text-secondary);
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    &:hover {
      color: #DC2626;
      border-color: #DC2626;
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(220, 38, 38, 0.15);
    }

    &.active {
      background: linear-gradient(135deg, #DC2626 0%, #B91C1C 100%);
      color: white;
      border-color: transparent;
      box-shadow: 0 4px 15px rgba(220, 38, 38, 0.3);
    }
  }
}

// ============================================
// 筛选区域 - 玻璃效果
// ============================================
.filter-section {
  margin-bottom: 40px;

  &.glass-filter {
    display: inline-flex;
    gap: 16px;
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
        border-color: #DC2626;
        box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
      }
    }
  }

  .sort-select {
    width: 140px;

    :deep(.el-select__wrapper) {
      border-radius: 14px;
      box-shadow: none;
      border: 1px solid var(--color-border);
      background: white;

      &:hover, &.is-focused {
        border-color: #DC2626;
        box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
      }
    }
  }

  .upload-btn {
    border-radius: 14px;
    background: linear-gradient(135deg, #DC2626 0%, #B91C1C 100%);
    border: none;
    font-weight: 600;
    padding: 0 24px;

    &:hover {
      background: linear-gradient(135deg, #EF4444 0%, #DC2626 100%);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(220, 38, 38, 0.3);
    }
  }
  
  .view-toggle {
    display: flex;
    gap: 4px;
    background: rgba(0, 0, 0, 0.05);
    border-radius: 12px;
    padding: 4px;
    
    .toggle-btn {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      border: none;
      background: transparent;
      border-radius: 10px;
      font-size: 18px;
      cursor: pointer;
      transition: all 0.3s ease;
      
      &:hover {
        background: rgba(0, 0, 0, 0.1);
      }
      
      &.active {
        background: white;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }
  }
}

// ============================================
// 资源网格 - 3D卡片
// ============================================
.resources-grid {
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

.resource-card-wrapper {
  perspective: 1000px;
}

.resource-card {
  position: relative;
  background: white;
  border-radius: 16px;
  cursor: pointer;
  border: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden;

  &:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 40px rgba(220, 38, 38, 0.15);

    .cover-image {
      transform: scale(1.05);
    }

    .cover-overlay {
      opacity: 1;
    }
  }

  .resource-cover {
    position: relative;
    height: 180px;
    overflow: hidden;
    background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);

    .cover-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }

    .cover-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(to top, rgba(0,0,0,0.5) 0%, transparent 50%);
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    .type-tag {
      position: absolute;
      top: 12px;
      right: 12px;
      border-radius: 20px;
      padding: 6px 14px;
      font-weight: 600;
      backdrop-filter: blur(8px);
    }
  }

  .resource-content {
    padding: 20px;

    .resource-title {
      font-size: 16px;
      font-weight: 700;
      color: var(--color-text-primary);
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .resource-desc {
      font-size: 13px;
      color: var(--color-text-muted);
      line-height: 1.5;
      margin-bottom: 16px;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      min-height: 40px;
    }

    .resource-meta {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 12px;
        color: var(--color-text-muted);

        .el-icon {
          color: #DC2626;
          font-size: 14px;
        }
      }
    }
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
</style>
