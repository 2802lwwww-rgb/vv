<template>
  <div class="book-reader" :class="{ 'focus-mode': isFocusMode }">
    <!-- 阅读模式切换 -->
    <div class="reader-controls">
      <div class="control-group">
        <button 
          class="mode-btn"
          :class="{ active: readingMode === 'book' }"
          @click="readingMode = 'book'"
          title="翻书模式"
        >
          📖
        </button>
        <button 
          class="mode-btn"
          :class="{ active: readingMode === 'scroll' }"
          @click="readingMode = 'scroll'"
          title="滚动模式"
        >
          📜
        </button>
        <button 
          class="mode-btn"
          :class="{ active: isFocusMode }"
          @click="isFocusMode = !isFocusMode"
          title="专注模式"
        >
          🎯
        </button>
      </div>
      <div class="page-info" v-if="readingMode === 'book'">
        第 {{ currentPage + 1 }} / {{ totalPages }} 页
      </div>
    </div>

    <!-- 翻书模式 -->
    <div v-if="readingMode === 'book'" class="book-container">
      <div class="book">
        <!-- 左侧翻页按钮 -->
        <button 
          class="nav-btn nav-prev" 
          @click="prevPage" 
          :disabled="currentPage === 0"
        >
          <span class="nav-icon">‹</span>
        </button>

        <!-- 书页 -->
        <div class="pages-wrapper">
          <div 
            class="page"
            :class="{ 'page-turning': isTurning }"
            :style="pageStyle"
          >
            <!-- 页面正面 -->
            <div class="page-front paper-texture">
              <div class="page-header">
                <span class="page-number">{{ currentPage + 1 }}</span>
              </div>
              <div class="page-content" v-html="currentPageContent"></div>
            </div>
            <!-- 页面背面 -->
            <div class="page-back paper-texture">
              <div class="page-content" v-html="nextPageContent"></div>
            </div>
          </div>
        </div>

        <!-- 右侧翻页按钮 -->
        <button 
          class="nav-btn nav-next" 
          @click="nextPage" 
          :disabled="currentPage >= totalPages - 1"
        >
          <span class="nav-icon">›</span>
        </button>
      </div>

      <!-- 页面指示器 -->
      <div class="page-indicators">
        <div 
          v-for="(_, index) in totalPages" 
          :key="index"
          class="indicator"
          :class="{ active: index === currentPage }"
          @click="goToPage(index)"
        ></div>
      </div>
    </div>

    <!-- 滚动模式 -->
    <div v-else class="scroll-container">
      <div class="scroll-content paper-texture">
        <div class="content-wrapper" v-html="fullContent"></div>
      </div>
    </div>

    <!-- 键盘提示 -->
    <div class="keyboard-hint" v-if="readingMode === 'book'">
      <span>← → 翻页</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'

interface Props {
  content: string
  title?: string
  charsPerPage?: number
}

const props = withDefaults(defineProps<Props>(), {
  title: '',
  charsPerPage: 800
})

const readingMode = ref<'book' | 'scroll'>('book')
const isFocusMode = ref(false)
const currentPage = ref(0)
const isTurning = ref(false)
const turnDirection = ref<'next' | 'prev'>('next')

// 分页内容
const pages = computed(() => {
  const content = props.content || ''
  const pageSize = props.charsPerPage
  const result: string[] = []
  
  // 简单按字符分页（实际应用中可以按段落分）
  for (let i = 0; i < content.length; i += pageSize) {
    result.push(content.slice(i, i + pageSize))
  }
  
  return result.length > 0 ? result : ['暂无内容']
})

const totalPages = computed(() => pages.value.length)

const currentPageContent = computed(() => {
  return formatContent(pages.value[currentPage.value] || '')
})

const nextPageContent = computed(() => {
  return formatContent(pages.value[currentPage.value + 1] || '')
})

const fullContent = computed(() => {
  return formatContent(props.content || '暂无内容')
})

// 格式化内容（保留换行）
const formatContent = (text: string) => {
  return text.replace(/\n/g, '<br>')
}

// 页面样式
const pageStyle = computed(() => {
  if (!isTurning.value) return {}
  
  const rotation = turnDirection.value === 'next' ? -180 : 0
  return {
    transform: `rotateY(${rotation}deg)`
  }
})

// 翻页功能
const nextPage = () => {
  if (currentPage.value >= totalPages.value - 1 || isTurning.value) return
  
  turnDirection.value = 'next'
  isTurning.value = true
  
  setTimeout(() => {
    currentPage.value++
    isTurning.value = false
  }, 600)
}

const prevPage = () => {
  if (currentPage.value <= 0 || isTurning.value) return
  
  turnDirection.value = 'prev'
  isTurning.value = true
  
  setTimeout(() => {
    currentPage.value--
    isTurning.value = false
  }, 600)
}

const goToPage = (page: number) => {
  if (page < 0 || page >= totalPages.value || isTurning.value) return
  currentPage.value = page
}

// 键盘导航
const handleKeydown = (e: KeyboardEvent) => {
  if (readingMode.value !== 'book') return
  
  if (e.key === 'ArrowRight' || e.key === ' ') {
    nextPage()
  } else if (e.key === 'ArrowLeft') {
    prevPage()
  }
}

onMounted(() => {
  window.addEventListener('keydown', handleKeydown)
})

onUnmounted(() => {
  window.removeEventListener('keydown', handleKeydown)
})
</script>

<style scoped lang="scss">
.book-reader {
  position: relative;
  width: 100%;
  min-height: 600px;
  background: linear-gradient(135deg, #f5f5dc 0%, #e8dcc8 100%);
  border-radius: 16px;
  padding: 24px;
  transition: all 0.5s ease;

  &.focus-mode {
    background: linear-gradient(135deg, #2a2520 0%, #1a1512 100%);
    
    .page-front, .page-back, .scroll-content {
      background: linear-gradient(135deg, #d4c4a8 0%, #c4b498 100%);
      color: #2a2520;
    }
    
    .reader-controls {
      background: rgba(0, 0, 0, 0.3);
      border-color: rgba(255, 255, 255, 0.1);
      
      .mode-btn {
        color: rgba(255, 255, 255, 0.8);
        
        &:hover, &.active {
          background: rgba(255, 255, 255, 0.15);
        }
      }
      
      .page-info {
        color: rgba(255, 255, 255, 0.7);
      }
    }
    
    .nav-btn {
      background: rgba(0, 0, 0, 0.4);
      color: #d4c4a8;
      
      &:hover:not(:disabled) {
        background: rgba(0, 0, 0, 0.6);
      }
    }
    
    .keyboard-hint {
      color: rgba(255, 255, 255, 0.4);
    }
  }
}

// 阅读控制
.reader-controls {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.1);

  .control-group {
    display: flex;
    gap: 8px;
  }

  .mode-btn {
    width: 40px;
    height: 40px;
    border: none;
    background: transparent;
    border-radius: 10px;
    font-size: 20px;
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover, &.active {
      background: rgba(139, 69, 19, 0.15);
    }

    &.active {
      box-shadow: 0 2px 8px rgba(139, 69, 19, 0.2);
    }
  }

  .page-info {
    font-size: 14px;
    color: #666;
    font-weight: 500;
  }
}

// 翻书容器
.book-container {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.book {
  display: flex;
  align-items: center;
  gap: 20px;
  perspective: 2000px;
}

.pages-wrapper {
  position: relative;
  width: 500px;
  height: 650px;
  
  @media (max-width: 768px) {
    width: 100%;
    max-width: 400px;
    height: 520px;
  }
}

// 书页
.page {
  position: absolute;
  width: 100%;
  height: 100%;
  transform-style: preserve-3d;
  transform-origin: left center;
  transition: transform 0.6s cubic-bezier(0.645, 0.045, 0.355, 1);

  &.page-turning {
    z-index: 10;
  }
}

.page-front, .page-back {
  position: absolute;
  width: 100%;
  height: 100%;
  backface-visibility: hidden;
  border-radius: 4px 16px 16px 4px;
  padding: 40px 36px;
  box-sizing: border-box;
  overflow: auto;
  box-shadow: 
    2px 0 10px rgba(0, 0, 0, 0.1),
    inset -2px 0 30px rgba(0, 0, 0, 0.05);
}

.page-front {
  background: linear-gradient(135deg, #faf8f5 0%, #f5f0e8 100%);
}

.page-back {
  background: linear-gradient(135deg, #f5f0e8 0%, #ebe5d8 100%);
  transform: rotateY(180deg);
}

// 纸张纹理
.paper-texture {
  position: relative;
  
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 200 200' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%' height='100%' filter='url(%23noise)' opacity='0.04'/%3E%3C/svg%3E");
    pointer-events: none;
    border-radius: inherit;
  }
}

.page-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(139, 90, 43, 0.2);

  .page-number {
    font-size: 12px;
    color: #8b5a2b;
    font-style: italic;
  }
}

.page-content {
  font-size: 16px;
  line-height: 2;
  color: #333;
  text-align: justify;
  font-family: 'Noto Serif SC', 'Source Han Serif SC', serif;
  
  &::first-letter {
    font-size: 1.5em;
    color: #8b5a2b;
    float: left;
    line-height: 1.2;
    margin-right: 8px;
  }
}

// 导航按钮
.nav-btn {
  width: 50px;
  height: 50px;
  border: none;
  background: rgba(139, 90, 43, 0.1);
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;

  .nav-icon {
    font-size: 28px;
    color: #8b5a2b;
    line-height: 1;
  }

  &:hover:not(:disabled) {
    background: rgba(139, 90, 43, 0.2);
    transform: scale(1.1);
  }

  &:disabled {
    opacity: 0.3;
    cursor: not-allowed;
  }
}

// 页面指示器
.page-indicators {
  display: flex;
  gap: 8px;
  margin-top: 24px;

  .indicator {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    background: rgba(139, 90, 43, 0.3);
    cursor: pointer;
    transition: all 0.3s ease;

    &:hover {
      background: rgba(139, 90, 43, 0.5);
    }

    &.active {
      background: #8b5a2b;
      transform: scale(1.3);
    }
  }
}

// 滚动模式
.scroll-container {
  max-height: 600px;
  overflow-y: auto;
  border-radius: 12px;
  
  &::-webkit-scrollbar {
    width: 8px;
  }
  
  &::-webkit-scrollbar-track {
    background: rgba(139, 90, 43, 0.1);
    border-radius: 4px;
  }
  
  &::-webkit-scrollbar-thumb {
    background: rgba(139, 90, 43, 0.3);
    border-radius: 4px;
    
    &:hover {
      background: rgba(139, 90, 43, 0.5);
    }
  }
}

.scroll-content {
  padding: 40px;
  background: linear-gradient(135deg, #faf8f5 0%, #f5f0e8 100%);
  border-radius: 12px;
  min-height: 500px;

  .content-wrapper {
    font-size: 16px;
    line-height: 2;
    color: #333;
    text-align: justify;
    font-family: 'Noto Serif SC', 'Source Han Serif SC', serif;
  }
}

// 键盘提示
.keyboard-hint {
  text-align: center;
  margin-top: 16px;
  font-size: 12px;
  color: rgba(139, 90, 43, 0.5);
  
  span {
    background: rgba(139, 90, 43, 0.1);
    padding: 4px 12px;
    border-radius: 4px;
  }
}

// 响应式
@media (max-width: 768px) {
  .book-reader {
    padding: 16px;
  }
  
  .book {
    flex-direction: column;
    gap: 16px;
  }
  
  .nav-btn {
    width: 44px;
    height: 44px;
    
    .nav-icon {
      font-size: 24px;
    }
  }
  
  .page-front, .page-back {
    padding: 24px 20px;
  }
  
  .page-content {
    font-size: 14px;
    line-height: 1.8;
  }
}
</style>
