<template>
  <AdminLayout>
    <div class="admin-page resource-audit-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><Checked /></el-icon> 资源审核</h1>
          <p>审核用户上传的资源文件</p>
        </div>
      </div>

      <div class="table-card">
        <el-table :data="pendingResources" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="70" />
          <el-table-column prop="title" label="资源标题" min-width="200" />
          <el-table-column prop="uploaderName" label="上传者" width="120" />
          <el-table-column label="上传时间" width="160">
            <template #default="{ row }">{{ formatDate(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" text @click="$router.push(`/admin/resources/${row.id}`)"><el-icon><View /></el-icon> 查看</el-button>
              <el-button type="success" text @click="handleApprove(row)"><el-icon><Select /></el-icon> 通过</el-button>
              <el-button type="danger" text @click="handleReject(row)"><el-icon><CloseBold /></el-icon> 驳回</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="pendingResources.length === 0 && !loading" description="暂无待审核资源" />

        <div class="pagination-wrapper" v-if="total > 0">
          <el-pagination background layout="total, prev, pager, next" :current-page="page" :page-size="pageSize" :total="total" @current-change="handlePageChange" />
        </div>
      </div>
    </div>
  </AdminLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Checked, View, Select, CloseBold } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getAdminResourceList, auditResource } from '@/api/admin'
import dayjs from 'dayjs'

const loading = ref(false); const pendingResources = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)

const fetchPendingResources = async () => { loading.value = true; try { const res = await getAdminResourceList({ current: page.value, size: pageSize.value, status: 0 }); if (res.data) { pendingResources.value = res.data.records || []; total.value = res.data.total || 0 } } catch { ElMessage.error('获取待审核资源失败') } finally { loading.value = false } }
const handlePageChange = (val: number) => { page.value = val; fetchPendingResources() }
const handleApprove = async (row: any) => { try { await auditResource(row.id, '1', '审核通过'); ElMessage.success('审核通过'); fetchPendingResources() } catch (error: any) { ElMessage.error(error.message || '操作失败') } }
const handleReject = async (row: any) => { ElMessageBox.prompt('请输入驳回原因', '驳回资源', { confirmButtonText: '确认', cancelButtonText: '取消', inputPattern: /.+/, inputErrorMessage: '请输入驳回原因' }).then(async ({ value }) => { try { await auditResource(row.id, '2', value); ElMessage.success('已驳回'); fetchPendingResources() } catch (error: any) { ElMessage.error(error.message || '操作失败') } }) }
const formatDate = (date: string) => date ? dayjs(date).format('YYYY-MM-DD HH:mm') : '-'

onMounted(() => fetchPendingResources())
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
    .pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; }
  }
}
</style>
