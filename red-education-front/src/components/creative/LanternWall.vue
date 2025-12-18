<template>
  <div class="lantern-wall">
    <!-- 背景装饰 -->
    <div class="wall-background">
      <div class="bg-gradient"></div>
      <div class="floating-particles">
        <div v-for="i in 20" :key="i" class="particle" :style="getParticleStyle(i)"></div>
      </div>
    </div>

    <!-- 标题区 -->
    <div class="wall-header">
      <div class="header-decoration left">🏮</div>
      <div class="header-content">
        <h2 class="header-title">红色社区</h2>
        <p class="header-subtitle">点亮祝福，传递力量</p>
      </div>
      <div class="header-decoration right">🏮</div>
    </div>

    <!-- 灯笼墙 -->
    <div class="lanterns-container">
      <div 
        v-for="(post, index) in displayPosts" 
        :key="post.id"
        class="lantern"
        :class="{ 'lit': post.isLit, 'popular': post.likes > 50 }"
        :style="getLanternStyle(index)"
        @click="handleLanternClick(post)"
        @mouseenter="hoveredLantern = post.id"
        @mouseleave="hoveredLantern = null"
      >
        <!-- 灯笼绳 -->
        <div class="lantern-rope"></div>
        
        <!-- 灯笼主体 -->
        <div class="lantern-body">
          <!-- 顶部装饰 -->
          <div class="lantern-top"></div>
          
          <!-- 灯笼内容 -->
          <div class="lantern-content">
            <div class="lantern-glow"></div>
            <p class="lantern-text">{{ truncateText(post.title || post.content, 20) }}</p>
            <div class="lantern-author">
              <span class="author-avatar">{{ getAvatarEmoji(post.nickname) }}</span>
              <span class="author-name">{{ post.nickname || '匿名' }}</span>
            </div>
          </div>
          
          <!-- 底部装饰/流苏 -->
          <div class="lantern-bottom">
            <div class="tassel"></div>
          </div>
          
          <!-- 点亮效果 -->
          <div class="light-effect" v-if="post.isLit">
            <div class="inner-glow"></div>
          </div>
        </div>

        <!-- 点赞数 -->
        <div class="like-count" :class="{ 'highlight': post.likes > 30 }">
          <span class="fire-icon">🔥</span>
          <span>{{ post.likes || 0 }}</span>
        </div>

        <!-- 悬停详情卡片 -->
        <transition name="detail-card">
          <div v-if="hoveredLantern === post.id" class="lantern-detail">
            <h4 class="detail-title">{{ post.title || '心愿' }}</h4>
            <p class="detail-content">{{ post.content }}</p>
            <div class="detail-meta">
              <span class="meta-item">
                <span class="icon">👤</span>
                {{ post.nickname || '匿名' }}
              </span>
              <span class="meta-item">
                <span class="icon">💬</span>
                {{ post.commentCount || 0 }} 评论
              </span>
            </div>
            <div class="detail-action">点击查看详情 →</div>
          </div>
        </transition>
      </div>
    </div>

    <!-- 点亮按钮 -->
    <div class="action-section">
      <button class="light-btn" @click="$emit('create')">
        <span class="btn-icon">🏮</span>
        <span class="btn-text">点亮我的祝福</span>
        <div class="btn-glow"></div>
      </button>
    </div>

    <!-- 统计信息 -->
    <div class="stats-bar">
      <div class="stat-item">
        <span class="stat-icon">🏮</span>
        <span class="stat-value">{{ totalPosts }}</span>
        <span class="stat-label">盏心愿灯</span>
      </div>
      <div class="stat-item">
        <span class="stat-icon">🔥</span>
        <span class="stat-value">{{ totalLikes }}</span>
        <span class="stat-label">次点亮</span>
      </div>
      <div class="stat-item">
        <span class="stat-icon">👥</span>
        <span class="stat-value">{{ uniqueUsers }}</span>
        <span class="stat-label">位参与者</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Post {
  id: number
  title?: string
  content: string
  nickname?: string
  avatar?: string
  likes: number
  commentCount?: number
  isLit?: boolean
  createTime?: string
}

interface Props {
  posts?: Post[]
}

const props = withDefaults(defineProps<Props>(), {
  posts: () => []
})

const emit = defineEmits(['select', 'create', 'like'])

const hoveredLantern = ref<number | null>(null)

// 默认演示数据
const defaultPosts: Post[] = [
  { id: 1, title: '祖国万岁', content: '愿祖国繁荣昌盛，人民幸福安康', nickname: '小红', likes: 128, commentCount: 23, isLit: true },
  { id: 2, title: '不忘初心', content: '传承红色基因，牢记革命先烈', nickname: '志愿者小李', likes: 89, commentCount: 15, isLit: true },
  { id: 3, title: '学习心得', content: '学习党史，感悟信仰力量', nickname: '学生小张', likes: 56, commentCount: 8, isLit: true },
  { id: 4, title: '红色精神', content: '井冈山精神永放光芒', nickname: '历史爱好者', likes: 72, commentCount: 12, isLit: true },
  { id: 5, title: '奋斗青春', content: '青春献给党，奋斗正当时', nickname: '青年党员', likes: 45, commentCount: 6, isLit: false },
  { id: 6, title: '感恩祝福', content: '感谢革命先辈的付出', nickname: '王老师', likes: 38, commentCount: 4, isLit: false },
  { id: 7, title: '学习有感', content: '红色教育让我更加珍惜今天', nickname: '读者小陈', likes: 29, commentCount: 3, isLit: false },
  { id: 8, title: '传承', content: '让红色故事代代相传', nickname: '志愿者小刘', likes: 67, commentCount: 9, isLit: true },
]

const displayPosts = computed(() => {
  return props.posts.length > 0 ? props.posts.map(p => ({ ...p, isLit: (p.likes || 0) > 20 })) : defaultPosts
})

const totalPosts = computed(() => displayPosts.value.length)
const totalLikes = computed(() => displayPosts.value.reduce((sum, p) => sum + (p.likes || 0), 0))
const uniqueUsers = computed(() => new Set(displayPosts.value.map(p => p.nickname)).size)

// 灯笼样式
const getLanternStyle = (index: number) => {
  const row = Math.floor(index / 4)
  const col = index % 4
  const xOffset = (col * 25) + 12.5 + (row % 2 ? 12.5 : 0)
  const yOffset = row * 180 + 20
  const rotation = (Math.random() - 0.5) * 10
  const delay = index * 0.1
  
  return {
    left: `${xOffset}%`,
    top: `${yOffset}px`,
    transform: `rotate(${rotation}deg)`,
    animationDelay: `${delay}s`
  }
}

// 粒子样式
const getParticleStyle = (index: number) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${2 + Math.random() * 4}px`,
    height: `${2 + Math.random() * 4}px`,
    animationDelay: `${Math.random() * 5}s`,
    animationDuration: `${3 + Math.random() * 4}s`
  }
}

// 头像表情
const getAvatarEmoji = (name?: string) => {
  const emojis = ['😊', '🙂', '😄', '🤗', '😎', '🥰', '🤩', '😇']
  const hash = (name || '').split('').reduce((a, b) => a + b.charCodeAt(0), 0)
  return emojis[hash % emojis.length]
}

// 截断文本
const truncateText = (text: string, maxLen: number) => {
  if (!text) return ''
  return text.length > maxLen ? text.slice(0, maxLen) + '...' : text
}

// 点击灯笼
const handleLanternClick = (post: Post) => {
  emit('select', post.id)
}
</script>

<style scoped lang="scss">
.lantern-wall {
  position: relative;
  min-height: 700px;
  background: linear-gradient(180deg, #1a0a0a 0%, #2a0f0f 50%, #1a0505 100%);
  border-radius: 24px;
  padding: 40px;
  overflow: hidden;
}

// 背景
.wall-background {
  position: absolute;
  inset: 0;
  z-index: 0;
  
  .bg-gradient {
    position: absolute;
    inset: 0;
    background: 
      radial-gradient(ellipse 50% 30% at 50% 0%, rgba(255, 100, 50, 0.15) 0%, transparent 100%),
      radial-gradient(ellipse 60% 40% at 30% 80%, rgba(255, 50, 50, 0.1) 0%, transparent 100%);
  }
  
  .floating-particles {
    position: absolute;
    inset: 0;
    pointer-events: none;
    
    .particle {
      position: absolute;
      background: radial-gradient(circle, rgba(255, 200, 100, 0.8) 0%, transparent 70%);
      border-radius: 50%;
      animation: float-particle 5s ease-in-out infinite;
    }
  }
}

// 头部
.wall-header {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 24px;
  margin-bottom: 40px;
  
  .header-decoration {
    font-size: 40px;
    animation: swing 3s ease-in-out infinite;
    
    &.right {
      animation-delay: -1.5s;
    }
  }
  
  .header-content {
    text-align: center;
  }
  
  .header-title {
    font-size: 36px;
    font-weight: 800;
    color: #ffd700;
    margin: 0 0 8px 0;
    text-shadow: 0 0 20px rgba(255, 215, 0, 0.5);
  }
  
  .header-subtitle {
    font-size: 16px;
    color: rgba(255, 200, 150, 0.8);
    margin: 0;
    letter-spacing: 2px;
  }
}

// 灯笼容器
.lanterns-container {
  position: relative;
  z-index: 1;
  min-height: 400px;
  margin-bottom: 40px;
}

// 单个灯笼
.lantern {
  position: absolute;
  cursor: pointer;
  animation: lantern-appear 0.6s ease forwards, lantern-sway 4s ease-in-out infinite;
  opacity: 0;
  
  &:hover {
    z-index: 10;
    
    .lantern-body {
      transform: scale(1.1);
    }
  }
  
  &.lit {
    .lantern-content {
      background: linear-gradient(180deg, #ff4500 0%, #dc143c 50%, #8b0000 100%);
    }
    
    .lantern-glow {
      opacity: 1;
    }
  }
  
  &.popular {
    .lantern-content {
      box-shadow: 0 0 30px rgba(255, 100, 50, 0.6);
    }
  }
}

.lantern-rope {
  width: 2px;
  height: 30px;
  background: linear-gradient(180deg, #8b4513 0%, #654321 100%);
  margin: 0 auto;
}

.lantern-body {
  position: relative;
  width: 80px;
  transition: transform 0.3s ease;
}

.lantern-top {
  width: 50px;
  height: 12px;
  margin: 0 auto;
  background: linear-gradient(180deg, #ffd700 0%, #daa520 100%);
  border-radius: 4px 4px 0 0;
  box-shadow: 0 -2px 10px rgba(255, 215, 0, 0.3);
}

.lantern-content {
  position: relative;
  width: 80px;
  min-height: 100px;
  background: linear-gradient(180deg, #8b0000 0%, #660000 50%, #4a0000 100%);
  border-radius: 8px;
  padding: 12px 8px;
  box-sizing: border-box;
  overflow: hidden;
  
  .lantern-glow {
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at center, rgba(255, 200, 100, 0.3) 0%, transparent 70%);
    opacity: 0;
    transition: opacity 0.5s ease;
  }
  
  .lantern-text {
    font-size: 11px;
    color: #ffd700;
    text-align: center;
    margin: 0 0 8px 0;
    line-height: 1.5;
    position: relative;
    z-index: 1;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  }
  
  .lantern-author {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    position: relative;
    z-index: 1;
    
    .author-avatar {
      font-size: 16px;
    }
    
    .author-name {
      font-size: 9px;
      color: rgba(255, 215, 0, 0.7);
    }
  }
}

.lantern-bottom {
  width: 50px;
  height: 12px;
  margin: 0 auto;
  background: linear-gradient(180deg, #daa520 0%, #ffd700 100%);
  border-radius: 0 0 4px 4px;
  display: flex;
  justify-content: center;
  
  .tassel {
    width: 6px;
    height: 25px;
    background: linear-gradient(180deg, #ffd700 0%, #ff6347 100%);
    border-radius: 0 0 3px 3px;
    margin-top: 5px;
    animation: tassel-sway 2s ease-in-out infinite;
  }
}

.light-effect {
  position: absolute;
  inset: -10px;
  pointer-events: none;
  
  .inner-glow {
    position: absolute;
    inset: 0;
    background: radial-gradient(ellipse at center, rgba(255, 150, 50, 0.4) 0%, transparent 60%);
    animation: pulse-glow 2s ease-in-out infinite;
  }
}

.like-count {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  margin-top: 8px;
  font-size: 12px;
  color: rgba(255, 200, 150, 0.8);
  
  &.highlight {
    color: #ffd700;
    
    .fire-icon {
      animation: fire-flicker 0.5s ease-in-out infinite;
    }
  }
  
  .fire-icon {
    font-size: 14px;
  }
}

// 详情卡片
.lantern-detail {
  position: absolute;
  top: -20px;
  left: 50%;
  transform: translateX(-50%) translateY(-100%);
  width: 180px;
  background: rgba(20, 10, 10, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 215, 0, 0.3);
  border-radius: 12px;
  padding: 16px;
  z-index: 100;
  
  &::after {
    content: '';
    position: absolute;
    top: 100%;
    left: 50%;
    transform: translateX(-50%);
    border: 8px solid transparent;
    border-top-color: rgba(20, 10, 10, 0.95);
  }
  
  .detail-title {
    font-size: 14px;
    font-weight: 700;
    color: #ffd700;
    margin: 0 0 8px 0;
  }
  
  .detail-content {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.8);
    line-height: 1.6;
    margin: 0 0 12px 0;
  }
  
  .detail-meta {
    display: flex;
    gap: 12px;
    margin-bottom: 10px;
    
    .meta-item {
      font-size: 11px;
      color: rgba(255, 200, 150, 0.7);
      display: flex;
      align-items: center;
      gap: 4px;
    }
  }
  
  .detail-action {
    font-size: 11px;
    color: #ff6347;
    font-weight: 500;
  }
}

.detail-card-enter-active,
.detail-card-leave-active {
  transition: all 0.3s ease;
}

.detail-card-enter-from,
.detail-card-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-90%);
}

// 点亮按钮
.action-section {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  margin-bottom: 32px;
}

.light-btn {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 40px;
  background: linear-gradient(135deg, #dc143c 0%, #8b0000 100%);
  border: 2px solid rgba(255, 215, 0, 0.5);
  border-radius: 50px;
  color: #ffd700;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-3px);
    box-shadow: 0 10px 40px rgba(220, 20, 60, 0.5);
    
    .btn-glow {
      opacity: 1;
    }
  }
  
  .btn-icon {
    font-size: 24px;
    animation: swing 2s ease-in-out infinite;
  }
  
  .btn-glow {
    position: absolute;
    inset: -2px;
    background: linear-gradient(135deg, rgba(255, 215, 0, 0.3) 0%, transparent 50%);
    border-radius: inherit;
    opacity: 0;
    transition: opacity 0.3s ease;
  }
}

// 统计条
.stats-bar {
  position: relative;
  z-index: 1;
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 16px;
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .stat-icon {
      font-size: 24px;
    }
    
    .stat-value {
      font-size: 24px;
      font-weight: 700;
      color: #ffd700;
    }
    
    .stat-label {
      font-size: 14px;
      color: rgba(255, 200, 150, 0.8);
    }
  }
}

// 动画
@keyframes lantern-appear {
  from {
    opacity: 0;
    transform: translateY(-30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes lantern-sway {
  0%, 100% {
    transform: rotate(0deg);
  }
  25% {
    transform: rotate(3deg);
  }
  75% {
    transform: rotate(-3deg);
  }
}

@keyframes swing {
  0%, 100% {
    transform: rotate(-5deg);
  }
  50% {
    transform: rotate(5deg);
  }
}

@keyframes tassel-sway {
  0%, 100% {
    transform: rotate(-5deg);
  }
  50% {
    transform: rotate(5deg);
  }
}

@keyframes pulse-glow {
  0%, 100% {
    opacity: 0.4;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

@keyframes fire-flicker {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

@keyframes float-particle {
  0%, 100% {
    transform: translateY(0) scale(1);
    opacity: 0.6;
  }
  50% {
    transform: translateY(-20px) scale(1.2);
    opacity: 1;
  }
}

// 响应式
@media (max-width: 768px) {
  .lantern-wall {
    padding: 24px 16px;
  }
  
  .wall-header {
    .header-title {
      font-size: 28px;
    }
    
    .header-decoration {
      font-size: 32px;
    }
  }
  
  .lantern-body {
    width: 65px;
  }
  
  .lantern-content {
    width: 65px;
    min-height: 80px;
    padding: 10px 6px;
    
    .lantern-text {
      font-size: 10px;
    }
  }
  
  .lantern-top, .lantern-bottom {
    width: 40px;
  }
  
  .stats-bar {
    flex-wrap: wrap;
    gap: 20px;
    
    .stat-item {
      .stat-value {
        font-size: 20px;
      }
      
      .stat-label {
        font-size: 12px;
      }
    }
  }
}
</style>
