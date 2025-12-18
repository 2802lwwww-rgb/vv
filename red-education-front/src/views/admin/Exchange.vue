<template>
  <AdminLayout>
    <div class="admin-page exchange-page">
      <div class="page-header">
        <div class="header-left">
          <h1><el-icon><Ticket /></el-icon> 兑换管理</h1>
          <p>管理用户积分兑换订单</p>
        </div>
      </div>

      <!-- 核销卡片 -->
      <div class="verify-card">
        <div class="verify-header">
          <el-icon :size="24"><CircleCheck /></el-icon>
          <span>取件码核销</span>
        </div>
        <div class="verify-body">
          <el-input v-model="verifyCode" placeholder="请输入8位取件码" maxlength="8" clearable size="large" class="code-input" @keyup.enter="handleVerify" />
          <el-button type="primary" size="large" @click="handleVerify" :loading="verifying">
            <el-icon><Check /></el-icon> 立即核销
          </el-button>
        </div>
      </div>

      <!-- 记录列表 -->
      <div class="table-card">
        <div class="card-header">
          <span>兑换记录</span>
          <div class="filter-area">
            <el-select v-model="filterStatus" placeholder="状态筛选" clearable style="width: 120px" @change="fetchRecords">
              <el-option label="待发货" :value="0" />
              <el-option label="已发货" :value="1" />
              <el-option label="已完成" :value="2" />
            </el-select>
            <el-input v-model="filterCode" placeholder="搜索取件码" clearable style="width: 150px" @keyup.enter="fetchRecords" />
            <el-button type="primary" @click="fetchRecords">搜索</el-button>
          </div>
        </div>

        <el-table :data="records" v-loading="loading" class="modern-table">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="username" label="用户" width="100" />
          <el-table-column label="商品信息" min-width="180">
            <template #default="{ row }">
              <div class="product-cell">
                <span class="product-name">{{ row.productName }}</span>
                <el-tag :type="row.productType === 1 ? 'primary' : 'success'" effect="dark" size="small" round>{{ row.productType === 1 ? '虚拟' : '实物' }}</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="积分" width="90">
            <template #default="{ row }">
              <span class="points-cost">-{{ row.pointsCost }}</span>
            </template>
          </el-table-column>
          <el-table-column label="取件码" width="110">
            <template #default="{ row }">
              <el-tag v-if="row.pickupCode" type="warning" effect="dark">{{ row.pickupCode }}</el-tag>
              <span v-else class="no-code">-</span>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="90">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" effect="dark" round size="small">{{ row.statusName }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="时间" width="160">
            <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="140" fixed="right">
            <template #default="{ row }">
              <el-button v-if="row.status === 0" type="primary" text @click="handleUpdateStatus(row, 1)">发货</el-button>
              <el-button v-if="row.status === 1 && row.productType === 2" type="success" text @click="handleUpdateStatus(row, 2)">完成</el-button>
              <span v-if="row.status === 2" class="completed-text">✓ 已完成</span>
            </template>
          </el-table-column>
        </el-table>

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
import { Check, Ticket, CircleCheck } from '@element-plus/icons-vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { getExchangeRecords, verifyPickupCode, updateExchangeStatus } from '@/api/admin'

const loading = ref(false); const verifying = ref(false); const records = ref<any[]>([]); const page = ref(1); const pageSize = ref(10); const total = ref(0)
const verifyCode = ref(''); const filterStatus = ref<number | undefined>(undefined); const filterCode = ref('')

const fetchRecords = async () => { loading.value = true; try { const params: any = { current: page.value, size: pageSize.value }; if (filterStatus.value !== undefined) params.status = filterStatus.value; if (filterCode.value) params.pickupCode = filterCode.value; const res = await getExchangeRecords(params); if (res.data) { records.value = res.data.records || []; total.value = res.data.total || 0 } } catch { ElMessage.error('获取兑换记录失败') } finally { loading.value = false } }
const handlePageChange = (newPage: number) => { page.value = newPage; fetchRecords() }
const handleVerify = async () => { if (!verifyCode.value || verifyCode.value.length !== 8) { ElMessage.warning('请输入8位取件码'); return }; verifying.value = true; try { const res = await verifyPickupCode(verifyCode.value); ElMessage.success(`核销成功！商品：${res.data.productName}`); verifyCode.value = ''; fetchRecords() } catch (error: any) { ElMessage.error(error.message || '核销失败') } finally { verifying.value = false } }
const handleUpdateStatus = (row: any, newStatus: number) => { const statusText = newStatus === 1 ? '发货' : '完成'; ElMessageBox.confirm(`确认将订单标记为"${statusText}"状态？`, '提示', { confirmButtonText: '确认', cancelButtonText: '取消', type: 'warning' }).then(async () => { try { await updateExchangeStatus(row.id, newStatus); ElMessage.success('状态更新成功'); fetchRecords() } catch (error: any) { ElMessage.error(error.message || '更新失败') } }) }
const getStatusType = (status: number) => ({ 0: 'warning', 1: 'primary', 2: 'success' }[status] || 'info')
const formatTime = (time: string) => time ? time.replace('T', ' ').substring(0, 16) : '-'

onMounted(() => fetchRecords())
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

  .verify-card {
    background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
    border-radius: 16px;
    padding: 24px;
    margin-bottom: 24px;
    color: white;

    .verify-header {
      display: flex; align-items: center; gap: 8px; font-size: 18px; font-weight: 600; margin-bottom: 16px;
    }

    .verify-body {
      display: flex; gap: 12px;
      .code-input { max-width: 240px; :deep(.el-input__wrapper) { background: rgba(255, 255, 255, 0.9); } }
      .el-button { background: white; color: #8B5CF6; border: none; &:hover { background: #f3f4f6; } }
    }
  }

  .table-card {
    background: white; border-radius: 12px; padding: 20px 24px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);

    .card-header {
      display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;
      font-weight: 600; font-size: 16px; color: #1f2937;
      .filter-area { display: flex; gap: 8px; }
    }

    .product-cell { .product-name { display: block; font-weight: 500; margin-bottom: 4px; } }
    .points-cost { color: #EF4444; font-weight: 600; }
    .no-code { color: #d1d5db; }
    .completed-text { color: #22C55E; font-size: 13px; }
    .pagination-wrapper { display: flex; justify-content: center; margin-top: 24px; }
  }
}
</style>
