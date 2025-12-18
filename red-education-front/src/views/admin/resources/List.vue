<template>
  <AdminLayout>
    <div class="admin-page resources-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><Document /></el-icon> 资源管理</h1>
          <p>管理平台学习资源</p>
        </div>
        <div class="header-actions">
          <el-button @click="$router.push('/admin/resources/audit')"><el-icon><Checked /></el-icon> 资源审核</el-button>
          <el-button type="primary" @click="$router.push('/admin/resources/upload')"><el-icon><Upload /></el-icon> 上传资源</el-button>
        </div>
      </div>

      <div class="table-card">
        <el-table :data="resources" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="资源信息" min-width="250">
            <template #default="{ row }">
              <div class="resource-cell">
                <img v-if="row.coverImage" :src="getFullUrl(row.coverImage)" class="resource-cover" />
                <div v-else class="no-cover-box"><el-icon><Picture /></el-icon></div>
                <div class="resource-info">
                  <span class="resource-title">{{ row.title }}</span>
                  <span class="resource-category">{{ row.category }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="authorName" label="上传者" width="100" />
          <el-table-column label="数据" width="120">
            <template #default="{ row }">
              <div class="stats-cell">
                <span>👁 {{ row.viewCount || 0 }}</span>
                <span>⬇ {{ row.downloadCount || 0 }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag v-if="row.status === 0" type="warning" effect="dark" round size="small">待审核</el-tag>
              <el-tag v-else-if="row.status === 1" type="success" effect="dark" round size="small">已发布</el-tag>
              <el-tag v-else-if="row.status === 2" type="danger" effect="dark" round size="small">已驳回</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text @click="handleEdit(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
              <el-button type="danger" text @click="handleDelete(row)"><el-icon><Delete /></el-icon> 删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination background layout="total, prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="handlePageChange" />
        </div>
      </div>

      <el-dialog v-model="dialogVisible" title="编辑资源" width="680px" class="modern-dialog">
        <el-form :model="editForm" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="标题" prop="title"><el-input v-model="editForm.title" placeholder="请输入资源标题" /></el-form-item>
          <el-form-item label="描述" prop="description"><el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入资源描述" /></el-form-item>
          <el-form-item label="详细内容"><el-input v-model="editForm.content" type="textarea" :rows="4" placeholder="请输入详细内容" /></el-form-item>
          <el-form-item label="标签"><el-input v-model="editForm.tags" placeholder="多个标签用逗号分隔" /></el-form-item>
          <el-form-item label="封面图">
            <el-upload class="cover-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleCoverSuccess" :before-upload="beforeCoverUpload" accept="image/*">
              <img v-if="editForm.coverImage" :src="getFullUrl(editForm.coverImage)" class="cover-preview" />
              <div v-else class="cover-placeholder"><el-icon :size="28"><Plus /></el-icon><span>上传封面</span></div>
            </el-upload>
          </el-form-item>
          <el-form-item label="资源文件">
            <el-upload class="file-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleFileSuccess" :before-upload="beforeFileUpload">
              <el-button type="primary"><el-icon><Upload /></el-icon> 重新上传文件</el-button>
            </el-upload>
            <div v-if="editForm.fileUrl" class="current-file">
              <el-tag type="info">{{ getFileName(editForm.fileUrl) }}</el-tag>
              <span v-if="editForm.fileSize" class="file-size">({{ formatFileSize(editForm.fileSize) }})</span>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { Plus, Upload, Document, Edit, Delete, Picture, Checked } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getAdminResourceList, updateResource, deleteResource } from '@/api/admin'
import { useUserStore } from '@/stores/user'
import { getPublicConfig } from '@/api/common'

const userStore = useUserStore()
const loading = ref(false); const saving = ref(false); const dialogVisible = ref(false); const formRef = ref<FormInstance>(); const resources = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const uploadUrl = '/api/files/upload'
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${userStore.token}` }))
const editForm = reactive({ id: 0, title: '', description: '', content: '', tags: '', categoryId: 0, coverImage: '', fileUrl: '', fileType: '', fileSize: 0 })
const rules: FormRules = { title: [{ required: true, message: '请输入资源标题', trigger: 'blur' }], description: [{ required: true, message: '请输入资源描述', trigger: 'blur' }] }

// 动态上传限制配置
const uploadConfig = reactive({ docMaxSize: 50, videoMaxSize: 500, imageMaxSize: 10 })
const fetchUploadConfig = async () => { try { const res = await getPublicConfig(); if (res.data) { uploadConfig.docMaxSize = parseInt(res.data['upload.doc_max_size']) || 50; uploadConfig.videoMaxSize = parseInt(res.data['upload.video_max_size']) || 500; uploadConfig.imageMaxSize = parseInt(res.data['upload.image_max_size']) || 10 } } catch { /* use defaults */ } }

const getFullUrl = (url: string) => { if (!url) return ''; if (url.startsWith('http')) return url; return '/api' + url }
const getFileName = (url: string) => url ? url.split('/').pop() || url : ''
const formatFileSize = (size: number) => { if (size < 1024) return size + ' B'; if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'; return (size / 1024 / 1024).toFixed(2) + ' MB' }

const beforeCoverUpload: UploadProps['beforeUpload'] = (rawFile) => { if (!rawFile.type.startsWith('image/')) { ElMessage.error('只能上传图片文件!'); return false }; if (rawFile.size / 1024 / 1024 > uploadConfig.imageMaxSize) { ElMessage.error(`图片大小不能超过 ${uploadConfig.imageMaxSize}MB!`); return false }; return true }
const handleCoverSuccess: UploadProps['onSuccess'] = (response) => { if (response.code === 200) { editForm.coverImage = response.data || response.message; ElMessage.success('封面图上传成功') } else { ElMessage.error(response.message || '上传失败') } }
const beforeFileUpload: UploadProps['beforeUpload'] = (rawFile) => { const ext = rawFile.name.split('.').pop()?.toLowerCase() || ''; let maxSize = uploadConfig.docMaxSize; let typeLabel = '文档'; if (['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv', 'ts'].includes(ext)) { maxSize = uploadConfig.videoMaxSize; typeLabel = '视频' } else if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)) { maxSize = uploadConfig.imageMaxSize; typeLabel = '图片' }; if (rawFile.size / 1024 / 1024 > maxSize) { ElMessage.error(`${typeLabel}文件大小不能超过 ${maxSize}MB!`); return false }; return true }
const handleFileSuccess: UploadProps['onSuccess'] = (response, uploadFile) => { if (response.code === 200) { const url = response.data || response.message; editForm.fileUrl = url; editForm.fileSize = uploadFile.size || 0; const ext = url.split('.').pop()?.toLowerCase() || ''; if (['pdf', 'doc', 'docx', 'txt'].includes(ext)) editForm.fileType = 'document'; else if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)) editForm.fileType = 'image'; else if (['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv', 'ts'].includes(ext)) editForm.fileType = 'video'; ElMessage.success('资源文件上传成功') } else { ElMessage.error(response.message || '上传失败') } }

const fetchResources = async () => { loading.value = true; try { const res = await getAdminResourceList({ current: page.value, size: pageSize.value }); if (res.data) { resources.value = res.data.records || []; total.value = res.data.total || 0 } } catch { ElMessage.error('获取资源列表失败') } finally { loading.value = false } }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchResources() }
const handleEdit = (row: any) => { Object.assign(editForm, { id: row.id, title: row.title || '', description: row.description || '', content: row.content || '', tags: row.tags || '', categoryId: row.categoryId || 0, coverImage: row.coverImage || '', fileUrl: row.fileUrl || '', fileType: row.fileType || '', fileSize: row.fileSize || 0 }); dialogVisible.value = true }
const handleSave = async () => { if (!formRef.value) return; await formRef.value.validate(async (valid) => { if (valid) { saving.value = true; try { await updateResource(editForm.id, { title: editForm.title, description: editForm.description, content: editForm.content, tags: editForm.tags, categoryId: editForm.categoryId, coverImage: editForm.coverImage, fileUrl: editForm.fileUrl, fileType: editForm.fileType, fileSize: editForm.fileSize }); ElMessage.success('更新成功'); dialogVisible.value = false; fetchResources() } catch (error: any) { ElMessage.error(error.message || '更新失败') } finally { saving.value = false } } }) }
const handleDelete = (row: any) => { ElMessageBox.confirm(`确认删除资源《${row.title}》吗？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(async () => { try { await deleteResource(row.id); ElMessage.success('删除成功'); fetchResources() } catch (error: any) { ElMessage.error(error.message || '删除失败') } }) }

onMounted(() => { fetchUploadConfig(); fetchResources() })
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px;
    .header-left {
      h1 { display: flex; align-items: center; gap: 8px; font-size: 24px; font-weight: 600; color: #1f2937; margin: 0 0 8px; }
      p { color: #6b7280; margin: 0; }
    }
    .header-actions { display: flex; gap: 12px; }
  }

  .table-card {
    background: white; border-radius: 12px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .resource-cell {
      display: flex; align-items: center; gap: 12px;
      .resource-cover { width: 60px; height: 45px; object-fit: cover; border-radius: 6px; }
      .no-cover-box { width: 60px; height: 45px; background: #f3f4f6; border-radius: 6px; display: flex; align-items: center; justify-content: center; color: #d1d5db; }
      .resource-info { .resource-title { display: block; font-weight: 500; } .resource-category { font-size: 12px; color: #9ca3af; } }
    }

    .stats-cell { display: flex; flex-direction: column; gap: 4px; font-size: 13px; color: #6b7280; }
    .pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; }
  }
}

.cover-uploader {
  .cover-preview { width: 160px; height: 120px; object-fit: cover; border-radius: 8px; }
  .cover-placeholder {
    width: 160px; height: 120px; border: 2px dashed #e5e7eb; border-radius: 8px;
    display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 6px;
    color: #9ca3af; cursor: pointer; transition: all 0.2s;
    &:hover { border-color: #D64541; color: #D64541; }
  }
}

.current-file { margin-top: 10px; display: flex; align-items: center; gap: 8px; .file-size { color: #6b7280; font-size: 12px; } }
</style>
