<template>
  <MainLayout>
    <div class="resource-upload-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="title-icon">📤</span>
            上传红色资源
          </h1>
          <p class="page-desc">分享优质红色教育资源，共建学习社区</p>
        </div>
      </div>

      <div class="page-container">
        <div class="upload-layout">
          <!-- 主表单 -->
          <div class="main-form">
            <el-form
              ref="uploadFormRef"
              :model="uploadForm"
              :rules="uploadRules"
              label-position="top"
              @submit.prevent="handleSubmit"
            >
              <el-form-item label="资源标题" prop="title">
                <el-input
                  v-model="uploadForm.title"
                  placeholder="请输入资源标题"
                  maxlength="100"
                  show-word-limit
                  size="large"
                />
              </el-form-item>

              <el-form-item label="资源分类" prop="categoryId">
                <el-select v-model="uploadForm.categoryId" placeholder="请选择分类" size="large" style="width: 100%">
                  <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
                </el-select>
              </el-form-item>

              <el-form-item label="资源描述" prop="description">
                <el-input
                  v-model="uploadForm.description"
                  type="textarea"
                  :rows="5"
                  placeholder="请输入资源描述，帮助其他用户了解资源内容"
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>

              <el-form-item label="封面图片">
                <el-upload
                  class="cover-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :show-file-list="false"
                  :on-success="handleCoverSuccess"
                  :before-upload="beforeCoverUpload"
                >
                  <img v-if="uploadForm.coverImage" :src="getFullUrl(uploadForm.coverImage)" class="cover-preview" />
                  <div v-else class="cover-placeholder">
                    <el-icon :size="32"><Plus /></el-icon>
                    <span>上传封面</span>
                  </div>
                </el-upload>
                <p class="form-tip">建议尺寸 800×600px，支持 JPG/PNG，不超过 2MB</p>
              </el-form-item>

              <el-form-item label="资源文件" prop="fileUrl" required>
                <el-upload
                  class="file-uploader"
                  :action="uploadUrl"
                  :headers="uploadHeaders"
                  :on-success="handleFileSuccess"
                  :on-progress="handleFileProgress"
                  :before-upload="beforeFileUpload"
                  :file-list="fileList"
                  :limit="1"
                  drag
                >
                  <el-icon class="upload-icon"><Upload /></el-icon>
                  <div class="upload-text">
                    <p>将文件拖到此处，或<em>点击上传</em></p>
                    <p class="upload-hint">文档最大{{ uploadConfig.docMaxSize }}MB / 视频最大{{ uploadConfig.videoMaxSize }}MB</p>
                  </div>
                </el-upload>
                <el-progress v-if="uploadProgress > 0 && uploadProgress < 100" :percentage="uploadProgress" :stroke-width="8" />
              </el-form-item>

              <div class="form-actions">
                <el-button size="large" @click="handleReset">重置</el-button>
                <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
                  <el-icon><Upload /></el-icon>
                  提交审核
                </el-button>
              </div>
            </el-form>
          </div>

          <!-- 侧边须知 -->
          <div class="sidebar">
            <div class="notice-card">
              <h3><el-icon><InfoFilled /></el-icon> 上传须知</h3>
              <ul>
                <li>上传的资源将进入审核状态，审核通过后才会公开展示</li>
                <li>请确保上传的资源内容符合红色教育主题</li>
                <li>禁止上传违法、暴力、色情等不良内容</li>
                <li>请尊重知识产权，不要上传侵权内容</li>
                <li>资源描述要真实准确，方便用户检索</li>
              </ul>
            </div>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadProps, UploadUserFile } from 'element-plus'
import { Upload, Plus, InfoFilled } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getResourceCategories, uploadResource } from '@/api/resource'
import { getPublicConfig } from '@/api/common'

interface Category { id: number; name: string }

const router = useRouter()
const uploadFormRef = ref<FormInstance>()
const categories = ref<Category[]>([])
const uploading = ref(false)
const submitting = ref(false)
const uploadProgress = ref(0)
const fileList = ref<UploadUserFile[]>([])

const uploadConfig = reactive({ docMaxSize: 50, videoMaxSize: 500, imageMaxSize: 10 })
const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/files/upload`
const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem('token')}` }

const uploadForm = reactive({ title: '', categoryId: null, description: '', coverImage: '', fileUrl: '', fileName: '', fileSize: 0 })
const uploadRules: FormRules = {
  title: [{ required: true, message: '请输入资源标题', trigger: 'blur' }, { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择资源分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入资源描述', trigger: 'blur' }, { min: 10, max: 500, message: '描述长度在 10 到 500 个字符', trigger: 'blur' }],
  fileUrl: [{ required: true, message: '请上传资源文件', trigger: 'change' }]
}

const fetchCategories = async () => { try { const res = await getResourceCategories(); if (res.data) categories.value = res.data } catch (error) { ElMessage.error('获取分类失败') } }

const beforeCoverUpload: UploadProps['beforeUpload'] = (file) => {
  if (!file.type.startsWith('image/')) { ElMessage.error('封面只能是图片格式!'); return false }
  if (file.size / 1024 / 1024 > 2) { ElMessage.error('封面图片大小不能超过 2MB!'); return false }
  return true
}

const handleCoverSuccess: UploadProps['onSuccess'] = (response) => { if (response.code === 200) { uploadForm.coverImage = response.data; ElMessage.success('封面上传成功') } else { ElMessage.error(response.message || '封面上传失败') } }

const beforeFileUpload: UploadProps['beforeUpload'] = (file) => {
  const ext = file.name.split('.').pop()?.toLowerCase() || ''
  const sizeMB = file.size / 1024 / 1024
  let maxSize = uploadConfig.docMaxSize
  if (['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)) maxSize = uploadConfig.imageMaxSize
  else if (['mp4', 'avi', 'mov', 'wmv', 'flv', 'mkv'].includes(ext)) maxSize = uploadConfig.videoMaxSize
  if (sizeMB > maxSize) { ElMessage.error(`文件大小不能超过 ${maxSize}MB!`); return false }
  uploading.value = true
  return true
}

const handleFileProgress: UploadProps['onProgress'] = (event) => { uploadProgress.value = Math.floor(event.percent || 0) }
const handleFileSuccess: UploadProps['onSuccess'] = (response, file) => {
  uploading.value = false; uploadProgress.value = 100
  if (response.code === 200) { uploadForm.fileUrl = response.data; uploadForm.fileName = file.name; uploadForm.fileSize = file.size || 0; fileList.value = [file as any]; ElMessage.success('文件上传成功'); uploadFormRef.value?.validateField('fileUrl') }
  else { ElMessage.error(response.message || '文件上传失败') }
}

const handleSubmit = async () => { if (!uploadFormRef.value) return; await uploadFormRef.value.validate(async (valid) => { if (valid) { if (!uploadForm.fileUrl) { ElMessage.error('请先上传资源文件'); return }; submitting.value = true; try { await uploadResource(uploadForm); ElMessage.success('资源上传成功，等待审核'); router.push('/resources') } catch (error: any) { ElMessage.error(error.message || '上传失败') } finally { submitting.value = false } } }) }
const handleReset = () => { uploadFormRef.value?.resetFields(); uploadForm.coverImage = ''; uploadForm.fileUrl = ''; fileList.value = []; uploadProgress.value = 0 }
const fetchUploadConfig = async () => { try { const res = await getPublicConfig(); if (res.data) { uploadConfig.docMaxSize = parseInt(res.data['upload.doc_max_size']) || 50; uploadConfig.videoMaxSize = parseInt(res.data['upload.video_max_size']) || 500; uploadConfig.imageMaxSize = parseInt(res.data['upload.image_max_size']) || 10 } } catch { /* use defaults */ } }
const getFullUrl = (url: string) => { if (!url) return ''; if (url.startsWith('http')) return url; return '/api' + url }

onMounted(() => { fetchCategories(); fetchUploadConfig() })
</script>

<style scoped lang="scss">
.resource-upload-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
  padding: 60px 20px;
  text-align: center;
  color: var(--color-secondary);

  .page-title {
    font-size: var(--font-size-4xl);
    font-weight: var(--font-weight-bold);
    margin-bottom: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    .title-icon { font-size: 40px; }
  }
  .page-desc { font-size: var(--font-size-base); opacity: 0.85; }
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 40px 20px;
}

.upload-layout {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 32px;

  @media (max-width: 768px) { grid-template-columns: 1fr; }
}

.main-form {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 32px;
  border: 1px solid var(--color-border);

  .form-tip {
    font-size: var(--font-size-xs);
    color: var(--color-text-muted);
    margin-top: 8px;
  }

  .cover-uploader {
    .cover-preview { width: 200px; height: 150px; object-fit: cover; border-radius: var(--radius-lg); }
    .cover-placeholder {
      width: 200px; height: 150px; border: 2px dashed var(--color-border); border-radius: var(--radius-lg);
      display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;
      color: var(--color-text-muted); cursor: pointer; transition: all var(--transition-base);
      &:hover { border-color: var(--color-warning); color: var(--color-warning); }
    }
  }

  .file-uploader {
    :deep(.el-upload-dragger) {
      padding: 40px 20px;
      border-radius: var(--radius-xl);
      border: 2px dashed var(--color-border);
      &:hover { border-color: var(--color-warning); }
    }
    .upload-icon { font-size: 48px; color: var(--color-text-muted); margin-bottom: 16px; }
    .upload-text { text-align: center; p { margin: 0; color: var(--color-text-secondary); } em { color: var(--color-warning); font-style: normal; } .upload-hint { font-size: var(--font-size-xs); color: var(--color-text-muted); margin-top: 4px; } }
  }

  .form-actions {
    display: flex; justify-content: flex-end; gap: 16px; margin-top: 24px; padding-top: 24px;
    border-top: 1px solid var(--color-border-light);
  }
}

.sidebar {
  .notice-card {
    background: var(--color-bg-primary);
    border-radius: var(--radius-xl);
    padding: 24px;
    border: 1px solid var(--color-border);
    position: sticky;
    top: calc(var(--header-height) + 20px);

    h3 {
      display: flex; align-items: center; gap: 8px;
      font-size: var(--font-size-base); color: var(--color-warning); margin-bottom: 16px;
    }
    ul {
      list-style: none; padding: 0; margin: 0;
      li {
        position: relative; padding-left: 16px; margin-bottom: 12px;
        font-size: var(--font-size-sm); color: var(--color-text-secondary); line-height: var(--line-height-relaxed);
        &::before { content: '•'; position: absolute; left: 0; color: var(--color-warning); }
      }
    }
  }
}
</style>
