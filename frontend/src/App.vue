<template>
  <div>
    <NavBar />
    <router-view />
  </div>
</template>

<script setup>
import NavBar from './components/NavBar.vue'
</script>
<style>
/* 全局重置样式：消除所有默认边距和填充 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
  background: #beecec;
  -webkit-tap-highlight-color: transparent;
  -webkit-font-smoothing: antialiased;
}

#app {
  width: 100%;
  min-height: 100vh;
}

/* 移动端安全区域适配 */
@media (max-width: 768px) {
  img, video {
    max-width: 100%;
    height: auto;
  }

  /* 修复 iOS Safari 背景 fixed 问题 */
  .featured, .about {
    background-attachment: scroll !important;
  }
}
</style>