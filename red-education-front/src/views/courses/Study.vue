<template>
  <div class="study-page" v-loading="loading">
    <div class="study-container" v-if="chapter">
      <!-- 顶部导航 -->
      <div class="study-header">
        <el-button class="back-btn" @click="backToCourse">
          <el-icon><ArrowLeft /></el-icon>
          返回课程
        </el-button>
        <div class="header-info">
          <h2 class="course-title">{{ course?.title }}</h2>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: studyProgress + '%' }"></div>
            <span class="progress-text">{{ studyProgress }}%</span>
          </div>
        </div>
        <el-button class="menu-trigger" @click="drawerVisible = true">
          <el-icon><List /></el-icon>
          目录
        </el-button>
      </div>

      <!-- 视频/内容播放区 -->
      <div class="content-area">
        <div class="video-wrapper" v-if="chapter.videoUrl">
          <video
            ref="videoRef"
            :src="chapter.videoUrl"
            controls
            @timeupdate="handleTimeUpdate"
            @seeking="handleVideoSeeking"
            @seeked="handleVideoSeeked"
            @ended="handleVideoEnd"
            class="video-player"
          >
            您的浏览器不支持视频播放
          </video>
        </div>
        <div class="text-content" v-else>
          <div v-html="chapter.content"></div>
        </div>
      </div>

      <!-- 章节信息 -->
      <div class="chapter-panel">
        <div class="chapter-header">
          <div class="chapter-info">
            <h3 class="chapter-title">{{ chapter.title }}</h3>
            <div class="study-stats">
              <span><el-icon><Clock /></el-icon> {{ formatDuration(studyDuration) }}</span>
              <span><el-icon><View /></el-icon> {{ Math.round(currentProgress) }}%</span>
            </div>
          </div>
          <div class="chapter-status">
            <el-button
              v-if="!isCompleted"
              type="success"
              size="large"
              @click="handleComplete"
              :loading="completing"
            >
              <el-icon><CircleCheck /></el-icon>
              标记完成
            </el-button>
            <div v-else class="completed-badge">
              <el-icon><CircleCheck /></el-icon>
              已完成
            </div>
          </div>
        </div>

        <div class="chapter-nav">
          <el-button
            size="large"
            :disabled="!prevChapter"
            @click="switchChapter(prevChapter?.id)"
          >
            <el-icon><ArrowLeft /></el-icon>
            上一章
          </el-button>
          <el-button
            size="large"
            type="primary"
            :disabled="!nextChapter"
            @click="switchChapter(nextChapter?.id)"
          >
            下一章
            <el-icon><ArrowRight /></el-icon>
          </el-button>
        </div>
      </div>

      <!-- 章节列表侧边栏 -->
      <el-drawer v-model="drawerVisible" title="课程目录" :size="360">
        <div class="chapters-list">
          <div
            v-for="(item, index) in allChapters"
            :key="item.id"
            class="chapter-item"
            :class="{ active: item.id === chapter.id, completed: completedChapterIds.includes(item.id) }"
            @click="switchChapter(item.id)"
          >
            <div class="chapter-num">{{ index + 1 }}</div>
            <div class="chapter-name">{{ item.title }}</div>
            <el-icon v-if="completedChapterIds.includes(item.id)" class="check-icon">
              <CircleCheck />
            </el-icon>
          </div>
        </div>
      </el-drawer>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ArrowLeft, ArrowRight, CircleCheck, Clock, View, List } from '@element-plus/icons-vue'
import { getCourseDetail, saveStudyProgress, completeChapter, getCourseStudy } from '@/api/course'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const completing = ref(false)
const drawerVisible = ref(false)
const course = ref<any>(null)
const chapter = ref<any>(null)
const allChapters = ref<any[]>([])
const completedChapterIds = ref<number[]>([])
const isCompleted = ref(false)
const videoRef = ref<HTMLVideoElement>()
const studyDuration = ref(0)
const currentProgress = ref(0)
const studyTimer = ref<any>(null)

const courseId = computed(() => Number(route.params.id))
const chapterId = computed(() => Number(route.params.chapterId))

const studyProgress = computed(() => {
  if (!allChapters.value.length) return 0
  return Math.round((completedChapterIds.value.length / allChapters.value.length) * 100)
})

const prevChapter = computed(() => {
  const index = allChapters.value.findIndex(c => c.id === chapterId.value)
  return index > 0 ? allChapters.value[index - 1] : null
})

const nextChapter = computed(() => {
  const index = allChapters.value.findIndex(c => c.id === chapterId.value)
  return index < allChapters.value.length - 1 ? allChapters.value[index + 1] : null
})

const fetchData = async () => {
  loading.value = true
  try {
    const courseRes = await getCourseDetail(courseId.value)
    if (courseRes.data) {
      course.value = courseRes.data
      allChapters.value = courseRes.data.chapters || []
      chapter.value = allChapters.value.find(c => c.id === chapterId.value)
    }
    const studyRes = await getCourseStudy(courseId.value)
    if (studyRes.data) {
      completedChapterIds.value = studyRes.data.completedChapterIds || []
      isCompleted.value = completedChapterIds.value.includes(chapterId.value)
    }
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 最大已观看位置（防止快进）
const maxWatchedPosition = ref(0)
// 上一次的播放位置，用于检测跳跃
const lastKnownPosition = ref(0)

const handleTimeUpdate = () => {
  if (!videoRef.value || !videoRef.value.duration) return
  
  const currentTime = videoRef.value.currentTime
  const timeDiff = currentTime - lastKnownPosition.value
  
  // 如果时间跳跃超过2秒，说明是seek操作，不要更新maxWatchedPosition
  if (timeDiff > 2) {
    // 这是一次跳跃，检查是否允许
    if (currentTime > maxWatchedPosition.value + 1) {
      console.log('Jump detected! Blocking:', currentTime, '->', maxWatchedPosition.value)
      videoRef.value.currentTime = maxWatchedPosition.value
      lastKnownPosition.value = maxWatchedPosition.value
      ElMessage.warning('请按顺序观看，不能快进')
      return
    }
  }
  
  // 正常播放，允许更新maxWatchedPosition
  if (currentTime > maxWatchedPosition.value) {
    maxWatchedPosition.value = currentTime
  }
  lastKnownPosition.value = currentTime
  
  currentProgress.value = (maxWatchedPosition.value / videoRef.value.duration) * 100
}

// seeking事件作为辅助检测
const handleVideoSeeking = () => {
  if (!videoRef.value) return
  
  const targetTime = videoRef.value.currentTime
  const maxAllowed = maxWatchedPosition.value + 1
  
  console.log('Seeking to:', targetTime, 'max allowed:', maxAllowed)
  
  if (targetTime > maxAllowed) {
    console.log('Seeking BLOCKED!')
    videoRef.value.currentTime = maxWatchedPosition.value
    lastKnownPosition.value = maxWatchedPosition.value
    ElMessage.warning('请按顺序观看，不能快进')
  }
}

// seeked事件作为最后防线
const handleVideoSeeked = () => {
  if (!videoRef.value) return
  
  const targetTime = videoRef.value.currentTime
  const maxAllowed = maxWatchedPosition.value + 1
  
  if (targetTime > maxAllowed) {
    console.log('Seeked BLOCKED!')
    videoRef.value.currentTime = maxWatchedPosition.value
    lastKnownPosition.value = maxWatchedPosition.value
    ElMessage.warning('请按顺序观看，不能快进')
  } else {
    lastKnownPosition.value = targetTime
  }
}

const handleVideoEnd = () => {
  currentProgress.value = 100
  if (!isCompleted.value) handleComplete()
}

const handleComplete = async () => {
  completing.value = true
  try {
    await completeChapter(courseId.value, chapterId.value)
    isCompleted.value = true
    completedChapterIds.value.push(chapterId.value)
    ElMessage.success('已标记完成')
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    completing.value = false
  }
}

const switchChapter = (id: number | undefined) => {
  if (id) {
    saveProgress()
    router.push(`/courses/${courseId.value}/study/${id}`)
    fetchData()
  }
}

const backToCourse = () => { saveProgress(); router.push(`/courses/${courseId.value}`) }

const saveProgress = async () => {
  try {
    await saveStudyProgress({
      courseId: courseId.value,
      chapterId: chapterId.value,
      progress: currentProgress.value,
      duration: studyDuration.value
    })
  } catch (error) {
    console.error('保存进度失败', error)
  }
}

const formatDuration = (seconds: number) => {
  const mins = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${mins}分${secs}秒`
}

const startStudyTimer = () => {
  studyTimer.value = setInterval(() => {
    studyDuration.value++
    if (studyDuration.value % 30 === 0) saveProgress()
  }, 1000)
}

onMounted(() => { fetchData(); startStudyTimer() })
onUnmounted(() => { if (studyTimer.value) { clearInterval(studyTimer.value); saveProgress() } })
</script>

<style scoped lang="scss">
.study-page {
  min-height: 100vh;
  background: linear-gradient(180deg, #1a1a2e 0%, #0f0f1a 100%);
  color: white;
}

.study-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.study-header {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 20px 24px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-xl);
  margin-bottom: 24px;
  border: 1px solid rgba(255, 255, 255, 0.1);

  @media (max-width: 768px) {
    flex-direction: column;
    gap: 16px;
  }

  .back-btn {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: white;

    &:hover {
      background: rgba(255, 255, 255, 0.2);
    }
  }

  .header-info {
    flex: 1;

    .course-title {
      font-size: var(--font-size-lg);
      margin: 0 0 8px 0;
    }

    .progress-bar {
      position: relative;
      height: 8px;
      background: rgba(255, 255, 255, 0.1);
      border-radius: 4px;
      overflow: visible;

      .progress-fill {
        height: 100%;
        background: linear-gradient(90deg, #D64541 0%, #FF6B6B 100%);
        border-radius: 4px;
        transition: width 0.3s ease;
      }

      .progress-text {
        position: absolute;
        right: 0;
        top: -20px;
        font-size: var(--font-size-xs);
        color: #22C55E;
      }
    }
  }

  .menu-trigger {
    background: rgba(255, 255, 255, 0.1);
    border: 1px solid rgba(255, 255, 255, 0.2);
    color: white;
  }
}

.content-area {
  margin-bottom: 24px;

  .video-wrapper {
    position: relative;
    width: 100%;
    padding-top: 56.25%;
    background: #000;
    border-radius: var(--radius-xl);
    overflow: hidden;

    .video-player {
      position: absolute;
      inset: 0;
      width: 100%;
      height: 100%;
    }
  }

  .text-content {
    padding: 40px;
    background: white;
    color: var(--color-text-primary);
    border-radius: var(--radius-xl);
    min-height: 400px;
    line-height: var(--line-height-relaxed);
  }
}

.chapter-panel {
  padding: 24px;
  background: rgba(255, 255, 255, 0.05);
  backdrop-filter: blur(10px);
  border-radius: var(--radius-xl);
  border: 1px solid rgba(255, 255, 255, 0.1);

  .chapter-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;

    @media (max-width: 576px) {
      flex-direction: column;
      gap: 16px;
    }

    .chapter-info {
      .chapter-title {
        font-size: var(--font-size-xl);
        margin: 0 0 12px 0;
      }

      .study-stats {
        display: flex;
        gap: 24px;
        font-size: var(--font-size-sm);
        color: rgba(255, 255, 255, 0.7);

        span {
          display: flex;
          align-items: center;
          gap: 6px;
        }
      }
    }

    .completed-badge {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 24px;
      background: rgba(34, 197, 94, 0.2);
      border: 1px solid #22C55E;
      border-radius: var(--radius-lg);
      color: #22C55E;
      font-weight: var(--font-weight-medium);
    }
  }

  .chapter-nav {
    display: flex;
    gap: 16px;
  }
}

// Drawer styles
.chapters-list {
  .chapter-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 16px;
    border: 1px solid var(--color-border-light);
    border-radius: var(--radius-lg);
    margin-bottom: 12px;
    cursor: pointer;
    transition: all var(--transition-base);

    &:hover {
      border-color: var(--color-primary);
      background: var(--color-bg-tertiary);
    }

    &.active {
      border-color: var(--color-primary);
      background: rgba(214, 69, 65, 0.1);
    }

    &.completed .chapter-num {
      background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%);
    }

    .chapter-num {
      width: 32px;
      height: 32px;
      background: var(--color-primary-gradient);
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: var(--font-size-sm);
      font-weight: var(--font-weight-bold);
      flex-shrink: 0;
    }

    .chapter-name {
      flex: 1;
      font-size: var(--font-size-sm);
    }

    .check-icon {
      color: #22C55E;
      font-size: 18px;
    }
  }
}
</style>
