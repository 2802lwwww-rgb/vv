<template>
  <AdminLayout>
    <div class="admin-page resource-upload-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><Upload /></el-icon> 上传资源</h1>
          <p>管理员上传的资源将自动审核通过</p>
        </div>
        <el-button @click="$router.back()"><el-icon><ArrowLeft /></el-icon> 返回列表</el-button>
      </div>

      <div class="upload-grid">
        <div class="upload-card">
          <el-form ref="uploadFormRef" :model="uploadForm" :rules="uploadRules" label-width="100px" @submit.prevent="handleSubmit">
            <el-form-item label="资源标题" prop="title">
              <el-input v-model="uploadForm.title" placeholder="请输入资源标题" maxlength="100" show-word-limit />
            </el-form-item>

            <el-form-item label="资源分类" prop="categoryId">
              <el-select v-model="uploadForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
              </el-select>
            </el-form-item>

            <el-form-item label="资源描述" prop="description">
              <el-input v-model="uploadForm.description" type="textarea" :rows="4" placeholder="请输入资源描述" maxlength="500" show-word-limit />
            </el-form-item>

            <el-form-item label="封面图片">
              <el-upload class="cover-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleCoverSuccess" :before-upload="beforeCoverUpload">
                <img v-if="uploadForm.coverImage" :src="getFullUrl(uploadForm.coverImage)" class="cover-image" />
                <div v-else class="cover-placeholder"><el-icon :size="32"><Plus /></el-icon><span>上传封面</span></div>
              </el-upload>
            </el-form-item>

            <el-form-item label="资源文件" prop="fileUrl" required>
              <el-upload class="file-uploader" :action="uploadUrl" :headers="uploadHeaders" :on-success="handleFileSuccess" :on-progress="handleFileProgress" :before-upload="beforeFileUpload" :file-list="fileList" :limit="1" drag>
                <el-icon :size="48"><Upload /></el-icon>
                <div class="upload-text">将文件拖放到此处，或点击上传</div>
                <div class="upload-hint">支持文档、图片、视频、音频格式，最大100MB</div>
              </el-upload>
              <el-progress v-if="uploadProgress > 0 && uploadProgress < 100" :percentage="uploadProgress" :stroke-width="10" style="margin-top: 12px" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">立即发布</el-button>
              <el-button size="large" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div class="tips-card">
          <h3>📝 上传须知</h3>
          <ul>
            <li>管理员上传的资源将自动审核通过</li>
            <li>封面图片建议尺寸：800×600px</li>
            <li>支持的格式：PDF、图片、视频、音频</li>
            <li>文件大小不超过100MB</li>
          </ul>
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadProps, UploadUserFile } from 'element-plus'
import { Upload, ArrowLeft, Plus } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getResourceCategories } from '@/api/resource'
import { adminUploadResource } from '@/api/admin'

interface Category { id: number; name: string }

const router = useRouter()
const uploadFormRef = ref<FormInstance>(); const categories = ref<Category[]>([]); const uploading = ref(false); const submitting = ref(false); const uploadProgress = ref(0); const fileList = ref<UploadUserFile[]>([])
const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/files/upload`
const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem('token')}` }

const uploadForm = reactive({ title: '', categoryId: null, description: '', coverImage: '', fileUrl: '', fileType: '', fileSize: 0, tags: '' })
const uploadRules: FormRules = { title: [{ required: true, message: '请输入资源标题', trigger: 'blur' }], categoryId: [{ required: true, message: '请选择资源分类', trigger: 'change' }], description: [{ required: true, message: '请输入资源描述', trigger: 'blur' }], fileUrl: [{ required: true, message: '请上传资源文件', trigger: 'change' }] }

const fetchCategories = async () => { try { const res = await getResourceCategories(); if (res.data) categories.value = res.data } catch { ElMessage.error('获取分类失败') } }
const getFullUrl = (url: string) => { if (!url) return ''; if (url.startsWith('http')) return url; return `${import.meta.env.VITE_API_BASE_URL}${url}` }
const beforeCoverUpload: UploadProps['beforeUpload'] = (file) => { if (!file.type.startsWith('image/')) { ElMessage.error('封面只能是图片格式!'); return false }; if (file.size / 1024 / 1024 > 2) { ElMessage.error('封面图片大小不能超过 2MB!'); return false }; return true }
const handleCoverSuccess: UploadProps['onSuccess'] = (response) => { if (response.code === 200) { uploadForm.coverImage = response.data || response.message; ElMessage.success('封面上传成功') } else { ElMessage.error(response.message || '封面上传失败') } }
const beforeFileUpload: UploadProps['beforeUpload'] = (file) => { if (file.size / 1024 / 1024 > 100) { ElMessage.error('文件大小不能超过 100MB!'); return false }; uploading.value = true; return true }
const handleFileProgress: UploadProps['onProgress'] = (event) => { uploadProgress.value = Math.floor(event.percent || 0) }
const handleFileSuccess: UploadProps['onSuccess'] = (response, file) => { uploading.value = false; uploadProgress.value = 100; if (response.code === 200) { uploadForm.fileUrl = response.data || response.message; uploadForm.fileSize = file.size || 0; const ext = file.name.split('.').pop()?.toLowerCase() || ''; if (['pdf'].includes(ext)) uploadForm.fileType = 'document'; else if (['jpg', 'jpeg', 'png', 'gif'].includes(ext)) uploadForm.fileType = 'image'; else if (['mp4', 'avi', 'mov'].includes(ext)) uploadForm.fileType = 'video'; else uploadForm.fileType = 'other'; fileList.value = [file as any]; ElMessage.success('文件上传成功'); uploadFormRef.value?.validateField('fileUrl') } else { ElMessage.error(response.message || '文件上传失败') } }
const handleSubmit = async () => { if (!uploadFormRef.value) return; await uploadFormRef.value.validate(async (valid) => { if (valid) { if (!uploadForm.fileUrl) { ElMessage.error('请先上传资源文件'); return }; submitting.value = true; try { await adminUploadResource(uploadForm); ElMessage.success('资源发布成功'); router.push('/admin/resources') } catch (error: any) { ElMessage.error(error.message || '发布失败') } finally { submitting.value = false } } }) }
const handleReset = () => { uploadFormRef.value?.resetFields(); uploadForm.coverImage = ''; uploadForm.fileUrl = ''; uploadForm.fileSize = 0; fileList.value = []; uploadProgress.value = 0 }

onMounted(() => fetchCategories())
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

  .upload-grid {
    display: grid; grid-template-columns: 2fr 1fr; gap: 24px;
    @media (max-width: 992px) { grid-template-columns: 1fr; }
  }

  .upload-card {
    background: white; border-radius: 16px; padding: 32px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  }

  .tips-card {
    background: white; border-radius: 16px; padding: 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05); height: fit-content; position: sticky; top: 24px;
    h3 { font-size: 16px; margin: 0 0 16px; color: #1f2937; }
    ul { margin: 0; padding-left: 20px; li { color: #6b7280; margin-bottom: 8px; line-height: 1.6; } }
  }
}

.cover-uploader {
  .cover-image { width: 200px; height: 150px; object-fit: cover; border-radius: 8px; }
  .cover-placeholder {
    width: 200px; height: 150px; border: 2px dashed #e5e7eb; border-radius: 8px;
    display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;
    color: #9ca3af; cursor: pointer; transition: all 0.2s;
    &:hover { border-color: #D64541; color: #D64541; }
  }
}

.file-uploader {
  :deep(.el-upload-dragger) {
    padding: 32px;
    .upload-text { font-size: 16px; color: #4b5563; margin-top: 12px; }
    .upload-hint { font-size: 13px; color: #9ca3af; margin-top: 8px; }
  }
}
</style>
