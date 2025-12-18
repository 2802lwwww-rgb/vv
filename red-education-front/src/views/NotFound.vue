<template>
  <div class="not-found-page">
    <div class="content">
      <div class="error-code">
        <span class="digit">4</span>
        <div class="icon-wrapper">
          <el-icon class="floating"><Search /></el-icon>
        </div>
        <span class="digit">4</span>
      </div>
      <h1 class="title">页面未找到</h1>
      <p class="desc">抱歉，您访问的页面不存在或已被移动</p>
      <div class="actions">
        <el-button type="primary" size="large" @click="$router.push('/')">
          <el-icon><HomeFilled /></el-icon>
          返回首页
        </el-button>
        <el-button size="large" @click="$router.back()">
          <el-icon><Back /></el-icon>
          返回上页
        </el-button>
      </div>
    </div>
    <div class="decoration">
      <div class="circle c1"></div>
      <div class="circle c2"></div>
      <div class="circle c3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Search, HomeFilled, Back } from '@element-plus/icons-vue'
</script>

<style scoped lang="scss">
.not-found-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  position: relative;
  overflow: hidden;

  .content {
    text-align: center;
    z-index: 10;
    color: white;
  }

  .error-code {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 16px;
    margin-bottom: 24px;

    .digit {
      font-size: 140px;
      font-weight: 800;
      background: linear-gradient(135deg, #D64541 0%, #FF6B6B 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      line-height: 1;

      @media (max-width: 576px) { font-size: 80px; }
    }

    .icon-wrapper {
      .floating {
        font-size: 100px;
        color: rgba(255, 255, 255, 0.3);
        animation: float 3s ease-in-out infinite;

        @media (max-width: 576px) { font-size: 60px; }
      }
    }
  }

  .title {
    font-size: var(--font-size-2xl);
    font-weight: var(--font-weight-bold);
    margin-bottom: 12px;
  }

  .desc {
    font-size: var(--font-size-base);
    color: rgba(255, 255, 255, 0.6);
    margin-bottom: 32px;
  }

  .actions {
    display: flex;
    gap: 16px;
    justify-content: center;

    @media (max-width: 576px) { flex-direction: column; }
  }

  .decoration {
    position: absolute;
    inset: 0;
    pointer-events: none;

    .circle {
      position: absolute;
      border-radius: 50%;
      background: linear-gradient(135deg, rgba(214, 69, 65, 0.1) 0%, rgba(139, 92, 246, 0.1) 100%);

      &.c1 { width: 400px; height: 400px; top: -100px; left: -100px; }
      &.c2 { width: 300px; height: 300px; bottom: -50px; right: -50px; }
      &.c3 { width: 200px; height: 200px; top: 50%; left: 10%; opacity: 0.5; }
    }
  }
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}
</style>
