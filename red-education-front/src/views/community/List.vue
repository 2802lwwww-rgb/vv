<template>
  <MainLayout>
    <div class="community-page">
      <!-- 页面头部 - 节日风格 -->
      <div class="page-header festive">
        <div class="header-bg">
          <div class="bg-gradient"></div>
          <div class="floating-lanterns">
            <div v-for="i in 6" :key="i" class="mini-lantern" :style="getLanternStyle(i)">🏮</div>
          </div>
        </div>
        <div class="header-content">
          <div class="header-badge">
            <span>🏮</span>
            <span>点亮祝福 传递力量</span>
          </div>
          <h1 class="page-title">
            <span class="text-gradient">红色社区</span>
          </h1>
          <p class="page-desc">分享学习心得，交流思想感悟</p>
          
          <!-- 视图切换 -->
          <div class="view-toggle">
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'lantern' }"
              @click="viewMode = 'lantern'"
            >
              <span>🏮</span> 灯笼墙
            </button>
            <button 
              class="toggle-btn" 
              :class="{ active: viewMode === 'list' }"
              @click="viewMode = 'list'"
            >
              <span>📋</span> 列表模式
            </button>
          </div>
        </div>
      </div>

      <div class="page-container">
        <!-- 灯笼墙模式 -->
        <div v-if="viewMode === 'lantern'" class="lantern-view">
          <LanternWall 
            :posts="lanternPosts" 
            @select="goToDetail" 
            @create="goToCreate"
          />
        </div>

        <!-- 列表模式 -->
        <div v-else class="list-view">
          <div class="content-layout">
            <!-- 主内容区 -->
            <div class="main-content">
              <!-- 筛选区域 -->
              <div class="filter-section">
                <el-input
                  v-model="filters.keyword"
                  placeholder="搜索帖子..."
                  size="large"
                  class="search-input"
                  clearable
                  @keyup.enter="handleSearch"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <el-select v-model="filters.orderBy" size="large" class="sort-select" @change="handleSearch">
                  <el-option label="最新发布" value="createTime" />
                  <el-option label="最多点赞" value="likeCount" />
                  <el-option label="最多评论" value="commentCount" />
                </el-select>
                <el-button 
                  v-if="userStore.isLoggedIn" 
                  class="create-btn-inline" 
                  size="large"
                  @click="goToCreate"
                >
                  <el-icon><Edit /></el-icon>
                  发帖
                </el-button>
              </div>

              <!-- 帖子列表 -->
              <div class="posts-list" v-loading="loading">
                <div 
                  v-for="(post, index) in posts" 
                  :key="post.id" 
                  class="post-card animate-fade-in-up"
                  :style="{ animationDelay: `${index * 50}ms` }"
                  @click="goToDetail(post.id)"
                >
                  <div class="post-header">
                    <div class="author-info">
                      <el-avatar :size="44" :src="getAvatarUrl(post.avatar)" class="author-avatar">
                        {{ post.username?.charAt(0) || 'U' }}
                      </el-avatar>
                      <div class="author-meta">
                        <span class="author-name">{{ post.nickname || post.username }}</span>
                        <span class="post-time">{{ formatDate(post.createTime) }}</span>
                      </div>
                    </div>
                    <el-tag v-if="post.topic" type="danger" effect="dark" size="small" class="topic-tag">
                      {{ post.topic }}
                    </el-tag>
                  </div>

                  <div class="post-body">
                    <h3 class="post-title">{{ post.title }}</h3>
                    <p class="post-excerpt">{{ post.content }}</p>
                    <div class="post-images" v-if="post.images && post.images.length > 0">
                      <img v-for="(img, idx) in post.images.slice(0, 3)" :key="idx" :src="img" />
                    </div>
                  </div>

                  <div class="post-footer">
                    <div class="post-stats">
                      <span class="stat-item">
                        <el-icon><View /></el-icon>
                        {{ post.viewCount || 0 }}
                      </span>
                      <span class="stat-item">
                        <el-icon><ChatLineSquare /></el-icon>
                        {{ post.commentCount || 0 }}
                      </span>
                      <span class="stat-item liked">
                        <el-icon><Star /></el-icon>
                        {{ post.likeCount || 0 }}
                      </span>
                    </div>
                  </div>
                </div>

                <el-empty v-if="posts.length === 0 && !loading" description="暂无帖子" />
              </div>

              <!-- 分页 -->
              <div class="pagination-wrapper" v-if="total > 0">
                <el-pagination
                  background
                  layout="total, prev, pager, next"
                  :current-page="filters.current"
                  :page-size="filters.size"
                  :total="total"
                  @current-change="handlePageChange"
                />
              </div>
            </div>

            <!-- 侧边栏 -->
            <div class="sidebar">
              <!-- 热门话题 -->
              <div class="sidebar-card">
                <h3 class="sidebar-title">
                  <span class="title-icon">🔥</span>
                  热门话题
                </h3>
                <div class="hot-topics" v-loading="topicsLoading">
                  <div 
                    v-for="topic in hotTopics" 
                    :key="topic.topic"
                    class="topic-item"
                    :class="{ active: filters.topic === topic.topic }"
                    @click="handleTopicClick(topic.topic)"
                  >
                    <span class="topic-name">{{ topic.topic }}</span>
                    <div class="topic-stats">
                      <span><el-icon><Document /></el-icon>{{ topic.postCount }}</span>
                      <span><el-icon><Star /></el-icon>{{ topic.totalLikes }}</span>
                    </div>
                  </div>
                  <el-empty v-if="hotTopics.length === 0 && !topicsLoading" description="暂无话题" :image-size="60" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Edit, Search, View, ChatLineSquare, Star, Document } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import LanternWall from '@/components/creative/LanternWall.vue'
import { useUserStore } from '@/stores/user'
import { getPostList, getHotTopics } from '@/api/community'
import dayjs from 'dayjs'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const topicsLoading = ref(false)
const posts = ref<any[]>([])
const total = ref(0)
const hotTopics = ref<any[]>([])
const viewMode = ref<'lantern' | 'list'>('lantern')

// 灯笼墙数据格式转换
const lanternPosts = computed(() => {
  return posts.value.map(post => ({
    id: post.id,
    title: post.title,
    content: post.content,
    nickname: post.nickname || post.username,
    avatar: post.avatar,
    likes: post.likeCount || 0,
    commentCount: post.commentCount || 0,
    isLit: (post.likeCount || 0) > 10,
    createTime: post.createTime
  }))
})

// 头部灯笼样式
const getLanternStyle = (index: number) => {
  return {
    left: `${10 + (index - 1) * 16}%`,
    top: `${20 + Math.sin(index) * 15}%`,
    animationDelay: `${index * 0.3}s`
  }
}

// 获取头像URL
const getAvatarUrl = (avatar: string | undefined) => {
  if (!avatar) return undefined
  if (avatar.startsWith('http')) return avatar
  return `${import.meta.env.VITE_API_BASE_URL}${avatar}`
}

const filters = reactive({
  current: 1,
  size: 10,
  keyword: '',
  topic: '',
  orderBy: 'createTime'
})

const fetchPosts = async () => {
  loading.value = true
  try {
    const params = { 
      current: filters.current,
      size: filters.size,
      keyword: filters.keyword || undefined,
      topic: filters.topic || undefined,
      orderBy: filters.orderBy
    }
    const res = await getPostList(params)
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

const handleSearch = () => {
  filters.current = 1
  fetchPosts()
}

const handlePageChange = (page: number) => {
  filters.current = page
  fetchPosts()
}

const goToCreate = () => {
  router.push('/community/create')
}

const goToDetail = (id: number) => {
  router.push(`/community/${id}`)
}

const fetchHotTopics = async () => {
  topicsLoading.value = true
  try {
    const res = await getHotTopics(6)
    if (res.data) {
      hotTopics.value = res.data || []
    }
  } catch (error) {
    console.error('获取热门话题失败', error)
  } finally {
    topicsLoading.value = false
  }
}

const handleTopicClick = (topic: string) => {
  if (filters.topic === topic) {
    filters.topic = ''
  } else {
    filters.topic = topic
  }
  handleSearch()
}

const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')

onMounted(() => {
  fetchPosts()
  fetchHotTopics()
})
</script>

<style scoped lang="scss">
.community-page {
  min-height: calc(100vh - var(--header-height));
  background: linear-gradient(180deg, #1a0a0a 0%, #2a1515 100%);
}

// 页面头部 - 节日风格
.page-header.festive {
  position: relative;
  padding: 80px 20px;
  text-align: center;
  overflow: hidden;

  .header-bg {
    position: absolute;
    inset: 0;
    z-index: 0;

    .bg-gradient {
      position: absolute;
      inset: 0;
      background: linear-gradient(180deg, #2a0a0a 0%, #4a1010 50%, #2a0505 100%);
    }

    .floating-lanterns {
      position: absolute;
      inset: 0;
      
      .mini-lantern {
        position: absolute;
        font-size: 32px;
        animation: lantern-float 4s ease-in-out infinite;
        filter: drop-shadow(0 0 20px rgba(255, 150, 50, 0.5));
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
    background: rgba(220, 38, 38, 0.3);
    backdrop-filter: blur(10px);
    padding: 10px 24px;
    border-radius: 50px;
    font-size: 14px;
    color: #fcd34d;
    margin-bottom: 24px;
    border: 1px solid rgba(220, 38, 38, 0.4);
  }

  .page-title {
    font-size: clamp(36px, 6vw, 56px);
    font-weight: 800;
    margin-bottom: 16px;

    .text-gradient {
      background: linear-gradient(135deg, #fcd34d 0%, #f97316 50%, #dc2626 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .page-desc {
    font-size: 18px;
    color: rgba(255, 255, 255, 0.7);
    margin-bottom: 32px;
  }
}

// 视图切换
.view-toggle {
  display: inline-flex;
  gap: 12px;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);

  .toggle-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    background: transparent;
    border: none;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.6);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      color: rgba(255, 255, 255, 0.9);
    }

    &.active {
      background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
      color: #fff;
      box-shadow: 0 4px 20px rgba(220, 38, 38, 0.4);
    }

    span {
      font-size: 16px;
    }
  }
}

.page-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 40px 20px;
}

// 灯笼视图
.lantern-view {
  // LanternWall组件自带样式
}

// 列表视图
.list-view {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 24px;
  padding: 24px;
}

// 发帖按钮
.create-btn-inline {
  background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%) !important;
  border: none !important;
  color: white !important;
  border-radius: 14px;
}

// 灯笼浮动动画
@keyframes lantern-float {
  0%, 100% {
    transform: translateY(0) rotate(-3deg);
  }
  50% {
    transform: translateY(-15px) rotate(3deg);
  }
}

// 内容布局
.content-layout {
  display: grid;
  grid-template-columns: 1fr 300px;
  gap: 32px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

// 主内容区
.main-content {
  min-width: 0;
}

// 筛选区域
.filter-section {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;

  @media (max-width: 576px) {
    flex-direction: column;
  }

  .search-input {
    flex: 1;

    :deep(.el-input__wrapper) {
      border-radius: var(--radius-lg);
      box-shadow: var(--shadow-sm);
    }
  }

  .sort-select {
    width: 150px;

    :deep(.el-input__wrapper) {
      border-radius: var(--radius-lg);
    }
  }
}

// 帖子列表
.posts-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 32px;
}

// 帖子卡片
.post-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  cursor: pointer;
  border: 1px solid var(--color-border);
  transition: all var(--transition-base);

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-lg);
    border-color: var(--color-success);
  }

  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 16px;

    .author-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .author-avatar {
        background: linear-gradient(135deg, #22C55E 0%, #15803D 100%);
        color: white;
        font-weight: var(--font-weight-bold);
      }

      .author-meta {
        display: flex;
        flex-direction: column;

        .author-name {
          font-weight: var(--font-weight-medium);
          color: var(--color-text-primary);
        }

        .post-time {
          font-size: var(--font-size-xs);
          color: var(--color-text-muted);
        }
      }
    }

    .topic-tag {
      background: var(--color-primary-gradient);
      border: none;
    }
  }

  .post-body {
    .post-title {
      font-size: var(--font-size-lg);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
      margin-bottom: 12px;
    }

    .post-excerpt {
      font-size: var(--font-size-sm);
      color: var(--color-text-secondary);
      line-height: var(--line-height-relaxed);
      margin-bottom: 16px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
    }

    .post-images {
      display: flex;
      gap: 8px;
      margin-bottom: 16px;

      img {
        width: 100px;
        height: 100px;
        object-fit: cover;
        border-radius: var(--radius-md);
      }
    }
  }

  .post-footer {
    .post-stats {
      display: flex;
      gap: 24px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);

        &.liked {
          color: var(--color-warning);
        }
      }
    }
  }
}

// 侧边栏
.sidebar {
  @media (max-width: 992px) {
    display: none;
  }
}

.sidebar-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-border);

  .sidebar-title {
    font-size: var(--font-size-base);
    font-weight: var(--font-weight-bold);
    color: var(--color-text-primary);
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 8px;

    .title-icon {
      font-size: 18px;
    }
  }

  .hot-topics {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .topic-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 16px;
      border-radius: var(--radius-lg);
      background: var(--color-bg-tertiary);
      cursor: pointer;
      transition: all var(--transition-fast);

      &:hover, &.active {
        background: linear-gradient(135deg, rgba(34, 197, 94, 0.1) 0%, rgba(21, 128, 61, 0.1) 100%);
        
        .topic-name {
          color: var(--color-success);
        }
      }

      &.active {
        border: 1px solid var(--color-success);
      }

      .topic-name {
        font-size: var(--font-size-sm);
        font-weight: var(--font-weight-medium);
        color: var(--color-text-primary);
      }

      .topic-stats {
        display: flex;
        gap: 12px;
        font-size: var(--font-size-xs);
        color: var(--color-text-muted);

        span {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }
    }
  }
}

// 分页
.pagination-wrapper {
  display: flex;
  justify-content: center;
}

// 动画
.animate-fade-in-up {
  opacity: 0;
  animation: fadeInUp 0.5s ease forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
