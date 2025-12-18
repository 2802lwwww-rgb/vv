<template>
  <MainLayout>
    <div class="profile-page">
      <!-- 炫酷页面头部 -->
      <div class="page-header">
        <div class="header-bg">
          <div class="stars">
            <div v-for="i in 30" :key="'star-'+i" class="star" :style="getStarStyle(i)"></div>
          </div>
          <div class="gradient-orbs">
            <div class="orb orb-1"></div>
            <div class="orb orb-2"></div>
          </div>
        </div>
        <div class="header-content">
          <div class="user-info">
            <div class="avatar-wrapper">
              <div class="avatar-ring"></div>
              <el-upload
                class="avatar-uploader"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="profileForm.avatar" :src="getFullUrl(profileForm.avatar)" class="avatar" />
                <div v-else class="avatar-placeholder">
                  <el-icon :size="40"><User /></el-icon>
                </div>
                <div class="avatar-overlay">
                  <el-icon><Camera /></el-icon>
                </div>
              </el-upload>
            </div>
            <div class="user-meta">
              <h1 class="user-name">{{ profileForm.nickname || '未设置昵称' }}</h1>
              <div class="user-tags">
                <el-tag effect="dark" class="role-tag">{{ getRoleName(profileForm.role) }}</el-tag>
                <span class="join-date">
                  <el-icon><Calendar /></el-icon>
                  {{ formatDate(profileForm.createTime) }} 加入
                </span>
              </div>
            </div>
          </div>
          
          <!-- 成就统计卡片 -->
          <div class="stats-grid">
            <div class="stat-card">
              <div class="stat-icon">📚</div>
              <div class="stat-info">
                <div class="stat-value">{{ userStats.studyDays }}</div>
                <div class="stat-label">学习天数</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">⭐</div>
              <div class="stat-info">
                <div class="stat-value">{{ userStats.points }}</div>
                <div class="stat-label">积分</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🏆</div>
              <div class="stat-info">
                <div class="stat-value">{{ userStats.rank }}</div>
                <div class="stat-label">排名</div>
              </div>
            </div>
            <div class="stat-card">
              <div class="stat-icon">🎯</div>
              <div class="stat-info">
                <div class="stat-value">{{ userStats.completedCourses }}</div>
                <div class="stat-label">已完成课程</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="page-container">
        <div class="content-layout">
          <!-- 个人资料 -->
          <div class="section-card">
            <div class="card-header">
              <h3 class="card-title">
                <el-icon><UserFilled /></el-icon>
                个人资料
              </h3>
            </div>
            <el-form :model="profileForm" :rules="profileRules" ref="profileFormRef" label-position="top">
              <el-form-item label="昵称" prop="nickname">
                <el-input 
                  v-model="profileForm.nickname" 
                  placeholder="请输入昵称" 
                  maxlength="20" 
                  show-word-limit 
                  size="large"
                  class="custom-input"
                />
              </el-form-item>

              <el-form-item label="邮箱">
                <el-input v-model="profileForm.email" disabled size="large" class="custom-input" />
              </el-form-item>

              <el-form-item>
                <el-button type="primary" size="large" class="save-btn" @click="handleUpdateProfile" :loading="updating">
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </div>

          <!-- 账号安全 -->
          <div class="section-card">
            <div class="card-header">
              <h3 class="card-title">
                <el-icon><Lock /></el-icon>
                修改密码
              </h3>
            </div>
            <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-position="top">
              <el-form-item label="当前密码" prop="oldPassword">
                <el-input 
                  v-model="passwordForm.oldPassword" 
                  type="password" 
                  placeholder="请输入当前密码" 
                  show-password 
                  size="large"
                  class="custom-input"
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPassword">
                <el-input 
                  v-model="passwordForm.newPassword" 
                  type="password" 
                  placeholder="请输入新密码（6-20个字符）" 
                  show-password 
                  size="large"
                  class="custom-input"
                />
              </el-form-item>

              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input 
                  v-model="passwordForm.confirmPassword" 
                  type="password" 
                  placeholder="请再次输入新密码" 
                  show-password 
                  size="large"
                  class="custom-input"
                />
              </el-form-item>

              <el-form-item>
                <el-button type="warning" size="large" class="save-btn" @click="handleUpdatePassword" :loading="updatingPassword">
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </div>
        </div>
      </div>
    </div>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules, UploadProps } from 'element-plus'
import { User, Camera, Calendar, UserFilled, Lock } from '@element-plus/icons-vue'
import MainLayout from '@/components/layout/MainLayout.vue'
import { useUserStore } from '@/stores/user'
import { updateUserInfo, updatePassword } from '@/api/user'
import { getStudyReport } from '@/api/course'
import { getPointsRanking } from '@/api/points'
import dayjs from 'dayjs'

const userStore = useUserStore()
const profileFormRef = ref<FormInstance>()
const passwordFormRef = ref<FormInstance>()
const updating = ref(false)
const updatingPassword = ref(false)

// 用户统计数据
const userStats = reactive({
  studyDays: 0,
  points: 0,
  rank: '-' as number | string,
  completedCourses: 0
})

// 获取用户统计数据
const fetchUserStats = async () => {
  try {
    // 获取学习报告（使用 /study/report API）
    const studyRes = await getStudyReport()
    if (studyRes.data) {
      userStats.studyDays = studyRes.data.studyDays || 0
      userStats.completedCourses = studyRes.data.completedCourses || 0
    }
    
    // 获取积分（从用户信息）
    userStats.points = userStore.userInfo?.points || 0
    
    // 获取排名
    const rankRes = await getPointsRanking({ page: 1, pageSize: 100 })
    if (rankRes.data?.records) {
      const myRank = rankRes.data.records.findIndex(
        (item: any) => item.userId === userStore.userInfo?.id
      )
      userStats.rank = myRank >= 0 ? myRank + 1 : '-'
    }
  } catch (error) {
    console.error('获取用户统计数据失败:', error)
    // 失败时使用默认值
    userStats.points = userStore.userInfo?.points || 0
  }
}

// 星星样式生成
const getStarStyle = (index: number) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${1 + Math.random() * 2}px`,
    height: `${1 + Math.random() * 2}px`,
    animationDelay: `${index * 0.15}s`,
    animationDuration: `${2 + Math.random() * 2}s`
  }
}

const uploadUrl = `${import.meta.env.VITE_API_BASE_URL}/files/upload`
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

const profileForm = reactive({
  avatar: '',
  nickname: '',
  email: '',
  role: '',
  createTime: ''
})

const passwordForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const profileRules: FormRules = {
  nickname: [
    { required: true, message: '请输入昵称', trigger: 'blur' },
    { min: 2, max: 20, message: '昵称长度在 2 到 20 个字符', trigger: 'blur' }
  ]
}

const passwordRules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [{ required: true, validator: validateConfirmPassword, trigger: 'blur' }]
}

const handleAvatarSuccess: UploadProps['onSuccess'] = (response) => {
  if (response.code === 200) {
    profileForm.avatar = response.data
    ElMessage.success('头像上传成功')
  } else {
    ElMessage.error(response.message || '头像上传失败')
  }
}

const beforeAvatarUpload: UploadProps['beforeUpload'] = (rawFile) => {
  if (!['image/jpeg', 'image/png'].includes(rawFile.type)) {
    ElMessage.error('头像必须是 JPG 或 PNG 格式')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('头像大小不能超过 2MB')
    return false
  }
  return true
}

const handleUpdateProfile = async () => {
  if (!profileFormRef.value) return
  await profileFormRef.value.validate(async (valid) => {
    if (valid) {
      updating.value = true
      try {
        await updateUserInfo({
          nickname: profileForm.nickname,
          avatar: profileForm.avatar
        })
        userStore.updateUserInfo({ nickname: profileForm.nickname, avatar: profileForm.avatar })
        ElMessage.success('个人资料更新成功')
      } catch (error: any) {
        ElMessage.error(error.message || '更新失败')
      } finally {
        updating.value = false
      }
    }
  })
}

const handleUpdatePassword = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      updatingPassword.value = true
      try {
        await updatePassword({
          oldPassword: passwordForm.oldPassword,
          newPassword: passwordForm.newPassword
        })
        ElMessage.success('密码修改成功，请重新登录')
        passwordFormRef.value?.resetFields()
        setTimeout(() => {
          userStore.logout()
          window.location.href = '/login'
        }, 1500)
      } catch (error: any) {
        ElMessage.error(error.message || '修改失败')
      } finally {
        updatingPassword.value = false
      }
    }
  })
}

const getRoleName = (role: string) => {
  const roleNames: Record<string, string> = {
    'SYS_ADMIN': '系统管理员',
    'CONTENT_ADMIN': '内容管理员',
    'USER': '普通用户'
  }
  return roleNames[role] || '普通用户'
}

const formatDate = (date: string) => dayjs(date).format('YYYY-MM-DD')

const getFullUrl = (url: string) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return '/api' + url
}

onMounted(() => {
  const userInfo = userStore.userInfo
  if (userInfo) {
    Object.assign(profileForm, {
      avatar: userInfo.avatar || '',
      nickname: userInfo.nickname || '',
      email: userInfo.email || '',
      role: userInfo.role || '',
      createTime: userInfo.createTime || ''
    })
  }
  // 获取用户统计数据
  fetchUserStats()
})
</script>

<style scoped lang="scss">
.profile-page {
  min-height: calc(100vh - var(--header-height));
  background: linear-gradient(180deg, #f0f4f8 0%, #e2e8f0 100%);
}

// 页面头部 - 红色主题
.page-header {
  position: relative;
  padding: 60px 20px 80px;
  overflow: hidden;
  
  .header-bg {
    position: absolute;
    inset: 0;
    background: linear-gradient(135deg, #7f1d1d 0%, #b91c1c 50%, #dc2626 100%);
    
    .stars {
      position: absolute;
      inset: 0;
      
      .star {
        position: absolute;
        background: rgba(255, 200, 150, 0.8);
        border-radius: 50%;
        animation: twinkle 2s ease-in-out infinite;
      }
    }
    
    .gradient-orbs {
      .orb {
        position: absolute;
        border-radius: 50%;
        filter: blur(60px);
        
        &.orb-1 {
          width: 300px;
          height: 300px;
          background: rgba(251, 191, 36, 0.35);
          top: -100px;
          right: -50px;
          animation: float-orb 6s ease-in-out infinite;
        }
        
        &.orb-2 {
          width: 200px;
          height: 200px;
          background: rgba(234, 88, 12, 0.3);
          bottom: -50px;
          left: -30px;
          animation: float-orb 8s ease-in-out infinite reverse;
        }
      }
    }
  }

  .header-content {
    position: relative;
    z-index: 1;
    max-width: 900px;
    margin: 0 auto;
  }

  .user-info {
    display: flex;
    align-items: center;
    gap: 28px;
    color: white;
    margin-bottom: 32px;

    @media (max-width: 576px) {
      flex-direction: column;
      text-align: center;
    }
  }

  .avatar-wrapper {
    position: relative;
    
    .avatar-ring {
      position: absolute;
      inset: -6px;
      border-radius: 50%;
      background: conic-gradient(from 0deg, #00D9FF, #8b5cf6, #ec4899, #00D9FF);
      animation: ring-rotate 4s linear infinite;
    }

    .avatar-uploader {
      :deep(.el-upload) {
        width: 130px;
        height: 130px;
        border-radius: 50%;
        overflow: hidden;
        border: 4px solid rgba(255, 255, 255, 0.9);
        cursor: pointer;
        position: relative;
        box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
      }

      .avatar {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .avatar-placeholder {
        width: 100%;
        height: 100%;
        background: linear-gradient(135deg, #00D9FF 0%, #0077B6 100%);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
      }

      .avatar-overlay {
        position: absolute;
        inset: 0;
        background: rgba(0, 0, 0, 0.5);
        display: flex;
        align-items: center;
        justify-content: center;
        opacity: 0;
        transition: opacity 0.3s;
        color: white;
        font-size: 24px;
      }

      &:hover .avatar-overlay {
        opacity: 1;
      }
    }
  }

  .user-meta {
    .user-name {
      font-size: 32px;
      font-weight: 700;
      margin-bottom: 12px;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
    }

    .user-tags {
      display: flex;
      align-items: center;
      gap: 16px;
      flex-wrap: wrap;

      @media (max-width: 576px) {
        justify-content: center;
      }

      .role-tag {
        background: rgba(255, 255, 255, 0.15);
        border: 1px solid rgba(255, 255, 255, 0.2);
        backdrop-filter: blur(10px);
        padding: 6px 14px;
      }

      .join-date {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        opacity: 0.85;
      }
    }
  }
  
  // 统计卡片
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
    
    @media (max-width: 768px) {
      grid-template-columns: repeat(2, 1fr);
    }
    
    @media (max-width: 480px) {
      grid-template-columns: repeat(2, 1fr);
      gap: 12px;
    }
  }
  
  .stat-card {
    background: rgba(255, 255, 255, 0.1);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.15);
    border-radius: 16px;
    padding: 20px;
    display: flex;
    align-items: center;
    gap: 14px;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(255, 255, 255, 0.15);
      transform: translateY(-4px);
      box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
    }
    
    .stat-icon {
      font-size: 32px;
      line-height: 1;
    }
    
    .stat-info {
      .stat-value {
        font-size: 24px;
        font-weight: 700;
        color: #fff;
        line-height: 1.2;
      }
      
      .stat-label {
        font-size: 12px;
        color: rgba(255, 255, 255, 0.7);
        margin-top: 2px;
      }
    }
  }
}

.page-container {
  max-width: 900px;
  margin: -40px auto 0;
  padding: 0 20px 40px;
  position: relative;
  z-index: 2;
}

.content-layout {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

// 卡片样式
.section-card {
  background: #fff;
  border-radius: 20px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid rgba(0, 0, 0, 0.05);

  .card-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #f1f5f9;
  }

  .card-title {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 18px;
    font-weight: 700;
    color: #1e293b;
    margin: 0;
  }

  .custom-input {
    :deep(.el-input__wrapper) {
      border-radius: 12px;
    }
  }

  .save-btn {
    width: 100%;
    height: 48px;
    font-size: 16px;
    font-weight: 600;
    border-radius: 12px;
  }
}

// 动画
@keyframes twinkle {
  0%, 100% { opacity: 0.3; transform: scale(1); }
  50% { opacity: 1; transform: scale(1.2); }
}

@keyframes float-orb {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(20px, -20px); }
}

@keyframes ring-rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}
</style>
