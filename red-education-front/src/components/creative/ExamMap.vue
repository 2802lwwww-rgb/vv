<template>
  <div class="exam-map">
    <!-- 地图背景 -->
    <div class="map-background">
      <svg class="bg-pattern" viewBox="0 0 100 100" preserveAspectRatio="none">
        <defs>
          <pattern id="grid" width="10" height="10" patternUnits="userSpaceOnUse">
            <path d="M 10 0 L 0 0 0 10" fill="none" stroke="rgba(255,255,255,0.05)" stroke-width="0.5"/>
          </pattern>
        </defs>
        <rect width="100" height="100" fill="url(#grid)"/>
      </svg>
    </div>

    <!-- 地图标题 -->
    <div class="map-header">
      <div class="header-badge">🗺️ 红色学习之旅</div>
      <h2 class="header-title">闯关地图</h2>
      <p class="header-desc">完成每个关卡，解锁新的学习挑战</p>
    </div>

    <!-- 进度条 -->
    <div class="progress-bar">
      <div class="progress-track">
        <div class="progress-fill" :style="{ width: progressPercentage + '%' }"></div>
      </div>
      <span class="progress-text">{{ completedCount }} / {{ levels.length }} 已完成</span>
    </div>

    <!-- 闯关路径 -->
    <div class="map-path">
      <!-- 连接线 SVG -->
      <svg class="path-lines" viewBox="0 0 1000 200" preserveAspectRatio="none">
        <defs>
          <linearGradient id="pathGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#fcd34d"/>
            <stop offset="100%" style="stop-color:#e11d48"/>
          </linearGradient>
        </defs>
        <path 
          class="path-bg" 
          d="M 50,100 Q 150,60 250,100 T 450,100 T 650,100 T 850,100 L 950,100"
          fill="none"
          stroke="rgba(255,255,255,0.1)"
          stroke-width="8"
          stroke-linecap="round"
        />
        <path 
          class="path-active" 
          d="M 50,100 Q 150,60 250,100 T 450,100 T 650,100 T 850,100 L 950,100"
          fill="none"
          stroke="url(#pathGradient)"
          stroke-width="8"
          stroke-linecap="round"
          :stroke-dasharray="pathLength"
          :stroke-dashoffset="pathOffset"
        />
      </svg>

      <!-- 关卡节点 -->
      <div class="levels-container">
        <div 
          v-for="(level, index) in levels" 
          :key="level.id"
          class="level-node"
          :class="{
            'completed': level.status === 'completed',
            'current': level.status === 'current',
            'locked': level.status === 'locked'
          }"
          :style="getLevelPosition(index)"
          @click="handleLevelClick(level)"
        >
          <!-- 节点光晕 -->
          <div class="node-glow"></div>
          
          <!-- 节点主体 -->
          <div class="node-body">
            <span class="node-icon">{{ level.icon }}</span>
            <div class="node-stars" v-if="level.status === 'completed'">
              <span v-for="star in level.stars" :key="star" class="star">⭐</span>
            </div>
            <div class="lock-icon" v-if="level.status === 'locked'">🔒</div>
          </div>
          
          <!-- 节点标签 -->
          <div class="node-label">
            <span class="level-name">{{ level.name }}</span>
            <span class="level-info" v-if="level.status === 'completed'">
              得分: {{ level.score }}
            </span>
            <span class="level-info" v-else-if="level.status === 'current'">
              进行中
            </span>
            <span class="level-info locked" v-else>
              未解锁
            </span>
          </div>

          <!-- 悬停卡片 -->
          <transition name="tooltip">
            <div v-if="hoveredLevel === level.id" class="level-tooltip">
              <h4>{{ level.name }}</h4>
              <p>{{ level.description }}</p>
              <div class="tooltip-stats">
                <span>📝 {{ level.questionCount }} 道题</span>
                <span>⏱️ {{ level.timeLimit }} 分钟</span>
              </div>
              <div class="tooltip-action">
                {{ level.status === 'locked' ? '完成上一关解锁' : '点击开始' }}
              </div>
            </div>
          </transition>
        </div>
      </div>
    </div>

    <!-- 成就展示 -->
    <div class="achievements-section">
      <h3 class="section-title">🏆 成就徽章</h3>
      <div class="achievements-grid">
        <div 
          v-for="achievement in achievements" 
          :key="achievement.id"
          class="achievement-badge"
          :class="{ unlocked: achievement.unlocked }"
        >
          <span class="badge-icon">{{ achievement.icon }}</span>
          <span class="badge-name">{{ achievement.name }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Level {
  id: number
  name: string
  description: string
  icon: string
  status: 'completed' | 'current' | 'locked'
  stars?: number
  score?: number
  totalScore?: number
  questionCount: number
  timeLimit: number
  examId?: number
}

interface Achievement {
  id: number
  name: string
  icon: string
  unlocked: boolean
}

interface Props {
  exams?: any[]
}

const props = withDefaults(defineProps<Props>(), {
  exams: () => []
})

const emit = defineEmits(['select'])

const hoveredLevel = ref<number | null>(null)

// 根据真实考试数据生成关卡
const levels = computed<Level[]>(() => {
  if (props.exams && props.exams.length > 0) {
    // 图标池
    const icons: string[] = ['🏔️', '🌉', '🏕️', '🏛️', '🏯', '🎯', '🌟', '⛰️', '🗿', '🎪']
    
    return props.exams.slice(0, 5).map((exam, index) => {
      // 根据 hasJoined 和位置确定状态
      let status: 'completed' | 'current' | 'locked' = 'locked'
      let score = 0
      let stars = 0
      
      if (exam.hasJoined) {
        // 已参加过考试视为完成
        status = 'completed'
        score = exam.bestScore || 0 // 后端返回的最高分
        // 满分3星，80%以上2星，否则1星
        const totalScore = exam.totalScore || 100
        stars = score === totalScore ? 3 : score >= totalScore * 0.8 ? 2 : 1
      } else if (index === 0 || (props.exams[index - 1]?.hasJoined)) {
        // 第一个考试或前一个已完成，则当前可进行
        status = 'current'
      }
      
      return {
        id: exam.id,
        name: exam.name || exam.title || `关卡 ${index + 1}`,
        description: exam.description || '挑战这个考试',
        icon: icons[index % icons.length] || '🎯',
        status,
        stars: status === 'completed' ? stars : undefined,
        score: status === 'completed' ? score : undefined,
        totalScore: exam.totalScore || 100,
        questionCount: exam.questionCount || 10,
        timeLimit: exam.duration || 30,
        examId: exam.id
      }
    })
  }
  
  // 无数据时返回空数组
  return []
})

// 基于考试数据计算成就
const achievements = computed<Achievement[]>(() => {
  const completedLevels = levels.value.filter(l => l.status === 'completed')
  // 满分 = 得分等于试卷总分
  const perfectScores = completedLevels.filter(l => l.score !== undefined && l.totalScore !== undefined && l.score === l.totalScore)
  
  return [
    { 
      id: 1, 
      name: '初出茅庐', 
      icon: '🌱', 
      unlocked: completedLevels.length >= 1 // 完成1个考试
    },
    { 
      id: 2, 
      name: '学有所成', 
      icon: '📚', 
      unlocked: completedLevels.length >= 2 // 完成2个考试
    },
    { 
      id: 3, 
      name: '满分达人', 
      icon: '💯', 
      unlocked: perfectScores.length >= 1 // 有一次考试满分
    },
    { 
      id: 4, 
      name: '连胜五关', 
      icon: '🔥', 
      unlocked: completedLevels.length >= 5 // 完成5个考试
    },
    { 
      id: 5, 
      name: '学习大师', 
      icon: '🎓', 
      unlocked: levels.value.length > 0 && completedLevels.length === levels.value.length // 全部完成
    },
  ]
})

const completedCount = computed(() => 
  levels.value.filter(l => l.status === 'completed').length
)

const progressPercentage = computed(() => 
  (completedCount.value / levels.value.length) * 100
)

// SVG路径计算
const pathLength = 1200
const pathOffset = computed(() => 
  pathLength - (pathLength * progressPercentage.value / 100)
)

// 获取关卡位置
const getLevelPosition = (index: number) => {
  const totalLevels = levels.value.length
  const spacing = 100 / (totalLevels + 1)
  const left = spacing * (index + 1)
  
  // 波浪形Y位置
  const baseTop = 50
  const amplitude = 15
  const offset = Math.sin(index * Math.PI / 2) * amplitude
  
  return {
    left: `${left}%`,
    top: `${baseTop + offset}%`
  }
}

const handleLevelClick = (level: Level) => {
  if (level.status === 'locked') return
  
  emit('select', level.examId || level.id)
}
</script>

<style scoped lang="scss">
.exam-map {
  position: relative;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  border-radius: 24px;
  padding: 40px;
  overflow: hidden;
  min-height: 500px;
}

// 背景
.map-background {
  position: absolute;
  inset: 0;
  z-index: 0;
  
  .bg-pattern {
    width: 100%;
    height: 100%;
  }
}

// 头部
.map-header {
  position: relative;
  z-index: 1;
  text-align: center;
  margin-bottom: 30px;

  .header-badge {
    display: inline-block;
    background: rgba(252, 211, 77, 0.2);
    color: #fcd34d;
    padding: 8px 20px;
    border-radius: 50px;
    font-size: 14px;
    margin-bottom: 16px;
    border: 1px solid rgba(252, 211, 77, 0.3);
  }

  .header-title {
    font-size: 32px;
    font-weight: 800;
    color: #fff;
    margin: 0 0 12px 0;
    text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);
  }

  .header-desc {
    font-size: 16px;
    color: rgba(255, 255, 255, 0.7);
    margin: 0;
  }
}

// 进度条
.progress-bar {
  position: relative;
  z-index: 1;
  max-width: 400px;
  margin: 0 auto 40px;

  .progress-track {
    height: 8px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 10px;
    overflow: hidden;
  }

  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #fcd34d 0%, #e11d48 100%);
    border-radius: 10px;
    transition: width 0.5s ease;
  }

  .progress-text {
    display: block;
    text-align: center;
    margin-top: 8px;
    font-size: 13px;
    color: rgba(255, 255, 255, 0.6);
  }
}

// 地图路径
.map-path {
  position: relative;
  z-index: 1;
  height: 200px;
  margin-bottom: 40px;
}

.path-lines {
  position: absolute;
  width: 100%;
  height: 100%;
  
  .path-active {
    transition: stroke-dashoffset 0.8s ease;
  }
}

// 关卡容器
.levels-container {
  position: absolute;
  inset: 0;
}

// 关卡节点
.level-node {
  position: absolute;
  transform: translate(-50%, -50%);
  cursor: pointer;
  z-index: 5;

  &:hover {
    z-index: 20;
    
    .node-body {
      transform: scale(1.15);
    }
    
    .node-glow {
      opacity: 1;
      transform: scale(1.5);
    }
  }

  // 完成状态
  &.completed {
    .node-body {
      background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
      border-color: #4ade80;
    }
    
    .node-glow {
      background: radial-gradient(circle, rgba(34, 197, 94, 0.4) 0%, transparent 70%);
    }
  }

  // 当前状态
  &.current {
    .node-body {
      background: linear-gradient(135deg, #fcd34d 0%, #f59e0b 100%);
      border-color: #fbbf24;
      animation: pulse 2s ease-in-out infinite;
    }
    
    .node-glow {
      background: radial-gradient(circle, rgba(252, 211, 77, 0.5) 0%, transparent 70%);
      opacity: 0.8;
      animation: glow-pulse 2s ease-in-out infinite;
    }
  }

  // 锁定状态
  &.locked {
    cursor: not-allowed;
    
    .node-body {
      background: linear-gradient(135deg, #374151 0%, #1f2937 100%);
      border-color: #4b5563;
      opacity: 0.7;
    }
  }
}

.node-glow {
  position: absolute;
  inset: -20px;
  border-radius: 50%;
  opacity: 0;
  transition: all 0.3s ease;
}

.node-body {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  border: 3px solid;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.3);

  .node-icon {
    font-size: 28px;
  }

  .node-stars {
    position: absolute;
    bottom: -8px;
    display: flex;
    gap: 2px;
    
    .star {
      font-size: 12px;
    }
  }

  .lock-icon {
    font-size: 24px;
    opacity: 0.8;
  }
}

.node-label {
  position: absolute;
  top: calc(100% + 12px);
  left: 50%;
  transform: translateX(-50%);
  text-align: center;
  white-space: nowrap;

  .level-name {
    display: block;
    font-size: 14px;
    font-weight: 600;
    color: #fff;
    margin-bottom: 4px;
  }

  .level-info {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    
    &.locked {
      color: rgba(255, 255, 255, 0.4);
    }
  }
}

// 悬停提示
.level-tooltip {
  position: absolute;
  bottom: calc(100% + 20px);
  left: 50%;
  transform: translateX(-50%);
  width: 200px;
  background: rgba(15, 15, 25, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
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
    border-top-color: rgba(15, 15, 25, 0.95);
  }

  h4 {
    font-size: 15px;
    font-weight: 700;
    color: #fff;
    margin: 0 0 8px 0;
  }

  p {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.7);
    margin: 0 0 12px 0;
    line-height: 1.5;
  }

  .tooltip-stats {
    display: flex;
    gap: 16px;
    margin-bottom: 12px;
    
    span {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.6);
    }
  }

  .tooltip-action {
    font-size: 12px;
    color: #fcd34d;
    font-weight: 500;
  }
}

.tooltip-enter-active,
.tooltip-leave-active {
  transition: all 0.3s ease;
}

.tooltip-enter-from,
.tooltip-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(10px);
}

// 成就区域
.achievements-section {
  position: relative;
  z-index: 1;
  
  .section-title {
    font-size: 18px;
    font-weight: 700;
    color: #fff;
    margin: 0 0 20px 0;
    text-align: center;
  }
}

.achievements-grid {
  display: flex;
  justify-content: center;
  gap: 16px;
  flex-wrap: wrap;
}

.achievement-badge {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  transition: all 0.3s ease;
  
  &.unlocked {
    background: rgba(252, 211, 77, 0.1);
    border-color: rgba(252, 211, 77, 0.3);
    
    .badge-icon {
      filter: none;
    }
  }

  .badge-icon {
    font-size: 28px;
    filter: grayscale(100%);
    transition: filter 0.3s ease;
  }

  .badge-name {
    font-size: 12px;
    color: rgba(255, 255, 255, 0.7);
    font-weight: 500;
  }
}

// 动画
@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes glow-pulse {
  0%, 100% {
    opacity: 0.5;
    transform: scale(1);
  }
  50% {
    opacity: 0.8;
    transform: scale(1.3);
  }
}

// 响应式
@media (max-width: 768px) {
  .exam-map {
    padding: 24px 16px;
  }
  
  .map-header {
    .header-title {
      font-size: 24px;
    }
  }
  
  .map-path {
    height: 150px;
  }
  
  .node-body {
    width: 55px;
    height: 55px;
    
    .node-icon {
      font-size: 22px;
    }
  }
  
  .node-label {
    .level-name {
      font-size: 12px;
    }
    
    .level-info {
      font-size: 10px;
    }
  }
  
  .achievements-grid {
    gap: 10px;
  }
  
  .achievement-badge {
    padding: 12px 16px;
    
    .badge-icon {
      font-size: 24px;
    }
  }
}
</style>
