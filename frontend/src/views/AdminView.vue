<template>
  <div class="admin-container">
    <h1>后台管理</h1>

    <div class="admin-layout">
      <!-- 左侧：文章管理 -->
      <div class="articles-section">
        <div class="section-header">
          <h2>文章管理</h2>
          <button class="create-btn" @click="openCreateModal">+ 新建文章</button>
        </div>

        <table class="article-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>标题</th>
              <th>分类</th>
              <th>创建时间</th>
              <th>更新时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="article in articles" :key="article.id">
              <td>{{ article.id }}</td>
              <td>{{ article.title }}</td>
              <td>{{ article.categoryId || '未分类' }}</td>
              <td>{{ formatDate(article.createTime) }}</td>
              <td>{{ formatDate(article.updateTime) }}</td>
              <td>
                <button class="edit-btn" @click="openEditModal(article)">编辑</button>
                <button class="delete-btn" @click="deleteArticle(article.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 右侧：标签管理 -->
      <div class="tags-section">
        <div class="section-header">
          <h2>标签管理</h2>
          <div class="add-tag">
            <input type="text" v-model="newTagName" placeholder="新标签名称" />
            <button class="add-tag-btn" @click="createTag">添加</button>
          </div>
        </div>
        <div class="tags-list">
          <div v-for="tag in tags" :key="tag.id" class="tag-item">
            <span>{{ tag.name }}</span>
            <button class="tag-delete" @click="deleteTag(tag.id)">删除</button>
          </div>
          <div v-if="tags.length === 0" class="no-tags">暂无标签，请添加</div>
        </div>
      </div>
    </div>

    <!-- 文章编辑模态框 -->
    <div v-if="showModal" class="modal">
      <div class="modal-content">
        <h2>{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
        <form @submit.prevent="submitForm">
          <div class="form-group">
            <label>标题 *</label>
            <input v-model="form.title" required />
          </div>
          <div class="form-group">
            <label>摘要</label>
            <textarea v-model="form.summary" rows="2"></textarea>
          </div>
          <div class="form-group">
            <label>内容 (Markdown) *</label>
            <textarea v-model="form.content" rows="10" required></textarea>
          </div>
          <div class="form-group">
            <label>分类ID</label>
            <input type="number" v-model="form.categoryId" />
          </div>
          <div class="form-group">
            <label>标签（多选）</label>
            <select multiple v-model="selectedTagIds" class="tag-select">
              <option v-for="tag in tags" :value="tag.id" :key="tag.id">{{ tag.name }}</option>
            </select>
            <small>按住 Ctrl 多选</small>
          </div>
          <div class="form-actions">
            <button type="submit" :disabled="submitting">{{ submitting ? '保存中...' : '保存' }}</button>
            <button type="button" @click="closeModal">取消</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const articles = ref([])
const tags = ref([])
const showModal = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const newTagName = ref('')

const form = ref({
  id: null,
  title: '',
  summary: '',
  content: '',
  categoryId: null
})
const selectedTagIds = ref([])

const fetchArticles = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/articles')
    articles.value = res.data
  } catch (error) {
    console.error('获取文章失败', error)
  }
}

const fetchTags = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/tags')
    tags.value = res.data
  } catch (error) {
    console.error('获取标签失败', error)
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleString()
}

const openCreateModal = () => {
  isEdit.value = false
  form.value = { id: null, title: '', summary: '', content: '', categoryId: null }
  selectedTagIds.value = []
  showModal.value = true
}

const openEditModal = async (article) => {
  isEdit.value = true
  form.value = { ...article }
  try {
    const res = await axios.get(`http://localhost:8080/api/articles/${article.id}/tags`)
    selectedTagIds.value = res.data.map(t => t.id)
  } catch (error) {
    console.error('获取文章标签失败', error)
    selectedTagIds.value = []
  }
  showModal.value = true
}

const closeModal = () => {
  showModal.value = false
  submitting.value = false
}

const submitForm = async () => {
  submitting.value = true
  const token = localStorage.getItem('token')
  try {
    let articleId = form.value.id
    if (isEdit.value) {
      await axios.put(`http://localhost:8080/api/articles/${articleId}`, form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      await axios.put(`http://localhost:8080/api/articles/${articleId}/tags`, selectedTagIds.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      alert('更新成功')
    } else {
      const res = await axios.post('http://localhost:8080/api/articles', form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      articleId = res.data.id
      if (selectedTagIds.value.length) {
        await axios.put(`http://localhost:8080/api/articles/${articleId}/tags`, selectedTagIds.value, {
          headers: { Authorization: `Bearer ${token}` }
        })
      }
      alert('创建成功')
    }
    await fetchArticles()
    closeModal()
  } catch (error) {
    console.error('保存失败', error)
    alert(error.response?.data?.message || '保存失败')
  } finally {
    submitting.value = false
  }
}

const deleteArticle = async (id) => {
  if (!confirm('确定删除该文章吗？')) return
  const token = localStorage.getItem('token')
  try {
    await axios.delete(`http://localhost:8080/api/articles/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    alert('删除成功')
    await fetchArticles()
  } catch (error) {
    console.error('删除失败', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

const createTag = async () => {
  if (!newTagName.value.trim()) return
  const token = localStorage.getItem('token')
  try {
    await axios.post('http://localhost:8080/api/tags', { name: newTagName.value }, {
      headers: { Authorization: `Bearer ${token}` }
    })
    alert('标签添加成功')
    newTagName.value = ''
    await fetchTags()
  } catch (error) {
    console.error('创建标签失败', error)
    alert(error.response?.data?.message || '创建失败')
  }
}

const deleteTag = async (id) => {
  if (!confirm('确定删除该标签吗？删除后不会影响文章，但会解除关联')) return
  const token = localStorage.getItem('token')
  try {
    await axios.delete(`http://localhost:8080/api/tags/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    alert('标签删除成功')
    await fetchTags()
  } catch (error) {
    console.error('删除标签失败', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  fetchArticles()
  fetchTags()
})
</script>

<style scoped>
/* 全部样式已优化，保持暖橘色主调 */
.admin-container {
  max-width: 1400px;
  margin: 80px auto 2rem;
  padding: 1rem;
  background: linear-gradient(135deg, #fff8f5 0%, #fff 100%);
  border-radius: 24px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.02);
}
h1 {
  font-size: 2rem;
  font-weight: 700;
  color: #ea9679;
  margin-bottom: 1.5rem;
  letter-spacing: -0.01em;
}
.admin-layout {
  display: flex;
  gap: 2rem;
}
.articles-section {
  flex: 2;
}
.tags-section {
  flex: 1;
  background: white;
  padding: 1.2rem;
  border-radius: 20px;
  border: 1px solid #ffddd0;
  box-shadow: 0 4px 12px rgba(0,0,0,0.03);
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
  flex-wrap: wrap;
  gap: 0.8rem;
}
.section-header h2 {
  font-size: 1.5rem;
  font-weight: 700;
  color: #ea9679;
  margin: 0;
}
.create-btn {
  background: #ea9679;
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 40px;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
}
.create-btn:hover {
  background: #d4785c;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(234,150,121,0.3);
}
.article-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.03);
}
.article-table th,
.article-table td {
  padding: 12px 16px;
  text-align: left;
  border-bottom: 1px solid #ffe4d9;
}
.article-table th {
  background: #ea9679;
  color: white;
  font-weight: 600;
  font-size: 0.9rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
.article-table tr:hover {
  background: #fff0eb;
  transition: background 0.2s;
}
.edit-btn {
  background: #ffc4b3;
  border: none;
  padding: 4px 12px;
  border-radius: 30px;
  color: #7a4a3a;
  font-weight: 500;
  cursor: pointer;
  margin-right: 8px;
  transition: all 0.2s;
}
.edit-btn:hover {
  background: #ffaa94;
  transform: scale(0.96);
}
.delete-btn {
  background: #ffb3b3;
  border: none;
  padding: 4px 12px;
  border-radius: 30px;
  color: #b33b3b;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.delete-btn:hover {
  background: #ff9999;
  transform: scale(0.96);
}
.add-tag {
  display: flex;
  gap: 8px;
  align-items: center;
}
.add-tag input {
  padding: 8px 12px;
  border: 1px solid #ffe4d9;
  border-radius: 30px;
  outline: none;
  font-size: 0.9rem;
  transition: all 0.2s;
}
.add-tag input:focus {
  border-color: #ea9679;
  box-shadow: 0 0 0 2px rgba(234,150,121,0.2);
}
.add-tag-btn {
  background: #ea9679;
  border: none;
  padding: 8px 16px;
  border-radius: 30px;
  color: white;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.add-tag-btn:hover {
  background: #d4785c;
  transform: translateY(-1px);
}
.tags-list {
  margin-top: 1rem;
}
.tag-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ffe4d9;
}
.tag-item span {
  font-size: 0.95rem;
  color: #4a4a4a;
}
.tag-delete {
  background: none;
  border: none;
  color: #ea9679;
  cursor: pointer;
  padding: 4px 10px;
  border-radius: 30px;
  transition: all 0.2s;
}
.tag-delete:hover {
  background: #fff0eb;
  color: #d4785c;
}
.no-tags {
  text-align: center;
  padding: 1rem;
  color: #bcbcbc;
}
.modal {
  position: fixed;
  top: 0; left: 0;
  width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}
.modal-content {
  background: white;
  padding: 2rem;
  border-radius: 28px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 35px rgba(0,0,0,0.2);
}
.form-group {
  margin-bottom: 1.2rem;
}
.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.4rem;
  color: #ea9679;
}
.form-group input, .form-group textarea {
  width: 100%;
  padding: 10px 14px;
  border: 1px solid #ffe4d9;
  border-radius: 16px;
  outline: none;
  transition: all 0.2s;
}
.form-group input:focus, .form-group textarea:focus {
  border-color: #ea9679;
  box-shadow: 0 0 0 2px rgba(234,150,121,0.2);
}
.tag-select {
  width: 100%;
  height: 120px;
  border-radius: 16px;
  border: 1px solid #ffe4d9;
  padding: 8px;
}
.form-actions {
  display: flex;
  gap: 1rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
}
.form-actions button {
  padding: 8px 24px;
  border-radius: 40px;
  border: none;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}
.form-actions button[type="submit"] {
  background: #ea9679;
  color: white;
}
.form-actions button[type="submit"]:hover {
  background: #d4785c;
  transform: translateY(-2px);
}
.form-actions button[type="button"] {
  background: #f0f0f0;
  color: #666;
}
.form-actions button[type="button"]:hover {
  background: #e0e0e0;
}
@media (max-width: 768px) {
  .admin-container {
    margin: 70px auto 1rem;
    padding: 0.8rem;
  }
  .admin-layout {
    flex-direction: column;
  }
  .article-table th,
  .article-table td {
    padding: 8px;
    font-size: 0.8rem;
  }
  .section-header {
    flex-direction: column;
    align-items: stretch;
  }
  .add-tag {
    width: 100%;
  }
  .add-tag input {
    flex: 1;
  }
}
</style>