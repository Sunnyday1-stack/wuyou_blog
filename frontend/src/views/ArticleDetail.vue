<template>
    <canvas ref="starCanvas" class="star-canvas"></canvas>
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
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import { marked } from 'marked'
import CommentSection from '@/components/CommentSection.vue'

const route = useRoute()
const router = useRouter()
const article = ref(null)
const loading = ref(true)

const starCanvas = ref(null)
let starAnimationId = null

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

// ========== 星空动画 ==========
const initStars = () => {
  const canvas = starCanvas.value
  if (!canvas) return
  const ctx = canvas.getContext('2d')
  const stars = []
  const shootingStars = []
  const STAR_COUNT = 200

  const resize = () => {
    canvas.width = window.innerWidth
    canvas.height = document.body.scrollHeight
  }
  resize()
  window.addEventListener('resize', resize)

  for (let i = 0; i < STAR_COUNT; i++) {
    stars.push({
      x: Math.random() * canvas.width,
      y: Math.random() * canvas.height,
      r: Math.random() * 2 + 0.5,
      opacity: Math.random(),
      speed: Math.random() * 0.02 + 0.005,
      phase: Math.random() * Math.PI * 2
    })
  }

  const spawnShootingStar = () => {
    if (shootingStars.length < 2 && Math.random() < 0.008) {
      shootingStars.push({
        x: Math.random() * canvas.width * 0.8,
        y: Math.random() * canvas.height * 0.3,
        len: Math.random() * 80 + 50,
        speed: Math.random() * 6 + 4,
        opacity: 1,
        angle: Math.PI / 6
      })
    }
  }

  const animate = () => {
    ctx.clearRect(0, 0, canvas.width, canvas.height)

    const grad = ctx.createLinearGradient(0, 0, 0, canvas.height)
    grad.addColorStop(0, '#0a0a1a')
    grad.addColorStop(0.5, '#101025')
    grad.addColorStop(1, '#1a1a2e')
    ctx.fillStyle = grad
    ctx.fillRect(0, 0, canvas.width, canvas.height)

    stars.forEach(star => {
      star.opacity += star.speed
      if (star.opacity > 1 || star.opacity < 0.2) star.speed *= -1
      ctx.beginPath()
      ctx.arc(star.x, star.y, star.r, 0, Math.PI * 2)
      ctx.fillStyle = `rgba(255, 255, 255, ${star.opacity})`
      ctx.fill()
      if (star.r > 1.5) {
        ctx.beginPath()
        ctx.arc(star.x, star.y, star.r * 2.5, 0, Math.PI * 2)
        ctx.fillStyle = `rgba(200, 220, 255, ${star.opacity * 0.15})`
        ctx.fill()
      }
    })

    shootingStars.forEach((s, i) => {
      s.x += s.speed * Math.cos(Math.PI - s.angle)
      s.y += s.speed * Math.sin(Math.PI - s.angle)
      s.opacity -= 0.012
      if (s.opacity <= 0) {
        shootingStars.splice(i, 1)
        return
      }
      const endX = s.x - s.len * Math.cos(Math.PI - s.angle)
      const endY = s.y - s.len * Math.sin(Math.PI - s.angle)
      const sg = ctx.createLinearGradient(s.x, s.y, endX, endY)
      sg.addColorStop(0, `rgba(255, 255, 255, ${s.opacity})`)
      sg.addColorStop(1, 'rgba(255, 255, 255, 0)')
      ctx.beginPath()
      ctx.moveTo(s.x, s.y)
      ctx.lineTo(endX, endY)
      ctx.strokeStyle = sg
      ctx.lineWidth = 1.2
      ctx.stroke()
    })

    spawnShootingStar()
    starAnimationId = requestAnimationFrame(animate)
  }

  animate()
}

onMounted(async () => {
  initStars()
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

onUnmounted(() => {
  if (starAnimationId) cancelAnimationFrame(starAnimationId)
})
</script>

<style scoped>
.star-canvas {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  pointer-events: none;
}

.article-detail-container {
  position: relative;
  z-index: 1;
  margin: 80px auto 0;
  padding: 2.5rem 3rem 3rem;
  min-height: 100vh;
}

.article-detail {
  max-width: 860px;
  margin: 0 auto;
}

.loading, .error {
  text-align: center;
  padding: 6rem 3rem;
  font-size: 1.2rem;
  color: #aaaac0;
}

/* 标题 */
.title {
  font-size: 2.2rem;
  font-weight: 700;
  margin-bottom: 0.8rem;
  color: #f0f0f8;
  line-height: 1.3;
  letter-spacing: -0.5px;
}

/* 元信息 */
.meta {
  font-size: 0.85rem;
  color: #8888aa;
  border-bottom: 2px solid rgba(255,255,255,0.08);
  padding-bottom: 1rem;
  margin-bottom: 2rem;
  display: flex;
  gap: 1.5rem;
}

/* ====== v-html 渲染内容 ====== */
.content {
  font-size: 1.05rem;
  line-height: 1.85;
  color: #d4d4e0;
  word-break: break-word;
}

/* 图片自适应 */
:deep(.content img) {
  display: block;
  max-width: 100%;
  height: auto;
  margin: 1.5rem auto;
  border-radius: 10px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  transition: box-shadow 0.3s ease;
}
:deep(.content img:hover) {
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

/* 段落 */
:deep(.content p) {
  margin-bottom: 1.2rem;
}

/* 标题层级 */
:deep(.content h1) { font-size: 1.8rem; margin: 2rem 0 1rem; color: #f0f0f8; }
:deep(.content h2) { font-size: 1.5rem; margin: 1.8rem 0 0.8rem; color: #e8e8f0; padding-bottom: 0.4rem; border-bottom: 1px solid rgba(255,255,255,0.1); }
:deep(.content h3) { font-size: 1.25rem; margin: 1.5rem 0 0.6rem; color: #d8d8e8; }
:deep(.content h4) { font-size: 1.1rem; margin: 1.2rem 0 0.5rem; color: #c8c8d8; }

/* 引用块 */
:deep(.content blockquote) {
  margin: 1.2rem 0;
  padding: 0.8rem 1.2rem;
  border-left: 4px solid #ea9679;
  background: rgba(255,255,255,0.06);
  color: #aaaac0;
  border-radius: 0 8px 8px 0;
}
:deep(.content blockquote p) {
  margin-bottom: 0.3rem;
}

/* 代码块 */
:deep(.content pre) {
  background: #1e1e2e;
  color: #cdd6f4;
  padding: 1.2rem;
  border-radius: 10px;
  overflow-x: auto;
  margin: 1.2rem 0;
  font-size: 0.9rem;
  line-height: 1.6;
}
:deep(.content code) {
  background: rgba(255,255,255,0.1);
  color: #e0e0f0;
  padding: 0.15rem 0.4rem;
  border-radius: 4px;
  font-size: 0.9em;
  font-family: "Consolas", "Monaco", monospace;
}
:deep(.content pre code) {
  background: none;
  padding: 0;
}

/* 列表 */
:deep(.content ul),
:deep(.content ol) {
  padding-left: 1.5rem;
  margin-bottom: 1.2rem;
}
:deep(.content li) {
  margin-bottom: 0.4rem;
}

/* 链接 */
:deep(.content a) {
  color: #ea9679;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: border-color 0.2s;
}
:deep(.content a:hover) {
  border-bottom-color: #ea9679;
}

/* 分割线 */
:deep(.content hr) {
  border: none;
  border-top: 1px solid rgba(255,255,255,0.1);
  margin: 2rem 0;
}

/* 表格 */
:deep(.content table) {
  width: 100%;
  border-collapse: collapse;
  margin: 1.2rem 0;
}
:deep(.content th),
:deep(.content td) {
  padding: 0.6rem 1rem;
  border: 1px solid rgba(255,255,255,0.1);
  text-align: left;
}
:deep(.content th) {
  background: rgba(255,255,255,0.06);
  color: #d0d0e0;
  font-weight: 600;
}
:deep(.content td) {
  color: #c0c0d0;
}

/* 返回按钮 */
.back-btn {
  margin-top: 3rem;
  padding: 0.8em 2.4em;
  color: #aaaac0;
  background: rgba(255,255,255,0.08);
  border: 1px solid rgba(255,255,255,0.12);
  border-radius: 40px;
  font-size: 0.85rem;
  text-transform: uppercase;
  letter-spacing: 2px;
  font-weight: 600;
  transition: all 0.3s ease;
  cursor: pointer;
}
.back-btn:hover {
  background: #ea9679;
  color: #fff;
  border-color: #ea9679;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(234, 150, 121, 0.3);
}

@media (max-width: 768px) {
  .article-detail-container {
    padding: 1.5rem 1rem 2rem;
    margin-top: 60px;
  }
  .title {
    font-size: 1.6rem;
  }
  :deep(.content img) {
    border-radius: 6px;
    margin: 1rem auto;
  }
}
</style>