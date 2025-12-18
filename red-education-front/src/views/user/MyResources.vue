<template>
  <MainLayout>
    <div class="my-resources-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-top">
            <div class="title-section">
              <h1 class="page-title">
                <span class="title-icon">📁</span>
                我的资源
              </h1>
              <p class="page-desc">管理我上传的学习资源</p>
            </div>
            <el-button class="upload-btn" size="large" @click="goToUpload">
              <el-icon><Upload /></el-icon>
              上传资源
            </el-button>
          </div>
        </div>
      </div>

      <div class="page-container">
        <div class="section-card" v-loading="loading">
          <div class="resources-list">
            <div 
              v-for="(resource, index) in resources" 
              :key="resource.id" 
              class="resource-item animate-fade-in-up"
              :style="{ animationDelay: `${index * 50}ms` }"
            >
              <div class="resource-cover">
                <img v-if="resource.coverImage" :src="getFullUrl(resource.coverImage)" alt="封面" />
                <div v-else class="no-cover">
                  <el-icon :size="32"><Picture /></el-icon>
                </div>
              </div>
              
              <div class="resource-info">
                <div class="resource-header">
                  <div class="resource-title-row">
                    <h3 class="resource-title" @click="goToDetail(resource.id)">{{ resource.title }}</h3>
                    <el-tag :type="getStatusType(resource.status)" size="small" effect="dark">
                      {{ getStatusText(resource.status) }}
                    </el-tag>
                  </div>
                  <el-button text type="primary" @click="goToDetail(resource.id)">
                    <el-icon><View /></el-icon> 查看
                  </el-button>
                </div>

                <el-alert
                  v-if="resource.status === 2 && resource.auditComment"
                  :title="`驳回原因：${resource.auditComment}`"
                  type="error"
                  :closable="false"
                  show-icon
                  class="reject-alert"
                />

                <p class="resource-excerpt">{{ resource.description }}</p>

                <div class="resource-footer">
                  <div class="resource-stats">
                    <span><el-icon><View /></el-icon> {{ resource.viewCount || 0 }}</span>
                    <span><el-icon><Download /></el-icon> {{ resource.downloadCount || 0 }}</span>
                    <el-tag size="small" class="category-tag">{{ resource.categoryName }}</el-tag>
                  </div>
                  <span class="resource-time">{{ formatDate(resource.createTime) }}</span>
                </div>
              </div>
            </div>

            <el-empty v-if="resources.length === 0 && !loading" description="还没有上传过资源">
              <el-button type="primary" @click="goToUpload">上传第一个资源</el-button>
            </el-empty>
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
import { Upload, View, Download, Picture } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getMyResources } from '@/api/resource'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const resources = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchMyResources = async () => {
  loading.value = true
  try {
    const res = await getMyResources({ current: page.value, size: pageSize.value })
    if (res.data) {
      resources.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取资源列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage: number) => { page.value = newPage; fetchMyResources() }
const goToUpload = () => router.push('/resources/upload')
const goToDetail = (id: number) => router.push(`/resources/${id}`)
const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')

const getFullUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return '/api' + url
}

const getStatusText = (status: number) => ({ 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '未知')
const getStatusType = (status: number) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info')

onMounted(() => fetchMyResources())
</script>

<style scoped lang="scss">
.my-resources-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
  padding: 60px 20px;
  color: var(--color-secondary);

  .header-content {
    max-width: 900px;
    margin: 0 auto;
  }

  .header-top {
    display: flex;
    justify-content: space-between;
    align-items: center;

    @media (max-width: 576px) {
      flex-direction: column;
      gap: 20px;
      text-align: center;
    }
  }

  .page-title {
    font-size: var(--font-size-4xl);
    font-weight: var(--font-weight-bold);
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 12px;
    .title-icon { font-size: 40px; }
  }

  .page-desc { font-size: var(--font-size-base); opacity: 0.85; }

  .upload-btn {
    background: var(--color-secondary);
    color: #F59E0B;
    border: none;
    font-weight: var(--font-weight-medium);

    &:hover { opacity: 0.9; }
  }
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-border);
}

.resources-list {
  .resource-item {
    display: flex;
    gap: 20px;
    padding: 24px;
    border-radius: var(--radius-lg);
    border: 1px solid var(--color-border-light);
    margin-bottom: 16px;
    transition: all var(--transition-base);

    &:hover {
      border-color: var(--color-warning);
      box-shadow: var(--shadow-sm);
    }

    @media (max-width: 576px) {
      flex-direction: column;
    }

    .resource-cover {
      flex-shrink: 0;
      width: 140px;
      height: 100px;
      border-radius: var(--radius-lg);
      overflow: hidden;
      background: var(--color-bg-tertiary);

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .no-cover {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--color-text-muted);
      }
    }

    .resource-info {
      flex: 1;
      min-width: 0;

      .resource-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 8px;

        @media (max-width: 576px) {
          flex-direction: column;
          gap: 8px;
        }

        .resource-title-row {
          display: flex;
          align-items: center;
          gap: 10px;
          flex-wrap: wrap;
        }

        .resource-title {
          font-size: var(--font-size-base);
          font-weight: var(--font-weight-bold);
          color: var(--color-text-primary);
          margin: 0;
          cursor: pointer;
          transition: color var(--transition-fast);

          &:hover { color: var(--color-warning); }
        }
      }

      .reject-alert {
        margin-bottom: 12px;
        border-radius: var(--radius-md);
      }

      .resource-excerpt {
        font-size: var(--font-size-sm);
        color: var(--color-text-secondary);
        line-height: var(--line-height-relaxed);
        margin-bottom: 12px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .resource-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;

        .resource-stats {
          display: flex;
          gap: 16px;
          font-size: var(--font-size-sm);
          color: var(--color-text-muted);

          span {
            display: flex;
            align-items: center;
            gap: 4px;
          }

          .category-tag {
            background: rgba(255, 217, 61, 0.2);
            color: #D97706;
            border: none;
          }
        }

        .resource-time {
          font-size: var(--font-size-xs);
          color: var(--color-text-muted);
        }
      }
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
