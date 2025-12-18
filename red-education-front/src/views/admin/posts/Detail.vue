<template>
  <AdminLayout>
    <div class="admin-page post-detail-page" v-loading="loading">
      <div class="page-header">
        <el-button @click="$router.back()"><el-icon><ArrowLeft /></el-icon> 返回列表</el-button>
        <el-tag v-if="post" :type="getStatusType(post.status)" effect="dark" size="large" round>{{ getStatusText(post.status) }}</el-tag>
      </div>

      <div class="detail-card" v-if="post">
        <div class="author-section">
          <el-avatar :size="56" class="author-avatar">{{ post.username?.charAt(0) }}</el-avatar>
          <div class="author-meta">
            <span class="author-name">{{ post.username }}</span>
            <span class="post-time">发布于 {{ formatDate(post.createTime) }}</span>
          </div>
        </div>

        <div class="topic-tag" v-if="post.topic">
          <el-tag type="danger" effect="plain" round>{{ post.topic }}</el-tag>
        </div>

        <h1 class="post-title">{{ post.title }}</h1>

        <div class="post-content">
          <p>{{ post.fullContent || post.content }}</p>
        </div>

        <div class="post-images" v-if="post.imageList && post.imageList.length > 0">
          <h4>附带图片</h4>
          <div class="image-grid">
            <el-image v-for="(img, idx) in post.imageList" :key="idx" :src="getImageUrl(img)" :preview-src-list="post.imageList.map((i: string) => getImageUrl(i))" fit="cover" class="post-image" />
          </div>
        </div>

        <div class="post-stats">
          <span><el-icon><View /></el-icon> {{ post.viewCount || 0 }} 浏览</span>
          <span><el-icon><Star /></el-icon> {{ post.likeCount || 0 }} 点赞</span>
          <span><el-icon><ChatLineSquare /></el-icon> {{ post.commentCount || 0 }} 评论</span>
        </div>

        <div class="audit-actions" v-if="post.status === 0">
          <el-button type="success" size="large" @click="handleApprove"><el-icon><Select /></el-icon> 通过审核</el-button>
          <el-button type="danger" size="large" @click="showRejectDialog = true"><el-icon><CloseBold /></el-icon> 拒绝</el-button>
        </div>

        <el-alert v-if="post.status === 2 && post.auditComment" :title="`驳回原因：${post.auditComment}`" type="error" :closable="false" style="margin-top: 24px" />
      </div>

      <el-dialog v-model="showRejectDialog" title="拒绝帖子" width="500px" class="modern-dialog">
        <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请输入拒绝原因" />
        <template #footer>
          <el-button @click="showRejectDialog = false">取消</el-button>
          <el-button type="danger" @click="handleReject">确认拒绝</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, View, Star, ChatLineSquare, Select, CloseBold } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { auditPost } from '@/api/admin'
import request from '@/utils/request'
import dayjs from 'dayjs'

const route = useRoute(); const router = useRouter()
const loading = ref(false); const post = ref<any>(null); const showRejectDialog = ref(false); const rejectReason = ref('')
const postId = Number(route.params.id)

const fetchPostDetail = async () => { loading.value = true; try { const res = await request({ url: `/admin/post/${postId}`, method: 'get' }); if (res.data) post.value = res.data } catch (error: any) { ElMessage.error(error.message || '获取帖子详情失败'); router.back() } finally { loading.value = false } }
const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')
const getStatusType = (status: number) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[status] || 'info')
const getStatusText = (status: number) => ({ 0: '待审核', 1: '已通过', 2: '已驳回' }[status] || '未知')
const getImageUrl = (url: string) => { if (!url) return ''; if (url.startsWith('http')) return url; return (import.meta.env.VITE_API_BASE_URL || '') + url }
const handleApprove = async () => { try { await ElMessageBox.confirm('确认通过此帖子的审核吗？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'success' }); await auditPost(postId, 'APPROVED'); ElMessage.success('审核通过'); router.back() } catch (error: any) { if (error !== 'cancel') ElMessage.error(error.message || '操作失败') } }
const handleReject = async () => { if (!rejectReason.value.trim()) { ElMessage.warning('请输入拒绝原因'); return }; try { await auditPost(postId, 'REJECTED', rejectReason.value); ElMessage.success('已拒绝'); showRejectDialog.value = false; router.back() } catch (error: any) { ElMessage.error(error.message || '操作失败') } }

onMounted(() => fetchPostDetail())
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;
  }

  .detail-card {
    background: white; border-radius: 16px; padding: 32px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .author-section {
      display: flex; align-items: center; gap: 16px; margin-bottom: 24px;
      .author-avatar { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); color: white; font-weight: 600; }
      .author-meta {
        .author-name { display: block; font-size: 18px; font-weight: 600; color: #1f2937; }
        .post-time { font-size: 14px; color: #9ca3af; }
      }
    }

    .topic-tag { margin-bottom: 16px; }
    .post-title { font-size: 28px; font-weight: 700; color: #1f2937; margin: 0 0 24px; }
    .post-content { font-size: 16px; line-height: 1.8; color: #4b5563; margin-bottom: 24px; white-space: pre-wrap; p { margin: 0; } }

    .post-images {
      margin-bottom: 24px;
      h4 { font-size: 14px; color: #6b7280; margin: 0 0 12px; }
      .image-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 12px; }
      .post-image { width: 100%; height: 180px; border-radius: 12px; }
    }

    .post-stats {
      display: flex; gap: 24px; padding: 16px 0; border-top: 1px solid #f3f4f6; border-bottom: 1px solid #f3f4f6; color: #6b7280;
      span { display: flex; align-items: center; gap: 6px; }
    }

    .audit-actions { display: flex; justify-content: center; gap: 16px; margin-top: 32px; }
  }
}
</style>
