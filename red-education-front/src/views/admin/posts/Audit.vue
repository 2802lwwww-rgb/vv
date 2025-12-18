<template>
  <AdminLayout>
    <div class="admin-page audit-posts-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><ChatLineSquare /></el-icon> 内容审核</h1>
          <p>审核用户发布的社区帖子</p>
        </div>
      </div>

      <div class="content-card">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange" class="modern-tabs">
          <el-tab-pane label="待审核" name="pending">
            <div class="posts-list" v-loading="loading">
              <div v-for="post in posts" :key="post.id" class="post-item">
                <div class="post-header">
                  <div class="author-info">
                    <el-avatar :size="44" class="author-avatar">{{ post.username?.charAt(0) }}</el-avatar>
                    <div class="author-meta">
                      <span class="author-name">{{ post.username }}</span>
                      <span class="post-time">{{ formatDate(post.createTime) }}</span>
                    </div>
                  </div>
                  <div class="post-actions">
                    <el-button type="primary" @click="viewPostDetail(post)"><el-icon><View /></el-icon> 详情</el-button>
                    <el-button type="success" @click="handleApprove(post)"><el-icon><Select /></el-icon> 通过</el-button>
                    <el-button type="danger" @click="handleReject(post)"><el-icon><CloseBold /></el-icon> 拒绝</el-button>
                  </div>
                </div>
                <h3 class="post-title">{{ post.title }}</h3>
                <div class="post-content"><p>{{ post.content }}</p></div>
                <div class="post-images" v-if="post.images && post.images.length > 0">
                  <el-image v-for="(img, idx) in post.images.slice(0, 3)" :key="idx" :src="img" fit="cover" class="post-img" />
                </div>
              </div>

              <el-empty v-if="posts.length === 0 && !loading" description="暂无待审核内容" />
            </div>

            <div class="pagination-wrapper" v-if="total > 0">
              <el-pagination background layout="total, prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="handlePageChange" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>

      <el-dialog v-model="rejectDialogVisible" title="拒绝原因" width="500px" class="modern-dialog">
        <el-input v-model="rejectReason" type="textarea" :rows="4" placeholder="请输入拒绝原因（必填）" />
        <template #footer>
          <el-button @click="rejectDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmReject" :loading="saving">确认拒绝</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Select, CloseBold, View, ChatLineSquare } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getPendingPosts, auditPost } from '@/api/admin'
import dayjs from 'dayjs'

const router = useRouter()
const loading = ref(false); const saving = ref(false); const activeTab = ref('pending'); const posts = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const rejectDialogVisible = ref(false); const rejectReason = ref(''); const currentPost = ref<any>(null)

const fetchPosts = async () => { loading.value = true; try { const res = await getPendingPosts({ page: page.value, pageSize: pageSize.value }); if (res.data) { posts.value = res.data.records || []; total.value = res.data.total || 0 } } catch { ElMessage.error('获取待审核内容失败') } finally { loading.value = false } }
const handleTabChange = () => { page.value = 1; fetchPosts() }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchPosts() }
const handleApprove = (post: any) => { ElMessageBox.confirm(`确认通过帖子《${post.title}》吗？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'success' }).then(async () => { try { await auditPost(post.id, 'APPROVED'); ElMessage.success('审核通过'); fetchPosts() } catch (error: any) { ElMessage.error(error.message || '操作失败') } }) }
const viewPostDetail = (post: any) => { router.push(`/admin/posts/detail/${post.id}`) }
const handleReject = (post: any) => { currentPost.value = post; rejectReason.value = ''; rejectDialogVisible.value = true }
const confirmReject = async () => { if (!rejectReason.value.trim()) { ElMessage.warning('请输入拒绝原因'); return }; saving.value = true; try { await auditPost(currentPost.value.id, 'REJECTED', rejectReason.value); ElMessage.success('已拒绝'); rejectDialogVisible.value = false; fetchPosts() } catch (error: any) { ElMessage.error(error.message || '操作失败') } finally { saving.value = false } }
const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')

onMounted(() => fetchPosts())
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;
    .header-left {
      h1 { display: flex; align-items: center; gap: 8px; font-size: 24px; font-weight: 600; color: #1f2937; margin: 0 0 8px; }
      p { color: #6b7280; margin: 0; }
    }
  }

  .content-card {
    background: white; border-radius: 16px; padding: 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .modern-tabs {
      :deep(.el-tabs__item.is-active) { color: #22C55E; }
      :deep(.el-tabs__active-bar) { background: #22C55E; }
    }

    .posts-list { min-height: 400px; }

    .post-item {
      padding: 20px;
      border: 1px solid #f3f4f6;
      border-radius: 12px;
      margin-bottom: 16px;
      transition: all 0.2s;

      &:hover { border-color: #e5e7eb; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05); }

      .post-header {
        display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px;

        .author-info {
          display: flex; align-items: center; gap: 12px;
          .author-avatar { background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%); color: white; font-weight: 600; }
          .author-meta {
            .author-name { display: block; font-weight: 500; color: #1f2937; }
            .post-time { font-size: 12px; color: #9ca3af; }
          }
        }
        .post-actions { display: flex; gap: 8px; }
      }

      .post-title { font-size: 16px; font-weight: 600; color: #1f2937; margin: 0 0 8px; }
      .post-content { color: #6b7280; line-height: 1.6; margin-bottom: 12px; p { margin: 0; } }
      .post-images { display: flex; gap: 8px; .post-img { width: 80px; height: 80px; border-radius: 8px; } }
    }

    .pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; }
  }
}
</style>
