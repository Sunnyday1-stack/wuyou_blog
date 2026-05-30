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
      <div class="about-card fade-up">
        <div class="about-header slide-in">
          <div class="avatar-wrapper">
            <img src="/avatar.jpg" alt="avatar" class="avatar-img" />
          </div>
          <h2 class="about-name">
            <span v-for="(ch, idx) in aboutTitleChars" :key="idx" class="title-char" :class="{ 'char-visible': aboutTitleVisible[idx] }">{{ ch }}</span>
          </h2>
          <p class="about-tagline">全栈开发者  /  终身学习者  /  生活记录者</p>
        </div>

        <div class="about-bio slide-in" style="--order:1">
          <p>
            <span v-for="(ch, idx) in aboutTextChars" :key="idx" class="text-char" :class="{ 'char-visible': aboutTextVisible[idx] }">{{ ch }}</span>
          </p>
        </div>

        <div class="about-resume">
          <div class="resume-col">
            <div class="resume-block slide-in" style="--order:2">
              <h3 class="block-title"><span class="block-icon">🎓</span> 教育背景</h3>
              <div class="edu-item">
                <div class="edu-dot"></div>
                <div class="edu-body">
                  <strong>西华师范大学</strong>
                  <span>计算机科学与技术 · 本科</span>
                  <span class="edu-time">2024 - 2028</span>
                </div>
              </div>
            </div>

            <div class="resume-block slide-in" style="--order:3">
              <h3 class="block-title"><span class="block-icon">💼</span> 项目经历</h3>
              <div class="project-item">
                <h4>个人博客系统</h4>
                <p>基于 Spring Boot + Vue 3 的全栈博客，支持文章管理、评论、标签分类、Markdown 渲染、图片上传。</p>
              </div>

            </div>

            <div class="resume-block slide-in" style="--order:4">
              <h3 class="block-title"><span class="block-icon">🏆</span> 获奖经历</h3>
              <div class="project-item">
                <h4>蓝桥杯全国软件和信息技术专业人才大赛</h4>
                <p>嵌入式设计与开发组 · 省级二等奖</p>
              </div>
            </div>
          </div>

          <div class="resume-col">
            <div class="resume-block slide-in" style="--order:5">
              <h3 class="block-title"><span class="block-icon">🛠</span> 技术栈</h3>
              <div class="skill-tags">
                <span class="skill-tag" v-for="s in skills" :key="s.name" :style="{ '--tag-bg': s.bg, '--tag-color': s.color }">{{ s.name }}</span>
              </div>
            </div>

            <div class="resume-block slide-in" style="--order:6">
              <h3 class="block-title"><span class="block-icon">📬</span> 联系方式</h3>
              <div class="contact-list">
                <a v-for="ct in contacts" :key="ct.label" :href="ct.link" target="_blank" class="contact-item">
                  <span class="contact-icon" v-html="ct.icon"></span>
                  <span class="contact-text">{{ ct.label }}: {{ ct.value }}</span>
                </a>
              </div>
            </div>
          </div>
        </div>

        <p class="about-footer slide-in" style="--order:7">— 用代码书写生活 —</p>
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

// ========== About 数据 ==========
const aboutTitle = 'wuyou'
const aboutText = '你好，我是 wuyou。热爱技术，也热爱生活。从大学开始接触编程，一路走来积累了不少项目经验。擅长 Spring Boot + Vue 全栈开发，对前端动效和用户体验有着执着的追求。业余时间喜欢摄影、阅读和写作，这个博客就是我记录成长、分享思考的小天地。希望我的文章能给你带来一些启发或乐趣。'

const aboutTitleChars = aboutTitle.split('')
const aboutTextChars = aboutText.split('')
const aboutTitleVisible = ref(Array(aboutTitleChars.length).fill(false))
const aboutTextVisible = ref(Array(aboutTextChars.length).fill(false))
let aboutTypingStarted = false
let aboutTitleTimer = null
let aboutTextTimer = null

const skills = [
  { name: 'Vue.js', bg: 'rgba(79,192,141,0.2)', color: '#4fc08d' },
  { name: 'React', bg: 'rgba(97,218,251,0.2)', color: '#61dafb' },
  { name: 'Spring Boot', bg: 'rgba(109,179,63,0.2)', color: '#6db33f' },
  { name: 'Java', bg: 'rgba(237,139,0,0.2)', color: '#f89820' },
  { name: 'MySQL', bg: 'rgba(0,117,143,0.2)', color: '#00758f' },
  { name: 'JavaScript', bg: 'rgba(247,223,30,0.2)', color: '#f0db4f' },
  { name: 'Git', bg: 'rgba(240,80,51,0.2)', color: '#f05032' },
  { name: 'Docker', bg: 'rgba(36,150,237,0.2)', color: '#2496ed' },
  { name: 'Node.js', bg: 'rgba(51,153,51,0.2)', color: '#339933' },
  { name: 'Redis', bg: 'rgba(220,56,45,0.2)', color: '#dc382d' },
  { name: 'STM32', bg: 'rgba(3,46,130,0.2)', color: '#5b9bd5' },
]

const contacts = [
  { label: 'Email', value: '1002701514@qq.com', link: 'mailto:1002701514@qq.com', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M22 4L12 13 2 4"/></svg>' },
  { label: 'GitHub', value: 'github.com/wuyou', link: 'https://github.com', icon: '<svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 0C5.37 0 0 5.37 0 12c0 5.3 3.438 9.8 8.205 11.387.6.113.82-.258.82-.577 0-.285-.01-1.04-.015-2.04-3.338.724-4.042-1.61-4.042-1.61-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.73.083-.73 1.205.085 1.838 1.236 1.838 1.236 1.07 1.835 2.809 1.305 3.495.998.108-.776.418-1.305.762-1.604-2.665-.3-5.466-1.332-5.466-5.93 0-1.31.465-2.38 1.235-3.22-.135-.303-.54-1.523.105-3.176 0 0 1.005-.322 3.3 1.23.96-.267 1.98-.399 3-.405 1.02.006 2.04.138 3 .405 2.28-1.552 3.285-1.23 3.285-1.23.645 1.653.24 2.873.12 3.176.765.84 1.23 1.91 1.23 3.22 0 4.61-2.805 5.625-5.475 5.92.42.36.81 1.096.81 2.22 0 1.606-.015 2.896-.015 3.286 0 .315.21.69.825.57C20.565 21.795 24 17.295 24 12c0-6.63-5.37-12-12-12z"/></svg>' },
]


const startAboutTyping = () => {
  if (aboutTypingStarted) return
  aboutTypingStarted = true
  let ti = 0
  aboutTitleTimer = setInterval(() => {
    if (ti < aboutTitleChars.length) { aboutTitleVisible.value[ti] = true; ti++ }
    else {
      clearInterval(aboutTitleTimer); aboutTitleTimer = null
      let bi = 0
      aboutTextTimer = setInterval(() => {
        if (bi < aboutTextChars.length) { aboutTextVisible.value[bi] = true; bi++ }
        else { clearInterval(aboutTextTimer); aboutTextTimer = null }
      }, 40)
    }
  }, 100)
}// ========== 业务逻辑 ==========
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

<style>
.about-card::-webkit-scrollbar{display:none}
</style>

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
.hero .content {
  position: relative;
  z-index: 2;
}
.hero .weather-card-pos {
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
  top: 90px;
  left: 24px;
  z-index: 10;
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

/* About 区块 - 个人简介 */
.about { padding-top: 80px; box-sizing: border-box; background: url('/about-bg.jpg') fixed center center / cover no-repeat;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.about::before {
  content: '';
  position: absolute;
  inset: 0;
  background: rgba(20, 20, 35, 0.4);
  pointer-events: none;
  z-index: 1;
}

.about-card {
  position: relative;
  z-index: 2;
  max-width: 820px;
  width: 90%;
  margin: 0 auto;
  padding: 2.5rem 2.8rem;
  background: rgba(30, 30, 50, 0.65);
  border-radius: 32px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  box-shadow: 0 12px 48px rgba(0, 0, 0, 0.2);
  overflow-y: auto;
  max-height: 88vh;
  scrollbar-width: none;
}
.fade-up {
  opacity: 0;
  transform: translateY(40px);
  transition: opacity 0.8s ease, transform 0.8s ease;
}
.fade-up.visible {
  opacity: 1;
  transform: translateY(0);
}

.slide-in {
  opacity: 0;
  transform: translateY(18px);
  animation: slideUp 0.55s ease-out forwards;
  animation-delay: calc(var(--order, 0) * 0.1s);
  animation-play-state: paused;
}
.fade-up.visible .slide-in {
  animation-play-state: running;
}
@keyframes slideUp {
  to { opacity: 1; transform: translateY(0); }
}

.about-header {
  text-align: center;
  margin-bottom: 1.5rem;
}
.avatar-wrapper {
  width: 90px; height: 90px;
  margin: 0 auto 0.8rem;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255,255,255,0.35);
  box-shadow: 0 0 20px rgba(255,255,255,0.1);
  background: rgba(255,255,255,0.08);
}
.avatar-img { width: 100%; height: 100%; object-fit: cover; display: block; }
.about-name {
  font-size: 2.2rem; font-weight: 600; color: #fff;
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
  margin-bottom: 0.3rem; letter-spacing: 3px;
}
.about-tagline {
  font-size: 0.9rem; color: rgba(255,255,255,0.65); letter-spacing: 1px;
}

.about-bio {
  max-width: 600px; margin: 0 auto 2rem; text-align: center;
}
.about-bio p {
  font-size: 1.05rem; line-height: 1.9; color: rgba(255,255,255,0.9);
  text-shadow: 0 1px 3px rgba(0,0,0,0.2);
}

.about-resume {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 1.2rem;
}
.resume-col {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}
.resume-block {
  background: rgba(255,255,255,0.1);
  border-radius: 18px;
  padding: 1.2rem 1.4rem;
  border: 1px solid rgba(255,255,255,0.12);
}

.block-title {
  font-size: 1rem; font-weight: 600; color: #fff;
  margin-bottom: 0.8rem; display: flex; align-items: center; gap: 0.4rem;
}
.block-icon { font-size: 1.1rem; }

.edu-item {
  display: flex; gap: 0.8rem; align-items: flex-start;
}
.edu-dot {
  width: 8px; height: 8px; border-radius: 50%;
  background: #f9a8d4; margin-top: 0.4rem; flex-shrink: 0;
}
.edu-body {
  display: flex; flex-direction: column; gap: 0.15rem;
}
.edu-body strong { color: #fff; font-size: 0.95rem; }
.edu-body span { color: rgba(255,255,255,0.7); font-size: 0.85rem; }
.edu-time { color: rgba(255,255,255,0.45) !important; font-size: 0.8rem !important; }

.project-item {
  margin-bottom: 0.8rem;
}
.project-item:last-child { margin-bottom: 0; }
.project-item h4 {
  font-size: 0.9rem; color: #f9d49a; margin-bottom: 0.2rem; font-weight: 600;
}
.project-item p {
  font-size: 0.82rem; color: rgba(255,255,255,0.65); line-height: 1.5;
}

.skill-tags {
  display: flex; flex-wrap: wrap; gap: 0.5rem;
}
.skill-tag {
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.8rem; font-weight: 500;
  background: var(--tag-bg, rgba(255,255,255,0.12));
  color: var(--tag-color, rgba(255,255,255,0.9));
  border: 1px solid rgba(255,255,255,0.15);
  cursor: default;
  transition: all 0.25s ease;
}
.skill-tag:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0,0,0,0.2);
  border-color: rgba(255,255,255,0.4);
}

.contact-list {
  display: flex; flex-direction: column; gap: 0.5rem;
}
.contact-item {
  display: flex; align-items: center; gap: 0.5rem;
  color: rgba(255,255,255,0.7); text-decoration: none;
  font-size: 0.85rem; padding: 0.35rem 0.6rem;
  border-radius: 10px;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.1);
  transition: all 0.25s ease;
}
.contact-item:hover {
  color: #fff; background: rgba(255,255,255,0.18);
  transform: translateX(4px);
}
.contact-icon { display: flex; align-items: center; flex-shrink: 0; }
.contact-text { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.about-footer {
  text-align: center; font-size: 0.85rem;
  color: rgba(255,255,255,0.35); letter-spacing: 2px; margin-top: 1rem;
}

.title-char, .text-char { opacity: 0; transition: opacity 0.08s ease; }
.title-char.char-visible, .text-char.char-visible { opacity: 1; }

.glass-card, .about-title, .about-text { display: none; }

@media (max-width: 768px) {
  .about-card {
    padding: 1.5rem 1.2rem;
    width: 92%;
    border-radius: 24px;
    max-height: 85vh;
  }
  .about-name { font-size: 1.6rem; }
  .about-tagline { font-size: 0.78rem; }
  .about-bio p { font-size: 0.9rem; }
  .about-resume {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  .resume-col { gap: 1rem; }
  .avatar-wrapper { width: 70px; height: 70px; }
  .skill-tag { font-size: 0.75rem; padding: 0.25rem 0.7rem; }
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

</style>

