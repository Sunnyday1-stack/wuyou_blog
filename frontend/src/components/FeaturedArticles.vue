<template>
  <div class="featured-container">
    <h2 class="section-title">最新文章</h2>

    <!-- 骨架屏加载状态 -->
    <div v-if="loading" class="articles-grid skeleton-grid">
      <div v-for="n in 3" :key="n" class="skeleton-card">
        <div class="skeleton-title"></div>
        <div class="skeleton-summary"></div>
        <div class="skeleton-date"></div>
      </div>
    </div>

    <!-- 实际文章列表 -->
    <div v-else class="articles-grid">
      <div
        v-for="article in articles"
        :key="article.id"
        class="card"
      >
        <div class="card-info">
          <router-link :to="`/article/${article.id}`" class="article-link">
            <h3 class="title">{{ article.title }}</h3>
          </router-link>
          <p class="summary">{{ article.summary || '暂无摘要' }}</p>
          <time class="date">{{ formatDate(article.createTime) }}</time>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import axios from 'axios'

const props = defineProps({
  categoryId: {
    type: Number,
    default: null
  }
})

const mockArticles = [
  {
    id: 1,
    title: 'Vue 3 新特性解析',
    summary: '详细介绍 Vue 3 的 Composition API 和其他改进',
    createTime: '2023-10-10'
  },
  {
    id: 2,
    title: 'CSS Grid 实战',
    summary: '如何使用 Grid 布局创建响应式网页',
    createTime: '2023-11-15'
  },
  {
    id: 3,
    title: '全栈开发之路',
    summary: '分享从零到一的成长经验',
    createTime: '2024-01-20'
  }
]

const articles = ref([])
const loading = ref(true)

const fetchArticles = async () => {
  try {
    loading.value = true
    const url = props.categoryId
      ? `http://localhost:8080/api/articles?categoryId=${props.categoryId}`
      : 'http://localhost:8080/api/articles'
    const res = await axios.get(url)
    articles.value = res.data
  } catch (error) {
    console.error('获取文章失败，使用 mock 数据:', error)
    articles.value = mockArticles
  } finally {
    loading.value = false
  }
}

watch(() => props.categoryId, () => {
  fetchArticles()
}, { immediate: true })

function formatDate(dateString) {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}

// 滚动渐现动画（可选）
const articleCards = ref([])
let observer = null

const observeCards = () => {
  if (!articleCards.value.length) return
  observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add('visible')
          observer.unobserve(entry.target)
        }
      })
    },
    { threshold: 0.2 }
  )
  articleCards.value.forEach((card) => {
    if (card) observer.observe(card)
  })
}

onMounted(() => {
  nextTick(() => {
    if (articleCards.value.length) observeCards()
  })
})

onUnmounted(() => {
  if (observer) observer.disconnect()
})
</script>

<style scoped>
.featured-container {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  text-align: center;
  margin-bottom: 2rem;
  font-size: 2rem;
  color: #333;
}

/* 响应式网格布局 */
.articles-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
}

@media (max-width: 992px) {
  .articles-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .articles-grid {
    grid-template-columns: 1fr;
  }
}

/* ========== Uiverse 卡片样式（完全替换） ========== */
.card {
  --background: linear-gradient(to right, #74ebd5 0%, #acb6e5 100%);
  width: 100%;          /* 自适应网格宽度，移除固定宽高 */
  height: 100%;
  padding: 5px;
  border-radius: 1rem;
  overflow: visible;
  background: #74ebd5;
  background: var(--background);
  position: relative;
  z-index: 1;
  box-sizing: border-box;
}

.card::before,
.card::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 1rem;
  z-index: -1;
}

.card::before {
  background: linear-gradient(to bottom right, #f6d365 0%, #fda085 100%);
  transform: rotate(2deg);
}

.card::after {
  background: linear-gradient(to top right, #84fab0 0%, #8fd3f4 100%);
  transform: rotate(-2deg);
}

.card-info {
  --color: #292b2c;
  background: var(--color);
  color: var(--color);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  overflow: visible;
  border-radius: 0.7rem;
  position: relative;
  z-index: 2;
  padding: 1.2rem;
  box-sizing: border-box;
  text-align: center;
}

.title {
  font-weight: bold;
  letter-spacing: 0.1em;
  font-size: 1.25rem;
  margin: 0 0 0.5rem 0;
  color: #fff;
}

.summary {
  color: #ddd;
  margin: 0.5rem 0;
  line-height: 1.4;
}

.date {
  display: block;
  color: #aaa;
  font-size: 0.85rem;
  margin-top: 0.5rem;
}

.article-link {
  text-decoration: none;
  color: inherit;
}

.card:hover::before,
.card:hover::after {
  opacity: 0;
}

.card:hover .card-info {
  color: #74ebd5;
  transition: color 1s;
}

.card:hover .title,
.card:hover .summary,
.card:hover .date {
  color: #74ebd5;
}

/* 骨架屏样式（保持不变） */
.skeleton-grid {
  --skeleton-bg: #e2e8f0;
}
.skeleton-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
}
.skeleton-title {
  height: 1.5rem;
  width: 80%;
  background: var(--skeleton-bg);
  border-radius: 6px;
  margin-bottom: 1rem;
  animation: pulse 1.2s infinite;
}
.skeleton-summary {
  height: 4rem;
  background: var(--skeleton-bg);
  border-radius: 6px;
  margin-bottom: 1rem;
  animation: pulse 1.2s infinite;
}
.skeleton-date {
  height: 1rem;
  width: 40%;
  background: var(--skeleton-bg);
  border-radius: 4px;
  animation: pulse 1.2s infinite;
}
@keyframes pulse {
  0% { opacity: 0.6; }
  50% { opacity: 1; }
  100% { opacity: 0.6; }
}

/* 滚动渐现动画（与卡片原有动画不冲突） */
.card {
  opacity: 1;
  transform: translateY(0);
  transition: opacity 0.4s ease, transform 0.4s ease;
}
</style>
