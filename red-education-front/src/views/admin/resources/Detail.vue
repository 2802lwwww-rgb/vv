<template>
  <AdminLayout>
    <div class="admin-page resource-detail-page" v-loading="loading">
      <div class="page-header">
        <el-button @click="$router.back()"><el-icon><ArrowLeft /></el-icon> 返回列表</el-button>
        <div class="header-right">
          <el-tag v-if="resource" :type="getStatusType(resource.status)" effect="dark" size="large" round>{{ getStatusText(resource.status) }}</el-tag>
          <el-button type="primary" plain @click="openFullScreen" v-if="resource?.fileUrl"><el-icon><FullScreen /></el-icon> 全屏观看</el-button>
        </div>
      </div>

      <div class="content-grid" v-if="resource">
        <!-- 左侧：资源预览 -->
        <div class="preview-section">
          <div class="preview-card">
            <div class="resource-preview">
              <img v-if="isImage" :src="getFullUrl(resource.fileUrl)" :alt="resource.title" />
              <video v-else-if="isVideo" :src="getFullUrl(resource.fileUrl)" controls></video>
              <audio v-else-if="isAudio" :src="getFullUrl(resource.fileUrl)" controls class="audio-player"></audio>
              <div v-else-if="isPdf" class="pdf-container"><VuePdfEmbed :source="getFullUrl(resource.fileUrl)" /></div>
              <div v-else class="no-preview">
                <el-icon :size="80"><Document /></el-icon>
                <p>该文件不支持在线预览</p>
                <el-button type="primary" link tag="a" :href="getFullUrl(resource.fileUrl)" target="_blank">点击下载</el-button>
              </div>
            </div>
            <div v-if="isPdf" class="pdf-fallback">
              <a :href="getFullUrl(resource.fileUrl)" target="_blank">如果无法预览，请点击此处在新窗口打开</a>
            </div>
          </div>
        </div>

        <!-- 右侧：资源信息 -->
        <div class="info-section">
          <div class="info-card">
            <h1 class="resource-title">{{ resource.title }}</h1>
            <div class="resource-tags">
              <el-tag type="warning" effect="dark" round>{{ resource.categoryName }}</el-tag>
            </div>

            <div class="resource-stats">
              <span><el-icon><View /></el-icon> {{ resource.viewCount }} 浏览</span>
              <span><el-icon><Clock /></el-icon> {{ formatDate(resource.createTime) }}</span>
            </div>

            <div class="resource-description">
              <h3>资源描述</h3>
              <p>{{ resource.description || '暂无描述' }}</p>
            </div>

            <div class="resource-meta">
              <div class="meta-row"><span class="label">文件名</span><span class="value">{{ resource.fileUrl ? resource.fileUrl.split('/').pop() : '未知' }}</span></div>
              <div class="meta-row"><span class="label">文件大小</span><span class="value">{{ formatSize(resource.fileSize) }}</span></div>
              <div class="meta-row"><span class="label">上传者</span><span class="value">{{ resource.uploaderName }}</span></div>
            </div>

            <div v-if="resource.status === 0" class="audit-actions">
              <el-button type="success" size="large" @click="handleApprove"><el-icon><Select /></el-icon> 通过审核</el-button>
              <el-button type="danger" size="large" @click="handleReject"><el-icon><CloseBold /></el-icon> 驳回申请</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Document, View, Clock, FullScreen, Select, CloseBold } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getAdminResourceDetail, auditResource } from '@/api/admin'
import dayjs from 'dayjs'
import VuePdfEmbed from 'vue-pdf-embed'

interface Resource { id: number; title: string; description: string; coverImage: string; fileUrl: string; fileName: string; fileSize: number; uploaderName: string; categoryName: string; categoryId: number; viewCount: number; status: number; createTime: string }

const route = useRoute(); const router = useRouter()
const loading = ref(false); const resource = ref<Resource | null>(null)
const resourceId = computed(() => Number(route.params.id))

const isImage = computed(() => { if (!resource.value?.fileUrl) return false; const ext = resource.value.fileUrl.split('.').pop()?.toLowerCase() || ''; return ['jpg', 'jpeg', 'png', 'gif', 'webp'].includes(ext) })
const isVideo = computed(() => { if (!resource.value?.fileUrl) return false; const ext = resource.value.fileUrl.split('.').pop()?.toLowerCase() || ''; return ['mp4', 'webm', 'ogg', 'avi', 'mov'].includes(ext) })
const isAudio = computed(() => { if (!resource.value?.fileUrl) return false; const ext = resource.value.fileUrl.split('.').pop()?.toLowerCase() || ''; return ['mp3', 'wav', 'ogg'].includes(ext) })
const isPdf = computed(() => resource.value?.fileUrl?.toLowerCase().endsWith('.pdf'))

const fetchResourceDetail = async () => { loading.value = true; try { const res = await getAdminResourceDetail(resourceId.value); if (res.data) resource.value = res.data } catch { ElMessage.error('获取资源详情失败'); router.back() } finally { loading.value = false } }
const handleApprove = async () => { if (!resource.value) return; try { await auditResource(resource.value.id, '1', '审核通过'); ElMessage.success('审核通过'); fetchResourceDetail() } catch (error: any) { ElMessage.error(error.message || '操作失败') } }
const handleReject = async () => { if (!resource.value) return; ElMessageBox.prompt('请输入驳回原因', '驳回资源', { confirmButtonText: '确认', cancelButtonText: '取消', inputPattern: /.+/, inputErrorMessage: '请输入驳回原因' }).then(async ({ value }) => { try { await auditResource(resource.value!.id, '2', value); ElMessage.success('已驳回'); fetchResourceDetail() } catch (error: any) { ElMessage.error(error.message || '操作失败') } }) }
const formatDate = (date: string | undefined) => date ? dayjs(date).format('YYYY-MM-DD HH:mm') : ''
const getFullUrl = (url: string) => { if (!url) return ''; if (url.startsWith('http')) return url; return `/api${url}` }
const formatSize = (bytes: number | undefined) => { if (!bytes) return '0 B'; const k = 1024; const sizes = ['B', 'KB', 'MB', 'GB']; const i = Math.floor(Math.log(bytes) / Math.log(k)); return (bytes / Math.pow(k, i)).toFixed(2) + ' ' + sizes[i] }
const getStatusType = (status: number | undefined) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[status ?? -1] || 'info')
const getStatusText = (status: number | undefined) => ({ 0: '待审核', 1: '已发布', 2: '未通过' }[status ?? -1] || '未知')
const openFullScreen = () => { if (resource.value?.fileUrl) window.open(getFullUrl(resource.value.fileUrl), '_blank') }

onMounted(() => fetchResourceDetail())
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px;
    .header-right { display: flex; gap: 12px; align-items: center; }
  }

  .content-grid {
    display: grid;
    grid-template-columns: 1.2fr 1fr;
    gap: 24px;

    @media (max-width: 992px) { grid-template-columns: 1fr; }
  }

  .preview-card {
    background: white; border-radius: 16px; padding: 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .resource-preview {
      min-height: 400px; display: flex; align-items: center; justify-content: center; background: #f9fafb; border-radius: 12px;
      img, video { max-width: 100%; max-height: 600px; border-radius: 8px; }
      .audio-player { width: 80%; }
      .pdf-container { width: 100%; height: 600px; overflow-y: auto; background: #525659; padding: 20px; border-radius: 8px; }
      .no-preview { text-align: center; color: #9ca3af; display: flex; flex-direction: column; align-items: center; p { margin: 16px 0; } }
    }

    .pdf-fallback { text-align: center; margin-top: 12px; a { color: #D64541; text-decoration: none; } }
  }

  .info-card {
    background: white; border-radius: 16px; padding: 32px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .resource-title { font-size: 24px; font-weight: 700; color: #1f2937; margin: 0 0 16px; }
    .resource-tags { margin-bottom: 16px; }
    .resource-stats { display: flex; gap: 24px; margin-bottom: 24px; color: #6b7280; span { display: flex; align-items: center; gap: 6px; } }
    .resource-description { margin-bottom: 24px; h3 { font-size: 14px; color: #6b7280; margin: 0 0 8px; } p { color: #4b5563; line-height: 1.7; margin: 0; } }
    .resource-meta {
      .meta-row { display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #f3f4f6; .label { color: #6b7280; } .value { font-weight: 500; color: #1f2937; } &:last-child { border: none; } }
    }
    .audit-actions { display: flex; gap: 16px; margin-top: 32px; .el-button { flex: 1; } }
  }
}
</style>
