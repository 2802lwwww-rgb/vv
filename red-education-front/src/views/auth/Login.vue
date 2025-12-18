<template>
  <div class="login-page">
    <!-- 动态粒子背景 -->
    <div class="particle-bg">
      <div class="stars">
        <div v-for="i in 50" :key="'star-'+i" class="star" :style="getStarStyle(i)"></div>
      </div>
      <div class="shooting-stars">
        <div v-for="i in 3" :key="'shoot-'+i" class="shooting-star" :style="{ animationDelay: `${i * 3}s` }"></div>
      </div>
      <div class="gradient-orbs">
        <div class="orb orb-1"></div>
        <div class="orb orb-2"></div>
        <div class="orb orb-3"></div>
      </div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card-wrapper">
      <div class="login-card glass-card">
        <!-- Logo和标题 -->
        <div class="card-header">
          <div class="logo-container">
            <div class="logo-icon pulse-glow">
              <el-icon :size="36"><Flag /></el-icon>
            </div>
          </div>
          <h1 class="card-title">红色教育平台</h1>
          <p class="card-subtitle">传承红色基因，弘扬革命精神</p>
        </div>

        <!-- 登录表单 -->
        <el-form
          ref="loginFormRef"
          :model="loginForm"
          :rules="loginRules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="usernameOrEmail">
            <el-input
              v-model="loginForm.usernameOrEmail"
              placeholder="用户名或邮箱"
              size="large"
              class="glass-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              size="large"
              show-password
              class="glass-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <div class="form-options">
            <el-checkbox v-model="rememberMe" class="remember-checkbox">记住我</el-checkbox>
            <el-link type="primary" :underline="false" @click="$router.push('/forgot-password')">
              忘记密码？
            </el-link>
          </div>

          <el-form-item>
            <button type="button" class="login-btn" :disabled="loading" @click="handleLogin">
              <span class="btn-text">{{ loading ? '登录中...' : '登 录' }}</span>
              <div class="btn-glow"></div>
            </button>
          </el-form-item>

          <div class="register-link">
            <span>还没有账号？</span>
            <el-link type="primary" :underline="false" @click="$router.push('/register')">
              立即注册
            </el-link>
          </div>
        </el-form>

        <!-- 返回首页 -->
        <div class="back-home">
          <el-link :underline="false" @click="$router.push('/')">
            <el-icon><ArrowLeft /></el-icon>
            返回首页
          </el-link>
        </div>
      </div>

      <!-- 装饰元素 -->
      <div class="floating-icons">
        <span class="float-icon" style="--delay: 0s; --x: -120px; --y: -80px">📚</span>
        <span class="float-icon" style="--delay: 1s; --x: 130px; --y: -60px">🎓</span>
        <span class="float-icon" style="--delay: 2s; --x: -100px; --y: 100px">⭐</span>
        <span class="float-icon" style="--delay: 0.5s; --x: 110px; --y: 90px">🏆</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock, Flag, ArrowLeft } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { login, getUserInfo } from '@/api/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref<FormInstance>()
const loading = ref(false)
const rememberMe = ref(false)

// 星星样式生成
const getStarStyle = (index: number) => {
  return {
    left: `${Math.random() * 100}%`,
    top: `${Math.random() * 100}%`,
    width: `${1 + Math.random() * 2}px`,
    height: `${1 + Math.random() * 2}px`,
    animationDelay: `${index * 0.1}s`,
    animationDuration: `${2 + Math.random() * 3}s`
  }
}

const loginForm = reactive({
  usernameOrEmail: '',
  password: ''
})

const loginRules: FormRules = {
  usernameOrEmail: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' },
    { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const res = await login(loginForm)
        userStore.setToken(res.data.token)
        
        const userRes = await getUserInfo()
        userStore.setUserInfo(userRes.data)
        
        if (rememberMe.value) {
          localStorage.setItem('rememberedUsername', loginForm.usernameOrEmail)
        } else {
          localStorage.removeItem('rememberedUsername')
        }

        ElMessage.success('登录成功！')
        
        const redirect = router.currentRoute.value.query.redirect as string
        router.push(redirect || '/')
      } catch (error: any) {
        ElMessage.error(error.message || '登录失败，请检查用户名和密码')
      } finally {
        loading.value = false
      }
    }
  })
}

// 自动填充记住的用户名
const rememberedUsername = localStorage.getItem('rememberedUsername')
if (rememberedUsername) {
  loginForm.usernameOrEmail = rememberedUsername
  rememberMe.value = true
}
</script>

<style scoped lang="scss">
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

// ============================================
// 粒子背景 - 红色主题
// ============================================
.particle-bg {
  position: absolute;
  inset: 0;
  background: linear-gradient(135deg, #1a0a0a 0%, #2d1515 50%, #3d1a1a 100%);
  z-index: 0;
}

.stars {
  position: absolute;
  inset: 0;
  
  .star {
    position: absolute;
    background: rgba(255, 200, 150, 0.8);
    border-radius: 50%;
    animation: twinkle 3s ease-in-out infinite;
  }
}

.shooting-stars {
  position: absolute;
  inset: 0;
  overflow: hidden;
  
  .shooting-star {
    position: absolute;
    top: 20%;
    left: 0;
    width: 100px;
    height: 2px;
    background: linear-gradient(90deg, rgba(255, 200, 150, 0.9) 0%, transparent 100%);
    animation: shoot 6s ease-in-out infinite;
    opacity: 0;
  }
}

.gradient-orbs {
  position: absolute;
  inset: 0;
  
  .orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
    animation: float-orb 8s ease-in-out infinite;
    
    &.orb-1 {
      width: 400px;
      height: 400px;
      background: rgba(220, 38, 38, 0.35);
      top: -100px;
      right: -100px;
    }
    
    &.orb-2 {
      width: 300px;
      height: 300px;
      background: rgba(234, 88, 12, 0.3);
      bottom: -50px;
      left: -50px;
      animation-delay: -3s;
    }
    
    &.orb-3 {
      width: 250px;
      height: 250px;
      background: rgba(251, 191, 36, 0.25);
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      animation-delay: -5s;
    }
  }
}

// ============================================
// 登录卡片
// ============================================
.login-card-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card.glass-card {
  width: 420px;
  padding: 48px 40px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
  
  @media (max-width: 480px) {
    width: calc(100vw - 40px);
    padding: 32px 24px;
    margin: 20px;
  }
}

.card-header {
  text-align: center;
  margin-bottom: 36px;
  
  .logo-container {
    margin-bottom: 20px;
  }
  
  .logo-icon {
    width: 72px;
    height: 72px;
    margin: 0 auto;
    background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: 0 8px 32px rgba(220, 38, 38, 0.4);
    
    &.pulse-glow {
      animation: pulse-glow 2s ease-in-out infinite;
    }
  }
  
  .card-title {
    font-size: 28px;
    font-weight: 700;
    color: #fff;
    margin: 0 0 8px 0;
  }
  
  .card-subtitle {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.6);
    margin: 0;
  }
}

// ============================================
// 表单样式
// ============================================
.login-form {
  .el-form-item {
    margin-bottom: 20px;
  }
  
  .glass-input {
    :deep(.el-input__wrapper) {
      background: rgba(255, 255, 255, 0.08);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 14px;
      padding: 6px 16px;
      box-shadow: none;
      transition: all 0.3s ease;
      
      &:hover {
        border-color: rgba(255, 255, 255, 0.3);
        background: rgba(255, 255, 255, 0.12);
      }
      
      &.is-focus {
        border-color: #dc2626;
        background: rgba(255, 255, 255, 0.15);
        box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.2);
      }
    }
    
    :deep(.el-input__inner) {
      color: #fff;
      
      &::placeholder {
        color: rgba(255, 255, 255, 0.4);
      }
    }
    
    .input-icon {
      color: rgba(255, 255, 255, 0.5);
    }
  }
  
  .form-options {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    
    .remember-checkbox {
      :deep(.el-checkbox__label) {
        color: rgba(255, 255, 255, 0.6);
      }
      
      :deep(.el-checkbox__inner) {
        background: transparent;
        border-color: rgba(255, 255, 255, 0.3);
      }
      
      :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
        background: #dc2626;
        border-color: #dc2626;
      }
    }
    
    .el-link {
      color: rgba(255, 255, 255, 0.6);
      
      &:hover {
        color: #fcd34d;
      }
    }
  }
  
  .login-btn {
    width: 100%;
    height: 52px;
    position: relative;
    background: linear-gradient(135deg, #dc2626 0%, #991b1b 100%);
    border: none;
    border-radius: 14px;
    color: white;
    font-size: 16px;
    font-weight: 600;
    cursor: pointer;
    overflow: hidden;
    transition: all 0.3s ease;
    
    &:hover:not(:disabled) {
      transform: translateY(-2px);
      box-shadow: 0 10px 40px rgba(220, 38, 38, 0.4);
      
      .btn-glow {
        opacity: 1;
      }
    }
    
    &:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
    
    .btn-text {
      position: relative;
      z-index: 1;
    }
    
    .btn-glow {
      position: absolute;
      inset: -2px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.2) 0%, transparent 50%);
      border-radius: inherit;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
  }
  
  .register-link {
    text-align: center;
    margin-top: 20px;
    font-size: 14px;
    color: rgba(255, 255, 255, 0.6);
    
    .el-link {
      color: #fcd34d;
      font-weight: 500;
    }
  }
}

.back-home {
  text-align: center;
  margin-top: 28px;
  
  .el-link {
    color: rgba(255, 255, 255, 0.5);
    font-size: 13px;
    
    &:hover {
      color: rgba(255, 255, 255, 0.8);
    }
  }
}

// ============================================
// 浮动装饰
// ============================================
.floating-icons {
  position: absolute;
  inset: 0;
  pointer-events: none;
  
  .float-icon {
    position: absolute;
    top: 50%;
    left: 50%;
    font-size: 28px;
    opacity: 0.6;
    transform: translate(var(--x), var(--y));
    animation: float-icon 4s ease-in-out infinite;
    animation-delay: var(--delay);
  }
}

// ============================================
// 动画
// ============================================
@keyframes twinkle {
  0%, 100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.2);
  }
}

@keyframes shoot {
  0% {
    transform: translateX(-100px) translateY(0);
    opacity: 0;
  }
  5% {
    opacity: 1;
  }
  20% {
    transform: translateX(100vw) translateY(100px);
    opacity: 0;
  }
  100% {
    opacity: 0;
  }
}

@keyframes float-orb {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.05);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.95);
  }
}

@keyframes pulse-glow {
  0%, 100% {
    box-shadow: 0 8px 32px rgba(220, 38, 38, 0.4);
  }
  50% {
    box-shadow: 0 8px 48px rgba(220, 38, 38, 0.6);
  }
}

@keyframes float-icon {
  0%, 100% {
    transform: translate(var(--x), var(--y)) translateY(0);
  }
  50% {
    transform: translate(var(--x), var(--y)) translateY(-15px);
  }
}
</style>
