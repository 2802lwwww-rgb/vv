<template>
  <div class="bookshelf-container">
    <!-- 书架层 -->
    <div 
      v-for="(shelf, shelfIndex) in shelves" 
      :key="shelfIndex" 
      class="bookshelf-row"
    >
      <div class="shelf-wood">
        <div class="wood-surface"></div>
        <div class="wood-front"></div>
        <div class="wood-shadow"></div>
      </div>
      
      <div class="books-row">
        <div 
          v-for="(book, bookIndex) in shelf" 
          :key="book.id"
          class="book-wrapper"
          :style="{ animationDelay: `${(shelfIndex * booksPerRow + bookIndex) * 0.1}s` }"
          @click="$emit('select', book)"
          @mouseenter="hoveredBook = book.id"
          @mouseleave="hoveredBook = null"
        >
          <div 
            class="book"
            :class="{ 
              'book-hovered': hoveredBook === book.id,
              [`book-color-${bookIndex % 6}`]: true
            }"
            :style="{ height: getBookHeight(book) + 'px' }"
          >
            <!-- 书脊 -->
            <div class="book-spine">
              <div class="spine-title">{{ truncateTitle(book.title) }}</div>
              <div class="spine-decoration"></div>
            </div>
            <!-- 书顶 -->
            <div class="book-top"></div>
            <!-- 书面（正面） -->
            <div class="book-front">
              <img 
                v-if="book.coverImage" 
                :src="getImageUrl(book.coverImage)" 
                :alt="book.title"
                class="book-cover"
              />
              <div class="book-cover-overlay"></div>
            </div>
          </div>
          
          <!-- 悬浮信息卡片 -->
          <Transition name="tooltip">
            <div v-if="hoveredBook === book.id" class="book-tooltip">
              <h4>{{ book.title }}</h4>
              <p class="tooltip-desc">{{ truncateDesc(book.description) }}</p>
              <div class="tooltip-stats">
                <span>👁 {{ book.viewCount || 0 }}</span>
                <span>⬇️ {{ book.downloadCount || 0 }}</span>
              </div>
              <div class="tooltip-hint">点击查看详情</div>
            </div>
          </Transition>
        </div>
        
        <!-- 装饰物品 -->
        <div class="shelf-decorations">
          <div class="decoration plant">🪴</div>
          <div class="decoration globe">🌍</div>
        </div>
      </div>
    </div>
    
    <!-- 空状态 -->
    <div v-if="resources.length === 0" class="empty-shelf">
      <div class="empty-icon">📚</div>
      <p>书架空空如也...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'

interface Resource {
  id: number
  title: string
  description: string
  coverImage: string
  viewCount: number
  downloadCount: number
  categoryId: number
}

const props = defineProps<{
  resources: Resource[]
  booksPerRow?: number
}>()

defineEmits<{
  (e: 'select', resource: Resource): void
}>()

const hoveredBook = ref<number | null>(null)
const booksPerRow = computed(() => props.booksPerRow || 5)

// 将资源分成书架层
const shelves = computed(() => {
  const result: Resource[][] = []
  for (let i = 0; i < props.resources.length; i += booksPerRow.value) {
    result.push(props.resources.slice(i, i + booksPerRow.value))
  }
  return result
})

// 获取图片URL
const getImageUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return '/api' + url
}

// 获取书本高度（基于标题长度等因素变化）
const getBookHeight = (book: Resource) => {
  const baseHeight = 160
  const variation = (book.id % 4) * 15
  return baseHeight + variation
}

// 截断标题
const truncateTitle = (title: string) => {
  if (title.length > 12) return title.slice(0, 12) + '...'
  return title
}

// 截断描述
const truncateDesc = (desc: string) => {
  if (!desc) return '暂无描述'
  if (desc.length > 50) return desc.slice(0, 50) + '...'
  return desc
}
</script>

<style scoped lang="scss">
.bookshelf-container {
  padding: 40px 20px;
  background: linear-gradient(180deg, #1a1410 0%, #2d2318 50%, #1a1410 100%);
  border-radius: 20px;
  min-height: 400px;
}

// ============================================
// 书架层
// ============================================
.bookshelf-row {
  position: relative;
  margin-bottom: 40px;
  
  &:last-child {
    margin-bottom: 0;
  }
}

// 木质书架
.shelf-wood {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 25px;
  
  .wood-surface {
    position: absolute;
    bottom: 10px;
    left: -20px;
    right: -20px;
    height: 15px;
    background: linear-gradient(90deg, #8B4513 0%, #A0522D 30%, #CD853F 50%, #A0522D 70%, #8B4513 100%);
    border-radius: 2px;
    box-shadow: 
      0 2px 4px rgba(0, 0, 0, 0.3),
      inset 0 2px 4px rgba(255, 255, 255, 0.1);
  }
  
  .wood-front {
    position: absolute;
    bottom: 0;
    left: -20px;
    right: -20px;
    height: 12px;
    background: linear-gradient(180deg, #6B3510 0%, #4A2508 100%);
    transform: perspective(100px) rotateX(-30deg);
    transform-origin: top;
  }
  
  .wood-shadow {
    position: absolute;
    bottom: -10px;
    left: 0;
    right: 0;
    height: 20px;
    background: linear-gradient(180deg, rgba(0, 0, 0, 0.4) 0%, transparent 100%);
    filter: blur(5px);
  }
}

// 书籍行
.books-row {
  display: flex;
  align-items: flex-end;
  justify-content: center;
  gap: 8px;
  padding: 0 40px 30px;
  min-height: 200px;
}

// ============================================
// 单本书籍
// ============================================
.book-wrapper {
  position: relative;
  cursor: pointer;
  animation: book-appear 0.5s ease backwards;
}

.book {
  width: 45px;
  position: relative;
  transform-style: preserve-3d;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  transform: rotateY(-5deg);
  
  &.book-hovered {
    transform: rotateY(-25deg) translateZ(30px) translateY(-10px);
    z-index: 10;
  }
  
  // 书籍颜色变体
  &.book-color-0 { --book-color: #8B0000; --book-color-dark: #5c0000; }
  &.book-color-1 { --book-color: #1e3a5f; --book-color-dark: #0f1d30; }
  &.book-color-2 { --book-color: #2d5016; --book-color-dark: #162808; }
  &.book-color-3 { --book-color: #4a3728; --book-color-dark: #251c14; }
  &.book-color-4 { --book-color: #5c3d6e; --book-color-dark: #2e1f37; }
  &.book-color-5 { --book-color: #8B4513; --book-color-dark: #5c2e0d; }
}

// 书脊
.book-spine {
  position: absolute;
  left: 0;
  top: 0;
  width: 45px;
  height: 100%;
  background: linear-gradient(90deg, var(--book-color-dark) 0%, var(--book-color) 30%, var(--book-color) 70%, var(--book-color-dark) 100%);
  border-radius: 3px 0 0 3px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 10px 5px;
  box-shadow: 
    inset -2px 0 4px rgba(0, 0, 0, 0.3),
    inset 2px 0 4px rgba(255, 255, 255, 0.1);
  
  .spine-title {
    writing-mode: vertical-rl;
    text-orientation: mixed;
    color: rgba(255, 255, 255, 0.9);
    font-size: 11px;
    font-weight: 600;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
    letter-spacing: 1px;
    flex: 1;
    display: flex;
    align-items: center;
  }
  
  .spine-decoration {
    width: 30px;
    height: 3px;
    background: linear-gradient(90deg, transparent, rgba(255, 215, 0, 0.6), transparent);
    border-radius: 2px;
    margin-top: 8px;
  }
}

// 书顶
.book-top {
  position: absolute;
  left: 0;
  top: 0;
  width: 45px;
  height: 30px;
  background: linear-gradient(180deg, #f5f0e6 0%, #e8e0d0 100%);
  transform: rotateX(90deg) translateZ(0px);
  transform-origin: top;
  border-radius: 2px;
  
  &::after {
    content: '';
    position: absolute;
    top: 2px;
    left: 2px;
    right: 2px;
    bottom: 2px;
    background: repeating-linear-gradient(
      90deg,
      #d4cfc4 0px,
      #d4cfc4 1px,
      transparent 1px,
      transparent 3px
    );
    opacity: 0.5;
  }
}

// 书面（正面）
.book-front {
  position: absolute;
  left: 45px;
  top: 0;
  width: 30px;
  height: 100%;
  background: linear-gradient(90deg, var(--book-color-dark) 0%, var(--book-color) 100%);
  transform: rotateY(90deg);
  transform-origin: left;
  overflow: hidden;
  border-radius: 0 3px 3px 0;
  
  .book-cover {
    width: 100%;
    height: 100%;
    object-fit: cover;
    opacity: 0.8;
  }
  
  .book-cover-overlay {
    position: absolute;
    inset: 0;
    background: linear-gradient(180deg, transparent 0%, rgba(0, 0, 0, 0.3) 100%);
  }
}

// ============================================
// 悬浮信息卡片
// ============================================
.book-tooltip {
  position: absolute;
  bottom: calc(100% + 15px);
  left: 50%;
  transform: translateX(-50%);
  width: 220px;
  padding: 16px;
  background: rgba(30, 30, 40, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
  z-index: 100;
  
  h4 {
    margin: 0 0 8px;
    font-size: 14px;
    font-weight: 600;
    color: #fff;
    line-height: 1.4;
  }
  
  .tooltip-desc {
    margin: 0 0 12px;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.7);
    line-height: 1.5;
  }
  
  .tooltip-stats {
    display: flex;
    gap: 16px;
    font-size: 12px;
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 8px;
  }
  
  .tooltip-hint {
    font-size: 11px;
    color: rgba(139, 92, 246, 0.9);
    text-align: center;
    padding-top: 8px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  &::after {
    content: '';
    position: absolute;
    bottom: -8px;
    left: 50%;
    transform: translateX(-50%);
    border: 8px solid transparent;
    border-top-color: rgba(30, 30, 40, 0.95);
  }
}

// ============================================
// 装饰物品
// ============================================
.shelf-decorations {
  position: absolute;
  right: 20px;
  bottom: 30px;
  display: flex;
  gap: 20px;
  
  .decoration {
    font-size: 28px;
    filter: drop-shadow(0 4px 6px rgba(0, 0, 0, 0.3));
    
    &.plant {
      animation: sway 3s ease-in-out infinite;
    }
    
    &.globe {
      animation: spin-slow 20s linear infinite;
    }
  }
}

// ============================================
// 空状态
// ============================================
.empty-shelf {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  color: rgba(255, 255, 255, 0.5);
  
  .empty-icon {
    font-size: 64px;
    margin-bottom: 16px;
    animation: bounce 2s ease-in-out infinite;
  }
  
  p {
    font-size: 18px;
  }
}

// ============================================
// 动画
// ============================================
@keyframes book-appear {
  from {
    opacity: 0;
    transform: translateY(30px) rotateY(-20deg);
  }
  to {
    opacity: 1;
    transform: translateY(0) rotateY(-5deg);
  }
}

@keyframes sway {
  0%, 100% { transform: rotate(-3deg); }
  50% { transform: rotate(3deg); }
}

@keyframes spin-slow {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.tooltip-enter-active,
.tooltip-leave-active {
  transition: all 0.2s ease;
}

.tooltip-enter-from,
.tooltip-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(10px);
}

// 响应式
@media (max-width: 768px) {
  .books-row {
    padding: 0 20px 30px;
    gap: 6px;
  }
  
  .book {
    width: 35px;
  }
  
  .book-spine {
    width: 35px;
    
    .spine-title {
      font-size: 9px;
    }
  }
  
  .book-front {
    left: 35px;
    width: 25px;
  }
  
  .book-tooltip {
    width: 180px;
    padding: 12px;
  }
  
  .shelf-decorations {
    display: none;
  }
}
</style>
