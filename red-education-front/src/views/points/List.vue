<template>
  <MainLayout>
    <div class="points-mall-page">
      <!-- 页面头部 -->
      <div class="page-header">
        <div class="header-content">
          <div class="title-section">
            <h1 class="page-title">
              <span class="title-icon">🛒</span>
              积分商城
            </h1>
            <p class="page-desc">使用积分兑换精美礼品</p>
          </div>
          <div class="header-actions">
            <div class="points-badge" v-if="userStore.isLoggedIn">
              <el-icon><Coin /></el-icon>
              <span class="points-value">{{ userPoints }}</span>
              <span class="points-label">积分</span>
            </div>
            <el-button type="warning" @click="$router.push('/points/ranking')">
              🏆 积分排行
            </el-button>
          </div>
        </div>
      </div>

      <div class="page-container">
        <!-- 商品列表 -->
        <div class="goods-grid" v-loading="loading">
          <div 
            v-for="(goods, index) in goodsList" 
            :key="goods.id" 
            class="goods-card animate-fade-in-up"
            :style="{ animationDelay: `${index * 50}ms` }"
          >
            <div class="goods-image">
              <img :src="goods.image || 'https://picsum.photos/300/200?random=' + goods.id" :alt="goods.name" />
              <el-tag class="goods-type-tag" :type="goods.type === 1 ? 'success' : 'warning'" effect="dark">
                {{ goods.type === 1 ? '虚拟' : '实物' }}
              </el-tag>
            </div>
            <div class="goods-info">
              <h4 class="goods-name">{{ goods.name }}</h4>
              <p class="goods-desc">{{ goods.description }}</p>
              <div class="goods-footer">
                <div class="goods-points">
                  <el-icon><Coin /></el-icon>
                  <span class="points">{{ goods.pointsRequired }}</span>
                </div>
                <el-tag v-if="goods.stock === 0" type="info" size="small">已兑完</el-tag>
                <el-tag v-else type="success" size="small">剩{{ goods.stock }}件</el-tag>
              </div>
              <el-button
                type="primary"
                class="exchange-btn"
                :disabled="goods.stock === 0 || userPoints < goods.pointsRequired"
                @click="handleExchange(goods)"
              >
                {{ goods.stock === 0 ? '已兑完' : '立即兑换' }}
              </el-button>
            </div>
          </div>
        </div>

        <el-empty v-if="goodsList.length === 0 && !loading" description="暂无商品" />

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
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Coin } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { useUserStore } from '@/stores/user'
import { getPointsGoods, exchangeGoods, getPointsBalance } from '@/api/points'

const userStore = useUserStore()
const loading = ref(false)
const goodsList = ref<any[]>([])
const userPoints = ref(0)
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

const fetchBalance = async () => {
  try {
    const res = await getPointsBalance()
    if (res.data) userPoints.value = res.data.points || 0
  } catch (error) {
    console.error('获取积分失败', error)
  }
}

const fetchGoods = async () => {
  loading.value = true
  try {
    const res = await getPointsGoods({ page: page.value, pageSize: pageSize.value })
    if (res.data) { goodsList.value = res.data.records || []; total.value = res.data.total || 0 }
  } catch (error) {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const handleExchange = (goods: any) => {
  ElMessageBox.confirm(`确认使用${goods.pointsRequired}积分兑换${goods.name}吗？`, '提示', {
    confirmButtonText: '确认兑换',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await exchangeGoods(goods.id)
      ElMessage.success('兑换成功')
      userPoints.value -= goods.pointsRequired
      fetchGoods()
    } catch (error: any) {
      ElMessage.error(error.message || '兑换失败')
    }
  })
}

const handlePageChange = (newPage: number) => { page.value = newPage; fetchGoods() }

onMounted(() => {
  if (userStore.isLoggedIn) fetchBalance()
  fetchGoods()
})
</script>

<style scoped lang="scss">
.points-mall-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

.page-header {
  background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
  padding: 60px 20px;
  color: var(--color-secondary);

  .header-content {
    max-width: 1200px;
    margin: 0 auto;
    display: flex;
    justify-content: space-between;
    align-items: center;

    @media (max-width: 576px) {
      flex-direction: column;
      gap: 20px;
      text-align: center;
    }
  }

  .page-title {
    font-size: var(--font-size-4xl);
    font-weight: var(--font-weight-bold);
    margin-bottom: 8px;
    display: flex;
    align-items: center;
    gap: 12px;
    .title-icon { font-size: 40px; }
  }

  .page-desc { font-size: var(--font-size-base); opacity: 0.85; }

  .header-actions {
    display: flex;
    align-items: center;
    gap: 16px;

    .el-button {
      background: rgba(255, 255, 255, 0.9);
      border: none;
      color: #F59E0B;
      font-weight: 600;
    }
  }

  .points-badge {
    display: flex;
    align-items: center;
    gap: 8px;
    background: white;
    padding: 16px 24px;
    border-radius: var(--radius-xl);
    box-shadow: var(--shadow-lg);

    .el-icon { font-size: 24px; color: #F59E0B; }
    .points-value { font-size: var(--font-size-2xl); font-weight: var(--font-weight-bold); color: #F59E0B; }
    .points-label { font-size: var(--font-size-sm); color: var(--color-text-muted); }
  }
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;

  @media (max-width: 1200px) { grid-template-columns: repeat(3, 1fr); }
  @media (max-width: 768px) { grid-template-columns: repeat(2, 1fr); }
  @media (max-width: 480px) { grid-template-columns: 1fr; }
}

.goods-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  overflow: hidden;
  border: 1px solid var(--color-border);
  transition: all var(--transition-base);

  &:hover {
    transform: translateY(-6px);
    box-shadow: var(--shadow-lg);
  }

  .goods-image {
    position: relative;
    height: 180px;
    overflow: hidden;

    img { width: 100%; height: 100%; object-fit: cover; transition: transform var(--transition-base); }
    &:hover img { transform: scale(1.05); }

    .goods-type-tag {
      position: absolute;
      top: 12px;
      left: 12px;
    }
  }

  .goods-info {
    padding: 20px;

    .goods-name {
      font-size: var(--font-size-base);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
      margin-bottom: 8px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .goods-desc {
      font-size: var(--font-size-sm);
      color: var(--color-text-muted);
      margin-bottom: 16px;
      height: 40px;
      overflow: hidden;
      text-overflow: ellipsis;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }

    .goods-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 16px;

      .goods-points {
        display: flex;
        align-items: center;
        gap: 4px;
        .el-icon { color: #F59E0B; }
        .points { font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); color: #F59E0B; }
      }
    }

    .exchange-btn {
      width: 100%;
      background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
      border: none;
      color: var(--color-secondary);
      font-weight: var(--font-weight-medium);

      &:disabled { background: var(--color-bg-tertiary); color: var(--color-text-muted); }
    }
  }
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
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
