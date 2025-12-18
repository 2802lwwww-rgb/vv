<template>
  <div class="forgot-page">
    <!-- 左侧装饰区域 -->
    <div class="forgot-banner">
      <div class="banner-content">
        <div class="banner-decoration">
          <div class="deco-circle deco-1"></div>
          <div class="deco-circle deco-2"></div>
          <div class="deco-circle deco-3"></div>
        </div>
        <div class="banner-text">
          <div class="banner-icon">
            <el-icon :size="64"><Key /></el-icon>
          </div>
          <h1>重置密码</h1>
          <p>我们将帮助您找回账号</p>
        </div>
      </div>
    </div>

    <!-- 右侧表单区域 -->
    <div class="forgot-form-wrapper">
      <div class="forgot-box">
        <div class="forgot-header">
          <h2>找回密码</h2>
          <p>通过邮箱验证重置您的密码</p>
        </div>

        <!-- 步骤指示器 -->
        <div class="steps-container">
          <div class="step" :class="{ active: currentStep >= 0, completed: currentStep > 0 }">
            <div class="step-number">1</div>
            <span class="step-label">验证邮箱</span>
          </div>
          <div class="step-line" :class="{ active: currentStep >= 1 }"></div>
          <div class="step" :class="{ active: currentStep >= 1, completed: currentStep > 1 }">
            <div class="step-number">2</div>
            <span class="step-label">重置密码</span>
          </div>
          <div class="step-line" :class="{ active: currentStep >= 2 }"></div>
          <div class="step" :class="{ active: currentStep >= 2 }">
            <div class="step-number">3</div>
            <span class="step-label">完成</span>
          </div>
        </div>

        <!-- 步骤1: 发送验证码 -->
        <el-form
          v-if="currentStep === 0"
          ref="emailFormRef"
          :model="emailForm"
          :rules="emailRules"
          class="step-form"
        >
          <el-form-item prop="email">
            <el-input
              v-model="emailForm.email"
              placeholder="请输入注册邮箱"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="submit-button"
              @click="sendVerificationCode"
            >
              <span v-if="!loading">发送验证码</span>
              <span v-else>发送中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 步骤2: 重置密码 -->
        <el-form
          v-if="currentStep === 1"
          ref="resetFormRef"
          :model="resetForm"
          :rules="resetRules"
          class="step-form"
        >
          <el-form-item prop="code">
            <el-input
              v-model="resetForm.code"
              placeholder="请输入验证码"
              size="large"
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Key /></el-icon>
              </template>
              <template #append>
                <el-button :disabled="countdown > 0" @click="resendCode">
                  {{ countdown > 0 ? `${countdown}s` : '重发' }}
                </el-button>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="newPassword">
            <el-input
              v-model="resetForm.newPassword"
              type="password"
              placeholder="请输入新密码（6-20个字符）"
              size="large"
              show-password
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item prop="confirmPassword">
            <el-input
              v-model="resetForm.confirmPassword"
              type="password"
              placeholder="请再次输入新密码"
              size="large"
              show-password
              class="custom-input"
            >
              <template #prefix>
                <el-icon class="input-icon"><Lock /></el-icon>
              </template>
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="submit-button"
              @click="handleResetPassword"
            >
              <span v-if="!loading">重置密码</span>
              <span v-else>处理中...</span>
            </el-button>
          </el-form-item>
        </el-form>

        <!-- 步骤3: 完成 -->
        <div v-if="currentStep === 2" class="success-content">
          <div class="success-icon">
            <el-icon :size="64" color="#22C55E"><CircleCheck /></el-icon>
          </div>
          <h3>密码重置成功！</h3>
          <p>您的密码已成功重置，请使用新密码登录</p>
          <el-button type="primary" size="large" class="success-button" @click="$router.push('/login')">
            返回登录
          </el-button>
        </div>

        <div class="back-home" v-if="currentStep !== 2">
          <el-link :underline="false" @click="$router.push('/login')">
            <el-icon><ArrowLeft /></el-icon>
            返回登录
          </el-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { Message, Key, Lock, CircleCheck, ArrowLeft } from '@element-plus/icons-vue'
import { sendResetCode, resetPassword } from '@/api/user'

const router = useRouter()

const emailFormRef = ref<FormInstance>()
const resetFormRef = ref<FormInstance>()
const loading = ref(false)
const currentStep = ref(0)
const countdown = ref(0)

const emailForm = reactive({
  email: ''
})

const resetForm = reactive({
  code: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (_rule: any, value: any, callback: any) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== resetForm.newPassword) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const emailRules: FormRules = {
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ]
}

const resetRules: FormRules = {
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { len: 6, message: '验证码长度为6位', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const startCountdown = () => {
  countdown.value = 60
  const timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) {
      clearInterval(timer)
    }
  }, 1000)
}

const sendVerificationCode = async () => {
  if (!emailFormRef.value) return

  await emailFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await sendResetCode({ email: emailForm.email })
        
        ElMessage.success('验证码已发送，请查收邮箱')
        currentStep.value = 1
        startCountdown()
      } catch (error: any) {
        ElMessage.error(error.message || '发送验证码失败')
      } finally {
        loading.value = false
      }
    }
  })
}

const resendCode = async () => {
  loading.value = true
  try {
    await sendResetCode({ email: emailForm.email })
    ElMessage.success('验证码已重新发送')
    startCountdown()
  } catch (error: any) {
    ElMessage.error(error.message || '发送验证码失败')
  } finally {
    loading.value = false
  }
}

const handleResetPassword = async () => {
  if (!resetFormRef.value) return

  await resetFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await resetPassword({
          email: emailForm.email,
          code: resetForm.code,
          newPassword: resetForm.newPassword
        })

        ElMessage.success('密码重置成功')
        currentStep.value = 2
      } catch (error: any) {
        ElMessage.error(error.message || '重置密码失败')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped lang="scss">
.forgot-page {
  display: flex;
  min-height: 100vh;
  background: var(--color-bg-secondary);

  @media (max-width: 992px) {
    flex-direction: column;
  }
}

// ============================================
// 左侧装饰区域
// ============================================
.forgot-banner {
  flex: 1;
  background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  padding: 40px;

  @media (max-width: 992px) {
    min-height: 200px;
    padding: 40px 20px;
  }

  .banner-content {
    position: relative;
    z-index: 1;
    text-align: center;
    color: var(--color-secondary);
  }

  .banner-decoration {
    position: absolute;
    inset: 0;
    z-index: 0;

    .deco-circle {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.2);

      &.deco-1 {
        width: 280px;
        height: 280px;
        top: -40px;
        right: -40px;
        animation: float 6s ease-in-out infinite;
      }

      &.deco-2 {
        width: 180px;
        height: 180px;
        bottom: 15%;
        left: -20px;
        animation: float 8s ease-in-out infinite reverse;
      }

      &.deco-3 {
        width: 120px;
        height: 120px;
        top: 45%;
        left: 25%;
        background: rgba(44, 62, 80, 0.1);
        animation: float 5s ease-in-out infinite;
      }
    }
  }

  .banner-text {
    position: relative;
    z-index: 1;

    .banner-icon {
      margin-bottom: 20px;
      opacity: 0.9;
    }

    h1 {
      font-size: var(--font-size-4xl);
      font-weight: var(--font-weight-bold);
      margin-bottom: 12px;

      @media (max-width: 992px) {
        font-size: var(--font-size-2xl);
      }
    }

    p {
      font-size: var(--font-size-lg);
      opacity: 0.85;

      @media (max-width: 992px) {
        font-size: var(--font-size-base);
      }
    }
  }
}

// ============================================
// 右侧表单区域
// ============================================
.forgot-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
  background: var(--color-bg-primary);

  @media (max-width: 576px) {
    padding: 24px;
  }
}

.forgot-box {
  width: 100%;
  max-width: 420px;

  .forgot-header {
    text-align: center;
    margin-bottom: 32px;

    h2 {
      font-size: var(--font-size-2xl);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
      margin-bottom: 8px;
    }

    p {
      font-size: var(--font-size-sm);
      color: var(--color-text-muted);
    }
  }

  // 步骤指示器
  .steps-container {
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 40px;

    .step {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 8px;

      .step-number {
        width: 32px;
        height: 32px;
        border-radius: 50%;
        background: var(--color-border);
        color: var(--color-text-muted);
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: var(--font-weight-bold);
        font-size: var(--font-size-sm);
        transition: all var(--transition-base);
      }

      .step-label {
        font-size: var(--font-size-xs);
        color: var(--color-text-muted);
      }

      &.active .step-number {
        background: var(--color-warning);
        color: white;
      }

      &.completed .step-number {
        background: var(--color-success);
        color: white;
      }
    }

    .step-line {
      width: 40px;
      height: 2px;
      background: var(--color-border);
      margin: 0 8px;
      margin-bottom: 20px;
      transition: background var(--transition-base);

      &.active {
        background: var(--color-warning);
      }
    }
  }

  .step-form {
    .el-form-item {
      margin-bottom: 20px;
    }

    .custom-input {
      :deep(.el-input__wrapper) {
        border-radius: var(--radius-lg);
        padding: 4px 16px;
        box-shadow: none;
        border: 1px solid var(--color-border);
        transition: all var(--transition-fast);

        &:hover {
          border-color: var(--color-warning);
        }

        &.is-focus {
          border-color: var(--color-warning);
          box-shadow: 0 0 0 3px rgba(245, 158, 11, 0.1);
        }
      }

      .input-icon {
        color: var(--color-text-muted);
      }
    }

    .submit-button {
      width: 100%;
      height: 48px;
      font-size: var(--font-size-base);
      font-weight: var(--font-weight-medium);
      background: linear-gradient(135deg, #FFD93D 0%, #F59E0B 100%);
      border: none;
      border-radius: var(--radius-lg);
      color: var(--color-secondary);
      transition: all var(--transition-base);

      &:hover {
        transform: translateY(-2px);
        box-shadow: var(--shadow-glow-gold);
      }

      &:active {
        transform: translateY(0);
      }
    }
  }

  .success-content {
    text-align: center;
    padding: 40px 0;

    .success-icon {
      margin-bottom: 24px;
    }

    h3 {
      font-size: var(--font-size-xl);
      font-weight: var(--font-weight-bold);
      color: var(--color-text-primary);
      margin-bottom: 8px;
    }

    p {
      font-size: var(--font-size-sm);
      color: var(--color-text-muted);
      margin-bottom: 32px;
    }

    .success-button {
      min-width: 160px;
      height: 48px;
      background: var(--color-primary-gradient);
      border: none;
      border-radius: var(--radius-lg);

      &:hover {
        box-shadow: var(--shadow-glow-red);
      }
    }
  }

  .back-home {
    text-align: center;
    margin-top: 32px;

    .el-link {
      color: var(--color-text-muted);
      font-size: var(--font-size-sm);

      &:hover {
        color: var(--color-warning);
      }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}
</style>
