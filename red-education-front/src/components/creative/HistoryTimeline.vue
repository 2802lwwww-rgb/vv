<template>
  <div class="history-timeline" ref="timelineRef">
    <!-- 山水画背景 -->
    <div class="landscape-bg">
      <!-- 远景：天空和云 -->
      <svg class="layer layer-sky" viewBox="0 0 1920 400" preserveAspectRatio="xMidYMid slice">
        <defs>
          <linearGradient id="skyGradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" style="stop-color:#1a1a2e" />
            <stop offset="50%" style="stop-color:#2d2d44" />
            <stop offset="100%" style="stop-color:#4a3f5c" />
          </linearGradient>
          <filter id="glow">
            <feGaussianBlur stdDeviation="3" result="coloredBlur"/>
            <feMerge>
              <feMergeNode in="coloredBlur"/>
              <feMergeNode in="SourceGraphic"/>
            </feMerge>
          </filter>
        </defs>
        <rect width="100%" height="100%" fill="url(#skyGradient)"/>
        <!-- 月亮 -->
        <circle cx="150" cy="80" r="40" fill="#fff5e6" filter="url(#glow)" opacity="0.9"/>
      </svg>
      
      <!-- 远山层 -->
      <svg class="layer layer-far-mountain" viewBox="0 0 1920 300" preserveAspectRatio="none">
        <defs>
          <linearGradient id="farMountainGrad" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" style="stop-color:#3d3d5c" />
            <stop offset="100%" style="stop-color:#2d2d44" />
          </linearGradient>
        </defs>
        <path d="M0,300 L0,180 Q100,100 200,160 T400,120 T600,150 T800,100 T1000,140 T1200,90 T1400,130 T1600,80 T1800,120 L1920,100 L1920,300 Z" 
              fill="url(#farMountainGrad)" opacity="0.6"/>
      </svg>
      
      <!-- 中景山层 -->
      <svg class="layer layer-mid-mountain" viewBox="0 0 1920 350" preserveAspectRatio="none">
        <defs>
          <linearGradient id="midMountainGrad" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" style="stop-color:#4a4a6a" />
            <stop offset="100%" style="stop-color:#3a3a5a" />
          </linearGradient>
        </defs>
        <path d="M0,350 L0,220 Q150,120 300,200 T600,140 T900,180 T1200,100 T1500,160 T1800,130 L1920,150 L1920,350 Z" 
              fill="url(#midMountainGrad)" opacity="0.8"/>
      </svg>
      
      <!-- 近景山层 + 建筑轮廓 -->
      <svg class="layer layer-near" viewBox="0 0 1920 400" preserveAspectRatio="none">
        <defs>
          <linearGradient id="nearGrad" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" style="stop-color:#2a2a4a" />
            <stop offset="100%" style="stop-color:#1a1a3a" />
          </linearGradient>
        </defs>
        <path d="M0,400 L0,300 Q100,250 200,280 T400,240 T600,270 T800,200 T1000,250 T1200,180 T1400,220 T1600,170 T1800,210 L1920,190 L1920,400 Z" 
              fill="url(#nearGrad)"/>
      </svg>
    </div>
    
    <!-- 历史地标点 -->
    <div class="landmarks-container">
      <div 
        v-for="(landmark, index) in landmarks" 
        :key="landmark.year"
        class="landmark"
        :class="{ active: activeLandmark === index }"
        :style="getLandmarkStyle(index)"
        @mouseenter="activeLandmark = index"
        @mouseleave="activeLandmark = null"
        @click="handleLandmarkClick(landmark)"
      >
        <!-- 地标图标 -->
        <div class="landmark-icon">
          <span class="icon-emoji">{{ landmark.icon }}</span>
          <div class="icon-glow"></div>
        </div>
        
        <!-- 地标信息 -->
        <div class="landmark-info">
          <span class="landmark-year">{{ landmark.year }}</span>
          <h4 class="landmark-title">{{ landmark.title }}</h4>
        </div>
        
        <!-- 详细卡片 -->
        <transition name="landmark-card">
          <div v-if="activeLandmark === index" class="landmark-card">
            <p class="card-desc">{{ landmark.description }}</p>
            <span class="card-action">点击进入学习 →</span>
          </div>
        </transition>
        
        <!-- 连接线 -->
        <div v-if="index < landmarks.length - 1" class="connection-line"></div>
      </div>
    </div>
    
    <!-- 底部装饰 -->
    <div class="bottom-decoration">
      <svg viewBox="0 0 1920 100" preserveAspectRatio="none">
        <path d="M0,100 L0,50 Q480,20 960,40 T1920,30 L1920,100 Z" fill="rgba(139, 0, 0, 0.15)"/>
        <path d="M0,100 L0,70 Q480,50 960,60 T1920,55 L1920,100 Z" fill="rgba(139, 0, 0, 0.1)"/>
      </svg>
    </div>
    
    <!-- 滚动提示 -->
    <div class="scroll-hint">
      <span class="hint-text">← 滑动探索历史 →</span>
      <div class="hint-arrows">
        <span class="arrow left">‹</span>
        <span class="arrow right">›</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'

interface Landmark {
  year: number
  title: string
  description: string
  icon: string
  path: string
}

const router = useRouter()
const activeLandmark = ref<number | null>(null)

const landmarks: Landmark[] = [
  {
    year: 1921,
    title: '开天辟地',
    description: '中国共产党第一次全国代表大会在上海召开，中国革命的面貌从此焕然一新',
    icon: '🌟',
    path: '/resources'
  },
  {
    year: 1927,
    title: '井冈山',
    description: '创建第一个农村革命根据地，点燃了中国革命的星星之火',
    icon: '🏔️',
    path: '/resources'
  },
  {
    year: 1935,
    title: '遵义会议',
    description: '确立了以毛泽东为代表的新的中央正确领导，挽救了党和红军',
    icon: '🏛️',
    path: '/resources'
  },
  {
    year: 1937,
    title: '延安岁月',
    description: '革命圣地延安，党中央和毛泽东在这里战斗了十三个春秋',
    icon: '🏕️',
    path: '/resources'
  },
  {
    year: 1949,
    title: '开国大典',
    description: '中华人民共和国成立，中国人民从此站起来了',
    icon: '🏯',
    path: '/resources'
  }
]

const getLandmarkStyle = (index: number) => {
  const totalLandmarks = landmarks.length
  const spacing = 100 / (totalLandmarks + 1)
  const left = spacing * (index + 1)
  
  // 添加一些垂直变化，营造山水感
  const baseTop = 45
  const variation = Math.sin(index * 1.2) * 8
  
  return {
    left: `${left}%`,
    top: `${baseTop + variation}%`
  }
}

const handleLandmarkClick = (landmark: Landmark) => {
  router.push(landmark.path)
}
</script>

<style scoped lang="scss">
.history-timeline {
  position: relative;
  width: 100%;
  height: 450px;
  overflow: visible;
  background: linear-gradient(180deg, #1a1a2e 0%, #2d2d44 100%);
}

// 山水画背景层
.landscape-bg {
  position: absolute;
  inset: 0;
  
  .layer {
    position: absolute;
    width: 100%;
    left: 0;
    
    &.layer-sky {
      top: 0;
      height: 100%;
    }
    
    &.layer-far-mountain {
      bottom: 20%;
      height: 50%;
      animation: float-slow 20s ease-in-out infinite;
    }
    
    &.layer-mid-mountain {
      bottom: 10%;
      height: 55%;
      animation: float-slow 15s ease-in-out infinite reverse;
    }
    
    &.layer-near {
      bottom: 0;
      height: 60%;
    }
  }
}

// 地标容器
.landmarks-container {
  position: absolute;
  inset: 0;
  z-index: 10;
}

// 单个地标
.landmark {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  z-index: 10;
  
  &:hover, &.active {
    z-index: 20;
    
    .landmark-icon {
      transform: scale(1.2) translateY(-5px);
      
      .icon-glow {
        opacity: 1;
        transform: scale(1.5);
      }
    }
    
    .landmark-info {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  .landmark-icon {
    position: relative;
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(139, 0, 0, 0.8), rgba(180, 50, 50, 0.9));
    border-radius: 50%;
    border: 3px solid rgba(255, 215, 0, 0.6);
    box-shadow: 
      0 4px 20px rgba(0, 0, 0, 0.4),
      0 0 30px rgba(139, 0, 0, 0.4);
    transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
    
    .icon-emoji {
      font-size: 28px;
      filter: drop-shadow(0 2px 4px rgba(0,0,0,0.3));
    }
    
    .icon-glow {
      position: absolute;
      inset: -10px;
      background: radial-gradient(circle, rgba(255, 215, 0, 0.4) 0%, transparent 70%);
      border-radius: 50%;
      opacity: 0;
      transition: all 0.4s ease;
    }
  }
  
  .landmark-info {
    position: absolute;
    top: calc(100% + 10px);
    left: 50%;
    transform: translateX(-50%) translateY(10px);
    text-align: center;
    opacity: 0.8;
    transition: all 0.3s ease;
    white-space: nowrap;
    
    .landmark-year {
      display: block;
      font-size: 13px;
      font-weight: 600;
      color: #ffd700;
      margin-bottom: 4px;
      letter-spacing: 1px;
    }
    
    .landmark-title {
      font-size: 15px;
      font-weight: 700;
      color: #fff;
      margin: 0;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
    }
  }
  
  // 详细卡片 - 显示在图标上方
  .landmark-card {
    position: absolute;
    bottom: calc(100% + 90px);
    left: 50%;
    transform: translateX(-50%);
    width: 280px;
    padding: 18px;
    background: rgba(30, 30, 50, 0.95);
    backdrop-filter: blur(10px);
    border: 1px solid rgba(255, 215, 0, 0.3);
    border-radius: 12px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
    z-index: 100;
    
    &::after {
      content: '';
      position: absolute;
      top: 100%;
      left: 50%;
      transform: translateX(-50%);
      border: 8px solid transparent;
      border-top-color: rgba(30, 30, 50, 0.95);
    }
    
    .card-desc {
      font-size: 14px;
      color: rgba(255, 255, 255, 0.9);
      line-height: 1.7;
      margin: 0 0 12px 0;
      word-wrap: break-word;
      overflow-wrap: break-word;
    }
    
    .card-action {
      font-size: 12px;
      color: #ff6b6b;
      font-weight: 500;
    }
  }
  
  // 连接线
  .connection-line {
    position: absolute;
    top: 50%;
    left: calc(100% + 10px);
    width: 100px;
    height: 2px;
    background: linear-gradient(90deg, rgba(255, 215, 0, 0.6), rgba(255, 215, 0, 0.1));
    transform: translateY(-50%);
    
    &::before {
      content: '';
      position: absolute;
      right: 0;
      top: 50%;
      transform: translateY(-50%);
      width: 6px;
      height: 6px;
      background: rgba(255, 215, 0, 0.4);
      border-radius: 50%;
    }
  }
}

// 卡片动画
.landmark-card-enter-active,
.landmark-card-leave-active {
  transition: all 0.3s ease;
}

.landmark-card-enter-from,
.landmark-card-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(10px);
}

// 底部装饰
.bottom-decoration {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 100px;
  pointer-events: none;
  
  svg {
    width: 100%;
    height: 100%;
  }
}

// 滚动提示
.scroll-hint {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  opacity: 0.6;
  animation: fade-pulse 2s ease-in-out infinite;
  
  .hint-text {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
    letter-spacing: 2px;
  }
  
  .hint-arrows {
    margin-top: 8px;
    
    .arrow {
      display: inline-block;
      font-size: 20px;
      color: #ffd700;
      margin: 0 10px;
      animation: arrow-bounce 1.5s ease-in-out infinite;
      
      &.left {
        animation-direction: reverse;
      }
    }
  }
}

// 动画
@keyframes float-slow {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(-20px);
  }
}

@keyframes fade-pulse {
  0%, 100% {
    opacity: 0.6;
  }
  50% {
    opacity: 0.3;
  }
}

@keyframes arrow-bounce {
  0%, 100% {
    transform: translateX(0);
  }
  50% {
    transform: translateX(5px);
  }
}

// 响应式
@media (max-width: 992px) {
  .landmark {
    .landmark-icon {
      width: 50px;
      height: 50px;
      
      .icon-emoji {
        font-size: 24px;
      }
    }
    
    .connection-line {
      width: 60px;
    }
  }
}

@media (max-width: 768px) {
  .history-timeline {
    height: 350px;
  }
  
  .landmark {
    .landmark-icon {
      width: 44px;
      height: 44px;
      
      .icon-emoji {
        font-size: 20px;
      }
    }
    
    .landmark-info {
      .landmark-year {
        font-size: 11px;
      }
      
      .landmark-title {
        font-size: 13px;
      }
    }
    
    .connection-line {
      display: none;
    }
    
    .landmark-card {
      width: 180px;
      padding: 12px;
    }
  }
}
</style>
