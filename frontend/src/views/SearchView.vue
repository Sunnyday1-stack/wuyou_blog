<template>
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
      url = `http://localhost:8080/api/articles?tagId=${tagId}`
    } else if (keyword) {
      url = `http://localhost:8080/api/articles/search?keyword=${encodeURIComponent(keyword)}`
    } else {
      url = 'http://localhost:8080/api/articles'
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
.search-container {
  max-width: 1200px;
  margin: 80px auto 2rem;
  padding: 2rem;
  background: rgba(137, 205, 227, 0.4); /* #89cde3 40% 透明度 */
  border-radius: 32px;
  min-height: calc(100vh - 100px);
}

h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 2rem;
  text-align: center;
  font-weight: 600;
}

.loading, .no-results {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: #4a5568;
}

/* 文章网格布局：卡片式分隔 */
.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 2rem;
}

/* 文章卡片样式 */
.article-card {
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(8px);
  border-radius: 24px;
  padding: 1.5rem;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.article-card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.article-link {
  text-decoration: none;
  color: inherit;
}

.article-title {
  font-size: 1.3rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.75rem;
  line-height: 1.4;
  transition: color 0.2s;
}

.article-title:hover {
  color: #4299e1;
}

.article-summary {
  font-size: 0.95rem;
  color: #4a5568;
  line-height: 1.5;
  margin-bottom: 1rem;
}

.article-date {
  display: block;
  font-size: 0.8rem;
  color: #718096;
  margin-top: 0.5rem;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .search-container {
    margin: 70px 1rem 1rem;
    padding: 1rem;
  }
  h1 {
    font-size: 1.5rem;
    margin-bottom: 1.5rem;
  }
  .articles-grid {
    gap: 1rem;
  }
  .article-card {
    padding: 1rem;
  }
  .article-title {
    font-size: 1.1rem;
  }
}
</style>