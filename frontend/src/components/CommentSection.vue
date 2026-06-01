<template>
  <div class="comment-section">
    <h3>评论区</h3>

    <!-- 发表评论表单：仅登录用户可见 -->
    <div v-if="isLoggedIn">
      <form @submit.prevent="submitComment" class="comment-form">
        <div class="form-row">
          <!-- 昵称输入框（带图标） -->
          <div class="input">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
            </svg>
            <input v-model="form.nickname" type="text" placeholder="昵称 *" class="input-field" required />
          </div>
          <!-- 邮箱输入框（带图标） -->
          <div class="input">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
              <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
            </svg>
            <input v-model="form.email" type="email" placeholder="邮箱 (可选)" class="input-field" />
          </div>
        </div>
        <!-- 评论内容 textarea（带图标） -->
        <div class="input textarea-input">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor">
            <path d="M20 2H4c-1.1 0-2 .9-2 2v12c0 1.1.9 2 2 2h14l4 4V4c0-1.1-.9-2-2-2z"/>
          </svg>
          <textarea v-model="form.content" rows="4" placeholder="评论内容 *" class="input-field" required></textarea>
        </div>
        <button type="submit" :disabled="loading" class="btn-uiverse">发表评论</button>
      </form>
    </div>
    <div v-else class="login-prompt">
      请先 <router-link to="/login">登录</router-link> 后发表评论
    </div>

    <!-- 评论列表 -->
    <div v-if="loadingComments" class="loading">加载评论中...</div>
    <div v-else class="comment-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item">
        <div class="comment-header">
          <strong>{{ comment.nickname }}</strong>
          <span class="time">{{ formatDate(comment.createTime) }}</span>
          <button 
            v-if="canDelete(comment)" 
            class="delete-btn" 
            @click="deleteComment(comment.id)"
          >
            删除
          </button>
        </div>
        <div class="comment-content">{{ comment.content }}</div>
      </div>
      <div v-if="comments.length === 0" class="no-comments">暂无评论，沙发是你的！</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const props = defineProps({
  articleId: {
    type: Number,
    required: true
  }
})

const comments = ref([])
const loadingComments = ref(false)
const loading = ref(false)

const currentUser = ref(null)

const isLoggedIn = computed(() => !!localStorage.getItem('token'))

const form = ref({
  nickname: '',
  email: '',
  content: ''
})

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

const canDelete = (comment) => {
  if (!currentUser.value) return false
  return currentUser.value.role === 'ADMIN' || currentUser.value.id === comment.userId
}

const deleteComment = async (commentId) => {
  if (!confirm('确定要删除这条评论吗？')) return
  try {
    const token = localStorage.getItem('token')
    await axios.delete(`/api/comments/${commentId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    await fetchComments()
  } catch (error) {
    console.error('删除失败', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

const fetchComments = async () => {
  loadingComments.value = true
  try {
    const res = await axios.get(`/api/comments/article/${props.articleId}`)
    comments.value = res.data
  } catch (error) {
    console.error('获取评论失败', error)
  } finally {
    loadingComments.value = false
  }
}

const submitComment = async () => {
  if (!isLoggedIn.value) {
    alert('请先登录')
    return
  }
  if (!form.value.nickname || !form.value.content) {
    alert('昵称和评论内容不能为空')
    return
  }
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    await axios.post('/api/comments', {
      articleId: props.articleId,
      nickname: form.value.nickname,
      email: form.value.email || null,
      content: form.value.content
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    form.value.content = ''
    await fetchComments()
  } catch (error) {
    console.error('发表评论失败', error)
    alert(error.response?.data?.message || '发表失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const fetchCurrentUser = async () => {
  const token = localStorage.getItem('token')
  if (!token) return
  try {
    const role = localStorage.getItem('role')
    const id = localStorage.getItem('userId')
    if (id && role) {
      currentUser.value = { id: parseInt(id), role }
    } else {
      currentUser.value = null
    }
  } catch (error) {
    console.error('获取用户信息失败', error)
  }
}

onMounted(() => {
  fetchComments()
  fetchCurrentUser()
  if (isLoggedIn.value) {
    const username = localStorage.getItem('username')
    if (username) form.value.nickname = username
  }
})
</script>

<style scoped>
.comment-section {
  margin-top: 2rem;
  padding: 1.5rem;
  background: #f9f9f9;
  border-radius: 12px;
}

/* ========== Uiverse 输入框样式（应用于所有 .input-field） ========== */
.input {
  --icon-size: 28px;
  position: relative;
  margin-bottom: 1rem;
}

.input svg {
  position: absolute;
  width: var(--icon-size);
  top: 50%;
  left: 0.8rem;
  translate: 0 -50%;
  overflow: visible;
  color: rgba(156, 206, 224, 0.6);
  z-index: 1;
}

.input svg path {
  transform-box: fill-box;
  transform-origin: center;
}

.input:is(:hover, :focus-within) svg path {
  animation: pop 0.5s var(--d);
}

.input svg path:nth-of-type(1) {
  --d: 0.24s;
  --r: 20deg;
  --s: 1.5;
}
.input svg path:nth-of-type(2) {
  --d: 0.12s;
  --r: 10deg;
  --s: 1.4;
}
.input svg path:nth-of-type(3) {
  --d: 0s;
  --r: 0deg;
  --s: 1.25;
}

@keyframes pop {
  50% {
    scale: var(--s, 1);
    rotate: var(--r, 0deg);
  }
}

.input-field {
  box-shadow:
    0 20px 25px -5px rgb(0 0 0 / 0.15),
    0 8px 10px -6px rgb(0 0 0 / 0.25);
  font-family: inherit;
  width: 100%;
  padding: 0.8rem 0.8rem 0.8rem calc(0.8rem + var(--icon-size) + 0.5rem);
  font-size: 0.95rem;
  background:
    linear-gradient(white, white) padding-box,
    linear-gradient(120deg, #9ccee0, #9ccee0) border-box;
  border: 4px solid transparent;
  border-radius: 12px;
  outline: none;
  box-sizing: border-box;
  transition: all 0.2s;
}

/* 指定边框颜色为 #9ccee0 40% 透明度 */
.input-field {
  background:
    linear-gradient(white, white) padding-box,
    linear-gradient(120deg, rgba(156, 206, 224, 0.4), rgba(156, 206, 224, 0.4)) border-box;
}

/* 针对 textarea 特殊处理，使 SVG 图标与顶部对齐 */
.textarea-input svg {
  top: 1.2rem;
  transform: none;
}

.textarea-input .input-field {
  resize: vertical;
  min-height: 100px;
}

/* 占位符颜色 */
.input-field::placeholder {
  color: rgba(156, 206, 224, 0.6);
}

/* ========== 新 Uiverse 按钮样式 ========== */
.btn-uiverse {
  padding: 1.3em 3em;
  font-size: 12px;
  text-transform: uppercase;
  letter-spacing: 2.5px;
  font-weight: 500;
  color: #000;
  background-color: #fff;
  border: none;
  border-radius: 45px;
  box-shadow: 0px 8px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease 0s;
  cursor: pointer;
  outline: none;
  margin-top: 0.5rem;
  width: auto;
}

.btn-uiverse:hover {
  background-color: #23c483;
  box-shadow: 0px 15px 20px rgba(46, 229, 157, 0.4);
  color: #fff;
  transform: translateY(-7px);
}

.btn-uiverse:active {
  transform: translateY(-1px);
}

.btn-uiverse:disabled {
  opacity: 0.6;
  transform: none;
  cursor: not-allowed;
}

/* 原有其他样式（评论列表、登录提示等）保持不变 */
.comment-list {
  margin-top: 1rem;
}
.comment-item {
  padding: 1rem 0;
  border-bottom: 1px solid #eee;
}
.comment-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
  font-size: 0.9rem;
}
.comment-header strong {
  color: #2c3e50;
}
.time {
  color: #999;
  margin-left: auto;
}
.delete-btn {
  background: none;
  border: none;
  color: #e53e3e;
  cursor: pointer;
  padding: 0.2rem 0.5rem;
  font-size: 0.8rem;
  border-radius: 4px;
}
.delete-btn:hover {
  background: rgba(229, 62, 62, 0.1);
}
.comment-content {
  color: #555;
  line-height: 1.5;
}
.loading, .no-comments {
  text-align: center;
  padding: 1rem;
  color: #999;
}
.login-prompt {
  text-align: center;
  padding: 1rem;
  background: #e2e8f0;
  border-radius: 8px;
  margin-bottom: 1rem;
}
.login-prompt a {
  color: #4299e1;
  text-decoration: none;
}
.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 0;
}
.form-row .input {
  flex: 1;
}
</style>