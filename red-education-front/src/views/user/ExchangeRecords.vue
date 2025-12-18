<template>
  <MainLayout>
    <div class="exchange-records-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="title-icon">🎁</span>
            兑换记录
          </h1>
          <p class="page-desc">查看积分兑换历史</p>
        </div>
      </div>

      <div class="page-container">
        <div class="section-card" v-loading="loading">
          <div class="records-list">
            <div 
              v-for="(record, index) in records" 
              :key="record.id" 
              class="record-item animate-fade-in-up"
              :style="{ animationDelay: `${index * 50}ms` }"
            >
              <div class="record-header">
                <span class="order-no">订单号：{{ record.orderNo }}</span>
                <el-tag :type="getStatusType(record.status)" effect="dark" size="small">
                  {{ getStatusText(record.status) }}
                </el-tag>
              </div>

              <div class="record-content">
                <div class="goods-image">
                  <img :src="record.productImage || 'https://picsum.photos/100/100?random=' + record.id" alt="商品图片" />
                </div>
                <div class="goods-info">
                  <h4 class="goods-name">{{ record.productName }}</h4>
                  <div class="goods-meta">
                    <el-tag :type="record.productType === 1 ? 'success' : 'warning'" size="small">
                      {{ record.productType === 1 ? '虚拟商品' : '实体商品' }}
                    </el-tag>
                    <span class="exchange-time">{{ formatDate(record.createTime) }}</span>
                  </div>
                  <div class="points-used">
                    <el-icon><Coin /></el-icon>
                    <span>{{ record.pointsCost }} 积分</span>
                  </div>
                </div>
              </div>

              <div class="record-footer" v-if="record.productType === 2 && record.pickupCode">
                <div class="pickup-section">
                  <h5 class="pickup-label">取件码</h5>
                  <template v-if="record.status === 2">
                    <div class="pickup-code verified">已核销</div>
                    <p class="pickup-hint">该取件码已使用完成</p>
                  </template>
                  <template v-else>
                    <div class="pickup-code">{{ record.pickupCode }}</div>
                    <p class="pickup-hint">请凭此码到指定地点领取商品</p>
                  </template>
                </div>
              </div>
            </div>

            <el-empty v-if="records.length === 0 && !loading" description="暂无兑换记录" />
          </div>

          <div class="pagination-wrapper" v-if="total > 0">
            <el-pagination
              background
              layout="prev, pager, next"
              :current-page="page"
              :page-size="pageSize"
              :total="total"
              @current-change="handlePageChange"
            />
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Coin } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getExchangeRecords } from '@/api/points'
import dayjs from 'dayjs'

const loading = ref(false)
const records = ref<any[]>([])
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const fetchRecords = async () => {
  loading.value = true
  try {
    const res = await getExchangeRecords({ current: page.value, size: pageSize.value })
    if (res.data) {
      records.value = res.data.records || []
      total.value = res.data.total || 0
    }
  } catch (error) {
    ElMessage.error('获取兑换记录失败')
  } finally {
    loading.value = false
  }
}

const handlePageChange = (newPage: number) => { page.value = newPage; fetchRecords() }
const getStatusType = (status: number) => ({ 0: 'warning', 1: 'primary', 2: 'success' }[status] || 'info')
const getStatusText = (status: number) => ({ 0: '待发货', 1: '已发货', 2: '已完成' }[status] || '未知')
const formatDate = (date: string) => dayjs(date).format('MM-DD HH:mm')

onMounted(() => fetchRecords())
</script>

<style scoped lang="scss">
.exchange-records-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #F472B6 0%, #EC4899 100%);
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

  .page-desc { font-size: var(--font-size-lg); opacity: 0.9; }
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 40px 20px;
}

.section-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-border);
}

.records-list {
  .record-item {
    padding: 24px;
    border-radius: var(--radius-lg);
    border: 1px solid var(--color-border-light);
    margin-bottom: 16px;
    transition: all var(--transition-base);

    &:hover {
      border-color: #EC4899;
      box-shadow: var(--shadow-sm);
    }

    .record-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;
      padding-bottom: 16px;
      border-bottom: 1px solid var(--color-border-light);

      .order-no {
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);
      }
    }

    .record-content {
      display: flex;
      gap: 20px;

      @media (max-width: 576px) {
        flex-direction: column;
        align-items: center;
        text-align: center;
      }

      .goods-image {
        width: 100px;
        height: 100px;
        border-radius: var(--radius-lg);
        overflow: hidden;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .goods-info {
        flex: 1;

        .goods-name {
          font-size: var(--font-size-lg);
          font-weight: var(--font-weight-bold);
          color: var(--color-text-primary);
          margin-bottom: 12px;
        }

        .goods-meta {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 12px;
          flex-wrap: wrap;

          .exchange-time {
            font-size: var(--font-size-sm);
            color: var(--color-text-muted);
          }
        }

        .points-used {
          display: flex;
          align-items: center;
          gap: 6px;
          color: var(--color-warning);
          font-weight: var(--font-weight-bold);
          font-size: var(--font-size-base);
        }
      }
    }

    .record-footer {
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid var(--color-border-light);

      .pickup-section {
        text-align: center;

        .pickup-label {
          font-size: var(--font-size-sm);
          color: var(--color-text-secondary);
          margin-bottom: 12px;
        }

        .pickup-code {
          background: rgba(255, 217, 61, 0.1);
          border: 2px dashed var(--color-warning);
          border-radius: var(--radius-lg);
          padding: 16px;
          font-size: 28px;
          font-weight: var(--font-weight-bold);
          letter-spacing: 4px;
          color: var(--color-warning);
          font-family: 'Courier New', monospace;
          margin-bottom: 8px;

          &.verified {
            background: rgba(34, 197, 94, 0.1);
            border-color: var(--color-success);
            color: var(--color-success);
            font-size: 20px;
            letter-spacing: 2px;
          }
        }

        .pickup-hint {
          font-size: var(--font-size-xs);
          color: var(--color-text-muted);
          margin: 0;
        }
      }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.animate-fade-in-up {
  opacity: 0;
  animation: fadeInUp 0.5s ease forwards;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}
</style>
