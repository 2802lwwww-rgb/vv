<template>
  <div class="starry-sky" ref="skyRef">
    <!-- 背景渐变层 -->
    <div class="sky-gradient"></div>
    
    <!-- 星星粒子层 -->
    <div class="stars-container">
      <div 
        v-for="(star, index) in stars" 
        :key="index"
        class="star"
        :class="{ 'star-event': star.isEvent, 'active': activeStarIndex === index }"
        :style="getStarStyle(star, index)"
        @mouseenter="handleStarHover(index)"
        @mouseleave="handleStarLeave"
        @click="handleStarClick(star)"
      >
        <div class="star-glow"></div>
        <div class="star-core"></div>
        
        <!-- 历史事件信息卡片 -->
        <transition name="card-fade">
          <div v-if="star.isEvent && activeStarIndex === index" class="event-card">
            <div class="event-year">{{ star.year }}</div>
            <h4 class="event-title">{{ star.title }}</h4>
            <p class="event-desc">{{ star.description }}</p>
            <div class="event-action">
              <span class="action-hint">点击探索 →</span>
            </div>
          </div>
        </transition>
      </div>
    </div>
    
    <!-- 流星效果 -->
    <div class="shooting-stars">
      <div v-for="i in 3" :key="i" class="shooting-star" :style="getShootingStarStyle(i)"></div>
    </div>
    
    <!-- 时间轴指示器 -->
    <div class="timeline-indicator">
      <div class="timeline-track">
        <div 
          v-for="event in historyEvents" 
          :key="event.year"
          class="timeline-dot"
          :class="{ active: currentYear === event.year }"
          @click="scrollToYear(event.year)"
        >
          <span class="dot-year">{{ event.year }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

interface Star {
  x: number
  y: number
  size: number
  opacity: number
  twinkle: number
  isEvent?: boolean
  year?: number
  title?: string
  description?: string
  path?: string
}

interface HistoryEvent {
  year: number
  title: string
  description: string
  path: string
}

const emit = defineEmits(['navigate'])


const activeStarIndex = ref<number | null>(null)
const currentYear = ref(1921)

// 历史事件数据
const historyEvents: HistoryEvent[] = [
  { year: 1921, title: '中国共产党成立', description: '开天辟地的大事变，中国革命的面貌从此焕然一新', path: '/resources' },
  { year: 1927, title: '井冈山革命根据地', description: '点燃了工农武装割据的星星之火', path: '/resources' },
  { year: 1934, title: '红军长征', description: '两万五千里长征，中国革命的伟大转折', path: '/resources' },
  { year: 1935, title: '遵义会议', description: '确立了毛泽东的领导地位，挽救了党和红军', path: '/resources' },
  { year: 1937, title: '延安岁月', description: '革命圣地延安，培育了延安精神', path: '/resources' },
  { year: 1949, title: '新中国成立', description: '中国人民从此站起来了', path: '/resources' },
]

// 生成星星数据
const generateStars = (): Star[] => {
  const stars: Star[] = []
  
  // 普通背景星星
  for (let i = 0; i < 80; i++) {
    stars.push({
      x: Math.random() * 100,
      y: Math.random() * 100,
      size: 1 + Math.random() * 2,
      opacity: 0.3 + Math.random() * 0.5,
      twinkle: 2 + Math.random() * 4
    })
  }
  
  // 历史事件星星（更大更亮）
  historyEvents.forEach((event, index) => {
    stars.push({
      x: 15 + (index * 14),
      y: 30 + Math.sin(index * 0.8) * 15,
      size: 6 + Math.random() * 2,
      opacity: 0.9,
      twinkle: 1.5 + Math.random() * 0.5,
      isEvent: true,
      ...event
    })
  })
  
  return stars
}

const stars = ref<Star[]>(generateStars())

const getStarStyle = (star: Star, index: number) => {
  return {
    left: `${star.x}%`,
    top: `${star.y}%`,
    width: `${star.size}px`,
    height: `${star.size}px`,
    opacity: star.opacity,
    animationDuration: `${star.twinkle}s`,
    animationDelay: `${index * 0.1}s`,
    zIndex: star.isEvent ? 10 : 1
  }
}

const getShootingStarStyle = (index: number) => {
  return {
    top: `${10 + Math.random() * 40}%`,
    left: `${Math.random() * 60}%`,
    animationDelay: `${index * 4 + Math.random() * 2}s`
  }
}

const handleStarHover = (index: number) => {
  if (stars.value[index]?.isEvent) {
    activeStarIndex.value = index
  }
}

const handleStarLeave = () => {
  activeStarIndex.value = null
}

const handleStarClick = (star: Star) => {
  if (star.isEvent && star.path) {
    emit('navigate', star.path)
  }
}

const scrollToYear = (year: number) => {
  currentYear.value = year
  // 找到对应的事件星星并高亮
  const eventIndex = stars.value.findIndex(s => s.year === year)
  if (eventIndex !== -1) {
    activeStarIndex.value = eventIndex
    setTimeout(() => {
      activeStarIndex.value = null
    }, 2000)
  }
}

// 自动切换年份动画
let yearInterval: number | null = null

onMounted(() => {
  let yearIndex = 0
  yearInterval = window.setInterval(() => {
    yearIndex = (yearIndex + 1) % historyEvents.length
    currentYear.value = historyEvents[yearIndex]?.year ?? 1921
  }, 5000)
})

onUnmounted(() => {
  if (yearInterval) {
    clearInterval(yearInterval)
  }
})
</script>

<style scoped lang="scss">
.starry-sky {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 600px;
  overflow: hidden;
  background: #0a0a1a;
}

// 背景渐变
.sky-gradient {
  position: absolute;
  inset: 0;
  background: 
    radial-gradient(ellipse 80% 60% at 50% 120%, rgba(139, 0, 0, 0.3), transparent 60%),
    radial-gradient(ellipse 60% 40% at 20% 80%, rgba(139, 69, 19, 0.2), transparent 50%),
    radial-gradient(ellipse 50% 50% at 80% 20%, rgba(25, 25, 112, 0.3), transparent 50%),
    linear-gradient(180deg, #0a0a1a 0%, #1a1a3a 50%, #2a1a2a 100%);
}

// 星星容器
.stars-container {
  position: absolute;
  inset: 0;
  z-index: 2;
}

// 单个星星
.star {
  position: absolute;
  border-radius: 50%;
  cursor: default;
  transform: translate(-50%, -50%);
  
  .star-core {
    width: 100%;
    height: 100%;
    border-radius: 50%;
    background: radial-gradient(circle, #fff 0%, #fff8 50%, transparent 100%);
    animation: twinkle var(--twinkle-duration, 3s) ease-in-out infinite;
  }
  
  .star-glow {
    position: absolute;
    inset: -100%;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.3) 0%, transparent 70%);
    opacity: 0;
    transition: opacity 0.3s ease;
  }
  
  // 历史事件星星特殊样式
  &.star-event {
    cursor: pointer;
    
    .star-core {
      background: radial-gradient(circle, #ffd700 0%, #ff6b6b 50%, #d64541 100%);
      box-shadow: 
        0 0 10px rgba(255, 215, 0, 0.8),
        0 0 20px rgba(255, 107, 107, 0.5),
        0 0 30px rgba(214, 69, 65, 0.3);
    }
    
    .star-glow {
      background: radial-gradient(circle, rgba(255, 215, 0, 0.4) 0%, rgba(255, 107, 107, 0.2) 50%, transparent 70%);
      animation: pulse-glow 2s ease-in-out infinite;
    }
    
    &:hover, &.active {
      .star-glow {
        opacity: 1;
        transform: scale(1.5);
      }
      
      .star-core {
        transform: scale(1.3);
        box-shadow: 
          0 0 15px rgba(255, 215, 0, 1),
          0 0 30px rgba(255, 107, 107, 0.8),
          0 0 45px rgba(214, 69, 65, 0.5);
      }
    }
  }
}

// 事件信息卡片
.event-card {
  position: absolute;
  top: calc(100% + 20px);
  left: 50%;
  transform: translateX(-50%);
  width: 260px;
  padding: 20px;
  background: rgba(20, 20, 40, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 215, 0, 0.3);
  border-radius: 16px;
  box-shadow: 
    0 10px 40px rgba(0, 0, 0, 0.5),
    0 0 20px rgba(255, 215, 0, 0.2),
    inset 0 1px 0 rgba(255, 255, 255, 0.1);
  z-index: 100;
  
  &::before {
    content: '';
    position: absolute;
    top: -8px;
    left: 50%;
    transform: translateX(-50%) rotate(45deg);
    width: 16px;
    height: 16px;
    background: rgba(20, 20, 40, 0.95);
    border-left: 1px solid rgba(255, 215, 0, 0.3);
    border-top: 1px solid rgba(255, 215, 0, 0.3);
  }
  
  .event-year {
    font-size: 14px;
    font-weight: 600;
    color: #ffd700;
    margin-bottom: 8px;
    letter-spacing: 2px;
  }
  
  .event-title {
    font-size: 18px;
    font-weight: 700;
    color: #fff;
    margin: 0 0 12px 0;
    line-height: 1.4;
  }
  
  .event-desc {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.7);
    line-height: 1.6;
    margin: 0 0 16px 0;
  }
  
  .event-action {
    .action-hint {
      font-size: 13px;
      color: #ff6b6b;
      font-weight: 500;
      transition: color 0.2s;
      
      &:hover {
        color: #ffd700;
      }
    }
  }
}

// 卡片动画
.card-fade-enter-active,
.card-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-fade-enter-from,
.card-fade-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(10px);
}

// 流星效果
.shooting-stars {
  position: absolute;
  inset: 0;
  z-index: 1;
  overflow: hidden;
  pointer-events: none;
}

.shooting-star {
  position: absolute;
  width: 150px;
  height: 2px;
  background: linear-gradient(90deg, rgba(255, 255, 255, 0.8), transparent);
  border-radius: 2px;
  transform: rotate(-45deg);
  opacity: 0;
  animation: shooting 8s ease-in-out infinite;
  
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: -1px;
    width: 6px;
    height: 4px;
    background: #fff;
    border-radius: 50%;
    box-shadow: 0 0 10px 2px rgba(255, 255, 255, 0.8);
  }
}

// 时间轴指示器
.timeline-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 20;
  
  .timeline-track {
    display: flex;
    align-items: center;
    gap: 40px;
    padding: 16px 32px;
    background: rgba(0, 0, 0, 0.4);
    backdrop-filter: blur(10px);
    border-radius: 50px;
    border: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .timeline-dot {
    position: relative;
    width: 12px;
    height: 12px;
    background: rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &::before {
      content: '';
      position: absolute;
      inset: -4px;
      border: 2px solid transparent;
      border-radius: 50%;
      transition: border-color 0.3s;
    }
    
    .dot-year {
      position: absolute;
      bottom: calc(100% + 10px);
      left: 50%;
      transform: translateX(-50%);
      font-size: 12px;
      color: rgba(255, 255, 255, 0.5);
      white-space: nowrap;
      transition: color 0.3s;
    }
    
    &:hover, &.active {
      background: #ffd700;
      box-shadow: 0 0 15px rgba(255, 215, 0, 0.6);
      
      &::before {
        border-color: rgba(255, 215, 0, 0.4);
      }
      
      .dot-year {
        color: #ffd700;
        font-weight: 600;
      }
    }
  }
}

// 动画关键帧
@keyframes twinkle {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(0.8);
  }
}

@keyframes pulse-glow {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.2);
  }
}

@keyframes shooting {
  0% {
    opacity: 0;
    transform: translateX(0) translateY(0) rotate(-45deg);
  }
  5% {
    opacity: 1;
  }
  15% {
    opacity: 0;
    transform: translateX(300px) translateY(300px) rotate(-45deg);
  }
  100% {
    opacity: 0;
    transform: translateX(300px) translateY(300px) rotate(-45deg);
  }
}

// 响应式
@media (max-width: 768px) {
  .timeline-indicator {
    .timeline-track {
      gap: 20px;
      padding: 12px 20px;
    }
    
    .timeline-dot {
      width: 10px;
      height: 10px;
      
      .dot-year {
        font-size: 10px;
      }
    }
  }
  
  .event-card {
    width: 220px;
    padding: 16px;
    
    .event-title {
      font-size: 16px;
    }
    
    .event-desc {
      font-size: 13px;
    }
  }
}
</style>
