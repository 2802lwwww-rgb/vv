<template>
  <MainLayout>
    <div class="my-posts-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="header-top">
            <div class="title-section">
              <h1 class="page-title">
                <span class="title-icon">📝</span>
                我的帖子
              </h1>
              <p class="page-desc">管理我发布的社区帖子</p>
            </div>
            <el-button class="create-btn" size="large" @click="goToCreate">
              <el-icon><Edit /></el-icon>
              发布新帖
            </el-button>
          </div>
        </div>
      </div>

      <div class="page-container">
        <div class="section-card" v-loading="loading">
          <div class="posts-list">
            <div 
              v-for="(post, index) in posts" 
              :key="post.id" 
              class="post-item animate-fade-in-up"
              :style="{ animationDelay: `${index * 50}ms` }"
            >
              <div class="post-header">
                <div class="post-title-row">
                  <h3 class="post-title" @click="goToDetail(post.id)">{{ post.title }}</h3>
                  <el-tag :type="getStatusType(post.status)" size="small" effect="dark">
                    {{ getStatusText(post.status) }}
                  </el-tag>
                </div>
                <div class="post-actions">
                  <el-button text type="primary" @click="goToDetail(post.id)">
                    <el-icon><View /></el-icon> 查看
                  </el-button>
                  <el-button text type="danger" @click="handleDelete(post.id)">
                    <el-icon><Delete /></el-icon> 删除
                  </el-button>
                </div>
              </div>

              <el-alert
                v-if="post.status === 2 && post.auditComment"
                :title="`驳回原因：${post.auditComment}`"
                type="error"
                :closable="false"
                show-icon
                class="reject-alert"
              />

              <p class="post-excerpt">{{ post.content }}</p>

              <div class="post-footer">
                <div class="post-stats">
                  <span><el-icon><View /></el-icon> {{ post.viewCount }}</span>
                  <span><el-icon><ChatLineSquare /></el-icon> {{ post.commentCount }}</span>
                  <span class="like-stat"><el-icon><Star /></el-icon> {{ post.likeCount }}</span>
                </div>
                <span class="post-time">{{ formatDate(post.createTime) }}</span>
              </div>
            </div>

            <el-empty v-if="posts.length === 0 && !loading" description="还没有发布过帖子">
              <el-button type="primary" @click="goToCreate">发布第一篇帖子</el-button>
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, View, Delete, ChatLineSquare, Star } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getMyPosts, deletePost } from '@/api/community'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false)
const posts = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchMyPosts = async () => {
  loading.value = true
  try {
    const res = await getMyPosts({ current: page.value, size: pageSize.value })
    if (res.data) {
      posts.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取帖子列表失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage: number) => { page.value = newPage; fetchMyPosts() }
const goToCreate = () => router.push('/community/create')
const goToDetail = (id: number) => router.push(`/community/${id}`)

const handleDelete = (id: number) => {
  ElMessageBox.confirm('确认删除此帖子吗？删除后无法恢复。', '提示', {
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePost(id)
      ElMessage.success('删除成功')
      fetchMyPosts()
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')
const getStatusText = (status: number) => ({ 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '未知')
const getStatusType = (status: number) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info')

onMounted(() => fetchMyPosts())
</script>

<style scoped lang="scss">
.my-posts-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #22C55E 0%, #15803D 100%);
  padding: 60px 20px;
  color: white;

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

  .page-desc { font-size: var(--font-size-base); opacity: 0.9; }

  .create-btn {
    background: white;
    color: #15803D;
    border: none;
    font-weight: var(--font-weight-medium);

    &:hover { background: rgba(255, 255, 255, 0.9); }
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

.posts-list {
  .post-item {
    padding: 24px;
    border-radius: var(--radius-lg);
    border: 1px solid var(--color-border-light);
    margin-bottom: 16px;
    transition: all var(--transition-base);

    &:hover {
      border-color: var(--color-success);
      box-shadow: var(--shadow-sm);
    }

    .post-header {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      margin-bottom: 12px;

      @media (max-width: 576px) {
        flex-direction: column;
        gap: 12px;
      }

      .post-title-row {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;
      }

      .post-title {
        font-size: var(--font-size-lg);
        font-weight: var(--font-weight-bold);
        color: var(--color-text-primary);
        margin: 0;
        cursor: pointer;
        transition: color var(--transition-fast);

        &:hover { color: var(--color-success); }
      }

      .post-actions {
        display: flex;
        gap: 4px;
      }
    }

    .reject-alert {
      margin-bottom: 12px;
      border-radius: var(--radius-md);
    }

    .post-excerpt {
      font-size: var(--font-size-sm);
      color: var(--color-text-secondary);
      line-height: var(--line-height-relaxed);
      margin-bottom: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .post-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .post-stats {
        display: flex;
        gap: 20px;
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }

        .like-stat { color: var(--color-warning); }
      }

      .post-time {
        font-size: var(--font-size-xs);
        color: var(--color-text-muted);
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
