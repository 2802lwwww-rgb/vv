<template>
  <AdminLayout>
    <div class="admin-page config-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><Setting /></el-icon> 系统配置</h1>
          <p>管理系统全局设置</p>
        </div>
        <div class="header-actions">
          <el-button @click="fetchConfig"><el-icon><Refresh /></el-icon> 重新加载</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving"><el-icon><Check /></el-icon> 保存配置</el-button>
        </div>
      </div>

      <div class="config-card" v-loading="loading">
        <el-tabs v-model="activeTab" class="modern-tabs">
          <!-- 网站设置 -->
          <el-tab-pane label="网站设置" name="site">
            <el-form label-width="150px" class="config-form">
              <el-form-item label="网站名称"><el-input v-model="configMap['site.name']" placeholder="请输入网站名称" /></el-form-item>
              <el-form-item label="网站描述"><el-input v-model="configMap['site.description']" type="textarea" :rows="2" placeholder="请输入网站描述" /></el-form-item>
              <el-form-item label="网站关键词"><el-input v-model="configMap['site.keywords']" placeholder="多个关键词用逗号分隔" /></el-form-item>
              <el-form-item label="ICP备案号"><el-input v-model="configMap['site.icp']" placeholder="请输入ICP备案号" /></el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 积分规则 -->
          <el-tab-pane label="积分规则" name="points">
            <el-form label-width="200px" class="config-form">
              <div class="section-title">🎓 学习相关</div>
              <el-form-item label="完成课程奖励"><el-input-number v-model.number="configMap['points.course_complete']" :min="0" :max="100" /><span class="unit">积分</span></el-form-item>

              <div class="section-title">📝 考试相关</div>
              <el-form-item label="考试及格奖励"><el-input-number v-model.number="configMap['points.exam_pass']" :min="0" :max="100" /><span class="unit">积分</span></el-form-item>
              <el-form-item label="考试优秀奖励（90分以上）"><el-input-number v-model.number="configMap['points.exam_excellent']" :min="0" :max="100" /><span class="unit">积分</span></el-form-item>
              <el-form-item label="考试满分奖励"><el-input-number v-model.number="configMap['points.exam_perfect']" :min="0" :max="100" /><span class="unit">积分</span></el-form-item>

              <div class="section-title">💬 社区相关</div>
              <el-form-item label="发布帖子奖励"><el-input-number v-model.number="configMap['points.post_publish']" :min="0" :max="50" /><span class="unit">积分</span></el-form-item>
              <el-form-item label="帖子通过审核奖励"><el-input-number v-model.number="configMap['points.post_approved']" :min="0" :max="50" /><span class="unit">积分</span></el-form-item>
              <el-form-item label="帖子被点赞奖励"><el-input-number v-model.number="configMap['points.post_liked']" :min="0" :max="10" /><span class="unit">积分/次</span></el-form-item>
              <el-form-item label="发表评论奖励"><el-input-number v-model.number="configMap['points.comment_publish']" :min="0" :max="20" /><span class="unit">积分</span></el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 上传设置 -->
          <el-tab-pane label="上传设置" name="upload">
            <el-form label-width="180px" class="config-form">
              <div class="section-title">📦 文件大小限制</div>
              <el-form-item label="文档最大上传大小"><el-input-number v-model.number="configMap['upload.doc_max_size']" :min="1" :max="10240" /><span class="unit">MB</span></el-form-item>
              <el-form-item label="视频最大上传大小"><el-input-number v-model.number="configMap['upload.video_max_size']" :min="1" :max="10240" /><span class="unit">MB</span></el-form-item>
              <el-form-item label="图片最大上传大小"><el-input-number v-model.number="configMap['upload.image_max_size']" :min="1" :max="10240" /><span class="unit">MB</span></el-form-item>

              <div class="section-title">📁 允许的文件类型</div>
              <el-form-item label="允许的文档类型"><el-input v-model="configMap['upload.allowed_doc_types']" placeholder="pdf,doc,docx" /></el-form-item>
              <el-form-item label="允许的视频类型"><el-input v-model="configMap['upload.allowed_video_types']" placeholder="mp4,avi,mov" /></el-form-item>
              <el-form-item label="允许的图片类型"><el-input v-model="configMap['upload.allowed_image_types']" placeholder="jpg,jpeg,png,gif" /></el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 系统设置 -->
          <el-tab-pane label="系统设置" name="system">
            <el-form label-width="180px" class="config-form">
              <div class="section-title">⚙️ 功能开关</div>
              <el-form-item label="开放用户注册"><el-switch v-model="configMap['system.register_enabled']" active-value="true" inactive-value="false" /></el-form-item>
              <el-form-item label="帖子需要审核"><el-switch v-model="configMap['system.post_need_audit']" active-value="true" inactive-value="false" /></el-form-item>
              <el-form-item label="评论需要审核"><el-switch v-model="configMap['system.comment_need_audit']" active-value="true" inactive-value="false" /></el-form-item>
              <el-form-item label="资源需要审核"><el-switch v-model="configMap['system.resource_need_audit']" active-value="true" inactive-value="false" /></el-form-item>

              <div class="section-title">🔧 其他设置</div>
              <el-form-item label="默认分页大小"><el-input-number v-model.number="configMap['system.page_size']" :min="5" :max="50" /><span class="unit">条/页</span></el-form-item>
              <el-form-item label="会话超时时间"><el-input-number v-model.number="configMap['system.session_timeout']" :min="600" :max="86400" :step="600" /><span class="unit">秒</span></el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Check, Refresh, Setting } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getSystemConfig, updateSystemConfig } from '@/api/admin'

const loading = ref(false); const saving = ref(false); const activeTab = ref('site')
const configMap = reactive<Record<string, any>>({})
const configList = ref<any[]>([])

const fetchConfig = async () => { loading.value = true; try { const res = await getSystemConfig(); if (res.data) { configList.value = res.data; res.data.forEach((item: any) => { configMap[item.configKey] = /^\d+$/.test(item.configValue) ? parseInt(item.configValue) : item.configValue }) } } catch { ElMessage.error('获取配置失败') } finally { loading.value = false } }
const handleSave = async () => { saving.value = true; try { const managedKeys = Object.keys(configMap).filter(key => key.includes('.')); const updates = managedKeys.map(key => ({ configKey: key, configValue: String(configMap[key] ?? '') })); if (updates.length === 0) { ElMessage.warning('没有需要保存的配置'); return }; await updateSystemConfig({ configs: updates }); ElMessage.success('保存成功') } catch (error: any) { ElMessage.error(error.message || '保存失败') } finally { saving.value = false } }

onMounted(() => fetchConfig())
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

  .config-card {
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .modern-tabs {
      :deep(.el-tabs__header) { margin-bottom: 24px; }
      :deep(.el-tabs__item.is-active) { color: #D64541; }
      :deep(.el-tabs__active-bar) { background: #D64541; }
    }

    .config-form {
      max-width: 600px;

      .section-title {
        font-size: 15px;
        font-weight: 600;
        color: #1f2937;
        margin: 24px 0 16px;
        padding-bottom: 8px;
        border-bottom: 1px solid #f3f4f6;

        &:first-child { margin-top: 0; }
      }

      .unit {
        margin-left: 12px;
        color: #6b7280;
        font-size: 14px;
      }
    }
  }
}
</style>
