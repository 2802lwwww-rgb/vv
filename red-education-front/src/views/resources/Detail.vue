<template>
  <MainLayout>
    <div class="resource-detail-page" v-loading="loading">
      <template v-if="resource">
        <!-- 资源头部 -->
        <div class="resource-header">
          <div class="header-bg">
            <img :src="getCoverUrl(resource.coverImage)" :alt="resource.title" />
            <div class="header-overlay"></div>
          </div>
          <div class="header-content">
            <el-button class="back-btn" @click="$router.back()">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <div class="resource-info-header">
              <div class="resource-tags">
                <el-tag effect="dark" class="category-tag">{{ resource.categoryName }}</el-tag>
                <el-tag :type="getStatusType(resource.status)" effect="dark" size="small">
                  {{ getStatusText(resource.status) }}
                </el-tag>
              </div>
              <h1 class="resource-title">{{ resource.title }}</h1>
              <div class="resource-meta">
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ resource.viewCount || 0 }} 浏览
                </span>
                <span class="meta-item">
                  <el-icon><Download /></el-icon>
                  {{ resource.downloadCount || 0 }} 下载
                </span>
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ resource.uploaderName }}
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="page-container">
          <!-- 文件信息 -->
          <div class="section-card info-card">
            <h3 class="section-title">文件信息</h3>
            <div class="info-list">
              <div class="info-item">
                <span class="info-label">文件名</span>
                <span class="info-value">{{ resource.fileUrl ? resource.fileUrl.split('/').pop() : '未知' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">文件大小</span>
                <span class="info-value">{{ formatSize(resource.fileSize) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">上传时间</span>
                <span class="info-value">{{ formatDate(resource.createTime) }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">上传者</span>
                <span class="info-value">{{ resource.uploaderName }}</span>
              </div>
            </div>
          </div>

          <!-- 资源描述 -->
          <div class="section-card">
            <h3 class="section-title">
              <el-icon><List /></el-icon>
              资源描述
            </h3>
            <p class="resource-desc">{{ resource.description || '暂无描述' }}</p>
          </div>

          <!-- 下载按钮 -->
          <div class="section-card download-card" v-if="resource.fileUrl">
            <el-button type="primary" size="large" class="download-btn" @click="handleDownload" :loading="downloading">
              <el-icon><Download /></el-icon>
              下载资源
            </el-button>
          </div>

          <!-- 资源预览 -->
          <div class="section-card preview-card">
            <div class="preview-header">
              <h3 class="section-title">
                <el-icon><Picture /></el-icon>
                资源预览
              </h3>
              <el-button v-if="resource.fileUrl" size="small" @click="openFullScreen">
                <el-icon><FullScreen /></el-icon>
                全屏
              </el-button>
            </div>
            <div class="resource-preview">
              <img v-if="isImage" :src="getFullUrl(resource.fileUrl)" :alt="resource.title" />
              <video v-else-if="isVideo" :src="getFullUrl(resource.fileUrl)" controls></video>
              <audio v-else-if="isAudio" :src="getFullUrl(resource.fileUrl)" controls class="audio-player"></audio>
              <div v-else-if="isPdf" class="pdf-container">
                <VuePdfEmbed :source="getFullUrl(resource.fileUrl)" />
              </div>
              <div v-else class="no-preview">
                <div class="no-preview-icon">
                  <el-icon :size="48"><Document /></el-icon>
                </div>
                <p>该文件类型不支持在线预览</p>
                <el-button type="primary" @click="handleDownload">
                  <el-icon><Download /></el-icon>
                  下载查看
                </el-button>
              </div>
            </div>
          </div>

          <!-- 相关推荐 -->
          <div class="section-card" v-if="relatedResources.length > 0">
            <h3 class="section-title">
              <el-icon><Collection /></el-icon>
              相关推荐
            </h3>
            <div class="related-grid">
              <div 
                v-for="item in relatedResources" 
                :key="item.id" 
                class="related-item"
                @click="goToDetail(item.id)"
              >
                <img :src="getCoverUrl(item.coverImage)" :alt="item.title" />
                <h4>{{ item.title }}</h4>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, View, Download, User, Document, Picture, List, Collection, FullScreen
} from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getResourceDetail, getResourceList, downloadResource } from '@/api/resource'
import dayjs from 'dayjs'
import VuePdfEmbed from 'vue-pdf-embed'

interface Resource {
  id: number
  title: string
  description: string
  coverImage: string
  fileUrl: string
  fileSize: number
  uploaderName: string
  categoryName: string
  categoryId: number
  viewCount: number
  downloadCount: number
  status: number
  createTime: string
}

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const downloading = ref(false)
const resource = ref<Resource | null>(null)
const relatedResources = ref<Resource[]>([])

const resourceId = computed(() => Number(route.params.id))

const isImage = computed(() => {
  if (!resource.value?.fileUrl) return false
  const ext = resource.value.fileUrl.split('.').pop()?.toLowerCase() || ''
  return ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext)
})

const isVideo = computed(() => {
  if (!resource.value?.fileUrl) return false
  const ext = resource.value.fileUrl.split('.').pop()?.toLowerCase() || ''
  return ['mp4', 'webm', 'ogg'].includes(ext)
})

const isAudio = computed(() => {
  if (!resource.value?.fileUrl) return false
  const ext = resource.value.fileUrl.split('.').pop()?.toLowerCase() || ''
  return ['mp3', 'wav', 'ogg'].includes(ext)
})

const isPdf = computed(() => {
  if (!resource.value?.fileUrl) return false
  return resource.value.fileUrl.toLowerCase().endsWith('.pdf')
})




const fetchResourceDetail = async () => {
  loading.value = true
  try {
    const res = await getResourceDetail(resourceId.value)
    if (res.data) {
      resource.value = res.data
      fetchRelatedResources()
    }
  } catch (error) {
    ElMessage.error('获取资源详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const fetchRelatedResources = async () => {
  if (!resource.value?.categoryId) return
  try {
    const res = await getResourceList({
      page: 1,
      pageSize: 4,
      categoryId: resource.value.categoryId
    })
    if (res.data?.records) {
      relatedResources.value = res.data.records.filter((item: any) => item.id !== resourceId.value)
    }
  } catch (error) {
    console.error('获取相关推荐失败', error)
  }
}

const goToDetail = (id: number) => {
  router.push(`/resources/${id}`)
  fetchResourceDetail()
}

const handleDownload = async () => {
  if (!resource.value?.fileUrl) return
  downloading.value = true
  try {
    const res = await downloadResource(resourceId.value)
    if (res.data) {
      // 更新本地下载计数
      if (resource.value) {
        resource.value.downloadCount = (resource.value.downloadCount || 0) + 1
      }
      // 创建下载链接
      const fileUrl = getFullUrl(res.data)
      const fileName = res.data.split('/').pop() || resource.value?.title || 'download'
      
      // 使用 fetch 获取文件并触发下载
      const response = await fetch(fileUrl)
      const blob = await response.blob()
      const url = window.URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = fileName
      document.body.appendChild(a)
      a.click()
      window.URL.revokeObjectURL(url)
      document.body.removeChild(a)
      
      ElMessage.success('下载成功')
    }
  } catch (error) {
    console.error('下载失败', error)
    ElMessage.error('下载失败')
  } finally {
    downloading.value = false
  }
}

const openFullScreen = () => {
  if (!resource.value?.fileUrl) return
  window.open(getFullUrl(resource.value.fileUrl), '_blank')
}

const formatDate = (date: string | undefined) => {
  if (!date) return ''
  return dayjs(date).format('YYYY-MM-DD')
}

const getFullUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `/api${url}`
}

const getCoverUrl = (url: string) => {
  if (!url) return 'https://picsum.photos/400/300?random=' + Math.random()
  if (url.startsWith('http')) return url
  return '/api' + url
}

const formatSize = (bytes: number | undefined) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i]
}

const getStatusType = (status: number | undefined) => {
  if (status === undefined) return 'info'
  const types: Record<number, string> = { 0: 'warning', 1: 'success', 2: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status: number | undefined) => {
  if (status === undefined) return '未知'
  const texts: Record<number, string> = { 0: '待审核', 1: '已发布', 2: '未通过' }
  return texts[status] || '未知'
}

onMounted(() => {
  fetchResourceDetail()
})
</script>

<style scoped lang="scss">
.resource-detail-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

// 资源头部
.resource-header {
  position: relative;
  height: 280px;
  overflow: hidden;

  .header-bg {
    position: absolute;
    inset: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      filter: blur(2px);
    }

    .header-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(180deg, rgba(0, 0, 0, 0.4) 0%, rgba(0, 0, 0, 0.8) 100%);
    }
  }

  .header-content {
    position: relative;
    z-index: 1;
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .back-btn {
    align-self: flex-start;
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: white;
    backdrop-filter: blur(10px);

    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }

  .resource-info-header {
    margin-top: auto;
    color: white;

    .resource-tags {
      display: flex;
      gap: 8px;
      margin-bottom: 12px;

      .category-tag {
        background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
        border: none;
        color: var(--color-secondary);
      }
    }

    .resource-title {
      font-size: var(--font-size-2xl);
      font-weight: var(--font-weight-bold);
      margin-bottom: 16px;
    }

    .resource-meta {
      display: flex;
      gap: 24px;
      flex-wrap: wrap;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: var(--font-size-sm);
        opacity: 0.9;
      }
    }
  }
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

// 内容布局
.content-layout {
  display: grid;
  grid-template-columns: 350px 1fr;
  gap: 24px;
  margin-bottom: 24px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

.left-content {
  display: flex;
  flex-direction: column;
  gap: 24px;

  .section-card {
    margin-bottom: 0;
  }
}

.right-content {
  .section-card {
    margin-bottom: 0;
    height: 100%;
  }
}

.section-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-border);
  margin-bottom: 24px;

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

// 预览卡片
.preview-card {
  .preview-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .section-title {
      margin-bottom: 0;
    }
  }

  .resource-preview {
    min-height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--color-bg-tertiary);
    border-radius: var(--radius-lg);
    overflow: hidden;

    img, video {
      max-width: 100%;
      max-height: 500px;
      border-radius: var(--radius-md);
    }

    .audio-player {
      width: 100%;
      max-width: 400px;
    }

    .pdf-container {
      width: 100%;
      height: 600px;
      overflow-y: auto;
    }

    .no-preview {
      text-align: center;
      padding: 60px 20px;

      .no-preview-icon {
        width: 80px;
        height: 80px;
        background: var(--color-bg-primary);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin: 0 auto 16px;
        color: var(--color-text-muted);
      }

      p {
        color: var(--color-text-muted);
        margin-bottom: 16px;
      }
    }
  }
}

.resource-desc {
  color: var(--color-text-secondary);
  line-height: var(--line-height-relaxed);
}

// 相关推荐
.related-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  @media (max-width: 768px) {
    grid-template-columns: repeat(2, 1fr);
  }

  .related-item {
    cursor: pointer;
    border-radius: var(--radius-lg);
    overflow: hidden;
    transition: all var(--transition-base);

    &:hover {
      transform: translateY(-4px);
      box-shadow: var(--shadow-lg);
    }

    img {
      width: 100%;
      height: 100px;
      object-fit: cover;
    }

    h4 {
      padding: 12px;
      font-size: var(--font-size-sm);
      font-weight: var(--font-weight-medium);
      color: var(--color-text-primary);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      background: var(--color-bg-tertiary);
    }
  }
}

// 文件信息卡片
.info-card {
  .info-list {
    display: flex;
    flex-wrap: wrap;
    gap: 16px 32px;

    .info-item {
      display: flex;
      align-items: center;
      gap: 8px;

      .info-label {
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);
      }

      .info-value {
        font-size: var(--font-size-sm);
        color: var(--color-text-primary);
        font-weight: var(--font-weight-medium);
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

// 下载卡片
.download-card {
  display: flex;
  align-items: center;
  justify-content: center;

  .download-btn {
    min-width: 180px;
    height: 48px;
    font-size: var(--font-size-base);
    font-weight: var(--font-weight-bold);
    background: linear-gradient(135deg, #C62828 0%, #8E0000 100%);
    border: none;
    
    &:hover {
      background: linear-gradient(135deg, #D32F2F 0%, #A50000 100%);
      transform: translateY(-2px);
      box-shadow: var(--shadow-lg);
    }
  }
}
</style>
