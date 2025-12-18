<template>
  <AdminLayout>
    <div class="admin-page questions-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><Document /></el-icon> 题库管理</h1>
          <p>管理考试题目，支持批量导入</p>
        </div>
        <div class="header-actions">
          <el-button type="success" @click="downloadTemplate"><el-icon><Download /></el-icon> 下载模板</el-button>
          <el-upload style="display: inline-block; margin: 0 10px" :action="importUrl" :headers="uploadHeaders" :show-file-list="false" :on-success="handleImportSuccess" :on-error="handleImportError" :before-upload="beforeImport" accept=".xlsx,.xls">
            <el-button type="warning" :loading="importing"><el-icon><Upload /></el-icon> 批量导入</el-button>
          </el-upload>
          <el-button type="primary" @click="openDialog()"><el-icon><Plus /></el-icon> 新增题目</el-button>
        </div>
      </div>

      <div class="table-card">
        <el-table :data="questions" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="content" label="题目内容" min-width="300" show-overflow-tooltip />
          <el-table-column label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="row.type === 'SINGLE_CHOICE' ? 'primary' : 'warning'" effect="dark" round>{{ getTypeName(row.type) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="难度" width="90">
            <template #default="{ row }">
              <span :class="'difficulty-' + row.difficulty">{{ getDifficultyName(row.difficulty) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="分值" width="70">
            <template #default="{ row }">
              <span class="score-badge">{{ row.score }}</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
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

      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑题目' : '新增题目'" width="680px" class="modern-dialog">
        <el-form :model="form" label-width="100px" ref="formRef" :rules="formRules">
          <el-form-item label="题目类型" prop="type">
            <el-radio-group v-model="form.type">
              <el-radio label="SINGLE_CHOICE">单选题</el-radio>
              <el-radio label="JUDGE">判断题</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="题目内容" prop="content">
            <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入题目内容" />
          </el-form-item>
          <template v-if="form.type === 'SINGLE_CHOICE'">
            <el-form-item label="选项A" prop="optionA"><el-input v-model="form.optionA" placeholder="请输入选项A" /></el-form-item>
            <el-form-item label="选项B" prop="optionB"><el-input v-model="form.optionB" placeholder="请输入选项B" /></el-form-item>
            <el-form-item label="选项C"><el-input v-model="form.optionC" placeholder="请输入选项C（可选）" /></el-form-item>
            <el-form-item label="选项D"><el-input v-model="form.optionD" placeholder="请输入选项D（可选）" /></el-form-item>
          </template>
          <el-form-item label="正确答案" prop="correctAnswer">
            <el-select v-model="form.correctAnswer" placeholder="请选择正确答案" style="width: 200px">
              <template v-if="form.type === 'SINGLE_CHOICE'">
                <el-option label="A" value="A" /><el-option label="B" value="B" />
                <el-option label="C" value="C" v-if="form.optionC" /><el-option label="D" value="D" v-if="form.optionD" />
              </template>
              <template v-else>
                <el-option label="正确" value="TRUE" /><el-option label="错误" value="FALSE" />
              </template>
            </el-select>
          </el-form-item>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="难度" prop="difficulty">
                <el-select v-model="form.difficulty" style="width: 100%">
                  <el-option label="简单" :value="1" /><el-option label="中等" :value="2" /><el-option label="困难" :value="3" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="分值" prop="score">
                <el-input-number v-model="form.score" :min="1" :max="20" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="答案解析">
            <el-input v-model="form.explanation" type="textarea" :rows="2" placeholder="请输入答案解析（可选）" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Document, Plus, Edit, Delete, Download, Upload } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getQuestionList, createQuestion, updateQuestion, deleteQuestion } from '@/api/admin'

const loading = ref(false); const saving = ref(false); const importing = ref(false); const dialogVisible = ref(false); const isEdit = ref(false); const questions = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0); const formRef = ref<FormInstance>()

const importUrl = `${import.meta.env.VITE_API_BASE_URL}/admin/questions/import`
const uploadHeaders = { Authorization: `Bearer ${localStorage.getItem('token')}` }

const downloadTemplate = async () => { try { const response = await fetch(`${import.meta.env.VITE_API_BASE_URL}/admin/questions/template`, { headers: { Authorization: `Bearer ${localStorage.getItem('token')}` } }); if (!response.ok) throw new Error('下载失败'); const blob = await response.blob(); const url = window.URL.createObjectURL(blob); const a = document.createElement('a'); a.href = url; a.download = '题目导入模板.xlsx'; document.body.appendChild(a); a.click(); window.URL.revokeObjectURL(url); document.body.removeChild(a) } catch { ElMessage.error('下载模板失败') } }
const beforeImport = () => { importing.value = true; return true }
const handleImportSuccess = (response: any) => { importing.value = false; if (response.code === 200) { ElMessage.success(response.data || '导入成功'); fetchQuestions() } else { ElMessage.error(response.message || '导入失败') } }
const handleImportError = () => { importing.value = false; ElMessage.error('导入失败，请检查文件格式') }

const initialForm = { id: null as number | null, type: 'SINGLE_CHOICE', content: '', optionA: '', optionB: '', optionC: '', optionD: '', correctAnswer: '', explanation: '', difficulty: 1, score: 5 }
const form = reactive({ ...initialForm })
const formRules: FormRules = { type: [{ required: true, message: '请选择题目类型', trigger: 'change' }], content: [{ required: true, message: '请输入题目内容', trigger: 'blur' }], correctAnswer: [{ required: true, message: '请选择正确答案', trigger: 'change' }], difficulty: [{ required: true, message: '请选择难度', trigger: 'change' }], score: [{ required: true, message: '请输入分值', trigger: 'blur' }] }

const resetForm = () => { Object.assign(form, initialForm); formRef.value?.resetFields() }
const openDialog = (row?: any) => { resetForm(); if (row) { isEdit.value = true; Object.assign(form, { id: row.id, type: row.type, content: row.content, optionA: row.optionA || '', optionB: row.optionB || '', optionC: row.optionC || '', optionD: row.optionD || '', correctAnswer: row.correctAnswer, explanation: row.explanation || '', difficulty: row.difficulty, score: row.score }) } else { isEdit.value = false }; dialogVisible.value = true }

const fetchQuestions = async () => { loading.value = true; try { const res = await getQuestionList({ current: page.value, size: pageSize.value }); if (res.data) { questions.value = res.data.records || []; total.value = res.data.total || 0 } } catch { ElMessage.error('获取题目列表失败') } finally { loading.value = false } }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchQuestions() }
const handleSave = async () => { if (!formRef.value) return; await formRef.value.validate(async (valid) => { if (!valid) return; saving.value = true; try { if (isEdit.value && form.id) { await updateQuestion(form.id, form); ElMessage.success('更新成功') } else { await createQuestion(form); ElMessage.success('创建成功') }; dialogVisible.value = false; fetchQuestions() } catch (error: any) { ElMessage.error(error.message || '保存失败') } finally { saving.value = false } }) }
const handleDelete = (row: any) => { ElMessageBox.confirm('确认删除此题目吗？', '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(async () => { try { await deleteQuestion(row.id); ElMessage.success('删除成功'); fetchQuestions() } catch (error: any) { ElMessage.error(error.message || '删除失败') } }) }

const getTypeName = (type: string) => ({ 'SINGLE_CHOICE': '单选题', 'JUDGE': '判断题' }[type] || '未知')
const getDifficultyName = (difficulty: number) => ({ 1: '简单', 2: '中等', 3: '困难' }[difficulty] || '未知')

onMounted(() => fetchQuestions())
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    display: flex; justify-content: space-between; align-items: flex-start; margin-bottom: 24px; flex-wrap: wrap; gap: 16px;
    .header-left {
      h1 { display: flex; align-items: center; gap: 8px; font-size: 24px; font-weight: 600; color: #1f2937; margin: 0 0 8px; }
      p { color: #6b7280; margin: 0; }
    }
    .header-actions { display: flex; align-items: center; }
  }

  .table-card {
    background: white; border-radius: 12px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .difficulty-1 { color: #22C55E; font-weight: 500; }
    .difficulty-2 { color: #F59E0B; font-weight: 500; }
    .difficulty-3 { color: #EF4444; font-weight: 500; }

    .score-badge {
      background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
      color: white; padding: 4px 10px; border-radius: 12px; font-size: 12px; font-weight: 600;
    }

    .pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; }
  }
}
</style>
