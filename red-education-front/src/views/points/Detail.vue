<template>
  <MainLayout>
    <div class="goods-detail-page" v-loading="loading">
      <div class="page-container" v-if="goods">
        <el-button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回商城
        </el-button>

        <div class="detail-layout">
          <!-- 商品图片 -->
          <div class="goods-gallery">
            <el-image :src="goods.image || 'https://picsum.photos/500/400?random=' + goods.id" class="main-image" fit="cover" />
          </div>

          <!-- 商品信息 -->
          <div class="goods-info">
            <el-tag class="goods-type-tag" :type="goods.type === 1 ? 'success' : 'warning'" effect="dark" size="large">
              {{ goods.type === 1 ? '虚拟商品' : '实体商品' }}
            </el-tag>
            <h1 class="goods-name">{{ goods.name }}</h1>

            <div class="points-box">
              <el-icon :size="32"><Coin /></el-icon>
              <span class="points">{{ goods.pointsRequired }}</span>
              <span class="unit">积分</span>
            </div>

            <div class="info-row">
              <span class="label">库存</span>
              <el-tag v-if="goods.stock > 0" type="success">剩余 {{ goods.stock }} 件</el-tag>
              <el-tag v-else type="info">已兑完</el-tag>
            </div>

            <div class="desc-section">
              <h3>商品描述</h3>
              <p>{{ goods.description }}</p>
            </div>

            <div class="rules-section">
              <h3><el-icon><InfoFilled /></el-icon> 兑换规则</h3>
              <ul>
                <li>兑换后不可退换</li>
                <li>需要 {{ goods.pointsRequired }} 积分兑换</li>
                <li v-if="goods.type === 2">兑换后将生成取件码，请在兑换记录中查看</li>
                <li v-if="goods.type === 1">虚拟商品兑换后立即生效</li>
              </ul>
            </div>

            <div class="balance-box">
              <span class="label">我的积分</span>
              <span class="balance">{{ userPoints }}</span>
              <span v-if="userPoints < goods.pointsRequired" class="insufficient">（积分不足）</span>
            </div>

            <el-button
              type="primary"
              size="large"
              class="exchange-btn"
              :disabled="goods.stock === 0 || userPoints < goods.pointsRequired"
              @click="handleExchange"
              :loading="exchanging"
            >
              {{ goods.stock === 0 ? '已兑完' : '立即兑换' }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Coin, InfoFilled } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getGoodsDetail, exchangeGoods, getPointsBalance } from '@/api/points'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const exchanging = ref(false)
const goods = ref<any>(null)
const userPoints = ref(0)

const goodsId = computed(() => Number(route.params.id))

const fetchGoodsDetail = async () => {
  loading.value = true
  try {
    const res = await getGoodsDetail(goodsId.value)
    if (res.data) goods.value = res.data
  } catch (error) {
    ElMessage.error('获取商品详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const fetchBalance = async () => {
  try {
    const res = await getPointsBalance()
    if (res.data) userPoints.value = res.data.points || 0
  } catch (error) {
    console.error('获取积分失败', error)
  }
}

const handleExchange = () => {
  ElMessageBox.confirm(`确认使用${goods.value.pointsRequired}积分兑换${goods.value.name}吗？`, '提示', {
    confirmButtonText: '确认兑换',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    exchanging.value = true
    try {
      await exchangeGoods(goodsId.value)
      ElMessage.success('兑换成功')
      userPoints.value -= goods.value.pointsRequired
      goods.value.stock--
      router.push('/user/exchange-records')
    } catch (error: any) {
      ElMessage.error(error.message || '兑换失败')
    } finally {
      exchanging.value = false
    }
  })
}

onMounted(() => { fetchGoodsDetail(); fetchBalance() })
</script>

<style scoped lang="scss">
.goods-detail-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
  padding: 40px 20px;
}

.page-container {
  max-width: 1000px;
  margin: 0 auto;
}

.back-btn {
  margin-bottom: 24px;
  background: var(--color-bg-primary);
  border: 1px solid var(--color-border);
  &:hover { border-color: var(--color-warning); color: var(--color-warning); }
}

.detail-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;

  @media (max-width: 768px) { grid-template-columns: 1fr; }
}

.goods-gallery {
  .main-image {
    width: 100%;
    height: 400px;
    border-radius: var(--radius-xl);
    overflow: hidden;
  }
}

.goods-info {
  .goods-type-tag { margin-bottom: 16px; }

  .goods-name {
    font-size: var(--font-size-2xl);
    font-weight: var(--font-weight-bold);
    color: var(--color-text-primary);
    margin-bottom: 20px;
  }

  .points-box {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 20px;
    background: linear-gradient(135deg, rgba(255, 217, 61, 0.1) 0%, rgba(245, 158, 11, 0.1) 100%);
    border: 1px solid rgba(245, 158, 11, 0.3);
    border-radius: var(--radius-xl);
    margin-bottom: 20px;

    .el-icon { color: #F59E0B; }
    .points { font-size: 40px; font-weight: var(--font-weight-bold); color: #F59E0B; }
    .unit { font-size: var(--font-size-lg); color: var(--color-text-muted); }
  }

  .info-row {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    .label { color: var(--color-text-secondary); }
  }

  .desc-section, .rules-section {
    background: var(--color-bg-primary);
    border-radius: var(--radius-xl);
    padding: 20px;
    margin-bottom: 20px;
    border: 1px solid var(--color-border);

    h3 {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: var(--font-size-base);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
      margin-bottom: 12px;
    }

    p { color: var(--color-text-secondary); line-height: var(--line-height-relaxed); }
    ul { padding-left: 20px; li { color: var(--color-text-secondary); line-height: 2; } }
  }

  .balance-box {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 16px;
    background: var(--color-bg-tertiary);
    border-radius: var(--radius-lg);
    margin-bottom: 20px;

    .label { color: var(--color-text-secondary); }
    .balance { font-size: var(--font-size-xl); font-weight: var(--font-weight-bold); color: #F59E0B; }
    .insufficient { color: var(--color-error); font-size: var(--font-size-sm); }
  }

  .exchange-btn {
    width: 100%;
    height: 56px;
    font-size: var(--font-size-lg);
    font-weight: var(--font-weight-bold);
    background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
    border: none;
    color: var(--color-secondary);
  }
}
</style>
