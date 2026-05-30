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
  background: #beecec; /* 防止水平滚动条出现 */
}

#app {
  width: 100%;
  min-height: 100vh;
}
</style>