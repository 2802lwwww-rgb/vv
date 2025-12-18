<template>
  <MainLayout>
    <div class="post-detail-page" v-loading="loading">
      <div class="page-container" v-if="post">
        <!-- 返回按钮 -->
        <el-button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回社区
        </el-button>

        <!-- 帖子内容 -->
        <div class="post-card">
          <div class="post-header">
            <div class="author-info">
              <el-avatar :size="56" :src="getAvatarUrl(post.avatar)" class="author-avatar">
                {{ post.nickname?.charAt(0) || post.username?.charAt(0) }}
              </el-avatar>
              <div class="author-meta">
                <h3 class="author-name">{{ post.nickname || post.username }}</h3>
                <span class="post-time">{{ formatDate(post.createTime) }}</span>
              </div>
            </div>
            <el-button 
              v-if="isAuthor" 
              type="danger" 
              plain 
              size="small"
              @click="handleDelete"
            >
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </div>

          <div class="post-body">
            <div class="post-topic" v-if="post.topic">
              <el-tag type="danger" effect="dark" size="small">{{ post.topic }}</el-tag>
            </div>
            <h1 class="post-title">{{ post.title }}</h1>
            <div class="post-content" v-html="post.content"></div>

            <div class="post-images" v-if="post.imageList && post.imageList.length > 0">
              <el-image
                v-for="(img, idx) in post.imageList"
                :key="idx"
                :src="getImageUrl(img)"
                :preview-src-list="post.imageList.map((i: string) => getImageUrl(i))"
                fit="cover"
                class="post-image"
              />
            </div>
          </div>

          <div class="post-footer">
            <div class="post-stats">
              <span class="stat-item">
                <el-icon><View /></el-icon>
                {{ post.viewCount }} 浏览
              </span>
              <span class="stat-item">
                <el-icon><ChatLineSquare /></el-icon>
                {{ post.commentCount }} 评论
              </span>
            </div>
            <div class="post-actions">
              <el-button
                :type="post.isLiked ? 'primary' : 'default'"
                :class="{ liked: post.isLiked }"
                round
                @click="handleLike"
              >
                <el-icon><Star /></el-icon>
                {{ post.isLiked ? '已点赞' : '点赞' }} ({{ post.likeCount }})
              </el-button>
            </div>
          </div>
        </div>

        <!-- 评论区 -->
        <div class="comments-section">
          <h3 class="section-title">
            <el-icon><ChatLineSquare /></el-icon>
            评论 ({{ comments.length }})
          </h3>

          <!-- 发表评论 -->
          <div class="comment-input" v-if="userStore.isLoggedIn">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              placeholder="发表你的看法..."
              class="comment-textarea"
            />
            <div class="input-actions">
              <el-button type="primary" @click="handleComment" :loading="commenting">
                发表评论
              </el-button>
            </div>
          </div>
          <div class="login-prompt" v-else>
            <el-button type="primary" plain @click="$router.push('/login')">登录后发表评论</el-button>
          </div>

          <!-- 评论列表 -->
          <div class="comments-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <el-avatar :size="44" :src="getAvatarUrl(comment.avatar)" class="comment-avatar">
                {{ comment.nickname?.charAt(0) || comment.username?.charAt(0) }}
              </el-avatar>
              <div class="comment-content">
                <div class="comment-header">
                  <span class="commenter-name">{{ comment.nickname || comment.username }}</span>
                  <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
                </div>
                <p class="comment-text">{{ comment.content }}</p>
                <div class="comment-actions">
                  <el-button text size="small" @click="replyTo(comment)">
                    <el-icon><ChatLineRound /></el-icon>
                    回复
                  </el-button>
                  <el-button
                    v-if="comment.userId === userStore.userInfo?.id"
                    text
                    size="small"
                    type="danger"
                    @click="deleteComment(comment.id)"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </div>
              </div>
            </div>

            <el-empty v-if="comments.length === 0" description="暂无评论，来发表第一条吧~" />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Delete, Star, ChatLineSquare, View, ChatLineRound } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { useUserStore } from '@/stores/user'
import { getPostDetail, deletePost, likePost, unlikePost, getComments, createComment, deleteComment as deleteCommentApi } from '@/api/community'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const commenting = ref(false)
const post = ref<any>(null)
const comments = ref<any[]>([])
const commentContent = ref('')

const postId = computed(() => Number(route.params.id))
const isAuthor = computed(() => post.value?.userId === userStore.userInfo?.id)

const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return (import.meta.env.VITE_API_BASE_URL || '') + url
}

const getAvatarUrl = (avatar: string | undefined) => {
  if (!avatar) return undefined
  if (avatar.startsWith('http')) return avatar
  return `${import.meta.env.VITE_API_BASE_URL}${avatar}`
}

const fetchPostDetail = async () => {
  loading.value = true
  try {
    const res = await getPostDetail(postId.value)
    if (res.data) {
      post.value = res.data
      fetchComments()
    }
  } catch (error) {
    ElMessage.error('获取帖子详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const fetchComments = async () => {
  try {
    const res = await getComments(postId.value, {})
    if (res.data) {
      comments.value = res.data.records || []
    }
  } catch (error) {
    console.error('获取评论失败', error)
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return
  }
  try {
    if (post.value.isLiked) {
      await unlikePost(postId.value)
      post.value.likeCount--
      post.value.isLiked = false
    } else {
      await likePost(postId.value)
      post.value.likeCount++
      post.value.isLiked = true
    }
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

const handleComment = async () => {
  if (!commentContent.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  commenting.value = true
  try {
    await createComment({
      postId: postId.value,
      content: commentContent.value
    })
    ElMessage.success('评论成功')
    commentContent.value = ''
    fetchComments()
    post.value.commentCount++
  } catch (error: any) {
    ElMessage.error(error.message || '评论失败')
  } finally {
    commenting.value = false
  }
}

const replyTo = (comment: any) => {
  commentContent.value = `@${comment.username} `
}

const deleteComment = async (id: number) => {
  ElMessageBox.confirm('确认删除此评论吗？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCommentApi(id)
      ElMessage.success('删除成功')
      fetchComments()
      post.value.commentCount--
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const handleDelete = () => {
  ElMessageBox.confirm('确认删除此帖子吗？', '提示', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deletePost(postId.value)
      ElMessage.success('删除成功')
      router.push('/community')
    } catch (error: any) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')

onMounted(() => {
  fetchPostDetail()
})
</script>

<style scoped lang="scss">
.post-detail-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
  padding: 40px 20px;
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 24px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  
  &:hover {
    border-color: var(--color-primary);
    color: var(--color-primary);
  }
}

// 帖子卡片
.post-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 32px;
  border: 1px solid var(--color-border);
  margin-bottom: 24px;

  .post-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    padding-bottom: 24px;
    border-bottom: 1px solid var(--color-border-light);

    .author-info {
      display: flex;
      align-items: center;
      gap: 16px;

      .author-avatar {
        background: linear-gradient(135deg, #22C55E 0%, #15803D 100%);
        color: white;
        font-weight: var(--font-weight-bold);
        font-size: var(--font-size-lg);
      }

      .author-meta {
        .author-name {
          font-size: var(--font-size-lg);
          font-weight: var(--font-weight-bold);
          color: var(--color-text-primary);
          margin: 0 0 4px 0;
        }

        .post-time {
          font-size: var(--font-size-sm);
          color: var(--color-text-muted);
        }
      }
    }
  }

  .post-body {
    .post-topic {
      margin-bottom: 12px;

      .el-tag {
        background: var(--color-primary-gradient);
        border: none;
      }
    }

    .post-title {
      font-size: var(--font-size-2xl);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
      margin-bottom: 20px;
      line-height: var(--line-height-tight);
    }

    .post-content {
      font-size: var(--font-size-base);
      line-height: var(--line-height-relaxed);
      color: var(--color-text-secondary);
      margin-bottom: 24px;
    }

    .post-images {
      display: grid;
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
      margin-bottom: 24px;

      @media (max-width: 576px) {
        grid-template-columns: repeat(2, 1fr);
      }

      .post-image {
        width: 100%;
        height: 180px;
        border-radius: var(--radius-lg);
        object-fit: cover;
      }
    }
  }

  .post-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding-top: 20px;
    border-top: 1px solid var(--color-border-light);

    .post-stats {
      display: flex;
      gap: 20px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);
      }
    }

    .post-actions {
      .el-button.liked {
        background: var(--color-primary-gradient);
        border: none;
        color: white;
      }
    }
  }
}

// 评论区
.comments-section {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 32px;
  border: 1px solid var(--color-border);

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: var(--font-size-lg);
    font-weight: var(--font-weight-bold);
    color: var(--color-text-primary);
    margin: 0 0 24px 0;
  }

  .comment-input {
    margin-bottom: 32px;

    .comment-textarea {
      margin-bottom: 12px;

      :deep(.el-textarea__inner) {
        border-radius: var(--radius-lg);
        border: 1px solid var(--color-border);

        &:focus {
          border-color: var(--color-primary);
        }
      }
    }

    .input-actions {
      display: flex;
      justify-content: flex-end;
    }
  }

  .login-prompt {
    text-align: center;
    padding: 24px;
    background: var(--color-bg-tertiary);
    border-radius: var(--radius-lg);
    margin-bottom: 24px;
  }

  .comments-list {
    .comment-item {
      display: flex;
      gap: 16px;
      padding: 20px 0;
      border-bottom: 1px solid var(--color-border-light);

      &:last-child {
        border-bottom: none;
      }

      .comment-avatar {
        background: linear-gradient(135deg, #00D9FF 0%, #0077B6 100%);
        color: white;
        font-weight: var(--font-weight-medium);
        flex-shrink: 0;
      }

      .comment-content {
        flex: 1;
        min-width: 0;

        .comment-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .commenter-name {
            font-weight: var(--font-weight-medium);
            color: var(--color-text-primary);
          }

          .comment-time {
            font-size: var(--font-size-xs);
            color: var(--color-text-muted);
          }
        }

        .comment-text {
          color: var(--color-text-secondary);
          line-height: var(--line-height-relaxed);
          margin-bottom: 8px;
        }

        .comment-actions {
          display: flex;
          gap: 8px;
        }
      }
    }
  }
}
</style>
