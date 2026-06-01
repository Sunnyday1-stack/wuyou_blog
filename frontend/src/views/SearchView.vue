<template>
  <div class="search-page">
    <div class="search-container">
      <h1>{{ pageTitle }}</h1>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="articles.length === 0" class="no-results">未找到相关文章</div>
      <div v-else class="articles-grid">
        <div v-for="article in articles" :key="article.id" class="article-card">
          <router-link :to="`/article/${article.id}`" class="article-link">
            <h2 class="article-title">{{ article.title }}</h2>
          </router-link>
          <p class="article-summary">{{ article.summary || '暂无摘要' }}</p>
          <small class="article-date">{{ formatDate(article.createTime) }}</small>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const articles = ref([])
const loading = ref(true)

const keyword = route.query.q || ''
const tagId = route.query.tag

const pageTitle = computed(() => {
  if (tagId) return '标签文章'
  if (keyword) return `搜索结果：${keyword}`
  return '文章列表'
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}

onMounted(async () => {
  try {
    let url = ''
    if (tagId) {
      url = `/api/articles?tagId=${tagId}`
    } else if (keyword) {
      url = `/api/articles/search?keyword=${encodeURIComponent(keyword)}`
    } else {
      url = '/api/articles'
    }
    const res = await axios.get(url)
    articles.value = res.data
  } catch (error) {
    console.error('获取文章失败', error)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: url('/register-bg.jpg') fixed center center / cover no-repeat;
  position: relative;
}
.search-page::before {
  content: '';
  position: fixed;
  inset: 0;
  background: rgba(20, 20, 35, 0.45);
  pointer-events: none;
  z-index: 0;
}

.search-container {
  position: relative;
  z-index: 1;
  max-width: 1200px;
  margin: 80px auto 2rem;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.12);
  backdrop-filter: blur(12px);
  border-radius: 32px;
  min-height: calc(100vh - 100px);
  border: 1px solid rgba(255, 255, 255, 0.15);
}

h1 {
  font-size: 2rem;
  color: #f0f0f0;
  margin-bottom: 2rem;
  text-align: center;
  font-weight: 600;
}

.loading, .no-results {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.6);
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
}

.article-card {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 24px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.18);
}

.article-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.3);
  background: rgba(255, 255, 255, 0.5);
}

.article-link {
  text-decoration: none;
  color: inherit;
}

.article-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #f0f0f0;
  margin-bottom: 0.75rem;
  line-height: 1.4;
  transition: color 0.2s;
}

.article-title:hover {
  color: #f9d49a;
}

.article-summary {
  font-size: 0.95rem;
  color: rgba(255, 255, 255, 0.65);
  line-height: 1.5;
  margin-bottom: 1rem;
}

.article-date {
  display: block;
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.45);
  margin-top: 0.5rem;
}

@media (max-width: 768px) {
  .search-container {
    margin: 60px 0.5rem 1rem;
    padding: 1rem;
    border-radius: 20px;
  }
  h1 {
    font-size: 1.3rem;
    margin-bottom: 1rem;
  }
  .articles-grid {
    grid-template-columns: 1fr;
    gap: 0.8rem;
  }
  .article-card {
    padding: 1rem;
  }
  .article-title {
    font-size: 1rem;
  }
  .article-summary {
    font-size: 0.85rem;
  }
  .loading, .no-results {
    padding: 2rem 1rem;
    font-size: 1rem;
  }
}
</style>