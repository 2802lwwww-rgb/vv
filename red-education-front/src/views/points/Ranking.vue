<template>
  <MainLayout>
    <div class="ranking-page">
      <!-- 炫酷页面头部 -->
      <div class="page-header">
        <div class="header-bg">
          <div class="stars">
            <div v-for="i in 30" :key="'star-'+i" class="star" :style="getStarStyle(i)"></div>
          </div>
          <div class="gradient-orbs">
            <div class="orb orb-1"></div>
            <div class="orb orb-2"></div>
          </div>
        </div>
        <div class="header-content">
          <div class="trophy-icon">🏆</div>
          <h1 class="page-title">积分排行榜</h1>
          <p class="page-desc">学习之星，荣耀榜单</p>
        </div>
      </div>

      <div class="page-container" v-loading="loading">
        <!-- 3D 领奖台 -->
        <div class="podium-section" v-if="rankings.length >= 3">
          <!-- 庆祝粒子 -->
          <div class="confetti-container">
            <div v-for="i in 20" :key="'conf-'+i" class="confetti" :style="getConfettiStyle(i)"></div>
          </div>

          <div class="podium-wrapper">
            <!-- 第二名 -->
            <div class="podium-item second">
              <div class="medal-glow silver-glow"></div>
              <div class="medal-icon">🥈</div>
              <div class="avatar-wrapper">
                <div class="avatar-ring silver-ring"></div>
                <el-avatar :size="80" :src="getAvatarUrl(rankings[1]?.avatar)" class="user-avatar silver">
                  {{ rankings[1]?.nickname?.charAt(0) || rankings[1]?.username?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="user-name">{{ rankings[1]?.nickname || rankings[1]?.username }}</div>
              <div class="user-points">
                <span class="points-icon">⭐</span>
                {{ rankings[1]?.points }} 积分
              </div>
              <div class="podium-stand silver">
                <div class="stand-face front">2</div>
                <div class="stand-face side"></div>
              </div>
            </div>

            <!-- 第一名 -->
            <div class="podium-item first">
              <div class="crown-wrapper">
                <div class="crown-glow"></div>
                <div class="crown-icon">👑</div>
              </div>
              <div class="medal-glow gold-glow"></div>
              <div class="medal-icon">🥇</div>
              <div class="avatar-wrapper">
                <div class="avatar-ring gold-ring"></div>
                <el-avatar :size="100" :src="getAvatarUrl(rankings[0]?.avatar)" class="user-avatar gold">
                  {{ rankings[0]?.nickname?.charAt(0) || rankings[0]?.username?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="user-name champion">{{ rankings[0]?.nickname || rankings[0]?.username }}</div>
              <div class="user-points">
                <span class="points-icon">⭐</span>
                {{ rankings[0]?.points }} 积分
              </div>
              <div class="podium-stand gold">
                <div class="stand-face front">1</div>
                <div class="stand-face side"></div>
              </div>
            </div>

            <!-- 第三名 -->
            <div class="podium-item third">
              <div class="medal-glow bronze-glow"></div>
              <div class="medal-icon">🥉</div>
              <div class="avatar-wrapper">
                <div class="avatar-ring bronze-ring"></div>
                <el-avatar :size="72" :src="getAvatarUrl(rankings[2]?.avatar)" class="user-avatar bronze">
                  {{ rankings[2]?.nickname?.charAt(0) || rankings[2]?.username?.charAt(0) }}
                </el-avatar>
              </div>
              <div class="user-name">{{ rankings[2]?.nickname || rankings[2]?.username }}</div>
              <div class="user-points">
                <span class="points-icon">⭐</span>
                {{ rankings[2]?.points }} 积分
              </div>
              <div class="podium-stand bronze">
                <div class="stand-face front">3</div>
                <div class="stand-face side"></div>
              </div>
            </div>
          </div>
        </div>

        <!-- 排行榜列表 -->
        <div class="ranking-list" v-if="rankings.length > 3">
          <div class="list-header">
            <span class="col-rank">排名</span>
            <span class="col-user">用户</span>
            <span class="col-points">积分</span>
          </div>
          <div 
            v-for="(item, index) in rankings.slice(3)" 
            :key="item.userId" 
            class="ranking-item"
            :class="{ 'is-me': item.isCurrentUser }"
          >
            <div class="rank-num">{{ index + 4 }}</div>
            <div class="user-cell">
              <el-avatar :size="40" :src="getAvatarUrl(item.avatar)" class="avatar">
                {{ item.nickname?.charAt(0) || item.username?.charAt(0) }}
              </el-avatar>
              <span class="name">
                {{ item.nickname || item.username }}
                <el-tag v-if="item.isCurrentUser" type="success" size="small" round>我</el-tag>
              </span>
            </div>
            <div class="points-cell">
              <el-icon><Coin /></el-icon>
              <span>{{ item.points }}</span>
            </div>
          </div>
        </div>

        <el-empty v-if="rankings.length === 0 && !loading" description="暂无排行数据" />
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Coin } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getPointsRanking } from '@/api/points'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const loading = ref(false)
const rankings = ref<any[]>([])

// 星星样式生成
const getStarStyle = (index: number) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${1 + Math.random() * 2}px`,
    height: `${1 + Math.random() * 2}px`,
    animationDelay: `${index * 0.15}s`,
    animationDuration: `${2 + Math.random() * 2}s`
  }
}

// 庆祝彩纸样式
const getConfettiStyle = (index: number) => {
  const colors = ['#fbbf24', '#f472b6', '#60a5fa', '#34d399', '#a78bfa', '#f87171']
  return {
    left: `${10 + Math.random() * 80}%`,
    background: colors[index % colors.length],
    animationDelay: `${Math.random() * 3}s`,
    animationDuration: `${3 + Math.random() * 2}s`
  }
}

// 获取头像URL
const getAvatarUrl = (avatar: string | undefined) => {
  if (!avatar) return undefined
  if (avatar.startsWith('http')) return avatar
  return `${import.meta.env.VITE_API_BASE_URL}${avatar}`
}

const fetchRanking = async () => {
  loading.value = true
  try {
    const res = await getPointsRanking({ page: 1, pageSize: 50 })
    if (res.data) {
      rankings.value = (res.data.records || []).map((item: any) => ({
        ...item,
        isCurrentUser: item.userId === userStore.userInfo?.id
      }))
    }
  } catch (error) {
    ElMessage.error('获取排行榜失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => fetchRanking())
</script>

<style scoped lang="scss">
.ranking-page {
  min-height: calc(100vh - 64px);
  background: linear-gradient(180deg, #1a0a0a 0%, #2d1515 100%);
}

// ============================================
// 页面头部 - 红色主题
// ============================================
.page-header {
  position: relative;
  padding: 60px 20px;
  text-align: center;
  overflow: hidden;
  
  .header-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #7f1d1d 0%, #b91c1c 50%, #dc2626 100%);
    
    .stars {
      position: absolute;
      inset: 0;
      
      .star {
        position: absolute;
        background: rgba(255, 200, 150, 0.8);
        border-radius: 50%;
        animation: twinkle 2s ease-in-out infinite;
      }
    }
    
    .gradient-orbs {
      .orb {
        position: absolute;
        border-radius: 50%;
        filter: blur(60px);
        
        &.orb-1 {
          width: 300px;
          height: 300px;
          background: rgba(251, 191, 36, 0.35);
          top: -100px;
          right: -50px;
          animation: float-orb 6s ease-in-out infinite;
        }
        
        &.orb-2 {
          width: 200px;
          height: 200px;
          background: rgba(234, 88, 12, 0.3);
          bottom: -50px;
          left: -30px;
          animation: float-orb 8s ease-in-out infinite reverse;
        }
      }
    }
  }
  
  .header-content {
    position: relative;
    z-index: 1;
    color: white;
    
    .trophy-icon {
      font-size: 64px;
      margin-bottom: 12px;
      animation: bounce-trophy 2s ease-in-out infinite;
    }
    
    .page-title {
      font-size: 36px;
      font-weight: 800;
      margin: 0 0 8px;
      text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
    }
    
    .page-desc {
      font-size: 16px;
      opacity: 0.9;
      margin: 0;
    }
  }
}

.page-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 20px;
}

// ============================================
// 3D 领奖台
// ============================================
.podium-section {
  position: relative;
  margin-bottom: 40px;
  padding: 60px 20px 0;
  background: linear-gradient(180deg, #1e1b4b 0%, #312e81 50%, #3730a3 100%);
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.4);
  
  .confetti-container {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 200px;
    overflow: hidden;
    pointer-events: none;
    
    .confetti {
      position: absolute;
      top: -10px;
      width: 10px;
      height: 10px;
      border-radius: 2px;
      animation: confetti-fall 4s linear infinite;
    }
  }
  
  .podium-wrapper {
    display: flex;
    justify-content: center;
    align-items: flex-end;
    gap: 20px;
    position: relative;
    z-index: 1;
  }
  
  .podium-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    position: relative;
    
    .crown-wrapper {
      position: relative;
      margin-bottom: -10px;
      
      .crown-glow {
        position: absolute;
        width: 60px;
        height: 60px;
        background: radial-gradient(circle, rgba(251, 191, 36, 0.6) 0%, transparent 70%);
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        animation: pulse-glow 2s ease-in-out infinite;
      }
      
      .crown-icon {
        font-size: 36px;
        position: relative;
        z-index: 1;
        animation: bounce 2s infinite;
        filter: drop-shadow(0 0 10px rgba(251, 191, 36, 0.8));
      }
    }
    
    .medal-glow {
      position: absolute;
      width: 100px;
      height: 100px;
      border-radius: 50%;
      top: 40px;
      left: 50%;
      transform: translateX(-50%);
      filter: blur(30px);
      opacity: 0.5;
      
      &.gold-glow { background: #fbbf24; }
      &.silver-glow { background: #94a3b8; }
      &.bronze-glow { background: #ea580c; }
    }
    
    .medal-icon {
      font-size: 36px;
      margin-bottom: 12px;
      animation: medal-shine 3s ease-in-out infinite;
      filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
    }
    
    .avatar-wrapper {
      position: relative;
      margin-bottom: 16px;
      
      .avatar-ring {
        position: absolute;
        inset: -6px;
        border-radius: 50%;
        animation: ring-rotate 4s linear infinite;
        
        &.gold-ring {
          background: conic-gradient(from 0deg, #fbbf24, #f59e0b, #d97706, #fbbf24);
        }
        &.silver-ring {
          background: conic-gradient(from 0deg, #e2e8f0, #94a3b8, #64748b, #e2e8f0);
        }
        &.bronze-ring {
          background: conic-gradient(from 0deg, #fed7aa, #ea580c, #c2410c, #fed7aa);
        }
      }
    }
    
    .user-avatar {
      font-size: 28px;
      font-weight: 700;
      color: white;
      border: 4px solid rgba(255, 255, 255, 0.9);
      position: relative;
      z-index: 1;
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.3);
      
      &.gold { background: linear-gradient(135deg, #fbbf24 0%, #f59e0b 100%); }
      &.silver { background: linear-gradient(135deg, #94a3b8 0%, #64748b 100%); }
      &.bronze { background: linear-gradient(135deg, #ea580c 0%, #c2410c 100%); }
    }
    
    .user-name {
      font-size: 16px;
      font-weight: 600;
      color: white;
      margin-bottom: 6px;
      text-align: center;
      max-width: 130px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      
      &.champion {
        font-size: 18px;
        background: linear-gradient(90deg, #fbbf24, #fef3c7, #fbbf24);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        animation: shimmer 3s ease-in-out infinite;
        background-size: 200% auto;
      }
    }
    
    .user-points {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 16px;
      display: flex;
      align-items: center;
      gap: 4px;
      
      .points-icon {
        animation: star-pulse 1.5s ease-in-out infinite;
      }
    }
    
    .podium-stand {
      width: 110px;
      position: relative;
      transform-style: preserve-3d;
      transform: perspective(500px) rotateX(10deg);
      
      .stand-face {
        &.front {
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 28px;
          font-weight: 800;
          color: white;
          text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
          border-radius: 12px 12px 0 0;
        }
      }
      
      &.gold .front {
        height: 120px;
        background: linear-gradient(180deg, #fbbf24 0%, #d97706 100%);
        box-shadow: 0 10px 30px rgba(251, 191, 36, 0.4);
      }
      &.silver .front {
        height: 80px;
        background: linear-gradient(180deg, #94a3b8 0%, #475569 100%);
        box-shadow: 0 10px 30px rgba(148, 163, 184, 0.3);
      }
      &.bronze .front {
        height: 60px;
        background: linear-gradient(180deg, #ea580c 0%, #9a3412 100%);
        box-shadow: 0 10px 30px rgba(234, 88, 12, 0.3);
      }
    }
    
    &.first { order: 2; }
    &.second { order: 1; }
    &.third { order: 3; }
  }
}

// ============================================
// 排行榜列表
// ============================================
.ranking-list {
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.1);
  
  .list-header {
    display: flex;
    align-items: center;
    padding: 18px 28px;
    background: rgba(255, 255, 255, 0.05);
    font-size: 13px;
    font-weight: 600;
    color: rgba(255, 255, 255, 0.6);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    
    .col-rank { width: 60px; }
    .col-user { flex: 1; }
    .col-points { width: 100px; text-align: right; }
  }
  
  .ranking-item {
    display: flex;
    align-items: center;
    padding: 18px 28px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    transition: all 0.3s ease;
    
    &:last-child { border-bottom: none; }
    &:hover { background: rgba(255, 255, 255, 0.05); }
    
    &.is-me {
      background: linear-gradient(90deg, rgba(139, 92, 246, 0.2) 0%, rgba(139, 92, 246, 0.05) 100%);
      border-left: 3px solid #8b5cf6;
    }
    
    .rank-num {
      width: 60px;
      font-size: 20px;
      font-weight: 700;
      color: rgba(255, 255, 255, 0.4);
    }
    
    .user-cell {
      flex: 1;
      display: flex;
      align-items: center;
      gap: 14px;
      
      .avatar {
        background: linear-gradient(135deg, #8B5CF6 0%, #6D28D9 100%);
        color: white;
        font-weight: 600;
        border: 2px solid rgba(255, 255, 255, 0.2);
      }
      
      .name {
        font-weight: 500;
        color: #fff;
        display: flex;
        align-items: center;
        gap: 8px;
      }
    }
    
    .points-cell {
      width: 100px;
      display: flex;
      align-items: center;
      justify-content: flex-end;
      gap: 6px;
      font-size: 16px;
      font-weight: 600;
      color: #fbbf24;
      
      .el-icon { font-size: 18px; }
    }
  }
}

// ============================================
// 动画
// ============================================
@keyframes twinkle {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

@keyframes float-orb {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -20px); }
}

@keyframes bounce-trophy {
  0%, 100% { transform: translateY(0) rotate(-5deg); }
  50% { transform: translateY(-10px) rotate(5deg); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

@keyframes pulse-glow {
  0%, 100% { opacity: 0.5; transform: translate(-50%, -50%) scale(1); }
  50% { opacity: 0.8; transform: translate(-50%, -50%) scale(1.2); }
}

@keyframes medal-shine {
  0%, 100% { transform: scale(1) rotate(0deg); }
  25% { transform: scale(1.1) rotate(5deg); }
  75% { transform: scale(1.1) rotate(-5deg); }
}

@keyframes ring-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes shimmer {
  0% { background-position: -200% center; }
  100% { background-position: 200% center; }
}

@keyframes star-pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

@keyframes confetti-fall {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(200px) rotate(720deg);
    opacity: 0;
  }
}
</style>
