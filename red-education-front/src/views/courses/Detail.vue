<template>
  <MainLayout>
    <div class="course-detail-page" v-loading="loading">
      <template v-if="course">
        <!-- 课程封面头部 -->
        <div class="course-header">
          <div class="header-bg">
            <img :src="getFullUrl(course.coverImage)" :alt="course.title" />
            <div class="header-overlay"></div>
          </div>
          <div class="header-content">
            <el-button class="back-btn" @click="$router.back()">
              <el-icon><ArrowLeft /></el-icon>
              返回
            </el-button>
            <div class="course-info-header">
              <el-tag effect="dark" class="course-tag">免费课程</el-tag>
              <h1 class="course-title">{{ course.title }}</h1>
              <div class="course-meta">
                <span class="meta-item">
                  <el-icon><User /></el-icon>
                  {{ course.completeCount || 0 }} 人完成
                </span>
                <span class="meta-item">
                  <el-icon><Clock /></el-icon>
                  {{ course.duration || 0 }} 分钟
                </span>
                <span class="meta-item">
                  <el-icon><View /></el-icon>
                  {{ course.viewCount || 0 }} 浏览
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="page-container">
          <div class="content-layout">
            <!-- 左侧主内容 -->
            <div class="main-content">
              <!-- 课程简介 -->
              <div class="section-card">
                <h3 class="section-title">
                  <el-icon><Document /></el-icon>
                  课程简介
                </h3>
                <p class="course-intro">{{ course.intro || '暂无简介' }}</p>
                <div class="course-stats-grid">
                  <div class="stat-box">
                    <span class="stat-value">{{ course.totalChapters || 0 }}</span>
                    <span class="stat-label">章节数</span>
                  </div>
                  <div class="stat-box">
                    <span class="stat-value">{{ course.duration || 0 }}</span>
                    <span class="stat-label">总时长(分钟)</span>
                  </div>
                  <div class="stat-box">
                    <span class="stat-value">{{ course.completeCount || 0 }}</span>
                    <span class="stat-label">完成人数</span>
                  </div>
                  <div class="stat-box">
                    <span class="stat-value">{{ formatDate(course.createTime) }}</span>
                    <span class="stat-label">发布日期</span>
                  </div>
                </div>
              </div>

              <!-- 章节目录 / 课程视频 -->
              <div class="section-card">
                <h3 class="section-title">
                  <el-icon><List /></el-icon>
                  {{ chapters.length > 0 ? '课程目录' : '课程视频' }}
                </h3>
                <!-- 有章节时显示章节列表 -->
                <div class="chapters-list" v-if="chapters.length > 0">
                  <div
                    v-for="(chapter, index) in chapters"
                    :key="chapter.id"
                    class="chapter-item"
                    :class="{ completed: isChapterCompleted(chapter.id) }"
                    @click="goToStudy(chapter.id)"
                  >
                    <div class="chapter-number" :class="{ completed: isChapterCompleted(chapter.id) }">
                      <span v-if="!isChapterCompleted(chapter.id)">{{ index + 1 }}</span>
                      <el-icon v-else><Check /></el-icon>
                    </div>
                    <div class="chapter-info">
                      <h4>{{ chapter.title }}</h4>
                      <span class="chapter-duration">
                        <el-icon><Clock /></el-icon>
                        {{ chapter.duration }} 分钟
                      </span>
                    </div>
                    <el-icon class="chapter-arrow"><ArrowRight /></el-icon>
                  </div>
                </div>
                <div class="course-video-wrapper" v-else-if="course.videoUrl">
                  <video
                    ref="videoRef"
                    controls
                    preload="metadata"
                    :poster="getFullUrl(course.coverImage)"
                    class="course-video-player"
                    @loadedmetadata="handleVideoLoadedMetadata"
                    @play="handleVideoPlay"
                    @pause="handleVideoPause"
                    @timeupdate="handleVideoTimeUpdate"
                    @seeking="handleVideoSeeking"
                    @seeked="handleVideoSeeked"
                    @ended="handleVideoEnd"
                    @error="handleVideoError"
                  >
                    <source :src="getFullUrl(course.videoUrl)" :type="getVideoMimeType(course.videoUrl)" />
                    您的浏览器不支持视频播放
                  </video>
                  <div v-if="videoError" class="video-error">
                    <el-icon :size="48"><Warning /></el-icon>
                    <p>视频格式可能不受此浏览器支持</p>
                    <el-button type="primary" @click="downloadVideo">下载观看</el-button>
                  </div>
                </div>
                <!-- 既无章节也无视频 -->
                <el-empty v-else description="暂无课程内容" />
              </div>
            </div>

            <!-- 右侧边栏 -->
            <div class="sidebar">
              <!-- 开始学习 -->
              <div class="section-card action-card">
                <el-button
                  type="primary"
                  size="large"
                  class="start-btn"
                  @click="handleStartStudy"
                  :loading="starting"
                >
                  <el-icon><VideoPlay /></el-icon>
                  {{ studyInfo ? '继续学习' : '开始学习' }}
                </el-button>

                <!-- 学习进度 -->
                <div class="study-progress" v-if="studyInfo">
                  <div class="progress-header">
                    <span>学习进度</span>
                    <span class="progress-value">{{ studyInfo.progress || 0 }}%</span>
                  </div>
                  <el-progress 
                    :percentage="studyInfo.progress || 0" 
                    :stroke-width="8"
                    :show-text="false"
                    color="#D64541"
                  />
                  <p class="progress-text">
                    已完成 {{ studyInfo.completedChapters || 0 }} / {{ course.totalChapters || 0 }} 章节
                  </p>
                </div>
              </div>

              <!-- 课程信息 -->
              <div class="section-card info-card">
                <h3 class="section-title">课程信息</h3>
                <div class="info-list">
                  <div class="info-item">
                    <span class="info-label">课程难度</span>
                    <span class="info-value">入门级</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">更新时间</span>
                    <span class="info-value">{{ formatDate(course.updateTime || course.createTime) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  ArrowLeft, ArrowRight, User, Clock, View, Document, List, Check, VideoPlay, Warning
} from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { getCourseDetail, getCourseStudy, saveStudyProgress } from '@/api/course'
import { useUserStore } from '@/stores/user'
import dayjs from 'dayjs'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const starting = ref(false)
const course = ref<any>(null)
const chapters = ref<any[]>([])
const studyInfo = ref<any>(null)

const courseId = computed(() => Number(route.params.id))

const fetchCourseDetail = async () => {
  loading.value = true
  try {
    const res = await getCourseDetail(courseId.value)
    if (res.data) {
      course.value = res.data
      chapters.value = res.data.chapters || []
      if (userStore.isLoggedIn) {
        fetchStudyInfo()
      }
    }
  } catch (error) {
    ElMessage.error('获取课程详情失败')
    router.back()
  } finally {
    loading.value = false
  }
}

const fetchStudyInfo = async () => {
  try {
    const res = await getCourseStudy(courseId.value)
    if (res.data) {
      studyInfo.value = res.data
      // 恢复上次播放位置
      if (res.data.lastPosition && res.data.lastPosition > 0) {
        // 同时更新最大观看位置，允许用户回到之前看过的任意位置
        maxWatchedPosition.value = res.data.lastPosition
        setTimeout(() => {
          if (videoRef.value) {
            videoRef.value.currentTime = res.data.lastPosition
            ElMessage.info(`已恢复到上次观看位置 ${Math.floor(res.data.lastPosition / 60)}分${res.data.lastPosition % 60}秒`)
          }
        }, 500)
      }
    }
  } catch (error) {
    // 未开始学习
  }
}

const isChapterCompleted = (chapterId: number) => {
  if (!studyInfo.value || !studyInfo.value.completedChapterIds) return false
  return studyInfo.value.completedChapterIds.includes(chapterId)
}

// 视频播放相关
const videoRef = ref<HTMLVideoElement>()
const videoError = ref(false)

const handleVideoEnd = () => {
  ElMessage.success('恭喜！视频观看完成')
}

const handleVideoError = () => {
  videoError.value = true
}

const getVideoMimeType = (url: string) => {
  if (!url) return 'video/mp4'
  const ext = url.split('.').pop()?.toLowerCase() || ''
  const mimeTypes: Record<string, string> = {
    'mp4': 'video/mp4',
    'webm': 'video/webm',
    'ogg': 'video/ogg',
    'ts': 'video/mp2t',
    'mov': 'video/quicktime',
    'avi': 'video/x-msvideo',
    'mkv': 'video/x-matroska',
    'flv': 'video/x-flv'
  }
  return mimeTypes[ext] || 'video/mp4'
}

const downloadVideo = () => {
  if (!course.value?.videoUrl) return
  const url = getFullUrl(course.value.videoUrl)
  const a = document.createElement('a')
  a.href = url
  a.download = course.value.title + '.ts'
  document.body.appendChild(a)
  a.click()
  document.body.removeChild(a)
}

const handleStartStudy = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  // 有章节时跳转到学习页面
  if (chapters.value.length > 0) {
    const targetChapterId = studyInfo.value?.currentChapterId || chapters.value[0].id
    goToStudy(targetChapterId)
  } else if (course.value?.videoUrl && videoRef.value) {
    // 无章节但有视频时，滚动到视频区域并自动播放
    videoRef.value.scrollIntoView({ behavior: 'smooth' })
    videoRef.value.play()
  } else {
    ElMessage.info('暂无可学习的内容')
  }
}

const goToStudy = (chapterId: number) => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push('/login')
    return
  }
  router.push(`/courses/${courseId.value}/study/${chapterId}`)
}

const getFullUrl = (url: string) => {
  if (!url) return 'https://picsum.photos/1200/400?random=' + Math.random()
  if (url.startsWith('http')) return url
  return `${import.meta.env.VITE_API_BASE_URL}${url}`
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD')

// 视频学习时长统计
const studyDuration = ref(0) // 学习时长（秒）
const videoProgress = ref(0) // 视频观看进度（百分比）
const studyTimer = ref<any>(null)
const positionTimer = ref<any>(null) // 高频位置更新计时器
const isPlaying = ref(false)

const handleVideoPlay = () => {
  if (!userStore.isLoggedIn) return
  isPlaying.value = true
  
  // 开始学习计时（每秒）
  if (!studyTimer.value) {
    studyTimer.value = setInterval(() => {
      studyDuration.value++
      // 每30秒自动保存一次进度
      if (studyDuration.value % 30 === 0) {
        saveProgress()
      }
    }, 1000)
  }
  
  // 开始高频位置更新（每100ms），确保误差在2秒以内
  if (!positionTimer.value) {
    positionTimer.value = setInterval(() => {
      if (videoRef.value && videoRef.value.currentTime > maxWatchedPosition.value) {
        maxWatchedPosition.value = videoRef.value.currentTime
        lastKnownPosition.value = videoRef.value.currentTime
      }
    }, 100)
  }
}

const handleVideoPause = () => {
  isPlaying.value = false
  
  // 停止高频位置更新
  if (positionTimer.value) {
    clearInterval(positionTimer.value)
    positionTimer.value = null
  }
  
  // 暂停时确保保存当前位置为最大观看位置
  if (videoRef.value && videoRef.value.currentTime > maxWatchedPosition.value) {
    maxWatchedPosition.value = videoRef.value.currentTime
    lastKnownPosition.value = videoRef.value.currentTime
  }
  // 暂停时保存进度
  if (userStore.isLoggedIn) {
    saveProgress()
  }
}

// 最大已观看位置（防止快进刷进度）
const maxWatchedPosition = ref(0)
// 上一次的播放位置，用于检测跳跃
const lastKnownPosition = ref(0)
// 视频是否已加载
const videoReady = ref(false)

const handleVideoTimeUpdate = () => {
  if (!videoRef.value || !videoRef.value.duration) return
  
  const currentTime = videoRef.value.currentTime
  const timeDiff = currentTime - lastKnownPosition.value
  
  // 如果时间跳跃超过2秒，说明是seek操作
  if (timeDiff > 2) {
    // 先确保maxWatchedPosition是最新的（使用跳跃前的位置）
    if (lastKnownPosition.value > maxWatchedPosition.value) {
      maxWatchedPosition.value = lastKnownPosition.value
    }
    
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
  
  // 进度基于最大观看位置
  videoProgress.value = Math.round((maxWatchedPosition.value / videoRef.value.duration) * 100)
}

// 视频元数据加载完成
const handleVideoLoadedMetadata = () => {
  videoReady.value = true
  console.log('Video loaded, maxWatchedPosition:', maxWatchedPosition.value)
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
    // seek到允许的位置，更新lastKnownPosition
    lastKnownPosition.value = targetTime
  }
}

const saveProgress = async () => {
  if (!userStore.isLoggedIn || !courseId.value) return
  try {
    const lastPosition = videoRef.value ? Math.floor(videoRef.value.currentTime) : 0
    await saveStudyProgress({
      courseId: courseId.value,
      chapterId: 0, // 无章节时使用0
      progress: videoProgress.value,
      duration: studyDuration.value,
      lastPosition
    })
  } catch (error) {
    console.error('保存学习进度失败', error)
  }
}

const stopStudyTimer = () => {
  if (studyTimer.value) {
    clearInterval(studyTimer.value)
    studyTimer.value = null
  }
  if (positionTimer.value) {
    clearInterval(positionTimer.value)
    positionTimer.value = null
  }
}

onMounted(() => {
  fetchCourseDetail()
})

onUnmounted(() => {
  stopStudyTimer()
  // 离开页面前确保保存最新的视频位置
  if (videoRef.value && videoRef.value.currentTime > 0) {
    maxWatchedPosition.value = videoRef.value.currentTime
    lastKnownPosition.value = videoRef.value.currentTime
  }
  // 离开页面时保存进度
  if (userStore.isLoggedIn) {
    saveProgress()
  }
})
</script>

<style scoped lang="scss">
.course-detail-page {
  min-height: calc(100vh - var(--header-height));
  background: var(--color-bg-secondary);
}

// 课程头部
.course-header {
  position: relative;
  height: 320px;
  overflow: hidden;

  .header-bg {
    position: absolute;
    inset: 0;

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .header-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(
        180deg,
        rgba(0, 0, 0, 0.3) 0%,
        rgba(0, 0, 0, 0.7) 100%
      );
    }
  }

  .header-content {
    position: relative;
    z-index: 1;
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .back-btn {
    align-self: flex-start;
    background: rgba(255, 255, 255, 0.2);
    border: none;
    color: white;
    backdrop-filter: blur(10px);

    &:hover {
      background: rgba(255, 255, 255, 0.3);
    }
  }

  .course-info-header {
    margin-top: auto;
    color: white;

    .course-tag {
      background: var(--color-primary-gradient);
      border: none;
      margin-bottom: 12px;
    }

    .course-title {
      font-size: var(--font-size-3xl);
      font-weight: var(--font-weight-bold);
      margin-bottom: 16px;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
    }

    .course-meta {
      display: flex;
      gap: 24px;
      flex-wrap: wrap;

      .meta-item {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: var(--font-size-sm);
        opacity: 0.9;
      }
    }
  }
}

.page-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 20px;
}

// 内容布局
.content-layout {
  display: grid;
  grid-template-columns: 1fr 360px;
  gap: 32px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

// 卡片样式
.section-card {
  background: var(--color-bg-primary);
  border-radius: var(--radius-xl);
  padding: 24px;
  border: 1px solid var(--color-border);
  margin-bottom: 24px;

  .section-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: var(--font-size-lg);
    font-weight: var(--font-weight-bold);
    color: var(--color-text-primary);
    margin: 0 0 20px 0;
  }
}

// 课程简介
.course-intro {
  color: var(--color-text-secondary);
  line-height: var(--line-height-relaxed);
  margin-bottom: 24px;
}

.course-stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  @media (max-width: 576px) {
    grid-template-columns: repeat(2, 1fr);
  }

  .stat-box {
    text-align: center;
    padding: 16px;
    background: var(--color-bg-tertiary);
    border-radius: var(--radius-lg);

    .stat-value {
      display: block;
      font-size: var(--font-size-xl);
      font-weight: var(--font-weight-bold);
      color: var(--color-primary);
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: var(--font-size-xs);
      color: var(--color-text-muted);
    }
  }
}

// 章节列表
.chapters-list {
  .chapter-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: var(--radius-lg);
    cursor: pointer;
    transition: all var(--transition-base);
    border: 1px solid var(--color-border-light);
    margin-bottom: 12px;

    &:last-child {
      margin-bottom: 0;
    }

    &:hover {
      background: var(--color-bg-tertiary);
      border-color: var(--color-primary-light);

      .chapter-arrow {
        transform: translateX(4px);
        color: var(--color-primary);
      }
    }

    &.completed {
      background: rgba(34, 197, 94, 0.05);
      border-color: var(--color-success);
    }

    .chapter-number {
      width: 40px;
      height: 40px;
      background: var(--color-primary-gradient);
      color: white;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: var(--font-weight-bold);
      flex-shrink: 0;

      &.completed {
        background: linear-gradient(135deg, #22C55E 0%, #16A34A 100%);
      }
    }

    .chapter-info {
      flex: 1;
      min-width: 0;

      h4 {
        font-size: var(--font-size-base);
        font-weight: var(--font-weight-medium);
        color: var(--color-text-primary);
        margin-bottom: 4px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .chapter-duration {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: var(--font-size-xs);
        color: var(--color-text-muted);
      }
    }

    .chapter-arrow {
      color: var(--color-text-muted);
      transition: all var(--transition-fast);
    }
  }
}

// 课程视频播放器
.course-video-wrapper {
  position: relative;
  width: 100%;
  padding-top: 56.25%; // 16:9 比例
  background: #000;
  border-radius: var(--radius-lg);
  overflow: hidden;

  .course-video-player {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
  }

  .video-error {
    position: absolute;
    inset: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(0, 0, 0, 0.9);
    color: white;
    gap: 16px;

    p {
      color: var(--color-text-muted);
    }
  }
}

// 侧边栏
.sidebar {
  @media (max-width: 992px) {
    order: -1;
  }
}

.action-card {
  .start-btn {
    width: 100%;
    height: 52px;
    font-size: var(--font-size-lg);
    font-weight: var(--font-weight-medium);
    background: var(--color-primary-gradient);
    border: none;
    border-radius: var(--radius-lg);

    &:hover {
      box-shadow: var(--shadow-glow-red);
    }
  }

  .study-progress {
    margin-top: 24px;
    padding-top: 24px;
    border-top: 1px solid var(--color-border-light);

    .progress-header {
      display: flex;
      justify-content: space-between;
      margin-bottom: 12px;
      font-size: var(--font-size-sm);
      color: var(--color-text-secondary);

      .progress-value {
        font-weight: var(--font-weight-bold);
        color: var(--color-primary);
      }
    }

    .progress-text {
      margin-top: 12px;
      font-size: var(--font-size-xs);
      color: var(--color-text-muted);
      text-align: center;
    }
  }
}

.info-card {
  .info-list {
    .info-item {
      display: flex;
      justify-content: space-between;
      padding: 12px 0;
      border-bottom: 1px solid var(--color-border-light);

      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }

      .info-label {
        font-size: var(--font-size-sm);
        color: var(--color-text-muted);
      }

      .info-value {
        font-size: var(--font-size-sm);
        color: var(--color-text-primary);
        font-weight: var(--font-weight-medium);
      }
    }
  }
}
</style>
