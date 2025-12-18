<template>
  <AdminLayout>
    <div class="admin-page exams-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><EditPen /></el-icon> 试卷管理</h1>
          <p>创建和管理考试试卷</p>
        </div>
        <el-button type="primary" @click="openDialog()">
          <el-icon><Plus /></el-icon> 新增试卷
        </el-button>
      </div>

      <div class="table-card">
        <el-table :data="exams" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="name" label="试卷名称" min-width="200" />
          <el-table-column label="类型" width="110">
            <template #default="{ row }">
              <el-tag :type="row.type === 1 ? 'warning' : 'primary'" effect="dark" round>
                {{ row.type === 1 ? '随机组卷' : '固定试卷' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时长" width="80">
            <template #default="{ row }">{{ row.duration }}分</template>
          </el-table-column>
          <el-table-column label="分数" width="120">
            <template #default="{ row }">
              <span class="score-info">{{ row.passScore }}/{{ row.totalScore }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="questionCount" label="题数" width="70" />
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-switch :model-value="row.status === 1" @change="handleStatusChange(row)" />
            </template>
          </el-table-column>
          <el-table-column label="操作" width="220" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text @click="openDialog(row)"><el-icon><Edit /></el-icon> 编辑</el-button>
              <el-button type="success" text @click="handleManageQuestions(row)"><el-icon><List /></el-icon> 题目</el-button>
              <el-button type="danger" text @click="handleDelete(row)"><el-icon><Delete /></el-icon> 删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination background layout="total, prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="handlePageChange" />
        </div>
      </div>

      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑试卷' : '新增试卷'" width="620px" class="modern-dialog">
        <el-form :model="form" label-width="100px" ref="formRef" :rules="formRules">
          <el-form-item label="试卷名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入试卷名称" />
          </el-form-item>
          <el-form-item label="试卷说明">
            <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入试卷说明（可选）" />
          </el-form-item>
          <el-form-item label="试卷类型" prop="type">
            <el-radio-group v-model="form.type">
              <el-radio :label="1">随机组卷</el-radio>
              <el-radio :label="2">固定试卷</el-radio>
            </el-radio-group>
          </el-form-item>
          <template v-if="form.type === 2">
            <el-alert title="固定试卷请在创建后点击'题目'按钮进行选题" type="info" :closable="false" style="margin-bottom: 20px;" />
          </template>
          <template v-if="form.type === 1">
            <el-alert title="随机组卷将根据以下条件自动抽取题目" type="success" :closable="false" style="margin-bottom: 20px;" />
            <el-row :gutter="20">
              <el-col :span="12">
                <el-form-item label="题目难度">
                  <el-select v-model="form.randomConfig.difficulty" placeholder="不限" clearable style="width: 100%">
                    <el-option label="简单" :value="1" />
                    <el-option label="中等" :value="2" />
                    <el-option label="困难" :value="3" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="题目类型">
                  <el-select v-model="form.randomConfig.questionType" placeholder="不限" clearable style="width: 100%">
                    <el-option label="单选题" value="SINGLE_CHOICE" />
                    <el-option label="判断题" value="JUDGE" />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="考试时长" prop="duration">
                <el-input-number v-model="form.duration" :min="10" :max="180" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="题目数量" prop="questionCount">
                <el-input-number v-if="form.type === 2" v-model="form.questionCount" :min="0" :max="100" style="width: 100%" disabled />
                <el-input-number v-else v-model="form.randomConfig.questionCount" :min="5" :max="100" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <template v-if="form.type === 2">
            <el-alert title="固定试卷的总分、及格分将根据所选题目自动计算" type="warning" :closable="false" style="margin-bottom: 20px;" />
          </template>
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="总分" prop="totalScore">
                <el-input-number v-model="form.totalScore" :min="0" :max="500" style="width: 100%" :disabled="form.type === 2" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="及格分" prop="passScore">
                <el-input-number v-model="form.passScore" :min="0" :max="500" style="width: 100%" :disabled="form.type === 2" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio :label="1">启用</el-radio>
              <el-radio :label="0">禁用</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
        </template>
      </el-dialog>

      <el-dialog v-model="questionDialogVisible" title="题目管理" width="850px" class="modern-dialog">
        <el-alert title="使用说明：勾选题目后点击【>】按钮添加到右边，点击【<】按钮移除" type="info" :closable="false" style="margin-bottom: 15px;" />
        <div style="text-align: center">
          <el-transfer v-model="selectedQuestionIds" :data="allQuestions" :titles="['可选题目', '已选题目']" :button-texts="['移除', '添加']" filterable filter-placeholder="请输入题目内容关键词" :props="{ key: 'id', label: 'content' }">
            <template #default="{ option }">
              <span :title="option.content">
                <el-tag size="small" style="margin-right: 5px">{{ option.type === 'SINGLE_CHOICE' ? '单选' : '判断' }}</el-tag>
                {{ option.content }}
              </span>
            </template>
          </el-transfer>
        </div>
        <div style="margin-top: 10px; text-align: center; color: #6b7280; font-size: 13px">
          已选择 <strong>{{ selectedQuestionIds.length }}</strong> 道题目
        </div>
        <template #footer>
          <el-button @click="questionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSaveQuestions" :loading="savingQuestions">保存</el-button>
        </template>
      </el-dialog>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { EditPen, Plus, Edit, Delete, List } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getAdminExamList, createExam, updateExam, deleteExam, updateExamStatus, getQuestionList, getExamQuestions, setExamQuestions } from '@/api/admin'

const loading = ref(false); const saving = ref(false); const dialogVisible = ref(false); const isEdit = ref(false); const exams = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0); const formRef = ref<FormInstance>()
const questionDialogVisible = ref(false); const savingQuestions = ref(false); const currentExamId = ref<number | null>(null); const allQuestions = ref<any[]>([]); const selectedQuestionIds = ref<number[]>([])

const initialForm = { id: null as number | null, name: '', description: '', type: 2, duration: 60, totalScore: 100, passScore: 60, questionCount: 10, status: 1, randomConfig: { difficulty: null as number | null, questionType: null as string | null, questionCount: 10, category: null as string | null } }
const form = reactive({ ...initialForm, randomConfig: { ...initialForm.randomConfig } })
const formRules: FormRules = { name: [{ required: true, message: '请输入试卷名称', trigger: 'blur' }], type: [{ required: true, message: '请选择试卷类型', trigger: 'change' }], duration: [{ required: true, message: '请输入考试时长', trigger: 'blur' }], totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }], passScore: [{ required: true, message: '请输入及格分', trigger: 'blur' }], questionCount: [{ required: true, message: '请输入题目数量', trigger: 'blur' }] }

const resetForm = () => { Object.assign(form, initialForm); Object.assign(form.randomConfig, initialForm.randomConfig); formRef.value?.resetFields() }
const openDialog = (row?: any) => { resetForm(); if (row) { isEdit.value = true; Object.assign(form, { id: row.id, name: row.name, description: row.description || '', type: row.type, duration: row.duration, totalScore: row.totalScore, passScore: row.passScore, questionCount: row.questionCount, status: row.status }) } else { isEdit.value = false }; dialogVisible.value = true }

const fetchExams = async () => { loading.value = true; try { const res = await getAdminExamList({ current: page.value, size: pageSize.value }); if (res.data) { exams.value = res.data.records || []; total.value = res.data.total || 0 } } catch { ElMessage.error('获取试卷列表失败') } finally { loading.value = false } }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchExams() }
const handleSave = async () => { if (!formRef.value) return; await formRef.value.validate(async (valid) => { if (!valid) return; saving.value = true; try { if (isEdit.value && form.id) { await updateExam(form.id, form); ElMessage.success('更新成功') } else { await createExam(form); ElMessage.success('创建成功') }; dialogVisible.value = false; fetchExams() } catch (error: any) { ElMessage.error(error.message || '保存失败') } finally { saving.value = false } }) }
const handleStatusChange = async (row: any) => { try { const newStatus = row.status === 1 ? 0 : 1; await updateExamStatus(row.id, newStatus); ElMessage.success('状态更新成功'); fetchExams() } catch (error: any) { ElMessage.error(error.message || '状态更新失败') } }
const handleManageQuestions = async (row: any) => { currentExamId.value = row.id; questionDialogVisible.value = true; try { const [questionsRes, selectedRes] = await Promise.all([getQuestionList({ current: 1, size: 1000 }), getExamQuestions(row.id)]); if (questionsRes.data) allQuestions.value = questionsRes.data.records || []; if (selectedRes.data) selectedQuestionIds.value = selectedRes.data || [] } catch { ElMessage.error('获取题目数据失败') } }
const handleSaveQuestions = async () => { if (!currentExamId.value) return; savingQuestions.value = true; try { await setExamQuestions(currentExamId.value, selectedQuestionIds.value); ElMessage.success('题目设置成功'); questionDialogVisible.value = false; fetchExams() } catch (error: any) { ElMessage.error(error.message || '设置失败') } finally { savingQuestions.value = false } }
const handleDelete = (row: any) => { ElMessageBox.confirm(`确认删除试卷《${row.name}》吗？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(async () => { try { await deleteExam(row.id); ElMessage.success('删除成功'); fetchExams() } catch (error: any) { ElMessage.error(error.message || '删除失败') } }) }

onMounted(() => fetchExams())
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

  .table-card {
    background: white; border-radius: 12px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    .score-info { color: #8B5CF6; font-weight: 600; }
    .pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; }
  }
}
</style>
