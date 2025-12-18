<template>
  <div class="register-page">
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

    <!-- 注册卡片 -->
    <div class="register-card-wrapper">
      <div class="register-card glass-card">
        <!-- Logo和标题 -->
        <div class="card-header">
          <div class="logo-container">
            <div class="logo-icon pulse-glow">
              <el-icon :size="36"><UserFilled /></el-icon>
            </div>
          </div>
          <h1 class="card-title">加入我们</h1>
          <p class="card-subtitle">开启红色教育学习之旅</p>
        </div>

        <!-- 注册表单 -->
        <el-form
          ref="registerFormRef"
          :model="registerForm"
          :rules="registerRules"
          class="register-form"
        >
          <el-form-item prop="username">
            <el-input
              v-model="registerForm.username"
              placeholder="用户名（3-20个字符）"
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
              v-model="registerForm.password"
              type="password"
              placeholder="密码（6-20个字符）"
              size="large"
              show-password
              class="glass-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              size="large"
              show-password
              class="glass-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="邮箱"
              size="large"
              class="glass-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="phone">
            <el-input
              v-model="registerForm.phone"
              placeholder="手机号（可选）"
              size="large"
              class="glass-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="agree" class="agree-item">
            <el-checkbox v-model="registerForm.agree" class="agree-checkbox">
              我已阅读并同意
              <el-link type="primary" :underline="false">《用户协议》</el-link>
              和
              <el-link type="primary" :underline="false">《隐私政策》</el-link>
            </el-checkbox>
          </el-form-item>

          <el-form-item>
            <button type="button" class="register-btn" :disabled="loading" @click="handleRegister">
              <span class="btn-text">{{ loading ? '注册中...' : '注 册' }}</span>
              <div class="btn-glow"></div>
            </button>
          </el-form-item>

          <div class="login-link">
            <span>已有账号？</span>
            <el-link type="primary" :underline="false" @click="$router.push('/login')">
              立即登录
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
        <span class="float-icon" style="--delay: 0s; --x: -130px; --y: -100px">📚</span>
        <span class="float-icon" style="--delay: 1s; --x: 140px; --y: -80px">🎓</span>
        <span class="float-icon" style="--delay: 2s; --x: -110px; --y: 120px">⭐</span>
        <span class="float-icon" style="--delay: 0.5s; --x: 120px; --y: 100px">🏆</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { User, Lock, Message, Phone, UserFilled, ArrowLeft } from '@element-plus/icons-vue'
import { register } from '@/api/user'

const router = useRouter()

const registerFormRef = ref<FormInstance>()
const loading = ref(false)

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

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  email: '',
  phone: '',
  agree: false
})

const validateConfirmPassword = (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateAgree = (_rule: any, value: any, callback: any) => {
  if (!value) {
    callback(new Error('请阅读并同意用户协议'))
  } else {
    callback()
  }
}

const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3 到 20 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  agree: [
    { validator: validateAgree, trigger: 'change' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await register({
          username: registerForm.username,
          password: registerForm.password,
          confirmPassword: registerForm.confirmPassword,
          email: registerForm.email,
          phone: registerForm.phone || undefined
        })

        ElMessage.success('注册成功！请登录')
        router.push('/login')
      } catch (error: any) {
        ElMessage.error(error.message || '注册失败，请稀后重试')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.register-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

// ============================================
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
// 注册卡片
// ============================================
.register-card-wrapper {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  max-height: 100vh;
  overflow-y: auto;
}

.register-card.glass-card {
  width: 420px;
  padding: 40px 36px;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
  
  @media (max-width: 480px) {
    width: calc(100vw - 40px);
    padding: 32px 24px;
  }
}

.card-header {
  text-align: center;
  margin-bottom: 28px;
  
  .logo-container {
    margin-bottom: 16px;
  }
  
  .logo-icon {
    width: 72px;
    height: 72px;
    margin: 0 auto;
    background: linear-gradient(135deg, #00D9FF 0%, #0077B6 100%);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    box-shadow: 0 8px 32px rgba(0, 217, 255, 0.4);
    
    &.pulse-glow {
      animation: pulse-glow-cyan 2s ease-in-out infinite;
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
.register-form {
  .el-form-item {
    margin-bottom: 16px;
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
        border-color: #00D9FF;
        background: rgba(255, 255, 255, 0.15);
        box-shadow: 0 0 0 3px rgba(0, 217, 255, 0.2);
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
  
  .agree-item {
    margin-bottom: 20px;
    
    .agree-checkbox {
      :deep(.el-checkbox__label) {
        color: rgba(255, 255, 255, 0.6);
        font-size: 12px;
      }
      
      :deep(.el-checkbox__inner) {
        background: transparent;
        border-color: rgba(255, 255, 255, 0.3);
      }
      
      :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
        background: #00D9FF;
        border-color: #00D9FF;
      }
      
      .el-link {
        color: #00D9FF;
        font-size: 12px;
      }
    }
  }
  
  .register-btn {
    width: 100%;
    height: 52px;
    position: relative;
    background: linear-gradient(135deg, #00D9FF 0%, #0077B6 100%);
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
      box-shadow: 0 10px 40px rgba(0, 217, 255, 0.4);
      
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
  
  .login-link {
    text-align: center;
    margin-top: 16px;
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
  margin-top: 20px;
  
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

@keyframes pulse-glow-cyan {
  0%, 100% {
    box-shadow: 0 8px 32px rgba(0, 217, 255, 0.4);
  }
  50% {
    box-shadow: 0 8px 48px rgba(0, 217, 255, 0.6);
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
