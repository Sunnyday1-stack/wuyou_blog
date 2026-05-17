<template>
  <div class="scroll-container" @scroll="handleScroll">
    <!-- Hero 区块 -->
    <section id="hero" class="section hero" ref="sections">
      <video autoplay muted loop playsinline class="hero-video-bg">
        <source src="/hero-bg.mp4" type="video/mp4" />
      </video>
      <div class="glass-overlay"></div>
      <WeatherCard class="weather-card-pos" />
      <div class="content fade-in">
        <h1 class="hero-title">欢迎来到wuyou的博客</h1>
        <p class="hero-subtitle">
          <span
            v-for="(ch, idx) in chars"
            :key="idx"
            class="subtitle-char"
            :class="{ 'char-visible': visibleChars[idx] }"
          >{{ ch }}</span>
        </p>
      </div>
    </section>

    <!-- 精选文章区块 -->
    <section id="featured" class="section featured" ref="sections">
      <div class="content fade-in two-columns">
        <div class="main-content">
          <CategoryFilter @select="handleCategorySelect" />
          <FeaturedArticles :categoryId="selectedCategoryId" />
        </div>
        <aside class="sidebar">
          <TagCloud @select="handleTagSelect" />
        </aside>
      </div>
    </section>

    <!-- 关于我区块 -->
    <section id="about" class="section about" ref="sections">
      <div class="glass-card fade-up">
        <h2 class="about-title">
          <span v-for="(ch, idx) in aboutTitleChars" :key="idx" class="title-char" :class="{ 'char-visible': aboutTitleVisible[idx] }">{{ ch }}</span>
        </h2>
        <p class="about-text">
          <span v-for="(ch, idx) in aboutTextChars" :key="idx" class="text-char" :class="{ 'char-visible': aboutTextVisible[idx] }">{{ ch }}</span>
        </p>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import FeaturedArticles from '@/components/FeaturedArticles.vue'
import CategoryFilter from '@/components/CategoryFilter.vue'
import TagCloud from '@/components/TagCloud.vue'
import WeatherCard from '@/components/WeatherCard.vue'

const router = useRouter()
const route = useRoute()
const selectedCategoryId = ref(null)
const sections = ref([])

const sectionsIds = ['hero', 'featured', 'about']
let scrollTimeout = null
let observer = null

// ========== Hero 打字机动画 ==========
const fullText = '岁岁无忧，万事皆顺'
const chars = fullText.split('')
const visibleChars = ref(Array(chars.length).fill(false))
let typingTimer = null
let typingStarted = false

const startTyping = () => {
  if (typingStarted) return
  typingStarted = true
  let index = 0
  typingTimer = setInterval(() => {
    if (index < chars.length) {
      visibleChars.value[index] = true
      index++
    } else {
      clearInterval(typingTimer)
      typingTimer = null
    }
  }, 100)
}

const stopTyping = () => {
  if (typingTimer) {
    clearInterval(typingTimer)
    typingTimer = null
  }
}

// ========== About 打字机动画 ==========
const aboutTitle = '关于我'
const aboutText = '我始终相信，冰冷的代码里藏着温柔的力量，想用它为世界，点亮一束属于我的阳光。'
const aboutTitleChars = aboutTitle.split('')
const aboutTextChars = aboutText.split('')
const aboutTitleVisible = ref(Array(aboutTitleChars.length).fill(false))
const aboutTextVisible = ref(Array(aboutTextChars.length).fill(false))
let aboutTypingStarted = false
let aboutTitleTimer = null
let aboutTextTimer = null

const startAboutTyping = () => {
  if (aboutTypingStarted) return
  aboutTypingStarted = true
  let titleIndex = 0
  aboutTitleTimer = setInterval(() => {
    if (titleIndex < aboutTitleChars.length) {
      aboutTitleVisible.value[titleIndex] = true
      titleIndex++
    } else {
      clearInterval(aboutTitleTimer)
      aboutTitleTimer = null
      let textIndex = 0
      aboutTextTimer = setInterval(() => {
        if (textIndex < aboutTextChars.length) {
          aboutTextVisible.value[textIndex] = true
          textIndex++
        } else {
          clearInterval(aboutTextTimer)
          aboutTextTimer = null
        }
      }, 50)
    }
  }, 80)
}

// ========== 业务逻辑 ==========
const handleCategorySelect = (id) => {
  selectedCategoryId.value = id
}

const handleTagSelect = (tagId) => {
  router.push({ path: '/search', query: { tag: tagId } })
}

const handleScroll = (e) => {
  if (scrollTimeout) return
  scrollTimeout = setTimeout(() => {
    const container = e.target
    const scrollTop = container.scrollTop
    const sectionHeight = window.innerHeight
    const index = Math.round(scrollTop / sectionHeight)
    const activeId = sectionsIds[index]
    if (activeId && route.hash !== `#${activeId}`) {
      router.replace({ hash: `#${activeId}` })
    }
    scrollTimeout = null
  }, 100)
}

// 滚动渐现动画 + 启动打字机
const observeSections = () => {
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        // 处理 fade-in 元素（hero 和 featured 的内容）
        const fadeElement = entry.target.querySelector('.fade-in')
        if (fadeElement) {
          if (entry.isIntersecting) {
            fadeElement.classList.add('visible')
          } else {
            fadeElement.classList.remove('visible')
          }
        }
        // 处理 fade-up 元素（about 卡片）
        const fadeUpElement = entry.target.querySelector('.fade-up')
        if (fadeUpElement) {
          if (entry.isIntersecting) {
            fadeUpElement.classList.add('visible')
          } else {
            fadeUpElement.classList.remove('visible')
          }
        }
        // 启动 hero 打字机
        if (entry.target.id === 'hero' && entry.isIntersecting && !typingStarted) {
          startTyping()
        }
        // 启动 about 打字机
        if (entry.target.id === 'about' && entry.isIntersecting && !aboutTypingStarted) {
          startAboutTyping()
        }
      })
    },
    { threshold: 0.5 }
  )
  sections.value.forEach((section) => {
    if (section) observer.observe(section)
  })
}

onMounted(() => {
  sections.value = document.querySelectorAll('.section')
  observeSections()
  const container = document.querySelector('.scroll-container')
  if (route.hash) {
    const targetId = route.hash.slice(1)
    const targetIndex = sectionsIds.indexOf(targetId)
    if (targetIndex !== -1 && container) {
      container.scrollTo({ top: targetIndex * window.innerHeight, behavior: 'smooth' })
    }
  }
})

onUnmounted(() => {
  if (observer) observer.disconnect()
  window.removeEventListener('resize', () => {})
  stopTyping()
  if (aboutTitleTimer) clearInterval(aboutTitleTimer)
  if (aboutTextTimer) clearInterval(aboutTextTimer)
})
</script>

<style scoped>
/* 基础滚动容器 */
.scroll-container {
  height: 100vh;
  overflow-y: scroll;
  scroll-snap-type: y mandatory;
  scroll-behavior: smooth;
}
.scroll-container::-webkit-scrollbar {
  width: 6px;
  background: transparent;
}
.scroll-container::-webkit-scrollbar-track {
  background: rgba(0, 0, 0, 0.1);
  border-radius: 3px;
}
.scroll-container::-webkit-scrollbar-thumb {
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  width: 6px;
  height: 6px;
}
.scroll-container::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 0, 0, 0.6);
}

.section {
  scroll-snap-align: start;
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 2rem;
  position: relative;
}

/* Hero 区块视频背景 */
.hero {
  position: relative;
  overflow: hidden;
}
.hero-video-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 0;
}
.glass-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.2);
  z-index: 1;
  pointer-events: none;
}
.hero .content,
.hero .weather-card-pos {
  position: relative;
  z-index: 2;
}
.hero-title {
  font-family: 'Segoe UI', 'Poppins', system-ui, -apple-system, sans-serif;
  font-weight: 800;
  font-size: 3rem;
  color: rgba(255, 255, 255, 0.85);
  text-shadow: 0 0 6px rgba(255, 105, 180, 0.5), 0 0 12px rgba(255, 105, 180, 0.3);
  letter-spacing: -0.02em;
  margin-bottom: 1rem;
  line-height: 1.2;
}
.hero-subtitle {
  font-family: 'Segoe UI', 'Poppins', system-ui, -apple-system, sans-serif;
  font-weight: 400;
  font-size: 1.5rem;
  color: white;
  text-shadow: 0 0 4px rgba(255, 255, 255, 0.5);
  letter-spacing: 0.02em;
  display: inline-block;
  white-space: pre-wrap;
}
.subtitle-char {
  opacity: 0;
  transition: opacity 0.2s ease-in-out;
  display: inline-block;
  margin-right: 0.02em;
}
.subtitle-char.char-visible {
  opacity: 1;
}
.weather-card-pos {
  position: absolute;
  top: 100px;
  left: 20px;
  right: auto;
  z-index: 20;
}

/* Featured 区块 - 宫崎骏风格背景 + 毛玻璃元素 */
.featured {
  background: url('/featured-bg.jpg') center top / cover no-repeat fixed;
  position: relative;
}
.featured::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.2);
  z-index: 1;
  pointer-events: none;
}
.featured .content {
  position: relative;
  z-index: 2;
}

/* 两列布局 */
.two-columns {
  display: flex;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  width: 100%;
}
.main-content {
  flex: 2;
}
.sidebar {
  flex: 1;
}
@media (max-width: 768px) {
  .two-columns {
    flex-direction: column;
    gap: 1rem;
  }
}

/* 分类标签毛玻璃效果 */
:deep(.category-filter) {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 2rem;
}
:deep(.category-filter .btn-category) {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(8px);
  border-radius: 40px;
  color: white;
  font-weight: 500;
  padding: 0.6rem 1.2rem;
  transition: all 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
}
:deep(.category-filter .btn-category.active) {
  background: rgba(255, 255, 255, 0.35);
  border-color: #f5c542;
  color: #f5c542;
  box-shadow: 0 0 8px rgba(245, 197, 66, 0.5);
}
:deep(.category-filter .btn-category:hover) {
  transform: translateY(-2px);
  background: rgba(255, 255, 255, 0.3);
}

/* 文章卡片毛玻璃效果 */
:deep(.articles-grid) {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
}
@media (max-width: 992px) {
  :deep(.articles-grid) {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 768px) {
  :deep(.articles-grid) {
    grid-template-columns: 1fr;
  }
}
:deep(.articles-grid .card) {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  padding: 5px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.3);
}
:deep(.articles-grid .card:hover) {
  transform: translateY(-8px);
  box-shadow: 0 16px 30px rgba(0, 0, 0, 0.2);
}
:deep(.articles-grid .card-info) {
  background: transparent;
  color: white;
  padding: 1.5rem;
  text-align: center;
}
:deep(.articles-grid .article-title) {
  color: white;
  font-size: 1.3rem;
  font-weight: bold;
  margin-bottom: 0.75rem;
  text-shadow: 0 2px 6px rgba(245, 197, 66, 0.4);
}
:deep(.articles-grid .article-summary) {
  color: rgba(255, 255, 255, 0.85);
  font-size: 0.95rem;
  line-height: 1.5;
  margin-bottom: 0.75rem;
}
:deep(.articles-grid .article-date) {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.85rem;
}
/* 移除卡片原有的渐变伪元素，避免冲突 */
:deep(.articles-grid .card::before),
:deep(.articles-grid .card::after) {
  display: none;
}

/* 热门标签毛玻璃效果 */
:deep(.sidebar .tag-cloud) {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(12px);
  border-radius: 24px;
  padding: 1.2rem;
  border: 1px solid rgba(255, 255, 255, 0.3);
}
:deep(.sidebar .tag-cloud h3) {
  color: white;
  font-size: 1.2rem;
  margin-bottom: 1rem;
  text-shadow: 0 1px 2px rgba(0,0,0,0.2);
}
:deep(.sidebar .tag-cloud .tags) {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
}
:deep(.sidebar .tag-cloud .tag) {
  background: rgba(255, 255, 255, 0.2);
  backdrop-filter: blur(4px);
  color: white;
  padding: 0.4rem 1rem;
  border-radius: 30px;
  font-size: 0.85rem;
  transition: all 0.2s;
  border: 1px solid rgba(255, 255, 255, 0.3);
}
:deep(.sidebar .tag-cloud .tag:hover) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
}

/* 淡入上浮动画 */
.featured :deep(.category-filter) {
  animation: fadeUp 0.6s ease-out forwards;
  opacity: 0;
  transform: translateY(20px);
}
.featured :deep(.articles-grid .card) {
  opacity: 0;
  transform: translateY(20px);
  animation: fadeUp 0.6s ease-out forwards;
}
.featured :deep(.articles-grid .card:nth-child(1)) { animation-delay: 0.1s; }
.featured :deep(.articles-grid .card:nth-child(2)) { animation-delay: 0.2s; }
.featured :deep(.articles-grid .card:nth-child(3)) { animation-delay: 0.3s; }
.featured :deep(.articles-grid .card:nth-child(4)) { animation-delay: 0.4s; }
.featured :deep(.articles-grid .card:nth-child(5)) { animation-delay: 0.5s; }
.featured :deep(.articles-grid .card:nth-child(6)) { animation-delay: 0.6s; }
.featured :deep(.sidebar) {
  animation: fadeUp 0.6s ease-out 0.3s forwards;
  opacity: 0;
  transform: translateY(20px);
}
@keyframes fadeUp {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* About 区块 - 固定背景 + 毛玻璃卡片 + 粒子光斑 */
.about {
  background: url('/about-bg.jpg') fixed center center / cover no-repeat;
  position: relative;
  overflow: hidden;
}
.about::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
  background: radial-gradient(circle at 20% 40%, rgba(255, 230, 150, 0.2) 0%, transparent 50%),
              radial-gradient(circle at 80% 70%, rgba(150, 210, 180, 0.2) 0%, transparent 50%),
              repeating-radial-gradient(circle at 50% 50%, rgba(255, 200, 100, 0.1) 0px, rgba(255, 200, 100, 0) 2px);
  animation: floatParticles 8s infinite alternate;
}
@keyframes floatParticles {
  0% { background-position: 0% 0%; }
  100% { background-position: 5% 5%; }
}
.glass-card {
  background: rgba(255, 248, 225, 0.1);
  backdrop-filter: blur(6px);
  border-radius: 32px;
  padding: 2.5rem 3rem;
  max-width: 700px;
  width: 90%;
  margin: 0 auto;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  text-align: center;
  transition: transform 0.5s cubic-bezier(0.2, 0.9, 0.4, 1.1), opacity 0.8s ease;
}
.fade-up {
  opacity: 0;
  transform: translateY(40px);
}
.fade-up.visible {
  opacity: 1;
  transform: translateY(0);
}
.about-title {
  font-size: 2.5rem;
  font-weight: 600;
  color: #fef9e6;
  text-shadow: 0 2px 6px rgba(0,0,0,0.3);
  margin-bottom: 1.5rem;
  letter-spacing: 2px;
}
.about-text {
  font-size: 1.2rem;
  line-height: 1.8;
  color: #fff8e7;
  text-shadow: 0 1px 3px rgba(0,0,0,0.2);
  white-space: pre-wrap;
}
.title-char, .text-char {
  opacity: 0;
  transition: opacity 0.1s ease;
}
.title-char.char-visible, .text-char.char-visible {
  opacity: 1;
}

/* 通用动画 */
.fade-in {
  opacity: 0;
  transform: translateY(20px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}
.fade-in.visible {
  opacity: 1;
  transform: translateY(0);
}

/* 响应式 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  .hero-subtitle {
    font-size: 1.2rem;
  }
  .weather-card-pos {
    top: 120px;
    left: 10px;
    transform: scale(0.85);
    transform-origin: top left;
  }
  .glass-card {
    padding: 1.5rem;
    width: 85%;
  }
  .about-title {
    font-size: 1.8rem;
  }
  .about-text {
    font-size: 1rem;
  }
  :deep(.category-filter .btn-category) {
    padding: 0.4rem 0.8rem;
    font-size: 0.85rem;
  }
  :deep(.articles-grid .card-info) {
    padding: 1rem;
  }
  :deep(.articles-grid .article-title) {
    font-size: 1.1rem;
  }
  :deep(.sidebar .tag-cloud) {
    padding: 1rem;
  }
}
</style>