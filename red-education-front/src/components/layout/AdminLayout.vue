<template>
  <div class="admin-layout">
    <el-container>
      <!-- 侧边栏 -->
      <el-aside :width="appStore.sidebarOpened ? '240px' : '72px'" class="admin-aside">
        <div class="logo-container">
          <div class="logo-icon">🏛️</div>
          <span v-if="appStore.sidebarOpened" class="logo-text">管理后台</span>
        </div>
        
        <el-menu
          :default-active="$route.path"
          :collapse="!appStore.sidebarOpened"
          :unique-opened="true"
          router
          class="admin-menu"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <span>数据概览</span>
          </el-menu-item>
          
          <el-sub-menu index="users" v-if="userStore.isAdmin">
            <template #title>
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </template>
            <el-menu-item index="/admin/users">用户列表</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="resources">
            <template #title>
              <el-icon><Document /></el-icon>
              <span>资源管理</span>
            </template>
            <el-menu-item index="/admin/resources">资源列表</el-menu-item>
            <el-menu-item index="/admin/resources/upload">上传资源</el-menu-item>
            <el-menu-item index="/admin/resources/audit">资源审核</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="courses">
            <template #title>
              <el-icon><Reading /></el-icon>
              <span>课程管理</span>
            </template>
            <el-menu-item index="/admin/courses">课程列表</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="exam" v-if="userStore.isAdmin">
            <template #title>
              <el-icon><EditPen /></el-icon>
              <span>考试管理</span>
            </template>
            <el-menu-item index="/admin/questions">题库管理</el-menu-item>
            <el-menu-item index="/admin/exams">试卷管理</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="community">
            <template #title>
              <el-icon><ChatDotRound /></el-icon>
              <span>社区管理</span>
            </template>
            <el-menu-item index="/admin/posts/audit">内容审核</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="points">
            <template #title>
              <el-icon><ShoppingCart /></el-icon>
              <span>积分管理</span>
            </template>
            <el-menu-item index="/admin/products">商品管理</el-menu-item>
            <el-menu-item index="/admin/exchange">兑换管理</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="system" v-if="userStore.isAdmin">
            <template #title>
              <el-icon><Setting /></el-icon>
              <span>系统设置</span>
            </template>
            <el-menu-item index="/admin/config">系统配置</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </el-aside>

      <el-container>
        <!-- 顶部栏 -->
        <el-header class="admin-header">
          <div class="header-left">
            <el-icon class="toggle-btn" @click="appStore.toggleSidebar">
              <Fold v-if="appStore.sidebarOpened" />
              <Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{ currentPageTitle }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          
          <div class="header-right">
            <el-button type="primary" plain @click="$router.push('/')">
              <el-icon><House /></el-icon>
              返回前台
            </el-button>
            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="36" class="user-avatar">
                  {{ userStore.userInfo?.nickname?.charAt(0) }}
                </el-avatar>
                <span class="user-name">{{ userStore.userInfo?.nickname }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">
                    <el-icon><User /></el-icon> 个人信息
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>

        <!-- 主内容 -->
        <el-main class="admin-main">
          <slot />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { useAppStore } from '@/stores/app'
import {
  DataAnalysis, User, Document, Reading, EditPen, ChatDotRound, ShoppingCart,
  Setting, Fold, Expand, House, ArrowDown, SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const appStore = useAppStore()

const pageMap: Record<string, string> = {
  '/admin/dashboard': '数据概览',
  '/admin/users': '用户列表',
  '/admin/resources': '资源列表',
  '/admin/resources/upload': '上传资源',
  '/admin/resources/audit': '资源审核',
  '/admin/courses': '课程列表',
  '/admin/questions': '题库管理',
  '/admin/exams': '试卷管理',
  '/admin/posts/audit': '内容审核',
  '/admin/products': '商品管理',
  '/admin/exchange': '兑换管理',
  '/admin/config': '系统配置'
}

const currentPageTitle = computed(() => pageMap[route.path] || '管理')

const handleCommand = (command: string) => {
  if (command === 'logout') { userStore.logout(); router.push('/') }
  else if (command === 'profile') { router.push('/user/profile') }
}
</script>

<style scoped lang="scss">
.admin-layout {
  height: 100vh;
  background: #f0f2f5;

  .el-container { height: 100%; }
}

// 侧边栏
.admin-aside {
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  transition: width var(--transition-base);
  overflow: hidden;

  .logo-container {
    height: 64px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    background: rgba(0, 0, 0, 0.2);
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);

    .logo-icon { font-size: 28px; }
    .logo-text { font-size: 18px; font-weight: 700; color: white; white-space: nowrap; }
  }

  .admin-menu {
    border: none;
    background: transparent;

    // 主菜单项和子菜单标题
    :deep(.el-menu-item),
    :deep(.el-sub-menu__title) {
      color: rgba(255, 255, 255, 0.85) !important;
      border-radius: 8px;
      margin: 4px 8px;
      height: 44px;
      line-height: 44px;

      .el-icon { color: rgba(255, 255, 255, 0.85); }

      &:hover {
        background: rgba(255, 255, 255, 0.15);
        color: white !important;
      }
    }

    // 子菜单内的菜单项
    :deep(.el-sub-menu .el-menu .el-menu-item) {
      color: rgba(255, 255, 255, 0.8) !important;
      background: rgba(0, 0, 0, 0.15);
      margin: 2px 12px;
      padding-left: 52px !important;
      font-size: 13px;

      &:hover {
        background: rgba(255, 255, 255, 0.1);
        color: white !important;
      }
    }

    // 激活状态
    :deep(.el-menu-item.is-active) {
      background: linear-gradient(90deg, #D64541 0%, #FF6B6B 100%) !important;
      color: white !important;
    }

    :deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
      color: white !important;
      background: rgba(255, 255, 255, 0.08);
    }

    // 子菜单背景
    :deep(.el-sub-menu .el-menu) {
      background: transparent !important;
    }

    :deep(.el-menu--collapse) {
      .el-sub-menu__title { padding: 0 20px; }
    }
  }
}

// 顶部栏
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  border-bottom: 1px solid #e5e7eb;
  padding: 0 24px;
  height: 64px;

  .header-left {
    display: flex;
    align-items: center;
    gap: 20px;

    .toggle-btn {
      font-size: 20px;
      cursor: pointer;
      padding: 8px;
      border-radius: 8px;
      transition: all var(--transition-fast);

      &:hover { background: #f3f4f6; }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 16px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 6px 12px;
      border-radius: 8px;
      cursor: pointer;
      transition: all var(--transition-fast);

      &:hover { background: #f3f4f6; }

      .user-avatar {
        background: linear-gradient(135deg, #D64541 0%, #FF6B6B 100%);
        color: white;
        font-weight: 600;
      }

      .user-name { font-weight: 500; color: #374151; }
    }
  }
}

// 主内容区
.admin-main {
  background: #f0f2f5;
  padding: 24px;
  overflow-y: auto;
}
</style>
