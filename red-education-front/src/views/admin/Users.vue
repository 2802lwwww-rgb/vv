<template>
  <AdminLayout>
    <div class="admin-page users-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <h1><el-icon><User /></el-icon> 用户管理</h1>
        <p>管理平台用户，设置角色权限</p>
      </div>

      <!-- 筛选卡片 -->
      <div class="filter-card">
        <el-form :inline="true" :model="filters" class="filter-form">
          <el-form-item label="关键词">
            <el-input v-model="filters.keyword" placeholder="用户名/邮箱" clearable @keyup.enter="handleSearch" />
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="filters.role" clearable placeholder="选择角色">
              <el-option label="全部" value="" />
              <el-option label="普通用户" value="USER" />
              <el-option label="内容管理员" value="CONTENT_ADMIN" />
              <el-option label="系统管理员" value="SYS_ADMIN" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="filters.status" clearable placeholder="选择状态">
              <el-option label="全部" value="" />
              <el-option label="正常" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-form-item>
        </el-form>
      </div>

      <!-- 表格卡片 -->
      <div class="table-card">
        <el-table :data="users" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column label="用户信息" min-width="200">
            <template #default="{ row }">
              <div class="user-cell">
                <el-avatar :size="40" :src="getAvatarUrl(row.avatar)" class="user-avatar">{{ row.nickname?.charAt(0) }}</el-avatar>
                <div class="user-info">
                  <span class="username">{{ row.nickname || row.username }}</span>
                  <span class="email">{{ row.email }}</span>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="角色" width="130">
            <template #default="{ row }">
              <el-tag :type="getRoleType(row.role)" effect="dark" round>{{ getRoleName(row.role) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
            </template>
          </el-table-column>
          <el-table-column prop="points" label="积分" width="100">
            <template #default="{ row }">
              <span class="points-value">{{ row.points }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="注册时间" width="160">
            <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text @click="handleEdit(row)">
                <el-icon><Edit /></el-icon> 编辑
              </el-button>
              <el-button type="warning" text @click="handleResetPassword(row)">
                <el-icon><Refresh /></el-icon> 重置密码
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination
            background
            layout="total, prev, pager, next, sizes"
            :current-page="filters.page"
            :page-size="filters.pageSize"
            :page-sizes="[10, 20, 50, 100]"
            :total="total"
            @current-change="handlePageChange"
            @size-change="handleSizeChange"
          />
        </div>
      </div>

      <!-- 编辑对话框 -->
      <el-dialog v-model="dialogVisible" title="编辑用户角色" width="480px" class="modern-dialog">
        <el-form :model="editForm" :rules="rules" ref="formRef" label-width="80px">
          <el-form-item label="角色" prop="role">
            <el-select v-model="editForm.role" style="width: 100%">
              <el-option label="普通用户" value="USER" />
              <el-option label="内容管理员" value="CONTENT_ADMIN" />
              <el-option label="系统管理员" value="SYS_ADMIN" />
            </el-select>
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
import { Search, User, Edit, Refresh } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getUserList, updateUserRole, updateUserStatus, resetUserPassword } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const users = ref<any[]>([])
const total = ref(0)

const filters = reactive({ page: 1, pageSize: 10, keyword: '', role: '', status: '' as number | string })
const editForm = reactive({ id: 0, role: '' })
const rules: FormRules = { role: [{ required: true, message: '请选择角色', trigger: 'change' }] }

// 获取完整头像URL
const getAvatarUrl = (avatar: string) => {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  return `${baseUrl}${avatar}`
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const params = { ...filters, keyword: filters.keyword || undefined, role: filters.role || undefined, status: filters.status !== '' ? filters.status : undefined }
    const res = await getUserList(params)
    if (res.data) { users.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch (error) { ElMessage.error('获取用户列表失败') }
  finally { loading.value = false }
}

const handleSearch = () => { filters.page = 1; fetchUsers() }
const handleReset = () => { filters.keyword = ''; filters.role = ''; filters.status = ''; handleSearch() }
const handlePageChange = (page: number) => { filters.page = page; fetchUsers() }
const handleSizeChange = (size: number) => { filters.pageSize = size; filters.page = 1; fetchUsers() }

const handleStatusChange = async (row: any) => {
  try { await updateUserStatus(row.id, row.status); ElMessage.success(row.status === 1 ? '用户已启用' : '用户已禁用') }
  catch (error: any) { ElMessage.error(error.message || '操作失败'); row.status = row.status === 1 ? 0 : 1 }
}

const handleEdit = (row: any) => { Object.assign(editForm, { id: row.id, role: row.role }); dialogVisible.value = true }

const handleSave = async () => { if (!formRef.value) return; await formRef.value.validate(async (valid) => { if (valid) { saving.value = true; try { await updateUserRole(editForm.id, editForm.role); ElMessage.success('更新成功'); dialogVisible.value = false; fetchUsers() } catch (error: any) { ElMessage.error(error.message || '更新失败') } finally { saving.value = false } } }) }

const handleResetPassword = (row: any) => { ElMessageBox.confirm(`确认重置用户 ${row.username} 的密码吗？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(async () => { try { await resetUserPassword(row.id); ElMessage.success('密码已重置为：123456') } catch (error: any) { ElMessage.error(error.message || '重置失败') } }) }

const getRoleType = (role: string) => ({ 'SYS_ADMIN': 'danger', 'CONTENT_ADMIN': 'warning', 'USER': 'success' }[role] || 'info')
const getRoleName = (role: string) => ({ 'SYS_ADMIN': '系统管理员', 'CONTENT_ADMIN': '内容管理员', 'USER': '普通用户' }[role] || '未知')
const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD HH:mm')

onMounted(() => fetchUsers())
</script>

<style scoped lang="scss">
.admin-page {
  .page-header {
    margin-bottom: 24px;
    h1 { display: flex; align-items: center; gap: 8px; font-size: 24px; font-weight: 600; color: #1f2937; margin: 0 0 8px; }
    p { color: #6b7280; margin: 0; }
  }

  .filter-card {
    background: white;
    border-radius: 12px;
    padding: 20px 24px;
    margin-bottom: 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  }

  .table-card {
    background: white;
    border-radius: 12px;
    padding: 20px 24px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .modern-table {
      .user-cell {
        display: flex;
        align-items: center;
        gap: 12px;

        .user-avatar {
          background: linear-gradient(135deg, #D64541 0%, #FF6B6B 100%);
          color: white;
          font-weight: 600;
        }

        .user-info {
          .username { display: block; font-weight: 500; color: #1f2937; }
          .email { font-size: 12px; color: #9ca3af; }
        }
      }

      .points-value {
        font-weight: 600;
        color: #F59E0B;
      }
    }

    .pagination-wrapper {
      display: flex;
      justify-content: center;
      margin-top: 24px;
    }
  }
}
</style>
