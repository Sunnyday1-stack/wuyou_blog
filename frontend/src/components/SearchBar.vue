<template>
  <div class="search-bar">
    <div class="brutalist-container">
      <input 
        v-model="keyword" 
        type="text" 
        placeholder="搜索文章..."
        class="brutalist-input"
        @keyup.enter="doSearch"
      />
    </div>
    <button class="btn" @click="doSearch">搜索</button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const keyword = ref('')
const router = useRouter()

const doSearch = () => {
  if (keyword.value.trim()) {
    router.push({ path: '/search', query: { q: keyword.value } })
  }
}
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 0.5rem;
  margin: 1rem 0;
  align-items: center;
}

/* Brutalist 完整样式 */
.brutalist-container {
  position: relative;
  width: 250px;
  font-family: monospace;
  flex: 1;
}

.brutalist-input {
  width: 100%;
  padding: 15px;
  font-size: 18px;
  font-weight: bold;
  color: #000;
  background-color: #fff;
  border: 4px solid #000;
  position: relative;
  overflow: hidden;
  border-radius: 0;
  outline: none;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  box-shadow: 5px 5px 0 #000, 10px 10px 0 #4a90e2;
  box-sizing: border-box;
}

@keyframes glitch {
  0% { transform: translate(0); }
  20% { transform: translate(-2px, 2px); }
  40% { transform: translate(-2px, -2px); }
  60% { transform: translate(2px, 2px); }
  80% { transform: translate(2px, -2px); }
  100% { transform: translate(0); }
}

.brutalist-input:focus {
  animation: focus-pulse 4s cubic-bezier(0.25, 0.8, 0.25, 1) infinite,
             glitch 0.3s cubic-bezier(0.25, 0.8, 0.25, 1) infinite;
}

.brutalist-input:focus::after {
  content: "";
  position: absolute;
  top: -2px;
  left: -2px;
  right: -2px;
  bottom: -2px;
  background: white;
  z-index: -1;
}

.brutalist-input:focus::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: black;
  z-index: -2;
  clip-path: inset(0 100% 0 0);
  animation: glitch-slice 4s steps(2, end) infinite;
}

@keyframes glitch-slice {
  0% { clip-path: inset(0 100% 0 0); }
  10% { clip-path: inset(0 5% 0 0); }
  20% { clip-path: inset(0 80% 0 0); }
  30% { clip-path: inset(0 10% 0 0); }
  40% { clip-path: inset(0 50% 0 0); }
  50% { clip-path: inset(0 30% 0 0); }
  60% { clip-path: inset(0 70% 0 0); }
  70% { clip-path: inset(0 15% 0 0); }
  80% { clip-path: inset(0 90% 0 0); }
  90% { clip-path: inset(0 5% 0 0); }
  100% { clip-path: inset(0 100% 0 0); }
}

.brutalist-input::placeholder {
  color: #888;
  transition: color 0.3s ease;
}

.brutalist-input:focus::placeholder {
  color: transparent;
}

@keyframes focus-pulse {
  0%, 100% { border-color: #000; }
  50% { border-color: #4a90e2; }
}

/* 按钮样式（保持之前半透明青绿色） */
.btn {
  background-color: rgba(177, 231, 229, 0.4);
  padding: 10px 24px;
  color: #2c3e50;
  text-transform: uppercase;
  letter-spacing: 2px;
  cursor: pointer;
  border-radius: 10px;
  border: 2px dashed rgba(177, 231, 229, 0.8);
  box-shadow: rgba(50, 50, 93, 0.25) 0px 2px 5px -1px, rgba(0, 0, 0, 0.3) 0px 1px 3px -1px;
  transition: 0.4s;
  font-weight: bold;
}

.btn:hover {
  background-color: #fff;
  color: rgba(177, 231, 229, 0.9);
  border: 2px dashed rgba(177, 231, 229, 0.9);
  transform: translateY(-2px);
}

.btn:active {
  background-color: rgba(177, 231, 229, 0.6);
  transform: translateY(0);
}
</style>