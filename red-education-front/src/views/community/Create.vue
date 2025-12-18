<template>
  <MainLayout>
    <div class="create-post-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="title-icon">✍️</span>
            发布帖子
          </h1>
          <p class="page-desc">分享你的学习心得与见解</p>
        </div>
      </div>

      <div class="page-container">
        <div class="create-card">
          <el-form
            ref="formRef"
            :model="form"
            :rules="rules"
            label-position="top"
          >
            <el-form-item label="帖子标题" prop="title">
              <el-input
                v-model="form.title"
                placeholder="请输入一个吸引人的标题"
                maxlength="100"
                show-word-limit
                size="large"
              />
            </el-form-item>

            <el-form-item label="话题分类" prop="topic">
              <div class="topic-tags">
                <span 
                  v-for="topic in topics" 
                  :key="topic"
                  class="topic-tag"
                  :class="{ active: form.topic === topic }"
                  @click="form.topic = topic"
                >
                  {{ topic }}
                </span>
              </div>
            </el-form-item>

            <el-form-item label="帖子内容" prop="content">
              <el-input
                v-model="form.content"
                type="textarea"
                :rows="12"
                placeholder="分享你的想法、心得、见解..."
                maxlength="5000"
                show-word-limit
              />
            </el-form-item>

            <el-form-item label="上传图片">
              <el-upload
                v-model:file-list="fileList"
                :action="uploadUrl"
                :headers="uploadHeaders"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :limit="9"
              >
                <el-icon :size="24"><Plus /></el-icon>
              </el-upload>
              <p class="form-tip">最多上传 9 张图片，每张不超过 5MB</p>
            </el-form-item>

            <div class="form-actions">
              <el-button size="large" @click="handleReset">重置</el-button>
              <el-button type="primary" size="large" @click="handleSubmit" :loading="submitting">
                <el-icon><DocumentAdd /></el-icon>
                发布帖子
              </el-button>
            </div>
          </el-form>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { Plus, DocumentAdd } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { createPost } from '@/api/community'

const router = useRouter()
const formRef = ref<FormInstance>()
const submitting = ref(false)
const fileList = ref<any[]>([])

const topics = ['党史学习', '心得体会', '红色足迹', '英雄事迹', '学习资料', '其他']
const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/files/upload`
const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem('token')}` }

const form = reactive({ title: '', content: '', topic: '', images: [] as string[] })

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }, { min: 5, max: 100, message: '标题长度在 5 到 100 个字符', trigger: 'blur' }],
  topic: [{ required: true, message: '请选择话题分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }, { min: 10, max: 5000, message: '内容长度在 10 到 5000 个字符', trigger: 'blur' }]
}

const handleUploadSuccess: UploadProps['onSuccess'] = (response) => { if (response.code === 200) form.images.push(response.data || response.message) }

const handleSubmit = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      submitting.value = true
      try {
        await createPost({ title: form.title, content: form.content, topic: form.topic, images: form.images.join(',') })
        ElMessage.success('发布成功，等待审核')
        router.push('/community')
      } catch (error: any) {
        ElMessage.error(error.message || '发布失败')
      } finally {
        submitting.value = false
      }
    }
  })
}

const handleReset = () => { formRef.value?.resetFields(); form.images = []; fileList.value = [] }
</script>

<style scoped lang="scss">
.create-post-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #22C55E 0%, #15803D 100%);
  padding: 60px 20px;
  text-align: center;
  color: white;

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
  .page-desc { font-size: var(--font-size-base); opacity: 0.9; }
}

.page-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 40px 20px;
}

.create-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 40px;
  border: 1px solid var(--color-border);

  .form-tip {
    font-size: var(--font-size-xs);
    color: var(--color-text-muted);
    margin-top: 8px;
  }

  .topic-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;

    .topic-tag {
      padding: 10px 20px;
      border-radius: var(--radius-full);
      border: 1px solid var(--color-border);
      cursor: pointer;
      font-size: var(--font-size-sm);
      color: var(--color-text-secondary);
      transition: all var(--transition-fast);

      &:hover { border-color: var(--color-success); color: var(--color-success); }
      &.active { background: var(--color-success); border-color: var(--color-success); color: white; }
    }
  }

  :deep(.el-upload--picture-card) {
    border-radius: var(--radius-lg);
    border: 2px dashed var(--color-border);
    &:hover { border-color: var(--color-success); }
  }

  .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 16px;
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid var(--color-border-light);
  }
}
</style>
