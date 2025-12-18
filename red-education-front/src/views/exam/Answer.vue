<template>
  <div class="answer-page" v-loading="loading">
    <!-- 游戏化HUD顶部栏 -->
    <div class="game-hud" v-if="paper">
      <div class="hud-left">
        <div class="level-badge">
          <span class="level-icon">📝</span>
          <span class="level-text">{{ paper.examTitle }}</span>
        </div>
      </div>
      
      <!-- HP风格进度条 -->
      <div class="hp-bar-container">
        <div class="hp-bar">
          <div class="hp-fill" :style="{ width: progressPercent + '%' }"></div>
          <div class="hp-text">{{ answeredCount }} / {{ paper.questions.length }}</div>
        </div>
        <div class="hp-label">答题进度</div>
      </div>
      
      <!-- 连击计数器 -->
      <div class="combo-counter" :class="{ active: combo > 0 }">
        <div class="combo-flames" v-if="combo >= 3">🔥</div>
        <div class="combo-number">{{ combo }}</div>
        <div class="combo-label">连击</div>
      </div>
      
      <!-- 计时器 -->
      <div class="game-timer" :class="{ warning: timeLeft < 300, danger: timeLeft < 60 }">
        <div class="timer-icon">⏱️</div>
        <div class="timer-display">
          <div class="timer-value">{{ formatTime(timeLeft) }}</div>
          <div class="timer-ring" :style="{ '--progress': timerPercent }"></div>
        </div>
      </div>
    </div>

    <div class="answer-container" v-if="paper">
      <!-- 题目区域 -->
      <div class="question-area">
        <div class="question-card" :class="feedbackClass">
          <div class="question-header">
            <div class="question-num">
              <span class="num-badge">Q{{ currentIndex + 1 }}</span>
              <el-tag :type="getQuestionTagType(currentQuestion.type)" effect="dark" size="small">
                {{ getQuestionType(currentQuestion.type) }}
              </el-tag>
            </div>
            <div class="score-badge">
              <span class="score-icon">⭐</span>
              <span class="score-value">{{ currentQuestion.score || 10 }}分</span>
            </div>
          </div>
          
          <div class="question-content">
            <p class="question-title">{{ currentQuestion.content }}</p>
            
            <!-- 单选题 -->
            <div class="options-list" v-if="currentQuestion.type === 'SINGLE_CHOICE'">
              <div 
                v-for="(option, idx) in currentQuestion.options" 
                :key="option.key"
                class="option-item"
                :class="{ 
                  selected: answers[currentIndex] === option.key,
                  'animate-in': true
                }"
                :style="{ animationDelay: (idx * 0.1) + 's' }"
                @click="selectAnswer(option.key)"
              >
                <span class="option-key">{{ option.key }}</span>
                <span class="option-value">{{ option.value }}</span>
                <span class="option-check" v-if="answers[currentIndex] === option.key">✓</span>
              </div>
            </div>

            <!-- 多选题 -->
            <div class="options-list" v-else-if="currentQuestion.type === 'MULTIPLE'">
              <div 
                v-for="(option, idx) in currentQuestion.options" 
                :key="option.key"
                class="option-item multiple"
                :class="{ 
                  selected: (answers[currentIndex] || []).includes(option.key),
                  'animate-in': true  
                }"
                :style="{ animationDelay: (idx * 0.1) + 's' }"
                @click="toggleMultiple(option.key)"
              >
                <span class="option-key">{{ option.key }}</span>
                <span class="option-value">{{ option.value }}</span>
                <span class="option-check" v-if="(answers[currentIndex] || []).includes(option.key)">✓</span>
              </div>
            </div>

            <!-- 判断题 -->
            <div class="options-list judge" v-else-if="currentQuestion.type === 'JUDGE'">
              <div 
                class="option-item judge-btn correct-btn animate-in"
                :class="{ selected: answers[currentIndex] === 'A' }"
                @click="selectAnswer('A')"
              >
                <span class="judge-icon">✓</span>
                <span>正确</span>
              </div>
              <div 
                class="option-item judge-btn wrong-btn animate-in"
                :class="{ selected: answers[currentIndex] === 'B' }"
                style="animation-delay: 0.1s"
                @click="selectAnswer('B')"
              >
                <span class="judge-icon">✗</span>
                <span>错误</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 底部导航 -->
      <div class="answer-footer">
        <button 
          class="nav-btn prev-btn"
          :disabled="currentIndex === 0"
          @click="prevQuestion"
        >
          <span class="btn-icon">◀</span>
          <span class="btn-text">上一题</span>
        </button>
        
        <button class="nav-btn card-btn" @click="cardVisible = true">
          <span class="btn-icon">📋</span>
          <span class="btn-text">答题卡</span>
        </button>
        
        <button 
          class="nav-btn next-btn"
          :disabled="currentIndex === paper.questions.length - 1"
          @click="nextQuestion"
        >
          <span class="btn-text">下一题</span>
          <span class="btn-icon">▶</span>
        </button>
        
        <button class="nav-btn submit-btn" @click="handleSubmit">
          <span class="btn-icon">🏁</span>
          <span class="btn-text">交卷</span>
        </button>
      </div>

      <!-- 答题卡抽屉 -->
      <el-drawer v-model="cardVisible" title="答题卡" size="400px">
        <div class="answer-card-drawer">
          <div class="card-stats">
            <div class="stat-item answered">
              <span class="stat-icon">✅</span>
              <span class="num">{{ answeredCount }}</span>
              <span class="label">已答</span>
            </div>
            <div class="stat-item unanswered">
              <span class="stat-icon">⏳</span>
              <span class="num">{{ paper.questions.length - answeredCount }}</span>
              <span class="label">未答</span>
            </div>
            <div class="stat-item combo-stat">
              <span class="stat-icon">🔥</span>
              <span class="num">{{ maxCombo }}</span>
              <span class="label">最高连击</span>
            </div>
          </div>
          <div class="card-grid">
            <div
              v-for="(_, idx) in paper.questions"
              :key="idx"
              class="card-item"
              :class="{ 
                active: idx === currentIndex,
                answered: answers[idx] !== undefined && answers[idx] !== null && answers[idx] !== '' && (Array.isArray(answers[idx]) ? answers[idx].length > 0 : true)
              }"
              @click="jumpToQuestion(idx)"
            >
              {{ idx + 1 }}
            </div>
          </div>
        </div>
      </el-drawer>
    </div>
    
    <!-- 庆祝效果 -->
    <div class="celebration" v-if="showCelebration">
      <div class="confetti" v-for="i in 50" :key="i" :style="getConfettiStyle(i)"></div>
      <div class="celebration-text">🎉 答题完成！</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getExamDetail, submitExam } from '@/api/exam'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const paper = ref<any>(null)
const answers = ref<any[]>([])
const currentIndex = ref(0)
const timeLeft = ref(0)
const timer = ref<any>(null)
const cardVisible = ref(false)
const combo = ref(0)
const maxCombo = ref(0)
const feedbackClass = ref('')
const showCelebration = ref(false)

const examId = computed(() => Number(route.params.id))
const currentQuestion = computed(() => paper.value?.questions[currentIndex.value])
const answeredCount = computed(() => {
  return answers.value.filter(a => {
    if (Array.isArray(a)) return a.length > 0
    return a !== undefined && a !== null && a !== ''
  }).length
})

const progressPercent = computed(() => {
  if (!paper.value) return 0
  return (answeredCount.value / paper.value.questions.length) * 100
})

const timerPercent = computed(() => {
  if (!paper.value) return 100
  const totalTime = paper.value.duration * 60
  return (timeLeft.value / totalTime) * 100
})

// 彩纸样式
const getConfettiStyle = (index: number) => {
  const colors = ['#fbbf24', '#f472b6', '#60a5fa', '#34d399', '#a78bfa', '#f87171', '#22d3ee']
  return {
    left: `${Math.random() * 100}%`,
    top: `${-10 - Math.random() * 20}%`,
    background: colors[index % colors.length],
    transform: `rotate(${Math.random() * 360}deg)`,
    animationDelay: `${Math.random() * 2}s`,
    animationDuration: `${2 + Math.random() * 2}s`
  }
}

const getStorageKey = (key: string) => `exam_${examId.value}_${key}`

const saveStateToStorage = () => {
  try {
    localStorage.setItem(getStorageKey('answers'), JSON.stringify(answers.value))
    localStorage.setItem(getStorageKey('timeLeft'), String(timeLeft.value))
    localStorage.setItem(getStorageKey('currentIndex'), String(currentIndex.value))
    localStorage.setItem(getStorageKey('combo'), String(combo.value))
    localStorage.setItem(getStorageKey('maxCombo'), String(maxCombo.value))
  } catch (error) {
    console.error('保存状态失败:', error)
  }
}

const restoreStateFromStorage = () => {
  try {
    const savedAnswers = localStorage.getItem(getStorageKey('answers'))
    const savedTimeLeft = localStorage.getItem(getStorageKey('timeLeft'))
    const savedIndex = localStorage.getItem(getStorageKey('currentIndex'))
    const savedCombo = localStorage.getItem(getStorageKey('combo'))
    const savedMaxCombo = localStorage.getItem(getStorageKey('maxCombo'))
    if (savedAnswers) answers.value = JSON.parse(savedAnswers)
    if (savedTimeLeft) timeLeft.value = Number(savedTimeLeft)
    if (savedIndex) currentIndex.value = Number(savedIndex)
    if (savedCombo) combo.value = Number(savedCombo)
    if (savedMaxCombo) maxCombo.value = Number(savedMaxCombo)
    return !!(savedAnswers || savedTimeLeft || savedIndex)
  } catch (error) {
    return false
  }
}

const clearStateFromStorage = () => {
  localStorage.removeItem(getStorageKey('answers'))
  localStorage.removeItem(getStorageKey('timeLeft'))
  localStorage.removeItem(getStorageKey('currentIndex'))
  localStorage.removeItem(getStorageKey('combo'))
  localStorage.removeItem(getStorageKey('maxCombo'))
}

const fetchPaper = async () => {
  loading.value = true
  try {
    const res = await getExamDetail(examId.value)
    if (res.data) {
      const questions = (res.data.questions || []).map((q: any) => {
        const options = []
        const optA = q.option_a || q.optionA
        const optB = q.option_b || q.optionB
        const optC = q.option_c || q.optionC
        const optD = q.option_d || q.optionD
        if (optA) options.push({ key: 'A', value: optA })
        if (optB) options.push({ key: 'B', value: optB })
        if (optC) options.push({ key: 'C', value: optC })
        if (optD) options.push({ key: 'D', value: optD })
        return { ...q, type: q.type || 'SINGLE_CHOICE', options }
      })
      paper.value = { ...res.data, examTitle: res.data.name, questions }
      const restored = restoreStateFromStorage()
      if (!restored) {
        answers.value = questions.map((q: any) => q.type === 'MULTIPLE' ? [] : null)
        timeLeft.value = res.data.duration * 60
        currentIndex.value = 0
        combo.value = 0
        maxCombo.value = 0
      } else {
        if (answers.value.length !== questions.length) {
          answers.value = questions.map((q: any) => q.type === 'MULTIPLE' ? [] : null)
        }
        ElMessage.success('已恢复上次答题进度')
      }
      startTimer()
    }
  } catch (error) {
    ElMessage.error('获取试卷失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const startTimer = () => {
  timer.value = setInterval(() => {
    timeLeft.value--
    saveStateToStorage()
    if (timeLeft.value <= 0) { clearInterval(timer.value); autoSubmit() }
  }, 1000)
}

const incrementCombo = () => {
  combo.value++
  if (combo.value > maxCombo.value) {
    maxCombo.value = combo.value
  }
}

const selectAnswer = (key: string) => {
  const prevAnswer = answers.value[currentIndex.value]
  answers.value[currentIndex.value] = key
  
  // 连击逻辑
  if (prevAnswer === null || prevAnswer === undefined || prevAnswer === '') {
    incrementCombo()
    feedbackClass.value = 'feedback-success'
    setTimeout(() => feedbackClass.value = '', 500)
  }
  
  saveStateToStorage()
  
  // 自动跳下一题
  setTimeout(() => {
    if (currentIndex.value < paper.value.questions.length - 1) {
      nextQuestion()
    }
  }, 300)
}

const toggleMultiple = (key: string) => {
  const current = answers.value[currentIndex.value] || []
  const idx = current.indexOf(key)
  if (idx > -1) current.splice(idx, 1)
  else current.push(key)
  answers.value[currentIndex.value] = [...current]
  
  if (current.length === 1 && idx === -1) {
    incrementCombo()
  }
  
  saveStateToStorage()
}

const prevQuestion = () => { 
  if (currentIndex.value > 0) {
    currentIndex.value--
  }
}

const nextQuestion = () => { 
  if (currentIndex.value < paper.value.questions.length - 1) {
    currentIndex.value++
  }
}

const jumpToQuestion = (index: number) => { 
  currentIndex.value = index
  cardVisible.value = false 
}

const handleSubmit = () => {
  const unanswered = paper.value.questions.length - answeredCount.value
  const msg = unanswered > 0 ? `还有${unanswered}题未作答，确认交卷吗？` : '确认交卷吗？'
  ElMessageBox.confirm(msg, '提示', { confirmButtonText: '确认交卷', cancelButtonText: '取消', type: 'warning' }).then(() => doSubmit())
}

const autoSubmit = () => { ElMessage.warning('考试时间已到，系统自动交卷'); doSubmit() }

const doSubmit = async () => {
  try {
    const answerMap: any = {}
    paper.value.questions.forEach((q: any, idx: number) => {
      const ans = answers.value[idx]
      if (ans && (Array.isArray(ans) ? ans.length > 0 : true)) {
        answerMap[q.id] = Array.isArray(ans) ? ans.join(',') : ans
      }
    })
    
    // 显示庆祝效果
    showCelebration.value = true
    
    const res = await submitExam({
      examId: examId.value,
      answers: answerMap,
      duration: paper.value.duration * 60 - timeLeft.value
    })
    clearStateFromStorage()
    
    setTimeout(() => {
      ElMessage.success(`🎉 交卷成功！得分：${res.data?.score || 0}分`)
      router.push(res.data?.id || res.data?.recordId ? `/exam/result/${res.data.id || res.data.recordId}` : '/exam')
    }, 1500)
  } catch (error: any) {
    showCelebration.value = false
    ElMessage.error(error.message || '交卷失败')
  }
}

const formatTime = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const getQuestionType = (type: string) => ({ 'SINGLE_CHOICE': '单选', 'MULTIPLE': '多选', 'JUDGE': '判断' }[type] || '未知')
const getQuestionTagType = (type: string) => ({ 'SINGLE_CHOICE': 'primary', 'MULTIPLE': 'warning', 'JUDGE': 'success' }[type] || 'info')

onMounted(() => fetchPaper())
onUnmounted(() => { if (timer.value) clearInterval(timer.value); saveStateToStorage() })
</script>

<style scoped lang="scss">
.answer-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1a0a0a 0%, #2d1515 50%, #1a0a0a 100%);
  color: white;
  position: relative;
  overflow: hidden;
}

// ============================================
// 游戏化HUD顶部栏 - 红色主题
// ============================================
.game-hud {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: linear-gradient(180deg, rgba(0, 0, 0, 0.8) 0%, rgba(0, 0, 0, 0.4) 100%);
  border-bottom: 2px solid rgba(220, 38, 38, 0.3);
  position: sticky;
  top: 0;
  z-index: 100;
  
  .hud-left {
    .level-badge {
      display: flex;
      align-items: center;
      gap: 10px;
      background: rgba(220, 38, 38, 0.2);
      padding: 10px 16px;
      border-radius: 12px;
      border: 1px solid rgba(220, 38, 38, 0.4);
      
      .level-icon {
        font-size: 24px;
      }
      
      .level-text {
        font-size: 16px;
        font-weight: 600;
        max-width: 200px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}

// HP风格进度条
.hp-bar-container {
  flex: 1;
  max-width: 300px;
  margin: 0 24px;
  
  .hp-bar {
    height: 24px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 12px;
    position: relative;
    overflow: hidden;
    border: 2px solid rgba(34, 197, 94, 0.5);
    
    .hp-fill {
      height: 100%;
      background: linear-gradient(90deg, #22c55e 0%, #4ade80 50%, #86efac 100%);
      border-radius: 10px;
      transition: width 0.5s ease;
      box-shadow: 0 0 20px rgba(34, 197, 94, 0.6);
    }
    
    .hp-text {
      position: absolute;
      inset: 0;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 12px;
      font-weight: 700;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.8);
    }
  }
  
  .hp-label {
    text-align: center;
    font-size: 11px;
    color: rgba(255, 255, 255, 0.6);
    margin-top: 4px;
  }
}

// 连击计数器
.combo-counter {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 12px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  position: relative;
  
  &.active {
    background: rgba(251, 191, 36, 0.2);
    border-color: rgba(251, 191, 36, 0.5);
    animation: combo-pulse 0.5s ease;
  }
  
  .combo-flames {
    position: absolute;
    top: -12px;
    font-size: 20px;
    animation: flame-dance 0.5s ease-in-out infinite;
  }
  
  .combo-number {
    font-size: 28px;
    font-weight: 800;
    background: linear-gradient(180deg, #fbbf24 0%, #f59e0b 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    line-height: 1;
  }
  
  .combo-label {
    font-size: 10px;
    color: rgba(255, 255, 255, 0.6);
    text-transform: uppercase;
    letter-spacing: 1px;
  }
}

// 计时器
.game-timer {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  background: rgba(139, 92, 246, 0.2);
  border-radius: 12px;
  border: 2px solid rgba(139, 92, 246, 0.4);
  
  .timer-icon {
    font-size: 24px;
  }
  
  .timer-display {
    position: relative;
    
    .timer-value {
      font-size: 24px;
      font-weight: 700;
      font-family: 'Courier New', monospace;
    }
  }
  
  &.warning {
    background: rgba(251, 191, 36, 0.2);
    border-color: rgba(251, 191, 36, 0.5);
    
    .timer-value {
      color: #fbbf24;
    }
  }
  
  &.danger {
    background: rgba(239, 68, 68, 0.2);
    border-color: rgba(239, 68, 68, 0.5);
    animation: danger-pulse 0.5s ease-in-out infinite;
    
    .timer-value {
      color: #ef4444;
    }
  }
}

// ============================================
// 题目区域
// ============================================
.answer-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 24px;
}

.question-area {
  margin-bottom: 24px;
}

.question-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.05) 0%, rgba(255, 255, 255, 0.02) 100%);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 32px;
  border: 2px solid rgba(139, 92, 246, 0.2);
  min-height: 450px;
  transition: all 0.3s ease;
  
  &.feedback-success {
    animation: success-flash 0.5s ease;
    border-color: rgba(34, 197, 94, 0.6);
  }

  .question-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 28px;
    padding-bottom: 20px;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .question-num {
      display: flex;
      align-items: center;
      gap: 12px;
      
      .num-badge {
        font-size: 28px;
        font-weight: 800;
        background: linear-gradient(135deg, #8b5cf6 0%, #a78bfa 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }
    
    .score-badge {
      display: flex;
      align-items: center;
      gap: 6px;
      background: rgba(251, 191, 36, 0.2);
      padding: 8px 14px;
      border-radius: 20px;
      border: 1px solid rgba(251, 191, 36, 0.3);
      
      .score-icon {
        font-size: 16px;
      }
      
      .score-value {
        font-size: 14px;
        font-weight: 600;
        color: #fbbf24;
      }
    }
  }

  .question-content {
    .question-title {
      font-size: 20px;
      line-height: 1.8;
      margin-bottom: 32px;
      color: rgba(255, 255, 255, 0.95);
    }

    .options-list {
      display: flex;
      flex-direction: column;
      gap: 14px;

      &.judge {
        flex-direction: row;
        gap: 20px;
      }

      .option-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 18px 24px;
        background: rgba(255, 255, 255, 0.03);
        border: 2px solid rgba(255, 255, 255, 0.1);
        border-radius: 14px;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;

        &.animate-in {
          animation: option-slide-in 0.4s ease backwards;
        }

        &:hover {
          border-color: rgba(139, 92, 246, 0.6);
          background: rgba(139, 92, 246, 0.1);
          transform: translateX(8px);
        }
        
        &.selected {
          border-color: #8b5cf6;
          background: linear-gradient(135deg, rgba(139, 92, 246, 0.3) 0%, rgba(139, 92, 246, 0.1) 100%);
          transform: translateX(8px);
          
          .option-key {
            background: #8b5cf6;
            color: white;
          }
        }

        .option-key {
          width: 36px;
          height: 36px;
          background: rgba(255, 255, 255, 0.1);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-weight: 700;
          font-size: 16px;
          flex-shrink: 0;
          transition: all 0.3s ease;
        }

        .option-value {
          flex: 1;
          font-size: 16px;
        }
        
        .option-check {
          font-size: 20px;
          color: #8b5cf6;
          font-weight: 700;
          animation: check-pop 0.3s ease;
        }
        
        &.judge-btn {
          flex: 1;
          justify-content: center;
          padding: 24px;
          
          .judge-icon {
            font-size: 28px;
            font-weight: 700;
          }
          
          &.correct-btn:hover, &.correct-btn.selected {
            border-color: #22c55e;
            background: rgba(34, 197, 94, 0.2);
            
            .judge-icon { color: #22c55e; }
          }
          
          &.wrong-btn:hover, &.wrong-btn.selected {
            border-color: #ef4444;
            background: rgba(239, 68, 68, 0.2);
            
            .judge-icon { color: #ef4444; }
          }
        }
      }
    }
  }
}

// ============================================
// 底部导航
// ============================================
.answer-footer {
  display: flex;
  justify-content: center;
  gap: 16px;
  padding: 20px;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.1);

  .nav-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 14px 24px;
    border: 2px solid rgba(255, 255, 255, 0.2);
    border-radius: 12px;
    background: rgba(255, 255, 255, 0.05);
    color: white;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    
    .btn-icon {
      font-size: 16px;
    }
    
    &:hover:not(:disabled) {
      background: rgba(139, 92, 246, 0.2);
      border-color: rgba(139, 92, 246, 0.5);
      transform: translateY(-2px);
    }
    
    &:disabled {
      opacity: 0.4;
      cursor: not-allowed;
    }
    
    &.submit-btn {
      background: linear-gradient(135deg, #ef4444 0%, #dc2626 100%);
      border-color: transparent;
      
      &:hover {
        background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(239, 68, 68, 0.4);
      }
    }
    
    &.next-btn {
      background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%);
      border-color: transparent;
      
      &:hover:not(:disabled) {
        background: linear-gradient(135deg, #a78bfa 0%, #8b5cf6 100%);
        box-shadow: 0 8px 20px rgba(139, 92, 246, 0.4);
      }
    }
  }
}

// ============================================
// 答题卡抽屉
// ============================================
.answer-card-drawer {
  .card-stats {
    display: flex;
    gap: 16px;
    margin-bottom: 24px;

    .stat-item {
      flex: 1;
      text-align: center;
      padding: 16px;
      border-radius: 12px;
      background: var(--el-fill-color-lighter);
      
      &.answered {
        background: rgba(34, 197, 94, 0.1);
        .num { color: #22c55e; }
      }
      
      &.combo-stat {
        background: rgba(251, 191, 36, 0.1);
        .num { color: #fbbf24; }
      }
      
      .stat-icon {
        font-size: 20px;
        display: block;
        margin-bottom: 4px;
      }

      .num {
        display: block;
        font-size: 24px;
        font-weight: 700;
        margin-bottom: 2px;
      }
      
      .label {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }
  }

  .card-grid {
    display: grid;
    grid-template-columns: repeat(5, 1fr);
    gap: 10px;

    .card-item {
      aspect-ratio: 1;
      display: flex;
      align-items: center;
      justify-content: center;
      border: 2px solid var(--el-border-color);
      border-radius: 10px;
      cursor: pointer;
      font-weight: 600;
      font-size: 15px;
      transition: all 0.2s ease;

      &:hover {
        border-color: #8b5cf6;
        background: rgba(139, 92, 246, 0.1);
        transform: scale(1.05);
      }
      
      &.active {
        background: #8b5cf6;
        color: white;
        border-color: #8b5cf6;
      }
      
      &.answered {
        background: #22c55e;
        color: white;
        border-color: #22c55e;
      }
    }
  }
}

// ============================================
// 庆祝效果
// ============================================
.celebration {
  position: fixed;
  inset: 0;
  z-index: 1000;
  pointer-events: none;
  
  .confetti {
    position: absolute;
    width: 10px;
    height: 10px;
    animation: confetti-fall 3s linear forwards;
  }
  
  .celebration-text {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    font-size: 48px;
    font-weight: 800;
    color: white;
    text-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
    animation: celebration-pop 0.5s ease backwards;
  }
}

// ============================================
// 动画
// ============================================
@keyframes combo-pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

@keyframes flame-dance {
  0%, 100% { transform: translateY(0) rotate(-5deg); }
  50% { transform: translateY(-5px) rotate(5deg); }
}

@keyframes danger-pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.7; }
}

@keyframes success-flash {
  0% { box-shadow: 0 0 0 rgba(34, 197, 94, 0); }
  50% { box-shadow: 0 0 30px rgba(34, 197, 94, 0.5); }
  100% { box-shadow: 0 0 0 rgba(34, 197, 94, 0); }
}

@keyframes option-slide-in {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes check-pop {
  0% { transform: scale(0); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

@keyframes confetti-fall {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
  }
  100% {
    transform: translateY(100vh) rotate(720deg);
    opacity: 0;
  }
}

@keyframes celebration-pop {
  0% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(0.5);
  }
  50% {
    transform: translate(-50%, -50%) scale(1.1);
  }
  100% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1);
  }
}

// 响应式
@media (max-width: 768px) {
  .game-hud {
    flex-wrap: wrap;
    gap: 12px;
    padding: 12px 16px;
    
    .hud-left {
      width: 100%;
      
      .level-badge {
        width: 100%;
        justify-content: center;
      }
    }
    
    .hp-bar-container {
      order: 4;
      width: 100%;
      max-width: none;
      margin: 0;
    }
  }
  
  .answer-footer {
    flex-wrap: wrap;
    
    .nav-btn {
      padding: 12px 16px;
      font-size: 14px;
    }
  }
}
</style>
