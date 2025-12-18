<template>
  <MainLayout>
    <div class="home-page">
      <!-- Hero 区块 - 3D星空 -->
      <section class="hero-section">
        <!-- 星空背景 -->
        <StarrySky @navigate="handleNavigate" />
        
        <!-- 内容区 -->
        <div class="hero-content">
          <div class="hero-badge animate-fade-in">
            <span class="badge-icon">🔥</span>
            <span class="badge-text">红色教育平台</span>
            <span class="badge-glow"></span>
          </div>
          
          <h1 class="hero-title">
            <span class="title-line animate-fade-in-up">
              <span class="text-gradient-animated">传承红色基因</span>
            </span>
            <span class="title-line animate-fade-in-up delay-200">
              <span>弘扬革命精神</span>
            </span>
          </h1>
          
          <p class="hero-subtitle animate-fade-in-up delay-300">
            <span class="subtitle-icon">✦</span>
            系统化的红色教育资源
            <span class="subtitle-dot">·</span>
            沉浸式的学习体验
            <span class="subtitle-dot">·</span>
            互动交流社区
            <span class="subtitle-icon">✦</span>
          </p>
          
          <div class="hero-actions animate-fade-in-up delay-400">
            <el-button size="large" class="btn-hero-primary light-sweep" @click="$router.push('/courses')">
              <el-icon><Reading /></el-icon>
              <span>开始学习</span>
              <span class="btn-shine"></span>
            </el-button>
            <el-button size="large" class="btn-hero-outline" @click="$router.push('/resources')">
              <el-icon><Document /></el-icon>
              浏览资源
            </el-button>
          </div>
        </div>
      </section>
      
      <!-- 历史时间线 - 山水画风格 -->
      <section class="timeline-section">
        <HistoryTimeline />
      </section>

      <!-- 统计数据 - 玻璃卡片 -->
      <section class="statistics-section">
        <div class="section-container">
          <div class="stats-grid">
            <div 
              v-for="(stat, index) in statistics" 
              :key="stat.label" 
              class="stat-card glass-stat-card"
              :style="{ animationDelay: `${index * 150}ms` }"
              @mouseenter="onCardHover($event)"
              @mouseleave="onCardLeave($event)"
              @mousemove="onCardMove($event)"
            >
              <div class="stat-glow" :style="{ background: stat.gradient }"></div>
              <div class="stat-icon" :style="{ background: stat.gradient }">
                <el-icon :size="28">
                  <component :is="stat.icon" />
                </el-icon>
              </div>
              <div class="stat-info">
                <h3 class="stat-value">
                  <span class="counter" :data-target="stat.target">{{ stat.value }}</span>
                </h3>
                <p class="stat-label">{{ stat.label }}</p>
              </div>
              <div class="stat-decoration"></div>
            </div>
          </div>
        </div>
      </section>

      <!-- 快捷入口 - 3D 悬浮卡片 -->
      <section class="quick-links-section">
        <div class="section-container">
          <h2 class="section-title">
            <span class="title-icon">🚀</span>
            <span>快捷入口</span>
            <span class="title-line-decoration"></span>
          </h2>
          <div class="links-grid">
            <div 
              v-for="(link, index) in quickLinks" 
              :key="link.path" 
              class="link-card-wrapper"
              :style="{ animationDelay: `${index * 100}ms` }"
            >
              <div 
                class="link-card animate-fade-in-up"
                @click="$router.push(link.path)"
                @mouseenter="onCardHover($event)"
                @mouseleave="onCardLeave($event)"
                @mousemove="onCardMove($event)"
              >
                <div class="card-bg" :style="{ background: link.gradient }"></div>
                <div class="link-icon" :style="{ background: link.gradient }">
                  <el-icon :size="32">
                    <component :is="link.icon" />
                  </el-icon>
                </div>
                <h3 class="link-title">{{ link.title }}</h3>
                <p class="link-desc">{{ link.description }}</p>
                <div class="link-arrow">
                  <el-icon><ArrowRight /></el-icon>
                </div>
                <div class="card-shine"></div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 推荐资源 -->
      <section class="recommended-section">
        <div class="section-container">
          <div class="section-header">
            <h2 class="section-title">
              <span class="title-icon">📚</span>
              <span>推荐资源</span>
            </h2>
            <el-button text class="view-more-btn" @click="$router.push('/resources')">
              查看更多
              <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
          <div class="resources-grid" v-loading="resourcesLoading">
            <div 
              v-for="(resource, index) in recommendedResources" 
              :key="resource.id" 
              class="resource-card animate-fade-in-up"
              :style="{ animationDelay: `${index * 80}ms` }"
              @click="goToResource(resource.id)"
            >
              <div class="resource-cover">
                <img :src="getCoverUrl(resource.coverImage)" :alt="resource.title" />
                <div class="resource-category">{{ resource.categoryName || '资源' }}</div>
                <div class="resource-overlay">
                  <el-icon :size="32"><View /></el-icon>
                </div>
              </div>
              <div class="resource-info">
                <h4 class="resource-title">{{ resource.title }}</h4>
                <p class="resource-desc">{{ resource.description }}</p>
                <div class="resource-meta">
                  <span class="meta-item">
                    <el-icon><View /></el-icon>
                    {{ resource.viewCount || 0 }}
                  </span>
                  <span class="meta-item">
                    <el-icon><Download /></el-icon>
                    {{ resource.downloadCount || 0 }}
                  </span>
                </div>
              </div>
            </div>
            <el-empty v-if="recommendedResources.length === 0 && !resourcesLoading" description="暂无推荐资源" />
          </div>
        </div>
      </section>

      <!-- 特色功能 -->
      <section class="features-section">
        <div class="section-container">
          <h2 class="section-title text-center">
            <span class="title-icon">✨</span>
            平台特色
          </h2>
          <div class="features-grid">
            <div class="feature-card" v-for="(feature, index) in features" :key="feature.title">
              <div class="feature-icon-wrapper">
                <div class="feature-icon">{{ feature.emoji }}</div>
                <div class="feature-ring"></div>
              </div>
              <h3 class="feature-title">{{ feature.title }}</h3>
              <p class="feature-desc">{{ feature.description }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  User,
  Document,
  Reading,
  Trophy,
  View,
  Download,
  ArrowRight
} from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import StarrySky from '@/components/creative/StarrySky.vue'
import HistoryTimeline from '@/components/creative/HistoryTimeline.vue'
import { getResourceList } from '@/api/resource'
import { getPublicStatistics } from '@/api/statistics'

const router = useRouter()
const resourcesLoading = ref(false)

// 处理星空导航
const handleNavigate = (path: string) => {
  router.push(path)
}

// 统计数据
const statistics = ref([
  { 
    label: '注册用户', 
    value: '0+', 
    target: 0,
    icon: User, 
    gradient: 'linear-gradient(135deg, #FF6B6B 0%, #D64541 100%)' 
  },
  { 
    label: '红色资源', 
    value: '0+', 
    target: 0,
    icon: Document, 
    gradient: 'linear-gradient(135deg, #FFD93D 0%, #E6C235 100%)' 
  },
  { 
    label: '在线课程', 
    value: '0+', 
    target: 0,
    icon: Reading, 
    gradient: 'linear-gradient(135deg, #00D9FF 0%, #0099CC 100%)' 
  },
  { 
    label: '学习成就', 
    value: '0+', 
    target: 0,
    icon: Trophy, 
    gradient: 'linear-gradient(135deg, #22C55E 0%, #16A34A 100%)' 
  }
])

// 快捷入口
const quickLinks = ref([
  {
    title: '红色资源',
    description: '浏览丰富的红色教育资源',
    icon: Document,
    gradient: 'linear-gradient(135deg, #FF6B6B 0%, #C92A2A 100%)',
    path: '/resources'
  },
  {
    title: '在线课程',
    description: '系统学习红色教育课程',
    icon: Reading,
    gradient: 'linear-gradient(135deg, #00D9FF 0%, #0077B6 100%)',
    path: '/courses'
  },
  {
    title: '在线考试',
    description: '检验你的学习成果',
    icon: Trophy,
    gradient: 'linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%)',
    path: '/exam'
  },
  {
    title: '互动社区',
    description: '分享学习心得与体会',
    icon: User,
    gradient: 'linear-gradient(135deg, #22C55E 0%, #15803D 100%)',
    path: '/community'
  }
])

// 平台特色
const features = ref([
  {
    emoji: '📖',
    title: '丰富资源',
    description: '海量红色教育资源，涵盖文献、视频、音频等多种形式'
  },
  {
    emoji: '🎯',
    title: '系统学习',
    description: '科学的课程体系，由浅入深，循序渐进'
  },
  {
    emoji: '📝',
    title: '实战考核',
    description: '在线考试系统，实时检验学习效果'
  },
  {
    emoji: '🏆',
    title: '积分激励',
    description: '学习即可获得积分，兑换精美礼品'
  }
])

// 推荐资源
interface Resource {
  id: number
  title: string
  description: string
  coverImage: string
  categoryName: string
  viewCount: number
  downloadCount: number
}

const recommendedResources = ref<Resource[]>([])



// 3D 卡片交互
const onCardHover = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  card.style.transition = 'transform 0.1s ease-out'
}

const onCardLeave = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  card.style.transform = 'perspective(1000px) rotateX(0) rotateY(0) scale(1)'
  card.style.transition = 'transform 0.5s ease-out'
}

const onCardMove = (e: MouseEvent) => {
  const card = e.currentTarget as HTMLElement
  const rect = card.getBoundingClientRect()
  const x = e.clientX - rect.left
  const y = e.clientY - rect.top
  const centerX = rect.width / 2
  const centerY = rect.height / 2
  const rotateX = (y - centerY) / 10
  const rotateY = (centerX - x) / 10
  
  card.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg) scale(1.02)`
}

// 数字滚动动画
const animateNumbers = () => {
  const counters = document.querySelectorAll('.counter')
  counters.forEach((counter) => {
    const target = parseInt((counter as HTMLElement).dataset.target || '0')
    if (target === 0) return
    
    const duration = 2000
    const start = performance.now()
    
    const updateCounter = (currentTime: number) => {
      const elapsed = currentTime - start
      const progress = Math.min(elapsed / duration, 1)
      
      // 使用 easeOutExpo 缓动函数
      const easeProgress = 1 - Math.pow(1 - progress, 3)
      const current = Math.floor(easeProgress * target)
      
      counter.textContent = formatNumber(current)
      
      if (progress < 1) {
        requestAnimationFrame(updateCounter)
      } else {
        counter.textContent = formatNumber(target)
      }
    }
    
    requestAnimationFrame(updateCounter)
  })
}

// 格式化数字
const formatNumber = (num: number): string => {
  if (num >= 10000) {
    return `${(num / 10000).toFixed(1)}w+`
  }
  if (num >= 1000) {
    return `${(num / 1000).toFixed(1)}k+`
  }
  return `${num}+`
}

// 获取统计数据
const fetchStatistics = async () => {
  try {
    const res = await getPublicStatistics()
    const data = res?.data
    if (data) {
      if (statistics.value[0]) {
        statistics.value[0].target = data.totalUsers || 0
        statistics.value[0].value = formatNumber(data.totalUsers || 0)
      }
      if (statistics.value[1]) {
        statistics.value[1].target = data.totalResources || 0
        statistics.value[1].value = formatNumber(data.totalResources || 0)
      }
      if (statistics.value[2]) {
        statistics.value[2].target = data.totalCourses || 0
        statistics.value[2].value = formatNumber(data.totalCourses || 0)
      }
      if (statistics.value[3]) {
        statistics.value[3].target = data.totalPoints || 0
        statistics.value[3].value = formatNumber(data.totalPoints || 0)
      }
      
      // 触发数字动画
      setTimeout(animateNumbers, 500)
    }
  } catch (error) {
    console.error('获取统计数据失败', error)
  }
}

// 获取推荐资源
const fetchRecommendedResources = async () => {
  resourcesLoading.value = true
  try {
    const res = await getResourceList({ current: 1, size: 8, orderBy: 'viewCount' })
    if (res.data && res.data.records) {
      recommendedResources.value = res.data.records
    }
  } catch (error) {
    console.log('获取推荐资源失败', error)
  } finally {
    resourcesLoading.value = false
  }
}

// 跳转资源详情
const goToResource = (id: number) => {
  router.push(`/resources/${id}`)
}

// 获取封面图完整URL
const getCoverUrl = (url: string) => {
  if (!url) return 'https://picsum.photos/400/300?random=' + Math.random()
  if (url.startsWith('http')) return url
  return '/api' + url
}

onMounted(() => {
  fetchStatistics()
  fetchRecommendedResources()
})
</script>

<style scoped lang="scss">
// ============================================
// Hero 区块 - 3D星空效果
// ============================================
.hero-section {
  position: relative;
  min-height: 700px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  // StarrySky组件作为背景
  :deep(.starry-sky) {
    position: absolute;
    inset: 0;
  }

  .hero-content {
    position: relative;
    z-index: 10;
    text-align: center;
    max-width: 900px;
    padding: 0 20px;

    .hero-badge {
      display: inline-flex;
      align-items: center;
      gap: 10px;
      background: linear-gradient(135deg, rgba(214, 69, 65, 0.9), rgba(201, 42, 42, 0.9));
      color: white;
      padding: 10px 24px;
      border-radius: 50px;
      font-size: 15px;
      font-weight: 500;
      margin-bottom: 32px;
      box-shadow: 0 4px 20px rgba(214, 69, 65, 0.4);
      position: relative;
      overflow: hidden;

      .badge-icon {
        font-size: 18px;
      }

      .badge-glow {
        position: absolute;
        inset: -2px;
        background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
        border-radius: inherit;
        z-index: -1;
        opacity: 0.5;
        filter: blur(10px);
        animation: pulseGlow 2s ease-in-out infinite;
      }
    }

    .hero-title {
      font-size: clamp(40px, 8vw, 72px);
      font-weight: 800;
      line-height: 1.15;
      margin-bottom: 28px;
      color: #fff;
      text-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
      
      .title-line {
        display: block;
        opacity: 0;
      }

      .text-gradient-animated {
        background: linear-gradient(135deg, #FFD93D 0%, #FF6B6B 50%, #FFD93D 100%);
        background-size: 200% 200%;
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        animation: gradientShift 3s ease infinite;
      }
    }

    .hero-subtitle {
      font-size: 18px;
      color: rgba(255, 255, 255, 0.85);
      margin-bottom: 48px;
      line-height: 1.8;
      letter-spacing: 0.5px;

      .subtitle-icon {
        color: #ffd700;
        margin: 0 8px;
        opacity: 0.9;
      }

      .subtitle-dot {
        margin: 0 12px;
        color: #ffd700;
        opacity: 0.6;
      }
    }

    .hero-actions {
      display: flex;
      gap: 20px;
      justify-content: center;
      flex-wrap: wrap;

      .btn-hero-primary {
        position: relative;
        background: linear-gradient(135deg, #FF6B6B 0%, #C92A2A 100%) !important;
        border: none !important;
        color: white !important;
        padding: 16px 40px;
        font-size: 17px;
        font-weight: 600;
        border-radius: 50px;
        transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
        overflow: hidden;

        &:hover {
          transform: translateY(-3px) scale(1.02);
          box-shadow: 0 10px 40px rgba(214, 69, 65, 0.5);
        }

        .btn-shine {
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255,255,255,0.3), transparent);
          animation: shine 3s infinite;
        }
      }

      .btn-hero-outline {
        background: rgba(255, 255, 255, 0.15) !important;
        backdrop-filter: blur(10px);
        border: 2px solid rgba(255, 255, 255, 0.5) !important;
        color: white !important;
        padding: 16px 40px;
        font-size: 17px;
        font-weight: 600;
        border-radius: 50px;
        transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);

        &:hover {
          background: rgba(255, 255, 255, 0.25) !important;
          border-color: #ffd700 !important;
          transform: translateY(-3px);
          box-shadow: 0 10px 30px rgba(255, 215, 0, 0.2);
        }
      }
    }
  }
}

// ============================================
// 历史时间线区块
// ============================================
.timeline-section {
  position: relative;
  z-index: 5;
  margin-top: -2px;
}

// ============================================
// 统计区块 - 玻璃卡片
// ============================================
.statistics-section {
  padding: 80px 20px;
  background: linear-gradient(180deg, #FFFFFF 0%, var(--color-bg-secondary) 100%);
  margin-top: -80px;
  position: relative;
  z-index: 10;

  .section-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 24px;

    @media (max-width: 992px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .glass-stat-card {
    position: relative;
    display: flex;
    align-items: center;
    gap: 20px;
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(20px);
    -webkit-backdrop-filter: blur(20px);
    padding: 28px;
    border-radius: 20px;
    border: 1px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    overflow: hidden;
    animation: fadeInUp 0.6s ease forwards;
    opacity: 0;
    will-change: transform;

    &:hover {
      box-shadow: 0 20px 50px rgba(0, 0, 0, 0.12);
      border-color: rgba(214, 69, 65, 0.2);

      .stat-glow {
        opacity: 0.15;
      }

      .stat-decoration {
        transform: scale(1.5);
        opacity: 0.1;
      }
    }

    .stat-glow {
      position: absolute;
      top: -50%;
      right: -50%;
      width: 200%;
      height: 200%;
      opacity: 0;
      transition: opacity 0.4s ease;
      filter: blur(60px);
    }

    .stat-icon {
      width: 64px;
      height: 64px;
      border-radius: 16px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      flex-shrink: 0;
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
      position: relative;
      z-index: 1;
    }

    .stat-info {
      position: relative;
      z-index: 1;

      .stat-value {
        font-size: 32px;
        font-weight: 800;
        color: var(--color-text-primary);
        margin-bottom: 4px;
        letter-spacing: -0.5px;
      }

      .stat-label {
        font-size: 14px;
        color: var(--color-text-muted);
        font-weight: 500;
      }
    }

    .stat-decoration {
      position: absolute;
      bottom: -20px;
      right: -20px;
      width: 100px;
      height: 100px;
      border-radius: 50%;
      background: linear-gradient(135deg, var(--color-primary), var(--color-accent));
      opacity: 0.05;
      transition: all 0.4s ease;
    }
  }
}

// ============================================
// 快捷入口 - 3D 卡片
// ============================================
.quick-links-section {
  padding: 100px 20px;
  background: var(--color-bg-secondary);

  .section-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-title {
    font-size: 32px;
    font-weight: 800;
    color: var(--color-text-primary);
    margin-bottom: 50px;
    display: flex;
    align-items: center;
    gap: 14px;
    position: relative;

    .title-icon {
      font-size: 32px;
    }

    .title-line-decoration {
      flex: 1;
      height: 2px;
      background: linear-gradient(90deg, var(--color-primary), transparent);
      margin-left: 20px;
      border-radius: 2px;
    }
  }

  .links-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 28px;

    @media (max-width: 992px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .link-card-wrapper {
    perspective: 1000px;
  }

  .link-card {
    position: relative;
    background: white;
    padding: 36px;
    border-radius: 24px;
    border: 1px solid rgba(0, 0, 0, 0.05);
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    overflow: hidden;
    transform-style: preserve-3d;
    will-change: transform;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);

    &:hover {
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.12);

      .link-icon {
        transform: scale(1.1) rotate(-5deg);
      }

      .link-arrow {
        opacity: 1;
        transform: translateX(0);
      }

      .card-bg {
        opacity: 0.08;
        transform: scale(1.5);
      }

      .card-shine {
        opacity: 1;
        transform: translateX(200%);
      }
    }

    .card-bg {
      position: absolute;
      top: -50%;
      right: -50%;
      width: 200%;
      height: 200%;
      opacity: 0;
      transition: all 0.6s ease;
      filter: blur(50px);
    }

    .link-icon {
      width: 72px;
      height: 72px;
      border-radius: 18px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      margin-bottom: 24px;
      transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
      position: relative;
      z-index: 1;
    }

    .link-title {
      font-size: 20px;
      font-weight: 700;
      color: var(--color-text-primary);
      margin-bottom: 10px;
      position: relative;
      z-index: 1;
    }

    .link-desc {
      font-size: 14px;
      color: var(--color-text-muted);
      line-height: 1.6;
      position: relative;
      z-index: 1;
    }

    .link-arrow {
      position: absolute;
      right: 28px;
      top: 50%;
      transform: translateX(10px);
      opacity: 0;
      color: var(--color-primary);
      transition: all 0.4s ease;
      font-size: 20px;
    }

    .card-shine {
      position: absolute;
      top: 0;
      left: -100%;
      width: 50%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255,255,255,0.5), transparent);
      opacity: 0;
      transition: opacity 0.3s, transform 0.6s;
    }
  }
}

// ============================================
// 推荐资源
// ============================================
.recommended-section {
  padding: 100px 20px;
  background: white;

  .section-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 50px;

    .section-title {
      margin-bottom: 0;
      display: flex;
      align-items: center;
      gap: 14px;
      font-size: 32px;
      font-weight: 800;

      .title-icon {
        font-size: 32px;
      }
    }

    .view-more-btn {
      color: var(--color-primary);
      font-weight: 600;
      font-size: 15px;

      &:hover {
        color: var(--color-primary-dark);
      }
    }
  }

  .resources-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 28px;

    @media (max-width: 1200px) {
      grid-template-columns: repeat(3, 1fr);
    }

    @media (max-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .resource-card {
    background: white;
    border-radius: 20px;
    border: 1px solid rgba(0, 0, 0, 0.05);
    overflow: hidden;
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
    opacity: 0;
    animation: fadeInUp 0.6s ease forwards;

    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.12);

      .resource-cover img {
        transform: scale(1.1);
      }

      .resource-overlay {
        opacity: 1;
      }
    }

    .resource-cover {
      position: relative;
      width: 100%;
      padding-top: 66.67%;
      overflow: hidden;

      img {
        position: absolute;
        inset: 0;
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
      }

      .resource-category {
        position: absolute;
        top: 14px;
        left: 14px;
        background: linear-gradient(135deg, rgba(214, 69, 65, 0.9), rgba(201, 42, 42, 0.9));
        color: white;
        padding: 6px 14px;
        border-radius: 30px;
        font-size: 12px;
        font-weight: 600;
        backdrop-filter: blur(10px);
      }

      .resource-overlay {
        position: absolute;
        inset: 0;
        background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.6) 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        opacity: 0;
        transition: opacity 0.4s ease;
      }
    }

    .resource-info {
      padding: 20px;

      .resource-title {
        font-size: 16px;
        font-weight: 600;
        color: var(--color-text-primary);
        margin-bottom: 10px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .resource-desc {
        font-size: 13px;
        color: var(--color-text-muted);
        margin-bottom: 14px;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        line-height: 1.5;
        min-height: 40px;
      }

      .resource-meta {
        display: flex;
        gap: 16px;

        .meta-item {
          display: flex;
          align-items: center;
          gap: 5px;
          font-size: 12px;
          color: var(--color-text-muted);
        }
      }
    }
  }
}

// ============================================
// 特色功能
// ============================================
.features-section {
  padding: 100px 20px;
  background: linear-gradient(180deg, var(--color-bg-secondary) 0%, white 100%);

  .section-container {
    max-width: 1200px;
    margin: 0 auto;
  }

  .section-title {
    font-size: 32px;
    font-weight: 800;
    color: var(--color-text-primary);
    margin-bottom: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;

    .title-icon {
      font-size: 32px;
    }
  }

  .features-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 36px;

    @media (max-width: 992px) {
      grid-template-columns: repeat(2, 1fr);
    }

    @media (max-width: 576px) {
      grid-template-columns: 1fr;
    }
  }

  .feature-card {
    text-align: center;
    padding: 40px 28px;
    background: white;
    border-radius: 24px;
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    border: 1px solid rgba(0, 0, 0, 0.05);

    &:hover {
      transform: translateY(-8px);
      box-shadow: 0 25px 50px rgba(0, 0, 0, 0.1);

      .feature-icon-wrapper {
        .feature-ring {
          transform: scale(1.2);
          opacity: 0.3;
        }
      }

      .feature-icon {
        transform: scale(1.1);
      }
    }

    .feature-icon-wrapper {
      position: relative;
      display: inline-block;
      margin-bottom: 24px;

      .feature-ring {
        position: absolute;
        inset: -10px;
        border: 2px solid var(--color-primary);
        border-radius: 50%;
        opacity: 0.1;
        transition: all 0.4s ease;
      }
    }

    .feature-icon {
      font-size: 56px;
      display: block;
      transition: transform 0.4s ease;
    }

    .feature-title {
      font-size: 20px;
      font-weight: 700;
      color: var(--color-text-primary);
      margin-bottom: 14px;
    }

    .feature-desc {
      font-size: 14px;
      color: var(--color-text-muted);
      line-height: 1.7;
    }
  }
}

// ============================================
// 动画关键帧
// ============================================
@keyframes particleFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1);
    opacity: 0.6;
  }
  25% {
    transform: translate(15px, -25px) scale(1.1);
    opacity: 0.8;
  }
  50% {
    transform: translate(-10px, -50px) scale(0.9);
    opacity: 0.4;
  }
  75% {
    transform: translate(20px, -30px) scale(1.05);
    opacity: 0.7;
  }
}

@keyframes breathe {
  0%, 100% {
    transform: translateX(-50%) scale(1);
    opacity: 0.5;
  }
  50% {
    transform: translateX(-50%) scale(1.2);
    opacity: 0.3;
  }
}

@keyframes ringPulse {
  0%, 100% {
    transform: translate(-50%, -50%) scale(1);
    opacity: 0.1;
  }
  50% {
    transform: translate(-50%, -50%) scale(1.05);
    opacity: 0.2;
  }
}

@keyframes gradientShift {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

@keyframes shine {
  0% {
    left: -100%;
  }
  20%, 100% {
    left: 100%;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulseGlow {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.1);
  }
}

.animate-fade-in {
  opacity: 0;
  animation: fadeInUp 0.6s ease forwards;
}

.animate-fade-in-up {
  opacity: 0;
  animation: fadeInUp 0.6s ease forwards;
}

.delay-200 { animation-delay: 200ms; }
.delay-300 { animation-delay: 300ms; }
.delay-400 { animation-delay: 400ms; }
</style>
