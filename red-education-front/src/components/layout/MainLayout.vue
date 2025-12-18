<template>
  <div class="app-container">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header class="app-header" :class="{ scrolled: isScrolled }">
        <div class="header-content">
          <div class="logo" @click="$router.push('/')">
            <img src="@/assets/logo.png" alt="logo" />
            <span class="logo-text">{{ appTitle }}</span>
          </div>
          
          <nav class="main-nav">
            <div 
              v-for="item in navItems" 
              :key="item.path"
              class="nav-item"
              :class="{ active: isActive(item.path) }"
              @click="handleNavClick(item.path)"
            >
              {{ item.label }}
              <span class="nav-underline"></span>
            </div>
          </nav>

          <div class="user-actions">
            <template v-if="userStore.isLoggedIn">
              <el-dropdown @command="handleCommand" trigger="click">
                <div class="user-dropdown">
                  <el-avatar :size="36" :src="avatarUrl" class="user-avatar">
                    {{ userStore.userInfo?.nickname?.charAt(0) || 'U' }}
                  </el-avatar>
                  <span class="user-name">{{ userStore.userInfo?.nickname }}</span>
                  <el-icon class="dropdown-arrow"><ArrowDown /></el-icon>
                </div>
                <template #dropdown>
                  <el-dropdown-menu class="user-menu">
                    <el-dropdown-item command="profile">
                      <el-icon><User /></el-icon>
                      个人中心
                    </el-dropdown-item>
                    <el-dropdown-item command="my-posts">
                      <el-icon><Document /></el-icon>
                      我的帖子
                    </el-dropdown-item>
                    <el-dropdown-item command="my-resources">
                      <el-icon><Folder /></el-icon>
                      我的资源
                    </el-dropdown-item>
                    <el-dropdown-item command="study">
                      <el-icon><Reading /></el-icon>
                      学习记录
                    </el-dropdown-item>
                    <el-dropdown-item command="wrong-questions">
                      <el-icon><EditPen /></el-icon>
                      错题本
                    </el-dropdown-item>
                    <el-dropdown-item command="points">
                      <el-icon><Trophy /></el-icon>
                      我的积分
                    </el-dropdown-item>
                    <el-dropdown-item command="exchange-records">
                      <el-icon><ShoppingCart /></el-icon>
                      兑换记录
                    </el-dropdown-item>
                    <el-dropdown-item v-if="userStore.isContentAdmin" divided command="admin">
                      <el-icon><Setting /></el-icon>
                      管理后台
                    </el-dropdown-item>
                    <el-dropdown-item divided command="logout" class="logout-item">
                      <el-icon><SwitchButton /></el-icon>
                      退出登录
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
            <template v-else>
              <el-button class="btn-login" @click="$router.push('/login')">登录</el-button>
              <el-button class="btn-register" @click="$router.push('/register')">注册</el-button>
            </template>
          </div>

          <!-- 移动端菜单按钮 -->
          <div class="mobile-menu-btn" @click="mobileMenuOpen = !mobileMenuOpen">
            <el-icon :size="24"><Menu /></el-icon>
          </div>
        </div>
      </el-header>

      <!-- 移动端菜单 -->
      <transition name="slide-down">
        <div v-if="mobileMenuOpen" class="mobile-menu">
          <div 
            v-for="item in navItems" 
            :key="item.path"
            class="mobile-nav-item"
            :class="{ active: isActive(item.path) }"
            @click="handleMobileNavClick(item.path)"
          >
            {{ item.label }}
          </div>
        </div>
      </transition>

      <!-- 主内容区域 -->
      <el-main class="app-main">
        <slot />
      </el-main>

      <!-- 底部 -->
      <el-footer class="app-footer">
        <div class="footer-content">
          <div class="footer-grid">
            <div class="footer-section">
              <h4 class="footer-title">关于我们</h4>
              <p class="footer-desc">
                红色教育平台致力于传承红色基因，弘扬革命精神，提供系统化的红色教育资源和在线学习服务。
              </p>
            </div>
            <div class="footer-section">
              <h4 class="footer-title">快速链接</h4>
              <div class="footer-links">
                <a @click="$router.push('/resources')">红色资源</a>
                <a @click="$router.push('/courses')">在线课程</a>
                <a @click="$router.push('/exam')">在线考试</a>
                <a @click="$router.push('/community')">互动社区</a>
              </div>
            </div>
            <div class="footer-section">
              <h4 class="footer-title">联系方式</h4>
              <div class="footer-contact">
                <p><el-icon><Message /></el-icon> 1987910699@qq.com</p>
                <p><el-icon><Phone /></el-icon> 159-7695-9817</p>
              </div>
            </div>
          </div>
          <div class="footer-bottom">
            <p>© 2025 红色教育平台 | 传承红色基因 · 弘扬革命精神</p>
          </div>
        </div>
      </el-footer>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { 
  ArrowDown, 
  User, 
  Document, 
  Folder,
  Reading, 
  EditPen,
  Trophy, 
  ShoppingCart,
  Setting, 
  SwitchButton,
  Menu,
  Message,
  Phone
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const appTitle = import.meta.env.VITE_APP_TITLE || '红色教育平台'
const isScrolled = ref(false)
const mobileMenuOpen = ref(false)

// 导航项
const navItems = [
  { label: '首页', path: '/' },
  { label: '红色资源', path: '/resources' },
  { label: '在线课程', path: '/courses' },
  { label: '在线考试', path: '/exam' },
  { label: '互动社区', path: '/community' },
  { label: '积分商城', path: '/points' }
]

// 处理头像URL
const avatarUrl = computed(() => {
  const avatar = userStore.userInfo?.avatar
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return '/api' + avatar
})

// 判断是否激活
const isActive = (path: string) => {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
}

// 导航点击
const handleNavClick = (path: string) => {
  router.push(path)
}

const handleMobileNavClick = (path: string) => {
  router.push(path)
  mobileMenuOpen.value = false
}

// 用户菜单命令
const handleCommand = (command: string) => {
  switch (command) {
    case 'profile':
      router.push('/user/profile')
      break
    case 'study':
      router.push('/user/study')
      break
    case 'wrong-questions':
      router.push('/exam/wrong-questions')
      break
    case 'points':
      router.push('/user/points')
      break
    case 'my-posts':
      router.push('/user/my-posts')
      break
    case 'my-resources':
      router.push('/user/my-resources')
      break
    case 'exchange-records':
      router.push('/user/exchange-records')
      break
    case 'admin':
      router.push('/admin')
      break
    case 'logout':
      userStore.logout()
      router.push('/')
      break
  }
}

// 滚动监听
const handleScroll = () => {
  isScrolled.value = window.scrollY > 20
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped lang="scss">
.app-container {
  min-height: 100vh;
  
  .el-container {
    min-height: 100vh;
    flex-direction: column;
  }
}

// ============================================
// 导航栏
// ============================================
.app-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
  height: var(--header-height);
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid transparent;
  transition: all var(--transition-base);
  padding: 0;

  &.scrolled {
    background: rgba(255, 255, 255, 0.98);
    border-bottom-color: var(--color-border);
    box-shadow: var(--shadow-sm);
  }

  .header-content {
    max-width: 1400px;
    margin: 0 auto;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 24px;
  }

  .logo {
    display: flex;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    flex-shrink: 0;

    img {
      height: 40px;
      width: auto;
    }

    .logo-text {
      font-size: var(--font-size-lg);
      font-weight: var(--font-weight-bold);
      background: var(--color-primary-gradient);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }
  }

  .main-nav {
    display: flex;
    align-items: center;
    gap: 8px;

    @media (max-width: 992px) {
      display: none;
    }

    .nav-item {
      position: relative;
      padding: 8px 16px;
      font-size: var(--font-size-base);
      color: var(--color-text-secondary);
      cursor: pointer;
      transition: color var(--transition-fast);

      &:hover {
        color: var(--color-primary);
      }

      &.active {
        color: var(--color-primary);
        font-weight: var(--font-weight-medium);

        .nav-underline {
          width: 100%;
        }
      }

      .nav-underline {
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 0;
        height: 2px;
        background: var(--color-primary-gradient);
        border-radius: var(--radius-full);
        transition: width var(--transition-base);
      }

      &:hover .nav-underline {
        width: 60%;
      }
    }
  }

  .user-actions {
    display: flex;
    align-items: center;
    gap: 12px;

    @media (max-width: 576px) {
      display: none;
    }

    .btn-login {
      background: transparent;
      border: 1px solid var(--color-border);
      color: var(--color-text-primary);
      padding: 8px 20px;
      border-radius: var(--radius-lg);
      transition: all var(--transition-fast);

      &:hover {
        border-color: var(--color-primary);
        color: var(--color-primary);
      }
    }

    .btn-register {
      background: var(--color-primary-gradient);
      border: none;
      color: white;
      padding: 8px 20px;
      border-radius: var(--radius-lg);
      transition: all var(--transition-fast);

      &:hover {
        box-shadow: var(--shadow-glow-red);
        transform: translateY(-1px);
      }
    }

    .user-dropdown {
      display: flex;
      align-items: center;
      gap: 8px;
      cursor: pointer;
      padding: 4px;
      border-radius: var(--radius-lg);
      transition: background var(--transition-fast);

      &:hover {
        background: var(--color-bg-tertiary);
      }

      .user-avatar {
        background: var(--color-primary-gradient);
        color: white;
        font-weight: var(--font-weight-medium);
      }

      .user-name {
        font-size: var(--font-size-sm);
        color: var(--color-text-primary);
        max-width: 100px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }

      .dropdown-arrow {
        font-size: 12px;
        color: var(--color-text-muted);
        transition: transform var(--transition-fast);
      }
    }
  }

  .mobile-menu-btn {
    display: none;
    cursor: pointer;
    padding: 8px;
    color: var(--color-text-primary);

    @media (max-width: 992px) {
      display: block;
    }
  }
}

// 用户菜单样式
:deep(.user-menu) {
  .el-dropdown-menu__item {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 10px 16px;
  }

  .logout-item {
    color: var(--color-error);
  }
}

// 移动端菜单
.mobile-menu {
  position: fixed;
  top: var(--header-height);
  left: 0;
  right: 0;
  background: white;
  border-bottom: 1px solid var(--color-border);
  box-shadow: var(--shadow-lg);
  z-index: 999;

  .mobile-nav-item {
    padding: 16px 24px;
    font-size: var(--font-size-base);
    color: var(--color-text-secondary);
    border-bottom: 1px solid var(--color-border-light);
    cursor: pointer;
    transition: all var(--transition-fast);

    &:hover, &.active {
      color: var(--color-primary);
      background: rgba(214, 69, 65, 0.05);
    }
  }
}

.slide-down-enter-active,
.slide-down-leave-active {
  transition: all 0.3s ease;
}

.slide-down-enter-from,
.slide-down-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// ============================================
// 主内容
// ============================================
.app-main {
  flex: 1;
  padding: 0;
  margin-top: var(--header-height);
  background: var(--color-bg-secondary);
  overflow-x: hidden;
}

// ============================================
// 页脚
// ============================================
.app-footer {
  height: auto;
  background: linear-gradient(180deg, var(--color-secondary) 0%, var(--color-secondary-dark) 100%);
  color: white;
  padding: 0;

  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 48px 24px 24px;
  }

  .footer-grid {
    display: grid;
    grid-template-columns: 2fr 1fr 1fr;
    gap: 48px;
    margin-bottom: 40px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 32px;
    }
  }

  .footer-section {
    .footer-title {
      font-size: var(--font-size-base);
      font-weight: var(--font-weight-bold);
      margin-bottom: 16px;
      color: white;
    }

    .footer-desc {
      font-size: var(--font-size-sm);
      color: rgba(255, 255, 255, 0.7);
      line-height: var(--line-height-relaxed);
    }

    .footer-links {
      display: flex;
      flex-direction: column;
      gap: 12px;

      a {
        font-size: var(--font-size-sm);
        color: rgba(255, 255, 255, 0.7);
        cursor: pointer;
        transition: color var(--transition-fast);

        &:hover {
          color: var(--color-accent);
        }
      }
    }

    .footer-contact {
      p {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: var(--font-size-sm);
        color: rgba(255, 255, 255, 0.7);
        margin-bottom: 12px;
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: 24px;
    border-top: 1px solid rgba(255, 255, 255, 0.1);

    p {
      font-size: var(--font-size-sm);
      color: rgba(255, 255, 255, 0.5);
    }
  }
}
</style>
