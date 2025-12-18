<template>
  <AdminLayout>
    <div class="admin-page courses-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><Reading /></el-icon> 课程管理</h1>
          <p>管理平台课程内容</p>
        </div>
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon> 新增课程
        </el-button>
      </div>

      <div class="table-card">
        <el-table :data="courses" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column label="课程信息" min-width="280">
            <template #default="{ row }">
              <div class="course-cell">
                <img :src="getFullUrl(row.coverImage) || 'https://picsum.photos/80/60'" class="course-cover" />
                <div class="course-info">
                  <span class="course-title">{{ row.title }}</span>
                  <span class="course-category">{{ row.category }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="积分" width="80">
            <template #default="{ row }">
              <span class="points-badge">+{{ row.pointsReward }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="viewCount" label="浏览" width="80" />
          <el-table-column prop="completeCount" label="完成" width="80" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="dark" round>
                {{ row.status === 1 ? '已上架' : '已下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="推荐" width="80">
            <template #default="{ row }">
              <el-tag v-if="row.isRecommend === 1" type="warning" effect="dark" round>推荐</el-tag>
              <span v-else class="text-muted">-</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="160" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text @click="openDialog(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
              <el-button type="danger" text @click="handleDelete(row)"><el-icon><Delete /></el-icon> 删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination background layout="total, prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="handlePageChange" />
        </div>
      </div>

      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="680px" class="modern-dialog">
        <el-form :model="form" label-width="100px" :rules="formRules" ref="formRef">
          <el-form-item label="课程名称" prop="title">
            <el-input v-model="form.title" placeholder="请输入课程名称" />
          </el-form-item>
          <el-form-item label="课程分类" prop="category">
            <el-select v-model="form.category" placeholder="请选择分类" style="width: 100%">
              <el-option label="党史文献" value="党史文献" />
              <el-option label="英雄事迹" value="英雄事迹" />
              <el-option label="时政热点" value="时政热点" />
              <el-option label="视频资源" value="视频资源" />
            </el-select>
          </el-form-item>
          <el-form-item label="封面图片">
            <el-upload class="cover-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleCoverSuccess" :before-upload="beforeCoverUpload">
              <img v-if="form.coverImage" :src="getFullUrl(form.coverImage)" class="cover-preview" />
              <div v-else class="cover-placeholder"><el-icon :size="32"><Plus /></el-icon><span>上传封面</span></div>
            </el-upload>
          </el-form-item>
          <el-form-item label="课程视频">
            <el-upload class="video-uploader" :action="uploadUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleVideoSuccess" :on-progress="handleVideoProgress" :before-upload="beforeVideoUpload">
              <div v-if="form.videoUrl" class="video-info"><el-icon><VideoPlay /></el-icon><span>视频已上传</span><el-button text type="primary" size="small">重新上传</el-button></div>
              <el-button v-else type="primary" :loading="videoUploading"><el-icon><Upload /></el-icon> 上传视频</el-button>
            </el-upload>
            <el-progress v-if="videoProgress > 0 && videoProgress < 100" :percentage="videoProgress" :stroke-width="8" style="margin-top: 10px" />
          </el-form-item>
          <el-form-item label="课程简介" prop="intro">
            <el-input v-model="form.intro" type="textarea" :rows="2" placeholder="请输入课程简介" />
          </el-form-item>
          <el-form-item label="课程内容" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入课程详细内容" />
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="积分奖励"><el-input-number v-model="form.pointsReward" :min="0" :max="100" style="width: 100%" /></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="课程时长"><el-input-number v-model="form.duration" :min="0" placeholder="分钟" style="width: 100%" /></el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio :label="1">上架</el-radio><el-radio :label="0">下架</el-radio></el-radio-group></el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="推荐"><el-radio-group v-model="form.isRecommend"><el-radio :label="1">是</el-radio><el-radio :label="0">否</el-radio></el-radio-group></el-form-item>
            </el-col>
          </el-row>
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { Plus, VideoPlay, Upload, Reading, Edit, Delete } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getAdminCourseList, createCourse, updateCourse, deleteCourse } from '@/api/admin'
import { getPublicConfig } from '@/api/common'

const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/files/upload`
const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem('token')}` }
const videoUploading = ref(false)
const videoProgress = ref(0)

// 动态上传限制配置
const uploadConfig = reactive({ videoMaxSize: 500, imageMaxSize: 10 })
const fetchUploadConfig = async () => { try { const res = await getPublicConfig(); if (res.data) { uploadConfig.videoMaxSize = parseInt(res.data['upload.video_max_size']) || 500; uploadConfig.imageMaxSize = parseInt(res.data['upload.image_max_size']) || 10 } } catch { /* use defaults */ } }

const getFullUrl = (url: string) => { if (!url) return ''; if (url.startsWith('http')) return url; return `${import.meta.env.VITE_API_BASE_URL}${url}` }

const beforeCoverUpload: UploadProps['beforeUpload'] = (file) => { if (!file.type.startsWith('image/')) { ElMessage.error('封面只能是图片格式!'); return false }; if (file.size / 1024 / 1024 > uploadConfig.imageMaxSize) { ElMessage.error(`封面图片大小不能超过 ${uploadConfig.imageMaxSize}MB!`); return false }; return true }
const handleCoverSuccess: UploadProps['onSuccess'] = (response) => { if (response.code === 200) { form.coverImage = response.data || response.message; ElMessage.success('封面上传成功') } else { ElMessage.error(response.message || '封面上传失败') } }
const beforeVideoUpload: UploadProps['beforeUpload'] = (file) => { if (!file.type.startsWith('video/')) { ElMessage.error('请上传视频文件!'); return false }; if (file.size / 1024 / 1024 > uploadConfig.videoMaxSize) { ElMessage.error(`视频大小不能超过 ${uploadConfig.videoMaxSize}MB!`); return false }; videoUploading.value = true; videoProgress.value = 0; return true }
const handleVideoProgress: UploadProps['onProgress'] = (event) => { videoProgress.value = Math.floor(event.percent || 0) }
const handleVideoSuccess: UploadProps['onSuccess'] = (response) => { videoUploading.value = false; videoProgress.value = 100; if (response.code === 200) { form.videoUrl = response.data; ElMessage.success('视频上传成功') } else { ElMessage.error(response.message || '视频上传失败') } }

const loading = ref(false); const saving = ref(false); const dialogVisible = ref(false); const isEdit = ref(false); const courses = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0); const formRef = ref<FormInstance>()

const initialForm = { id: null as number | null, title: '', category: '', coverImage: '', videoUrl: '', intro: '', content: '', pointsReward: 10, duration: 0, status: 1, isRecommend: 0 }
const form = reactive({ ...initialForm })
const formRules: FormRules = { title: [{ required: true, message: '请输入课程名称', trigger: 'blur' }], category: [{ required: true, message: '请选择课程分类', trigger: 'change' }], intro: [{ required: true, message: '请输入课程简介', trigger: 'blur' }] }

const resetForm = () => { Object.assign(form, initialForm); formRef.value?.resetFields() }
const openDialog = (row?: any) => { resetForm(); if (row) { isEdit.value = true; Object.assign(form, { id: row.id, title: row.title, category: row.category, coverImage: row.coverImage || '', videoUrl: row.videoUrl || '', intro: row.intro, content: row.content, pointsReward: row.pointsReward || 10, duration: row.duration || 0, status: row.status, isRecommend: row.isRecommend || 0 }) } else { isEdit.value = false }; dialogVisible.value = true }

const fetchCourses = async () => { loading.value = true; try { const res = await getAdminCourseList({ page: page.value, pageSize: pageSize.value }); if (res.data) { courses.value = res.data.records || []; total.value = res.data.total || 0 } } catch (error) { ElMessage.error('获取课程列表失败') } finally { loading.value = false } }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchCourses() }
const handleSave = async () => { if (!formRef.value) return; await formRef.value.validate(async (valid) => { if (!valid) return; saving.value = true; try { if (isEdit.value && form.id) { await updateCourse(form.id, form); ElMessage.success('更新成功') } else { await createCourse(form); ElMessage.success('创建成功') }; dialogVisible.value = false; fetchCourses() } catch (error: any) { ElMessage.error(error.message || '保存失败') } finally { saving.value = false } }) }
const handleDelete = (row: any) => { ElMessageBox.confirm(`确认删除课程《${row.title}》吗？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(async () => { try { await deleteCourse(row.id); ElMessage.success('删除成功'); fetchCourses() } catch (error: any) { ElMessage.error(error.message || '删除失败') } }) }

onMounted(() => { fetchUploadConfig(); fetchCourses() })
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;

    .header-left {
      h1 { display: flex; align-items: center; gap: 8px; font-size: 24px; font-weight: 600; color: #1f2937; margin: 0 0 8px; }
      p { color: #6b7280; margin: 0; }
    }
  }

  .table-card {
    background: white;
    border-radius: 12px;
    padding: 20px 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .course-cell {
      display: flex;
      align-items: center;
      gap: 12px;

      .course-cover {
        width: 80px;
        height: 60px;
        object-fit: cover;
        border-radius: 8px;
      }

      .course-info {
        .course-title { display: block; font-weight: 500; color: #1f2937; }
        .course-category { font-size: 12px; color: #9ca3af; }
      }
    }

    .points-badge {
      background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
      color: white;
      padding: 4px 8px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 600;
    }

    .text-muted { color: #d1d5db; }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
  }
}

.cover-uploader {
  .cover-preview { width: 200px; height: 150px; object-fit: cover; border-radius: 8px; }
  .cover-placeholder {
    width: 200px; height: 150px; border: 2px dashed #e5e7eb; border-radius: 8px;
    display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 8px;
    color: #9ca3af; cursor: pointer; transition: all 0.2s;
    &:hover { border-color: #D64541; color: #D64541; }
  }
}

.video-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 8px;
  color: #16a34a;
}
</style>
