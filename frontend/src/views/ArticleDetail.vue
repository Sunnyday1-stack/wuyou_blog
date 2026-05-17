<template>
  <div class="article-detail-container">
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="article" class="article-detail">
      <h1 class="title">{{ article.title }}</h1>
      <div class="meta">
        <span>发布时间：{{ formatDate(article.createTime) }}</span>
        <span>阅读量：{{ article.viewCount || 0 }}</span>
      </div>
      <div class="content" v-html="renderedContent"></div>

      <!-- 评论区域 -->
      <CommentSection :articleId="article.id" />

      <button class="back-btn" @click="$router.back()">← 返回</button>
    </div>
    <div v-else class="error">文章不存在或已被删除</div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { marked } from 'marked'
import CommentSection from '@/components/CommentSection.vue'

const route = useRoute()
const router = useRouter()
const article = ref(null)
const loading = ref(true)

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

const renderedContent = computed(() => {
  if (article.value && article.value.content) {
    return marked.parse(article.value.content)
  }
  return ''
})

const incrementViewCount = async (id) => {
  try {
    await axios.put(`http://localhost:8080/api/articles/${id}/view`)
    if (article.value) {
      article.value.viewCount = (article.value.viewCount || 0) + 1
    }
  } catch (error) {
    console.error('更新阅读量失败', error)
  }
}

onMounted(async () => {
  const id = route.params.id
  try {
    const res = await axios.get(`http://localhost:8080/api/articles/${id}`)
    article.value = res.data
    incrementViewCount(id)
  } catch (error) {
    console.error('获取文章详情失败：', error)
    article.value = null
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
/* 整体容器样式保持不变 */
.article-detail-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem;
  background: white;
  min-height: 100vh;
}
.loading, .error {
  text-align: center;
  padding: 3rem;
  font-size: 1.2rem;
  color: #666;
}
.title {
  font-size: 2rem;
  margin-bottom: 1rem;
  color: #2c3e50;
}
.meta {
  font-size: 0.9rem;
  color: #999;
  border-bottom: 1px solid #eee;
  padding-bottom: 0.5rem;
  margin-bottom: 1.5rem;
  display: flex;
  gap: 1rem;
}
.content {
  font-size: 1rem;
  line-height: 1.6;
  color: #333;
}
.content img {
  max-width: 100%;
}

/* ========== 新 Uiverse 返回按钮样式 ========== */
.back-btn {
  padding: 1.3em 3em;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 2.5px;
  font-weight: 500;
  color: #000;
  background-color: #b3ddeb;        /* 不选择时的背景色 */
  border: none;
  border-radius: 45px;
  box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease 0s;
  cursor: pointer;
  outline: none;
  margin-top: 2rem;
  display: inline-block;
}

.back-btn:hover {
  background-color: rgba(228, 235, 189, 0.6); /* #e4ebbd 60% 透明度 */
  box-shadow: 0px 15px 20px rgba(46, 229, 157, 0.4);
  color: #fff;
  transform: translateY(-7px);
}

.back-btn:active {
  transform: translateY(-1px);
}
</style>