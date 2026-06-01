<template>
  <nav class="navbar" :class="{ 'navbar-hidden': isHidden, 'scrolled': isScrolled, 'admin-mode': isAdminRoute, 'article-mode': isArticleRoute }">
    <div class="nav-container">
      <!-- 移动端汉堡按钮 -->
      <button class="hamburger" @click="toggleMenu" :aria-label="isMenuOpen ? '关闭菜单' : '打开菜单'">
        <span :class="{ open: isMenuOpen }"></span>
        <span :class="{ open: isMenuOpen }"></span>
        <span :class="{ open: isMenuOpen }"></span>
      </button>

      <ul id="top-nav" class="desktop-nav">
        <li 
          v-for="item in navItems" 
          :key="item.anchor"
          @click="handleNavClick(item.anchor)"
        >
          {{ item.label }}
        </li>
        <li v-if="isAdmin" @click="handleNavClick('dashboard')">仪表盘</li>
        <li v-if="isAdmin" @click="handleNavClick('admin')">管理</li>
      </ul>

      <!-- 唤醒式搜索组件 -->
      <div class="search-wrapper">
        <div class="search-trigger" @click="toggleSearch" v-if="!isSearchExpanded">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="11" cy="11" r="8"></circle>
            <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
          </svg>
        </div>
        <div class="search-container" :class="{ expanded: isSearchExpanded }">
          <input 
            v-model="searchKeyword" 
            class="input" 
            type="text" 
            placeholder="搜索文章..."
            @keyup.enter="doSearch"
            @keyup.escape="closeSearch"
            ref="searchInput"
          />
          <button class="search__icon" @click="doSearch">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="18" height="18">
              <path d="M18.031 16.6168L22.3137 20.8995L20.8995 22.3137L16.6168 18.031C15.0769 19.263 13.124 20 11 20C6.032 20 2 15.968 2 11C2 6.032 6.032 2 11 2C15.968 2 20 6.032 20 11C20 13.124 19.263 15.0769 18.031 16.6168ZM16.0247 15.8748C17.2475 14.6146 18 12.8956 18 11C18 7.1325 14.8675 4 11 4C7.1325 4 4 7.1325 4 11C4 14.8675 7.1325 18 11 18C12.8956 18 14.6146 17.2475 15.8748 16.0247L16.0247 15.8748Z"></path>
            </svg>
          </button>
          <button class="search__close" @click="closeSearch" title="关闭搜索">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
      </div>


      <!-- 移动端唤醒菜单 -->
      <Teleport to="body">
        <div v-if="isMenuOpen" class="mobile-overlay" @click="closeMenu"></div>
        <div v-if="isMenuOpen" class="mobile-menu">
          <div class="mobile-menu-header">
            <span class="mobile-menu-title">WuyouBlog</span>
            <button class="mobile-menu-close" @click="closeMenu">&times;</button>
          </div>
          <ul class="mobile-nav">
            <li v-for="item in navItems" :key="item.anchor" @click="handleNavClick(item.anchor); closeMenu()">
              {{ item.label }}
            </li>
            <li v-if="isAdmin" @click="handleNavClick('dashboard'); closeMenu()">仪表盘</li>
            <li v-if="isAdmin" @click="handleNavClick('admin'); closeMenu()">管理</li>
          </ul>
          <div class="mobile-menu-footer">
            <template v-if="username">
              <span class="mobile-welcome">👤 {{ username }}</span>
              <button class="mobile-nav-btn" @click="logout(); closeMenu()">退出</button>
              <button class="mobile-nav-btn mobile-delete-btn" @click="deleteAccount(); closeMenu()">注销账号</button>
            </template>
            <template v-else>
              <router-link to="/login" class="mobile-nav-btn" @click="closeMenu()">登录</router-link>
              <router-link to="/register" class="mobile-nav-btn" @click="closeMenu()">注册</router-link>
            </template>
          </div>
        </div>
      </Teleport>

      <div class="user-area desktop-user">
        <template v-if="username">
          <span class="welcome">👋 {{ username }}</span>
          <button class="nav-btn logout-btn" @click="logout">退出</button>
          <button class="nav-btn delete-account-btn" @click="deleteAccount">注销账号</button>
        </template>
        <template v-else>
          <router-link to="/login" class="nav-link">登录</router-link>
          <router-link to="/register" class="nav-link">注册</router-link>
        </template>
      </div>
    </div>
  </nav>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const route = useRoute()
const username = ref('')
const searchKeyword = ref('')
const isSearchExpanded = ref(false)
const searchInput = ref(null)
const isHidden = ref(false)
const isScrolled = ref(false)

const isAdmin = ref(false)
const isAdminRoute = computed(() => route.path.startsWith('/admin'))
const isArticleRoute = computed(() => route.name === 'article')

// 移动端唤醒菜单
const isMenuOpen = ref(false)
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value
  document.body.style.overflow = isMenuOpen.value ? 'hidden' : ''
}
const closeMenu = () => {
  isMenuOpen.value = false
  document.body.style.overflow = ''
}

const navItems = [
  { label: '首页', anchor: 'hero' },
  { label: '精选', anchor: 'featured' },
  { label: '关于', anchor: 'about' }
]

// 滚动隐藏/显示相关
let scrollContainer = null
let lastScrollTop = 0
let ticking = false
let scrollThreshold = 5

const handleScroll = () => {
  if (!scrollContainer) return
  const currentScroll = scrollContainer.scrollTop
  const delta = currentScroll - lastScrollTop
  if (Math.abs(delta) > scrollThreshold) {
    if (delta > 0) {
      if (!isHidden.value) isHidden.value = true
    } else {
      if (isHidden.value) isHidden.value = false
    }
    lastScrollTop = currentScroll
  }
  ticking = false
}

const handleBgScroll = () => {
  if (!scrollContainer) return
  isScrolled.value = scrollContainer.scrollTop > 0
}

const onScrollHandler = () => {
  if (!ticking) {
    requestAnimationFrame(handleScroll)
    ticking = true
  }
}

const bindScrollListener = () => {
  scrollContainer = document.querySelector('.scroll-container')
  if (scrollContainer) {
    scrollContainer.addEventListener('scroll', onScrollHandler)
    scrollContainer.addEventListener('scroll', handleBgScroll)
    lastScrollTop = scrollContainer.scrollTop
    handleBgScroll()
  } else {
    isHidden.value = false
  }
}

const unbindScrollListener = () => {
  if (scrollContainer) {
    scrollContainer.removeEventListener('scroll', onScrollHandler)
    scrollContainer.removeEventListener('scroll', handleBgScroll)
  }
}

const toggleSearch = () => {
  isSearchExpanded.value = true
  nextTick(() => {
    if (searchInput.value) {
      searchInput.value.focus()
    }
    setTimeout(() => {
      document.addEventListener("click", handleClickOutside)
    }, 0)
  })
}

const closeSearch = () => {
  isSearchExpanded.value = false
  searchKeyword.value = ""
  document.removeEventListener("click", handleClickOutside)
}

const handleClickOutside = (e) => {
  const wrapper = document.querySelector(".search-wrapper")
  if (wrapper && !wrapper.contains(e.target)) {
    closeSearch()
  }
}

const doSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/search', query: { q: searchKeyword.value } })
    closeSearch()
  }
}

function handleNavClick(anchor) {
  if (anchor === 'admin') {
    router.push('/admin')
    return
  }
  if (anchor === 'dashboard') {
    router.push('/admin/dashboard')
    return
  }
  if (router.currentRoute.value.path !== '/') {
    router.push('/#' + anchor)
    return
  }
  const container = document.querySelector('.scroll-container')
  const sections = ['hero', 'featured', 'about']
  const index = sections.indexOf(anchor)
  if (container && index !== -1) {
    container.scrollTo({ top: index * window.innerHeight, behavior: 'smooth' })
    router.replace({ hash: '#' + anchor })
  }
}

function logout() {
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('userId')
  localStorage.removeItem('role')
  username.value = ''
  isAdmin.value = false
  router.push('/')
}

async function deleteAccount() {
  const password = prompt('请输入密码确认注销账号：\n此操作不可恢复，所有评论将被删除。')
  if (!password) return
  try {
    const token = localStorage.getItem('token')
    const res = await axios.delete('/api/auth/delete', {
      params: { password },
      headers: { Authorization: `Bearer ${token}` }
    })
    if (res.data.success) {
      alert('账号已注销')
      localStorage.clear()
      username.value = ''
      isAdmin.value = false
      router.push('/')
    } else {
      alert(res.data.message || '注销失败')
    }
  } catch (error) {
    console.error('注销失败', error)
    alert(error.response?.data?.message || '注销失败，请检查网络或密码')
  }
}

// 监听路由变化，实时更新用户名（解决登录/退出后需刷新的问题）
watch(() => route.path, () => {
  username.value = localStorage.getItem('username') || ''
  isAdmin.value = localStorage.getItem('role') === 'ADMIN'
}, { immediate: true })

onMounted(() => {
  bindScrollListener()
})

onUnmounted(() => {
  unbindScrollListener()
  document.body.style.overflow = ''
})
</script>

<style scoped>
/* ===== 基础导航栏样式（全透明，白色文字） ===== */
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 80px;
  z-index: 1000;
  transition: all 0.3s ease-in-out;
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: white;
  text-shadow: 0 0 6px rgba(0, 0, 0, 0.7);
}

.navbar.scrolled {
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  -webkit-backdrop-filter: blur(12px);
  color: white;
  text-shadow: 0 0 6px rgba(0, 0, 0, 0.7);
}

.navbar-hidden {
  transform: translateY(-100%);
}

.nav-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

#top-nav li,
.welcome,
.nav-link,
.nav-btn,
.search-trigger svg,
.search__icon path {
  transition: color 0.2s, fill 0.2s;
}

.navbar.scrolled #top-nav li,
.navbar.scrolled .welcome,
.navbar.scrolled .nav-link,
.navbar.scrolled .nav-btn,
.navbar.scrolled .search__icon path {
  color: #1a202c;
  fill: #1a202c;
}

.navbar.scrolled .search-trigger {
  background: rgba(255, 255, 255, 0.9);
}
.navbar .search-trigger {
  background: rgba(0, 0, 0, 0.3);
}

#top-nav {
  display: flex;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 0.5rem;
}

li {
  cursor: pointer;
  padding: 0.5rem 1rem;
  font-weight: 500;
  border-radius: 40px;
  transition: all 0.2s ease;
  font-size: 0.95rem;
}

li:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-1px);
}

.search-wrapper {
  position: relative;
  display: flex;
  justify-content: center;
  flex: 1;
  margin: 0 1rem;
}

.search-trigger {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.search-trigger:hover {
  transform: scale(1.05);
}

.search-container {
  display: flex;
  align-items: center;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 50px;
  padding: 2px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s cubic-bezier(0.2, 0.9, 0.4, 1.1);
  opacity: 0;
  transform: scale(0.9);
  pointer-events: none;
  width: 0;
  overflow: hidden;
}

.search-container.expanded {
  opacity: 1;
  transform: scale(1);
  pointer-events: auto;
  width: 240px;
}

.input {
  padding: 8px 12px;
  width: 100%;
  background: transparent;
  border: none;
  font-size: 14px;
  outline: none;
  color: #333;
}

.search__icon {
  width: 34px;
  aspect-ratio: 1;
  background: none;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.2s;
}
.search__icon:hover {
  background: rgba(0,0,0,0.05);
}

.user-area {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
  flex-shrink: 0;
}

.welcome {
  font-size: 0.9rem;
  background: rgba(0,0,0,0.3);
  padding: 0.4rem 0.8rem;
  border-radius: 30px;
  white-space: nowrap;
}
.navbar.scrolled .welcome {
  background: rgba(0,0,0,0.05);
}

.nav-link {
  text-decoration: none;
  font-weight: 500;
  padding: 0.4rem 0.8rem;
  border-radius: 30px;
  transition: all 0.2s;
  background: rgba(0,0,0,0.3);
  white-space: nowrap;
}
.navbar.scrolled .nav-link {
  background: rgba(0,0,0,0.05);
}
.nav-link:hover {
  background: rgba(0,0,0,0.2);
  transform: translateY(-1px);
}

.nav-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.4rem 0.8rem;
  border-radius: 30px;
  font-weight: 500;
  transition: all 0.2s;
  white-space: nowrap;
  font-size: 0.85rem;
}
.logout-btn {
  color: inherit;
  background: rgba(0,0,0,0.3);
}
.logout-btn:hover {
  background: rgba(0,0,0,0.2);
  transform: translateY(-1px);
}
.delete-account-btn {
  color: #e53e3e;
  background: rgba(0,0,0,0.3);
}
.delete-account-btn:hover {
  background: rgba(0,0,0,0.2);
  transform: translateY(-1px);
}

/* 文章页 - 星空深色背景 */
.navbar.article-mode {
  background: rgba(10, 10, 30, 0.75);
  backdrop-filter: blur(12px);
  color: #e0e0f0;
  box-shadow: 0 2px 20px rgba(0,0,0,0.3);
}
.navbar.article-mode #top-nav li,
.navbar.article-mode .welcome,
.navbar.article-mode .nav-link,
.navbar.article-mode .nav-btn,
.navbar.article-mode .search-trigger svg,
.navbar.article-mode .search__icon path {
  color: #e0e0f0;
  fill: #e0e0f0;
}
.navbar.article-mode .search-trigger {
  background: rgba(255,255,255,0.1);
}
.navbar.article-mode .nav-link,
.navbar.article-mode .welcome,
.navbar.article-mode .logout-btn,
.navbar.article-mode .delete-account-btn {
  background: rgba(255,255,255,0.08);
}
.navbar.article-mode .nav-link:hover,
.navbar.article-mode .logout-btn:hover,
.navbar.article-mode .delete-account-btn:hover {
  background: rgba(255,255,255,0.16);
}
.navbar.article-mode li:hover {
  background: rgba(255, 255, 255, 0.12);
}
.navbar.article-mode .search__close {
  color: rgba(255,255,255,0.5);
}
.navbar.article-mode .search__close:hover {
  background: rgba(255,255,255,0.1);
  color: #fff;
}

.navbar.admin-mode {
  background: #ea9679;
  backdrop-filter: none;
  color: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}
.navbar.admin-mode .nav-link,
.navbar.admin-mode .welcome,
.navbar.admin-mode .logout-btn,
.navbar.admin-mode .delete-account-btn,
.navbar.admin-mode .search-trigger svg,
.navbar.admin-mode .search__icon path {
  color: white;
  fill: white;
}
.navbar.admin-mode .search-trigger {
  background: rgba(255,255,255,0.2);
}
.navbar.admin-mode .nav-link,
.navbar.admin-mode .welcome,
.navbar.admin-mode .logout-btn,
.navbar.admin-mode .delete-account-btn {
  background: rgba(255,255,255,0.1);
}
.navbar.admin-mode .nav-link:hover,
.navbar.admin-mode .logout-btn:hover,
.navbar.admin-mode .delete-account-btn:hover {
  background: rgba(255,255,255,0.2);
}
.navbar.admin-mode .search__close {
  color: rgba(255,255,255,0.6);
}
.navbar.admin-mode .search__close:hover {
  background: rgba(255,255,255,0.2);
  color: #fff;
}

/* ========== 汉堡按钮（桌面端隐藏） ========== */
.hamburger {
  display: none;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 36px;
  height: 36px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  gap: 5px;
  z-index: 1001;
  flex-shrink: 0;
  margin-right: 0.5rem;
}

.hamburger span {
  display: block;
  width: 22px;
  height: 2px;
  background: white;
  border-radius: 2px;
  transition: all 0.3s ease;
  transform-origin: center;
}

.hamburger span.open:nth-child(1) {
  transform: translateY(7px) rotate(45deg);
}

.hamburger span.open:nth-child(2) {
  opacity: 0;
}

.hamburger span.open:nth-child(3) {
  transform: translateY(-7px) rotate(-45deg);
}

/* ========== 移动端唤醒菜单 ========== */
.mobile-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  z-index: 998;
  animation: fadeIn 0.2s ease;
}

.mobile-menu {
  position: fixed;
  top: 0;
  left: 0;
  width: 270px;
  max-width: 80vw;
  height: 100%;
  height: 100dvh;
  background: #1a1a2e;
  z-index: 999;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 30px rgba(0,0,0,0.3);
  animation: slideIn 0.25s ease;
}

.mobile-menu-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 1.2rem;
  border-bottom: 1px solid rgba(255,255,255,0.08);
}

.mobile-menu-title {
  font-weight: 700;
  font-size: 1.1rem;
  color: #ea9679;
}

.mobile-menu-close {
  background: none;
  border: none;
  font-size: 1.6rem;
  color: rgba(255,255,255,0.5);
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.mobile-menu-close:hover {
  color: #fff;
}

.mobile-nav {
  list-style: none;
  padding: 0.5rem 0;
  margin: 0;
  flex: 1;
  overflow-y: auto;
}

.mobile-nav li {
  padding: 0.85rem 1.5rem;
  font-size: 1rem;
  font-weight: 500;
  color: rgba(255,255,255,0.85);
  cursor: pointer;
  transition: all 0.15s;
  border-left: 3px solid transparent;
}

.mobile-nav li:hover,
.mobile-nav li:active {
  background: rgba(234, 150, 121, 0.12);
  border-left-color: #ea9679;
  color: #fff;
}

.mobile-menu-footer {
  padding: 1rem 1.2rem;
  border-top: 1px solid rgba(255,255,255,0.08);
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.mobile-welcome {
  width: 100%;
  font-size: 0.85rem;
  color: rgba(255,255,255,0.55);
  margin-bottom: 0.2rem;
}

.mobile-nav-btn {
  padding: 0.4rem 0.9rem;
  border-radius: 20px;
  font-size: 0.82rem;
  font-weight: 500;
  background: rgba(255,255,255,0.08);
  color: rgba(255,255,255,0.8);
  border: none;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.15s;
}

.mobile-nav-btn:hover {
  background: rgba(255,255,255,0.16);
  color: #fff;
}

.mobile-delete-btn {
  color: #ff8888;
  background: rgba(255,80,80,0.12);
}

.mobile-delete-btn:hover {
  background: rgba(255,80,80,0.22);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideIn {
  from { transform: translateX(-100%); }
  to { transform: translateX(0); }
}

@media (max-width: 768px) {
  .navbar {
    height: 50px;
    padding: 0;
  }
  .nav-container {
    padding: 0 0.8rem;
    flex-wrap: nowrap;
    gap: 0;
    justify-content: flex-start;
  }

  /* 显示汉堡，隐藏桌面导航和用户区 */
  .hamburger {
    display: flex;
  }
  .desktop-nav {
    display: none !important;
  }
  .desktop-user {
    display: none !important;
  }

  /* 搜索靠右 */
  .search-wrapper {
    margin-left: auto;
    margin-right: 0;
  }
  .search-container.expanded {
    width: 130px;
  }
  .input {
    font-size: 0.8rem;
    padding: 5px 8px;
  }
  .search-trigger {
    width: 30px;
    height: 30px;
  }
}

@media (max-width: 480px) {
  .navbar {
    height: 48px;
  }
  .nav-container {
    padding: 0 0.5rem;
  }
  .search-container.expanded {
    width: 110px;
  }
  .search-trigger {
    width: 28px;
    height: 28px;
  }
}
</style>
