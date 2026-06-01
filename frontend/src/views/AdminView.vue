<template>
  <div class="admin-container">
    <div class="admin-header">
      <h1>后台管理</h1>
      <span class="admin-badge">管理员</span>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon articles-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ articles.length }}</span>
          <span class="stat-label">文章总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon categories-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ categories.length }}</span>
          <span class="stat-label">分类数量</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon tags-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20.59 13.41l-7.17 7.17a2 2 0 0 1-2.83 0L2 12V2h10l8.59 8.59a2 2 0 0 1 0 2.82z"/><line x1="7" y1="7" x2="7.01" y2="7"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ tags.length }}</span>
          <span class="stat-label">标签数量</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon users-icon">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ users.length }}</span>
          <span class="stat-label">用户数量</span>
        </div>
      </div>
    </div>

    <div class="tab-nav">
      <button
        v-for="tab in tabs"
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key"
      >
        {{ tab.label }}
      </button>
    </div>

    <div v-show="activeTab === 'articles'" class="tab-panel">
      <div class="panel-header">
        <h2>文章管理</h2>
        <button class="create-btn" @click="openCreateModal">+ 新建文章</button>
      </div>
      <div class="table-wrap">
        <table class="data-table">
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
              <td class="td-id">{{ article.id }}</td>
              <td class="td-title">{{ article.title }}</td>
              <td><span class="category-pill">{{ article.categoryId || '未分类' }}</span></td>
              <td class="td-date">{{ formatDate(article.createTime) }}</td>
              <td class="td-date">{{ formatDate(article.updateTime) }}</td>
              <td class="td-actions">
                <button class="action-btn edit-btn" @click="openEditModal(article)">编辑</button>
                <button class="action-btn delete-btn" @click="deleteArticle(article.id)">删除</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="articles.length === 0" class="empty-state">
          <p>暂无文章，点击右上角按钮创建</p>
        </div>
      </div>
    </div>

    <div v-show="activeTab === 'categories'" class="tab-panel">
      <div class="panel-header">
        <h2>分类管理</h2>
      </div>
      <div class="add-row">
        <input
          type="text"
          v-model="newCategoryName"
          placeholder="输入新分类名称..."
          class="add-input"
          @keyup.enter="createCategory"
        />
        <button class="add-btn" @click="createCategory">添加分类</button>
      </div>
      <div class="card-grid">
        <div v-for="cat in categories" :key="cat.id" class="manage-card">
          <div class="manage-card-body">
            <span class="manage-card-name">{{ cat.name }}</span>
          </div>
          <button class="manage-card-delete" @click="deleteCategory(cat.id)" title="删除分类">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
        <div v-if="categories.length === 0" class="empty-state">
          <p>暂无分类，请在上方添加</p>
        </div>
      </div>
    </div>

    <div v-show="activeTab === 'tags'" class="tab-panel">
      <div class="panel-header">
        <h2>标签管理</h2>
      </div>
      <div class="add-row">
        <input
          type="text"
          v-model="newTagName"
          placeholder="输入新标签名称..."
          class="add-input"
          @keyup.enter="createTag"
        />
        <button class="add-btn" @click="createTag">添加标签</button>
      </div>
      <div class="card-grid">
        <div v-for="tag in tags" :key="tag.id" class="manage-card">
          <div class="manage-card-body">
            <span class="manage-card-name">{{ tag.name }}</span>
          </div>
          <button class="manage-card-delete" @click="deleteTag(tag.id)" title="删除标签">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"/><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/></svg>
          </button>
        </div>
        <div v-if="tags.length === 0" class="empty-state">
          <p>暂无标签，请在上方添加</p>
        </div>
      </div>
    </div>

    <div v-show="activeTab === 'users'" class="tab-panel">
      <div class="panel-header">
        <h2>账号管理</h2>
      </div>
      <div class="table-wrap">
        <table class="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>角色</th>
              <th>注册时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td class="td-id">{{ user.id }}</td>
              <td class="td-title">{{ user.username }}</td>
              <td>{{ user.email || '-' }}</td>
              <td>
                <span :class="['role-pill', user.role === 'ADMIN' ? 'role-admin' : 'role-user']">
                  {{ user.role === 'ADMIN' ? '管理员' : '普通用户' }}
                </span>
              </td>
              <td class="td-date">{{ formatDate(user.createTime) }}</td>
              <td>
                <button
                  v-if="user.role !== 'ADMIN'"
                  class="action-btn delete-btn"
                  @click="deleteUser(user.id)"
                >删除</button>
                <span v-else class="td-hint">&mdash;</span>
              </td>
            </tr>
          </tbody>
        </table>
        <div v-if="users.length === 0" class="empty-state">
          <p>暂无注册用户</p>
        </div>
      </div>
    </div>

    <div v-if="showModal" class="modal" @click.self="closeModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEdit ? '编辑文章' : '新建文章' }}</h2>
          <button class="modal-close" @click="closeModal">&times;</button>
        </div>
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
            <div class="upload-row">
              <el-upload
                class="upload-btn"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :on-success="handleUploadSuccess"
                :on-error="handleUploadError"
                :show-file-list="false"
                accept="image/*"
              >
                <el-button size="small" type="primary">上传图片</el-button>
              </el-upload>
              <span class="upload-hint">支持 jpg/png，不超过 10MB</span>
            </div>
            <textarea v-model="form.content" rows="12" class="content-editor"></textarea>
          </div>
          <div class="form-group">
            <label>分类</label>
            <input v-model.number="form.categoryId" type="number" placeholder="分类 ID" />
          </div>
          <div class="form-group">
            <label>标签</label>
            <select v-model="selectedTagIds" multiple class="tag-select">
              <option v-for="tag in tags" :key="tag.id" :value="tag.id">{{ tag.name }}</option>
            </select>
          </div>
          <div class="form-actions">
            <button type="button" @click="closeModal">取消</button>
            <button type="submit" :disabled="submitting">
              {{ submitting ? '保存中...' : (isEdit ? '更新' : '发布') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const articles = ref([])
const tags = ref([])
const showModal = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const categories = ref([])
const newCategoryName = ref('')
const newTagName = ref('')
const tabs = [
  { key: 'articles', label: '文章管理' },
  { key: 'categories', label: '分类管理' },
  { key: 'tags', label: '标签管理' },
  { key: 'users', label: '账号管理' }
]
const activeTab = ref('articles')
const users = ref([])

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
    const res = await axios.get('/api/articles')
    articles.value = res.data
  } catch (error) {
    console.error('获取文章失败', error)
  }
}

const fetchTags = async () => {
  try {
    const res = await axios.get('/api/tags')
    tags.value = res.data
  } catch (error) {
    console.error('获取标签失败', error)
  }
}

const fetchCategories = async () => {
  try {
    const res = await axios.get('/api/categories')
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
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
    const res = await axios.get(`/api/articles/${article.id}/tags`)
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
      await axios.put(`/api/articles/${articleId}`, form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      await axios.put(`/api/articles/${articleId}/tags`, selectedTagIds.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      alert('更新成功')
    } else {
      const res = await axios.post('/api/articles', form.value, {
        headers: { Authorization: `Bearer ${token}` }
      })
      articleId = res.data.id
      if (selectedTagIds.value.length) {
        await axios.put(`/api/articles/${articleId}/tags`, selectedTagIds.value, {
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

const uploadUrl = '/api/upload/image'
const uploadHeaders = {
  Authorization: `Bearer ${localStorage.getItem('token')}`
}

const handleUploadSuccess = (response, file) => {
  if (response.success) {
    const imageMarkdown = `![](${response.url})`
    form.value.content += '\n' + imageMarkdown + '\n'
    ElMessage.success('图片已插入内容末尾')
  } else {
    ElMessage.error('上传失败：' + (response.message || '未知错误'))
  }
}

const handleUploadError = (error) => {
  console.error('上传出错', error)
  ElMessage.error('上传失败，请检查网络或后端服务')
}

const deleteArticle = async (id) => {
  if (!confirm('确定删除该文章吗？')) return
  const token = localStorage.getItem('token')
  try {
    await axios.delete(`/api/articles/${id}`, {
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
    await axios.post('/api/tags', { name: newTagName.value }, {
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
    await axios.delete(`/api/tags/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    alert('标签删除成功')
    await fetchTags()
  } catch (error) {
    console.error('删除标签失败', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

const createCategory = async () => {
  if (!newCategoryName.value.trim()) return
  const token = localStorage.getItem('token')
  try {
    const rawSlug = newCategoryName.value.toLowerCase().replace(/\s+/g, '-').replace(/[^a-z0-9-]/g, '')
    const slug = rawSlug.length > 1 ? rawSlug : 'cat-' + Date.now()
    const finalSlug = slug + '-' + Date.now().toString(36)
    await axios.post('/api/categories', { name: newCategoryName.value, slug: finalSlug }, {
      headers: { Authorization: 'Bearer ' + token }
    })
    alert('分类添加成功')
    newCategoryName.value = ''
    await fetchCategories()
  } catch (error) {
    console.error('创建分类失败', error)
    alert(error.response?.data?.message || '创建失败')
  }
}

const deleteCategory = async (id) => {
  if (!confirm('确定删除该分类吗？')) return
  const token = localStorage.getItem('token')
  try {
    await axios.delete('/api/categories/' + id, {
      headers: { Authorization: 'Bearer ' + token }
    })
    alert('分类删除成功')
    await fetchCategories()
  } catch (error) {
    console.error('删除分类失败', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

const fetchUsers = async () => {
  try {
    const token = localStorage.getItem('token')
    const res = await axios.get('/api/admin/users', {
      headers: { Authorization: 'Bearer ' + token }
    })
    users.value = res.data
  } catch (error) {
    console.error('获取用户失败', error)
  }
}

const deleteUser = async (id) => {
  if (!confirm('确定删除该用户账号吗？此操作不可撤销。')) return
  const token = localStorage.getItem('token')
  try {
    await axios.delete('/api/admin/users/' + id, {
      headers: { Authorization: 'Bearer ' + token }
    })
    alert('账号已删除')
    await fetchUsers()
  } catch (error) {
    console.error('删除用户失败', error)
    alert(error.response?.data?.message || '删除失败')
  }
}

onMounted(() => {
  fetchArticles()
  fetchTags()
  fetchCategories()
  fetchUsers()
})
</script>
<style scoped>
* {
  box-sizing: border-box;
}

.admin-container {
  max-width: 1200px;
  margin: 80px auto 2rem;
  padding: 0 1.5rem;
  min-height: calc(100vh - 80px);
}

.admin-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.admin-header h1 {
  font-size: 1.8rem;
  font-weight: 800;
  color: #2d2d2d;
  margin: 0;
}

.admin-badge {
  background: linear-gradient(135deg, #ea9679, #d4785c);
  color: white;
  padding: 4px 14px;
  border-radius: 30px;
  font-size: 0.75rem;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.stat-card {
  background: white;
  border-radius: 16px;
  padding: 1.2rem 1rem;
  display: flex;
  align-items: center;
  gap: 0.8rem;
  border: 1px solid #f0e8e4;
  transition: all 0.25s;
  cursor: default;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.06);
}

.stat-icon {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.articles-icon { background: #fff0eb; color: #ea9679; }
.categories-icon { background: #e8f4fd; color: #4a90d9; }
.tags-icon { background: #f0e6f6; color: #8b5cf6; }
.users-icon { background: #e6f7e6; color: #4caf50; }

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 800;
  color: #2d2d2d;
  line-height: 1.2;
}

.stat-label {
  font-size: 0.78rem;
  color: #999;
  margin-top: 2px;
}

.tab-nav {
  display: flex;
  gap: 0;
  background: white;
  border-radius: 14px;
  padding: 5px;
  margin-bottom: 1.2rem;
  border: 1px solid #f0e8e4;
}

.tab-btn {
  flex: 1;
  padding: 10px 16px;
  border: none;
  background: transparent;
  color: #888;
  font-size: 0.9rem;
  font-weight: 500;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.tab-btn:hover {
  color: #ea9679;
  background: #fff5f2;
}

.tab-btn.active {
  background: #ea9679;
  color: white;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(234,150,121,0.3);
}

.tab-panel {
  background: white;
  border-radius: 16px;
  padding: 1.5rem;
  border: 1px solid #f0e8e4;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
  flex-wrap: wrap;
  gap: 0.8rem;
}

.panel-header h2 {
  font-size: 1.2rem;
  font-weight: 700;
  color: #2d2d2d;
  margin: 0;
}

.create-btn {
  background: linear-gradient(135deg, #ea9679, #d4785c);
  color: white;
  border: none;
  padding: 0.55rem 1.3rem;
  border-radius: 30px;
  font-weight: 600;
  font-size: 0.88rem;
  cursor: pointer;
  transition: all 0.2s;
}

.create-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(234,150,121,0.35);
}

.add-btn {
  background: #ea9679;
  color: white;
  border: none;
  padding: 0.55rem 1.3rem;
  border-radius: 30px;
  font-weight: 600;
  font-size: 0.88rem;
  cursor: pointer;
  transition: all 0.2s;
  white-space: nowrap;
}

.add-btn:hover {
  background: #d4785c;
}

.add-row {
  display: flex;
  gap: 0.6rem;
  margin-bottom: 1.2rem;
}

.add-input {
  flex: 1;
  padding: 0.6rem 1rem;
  border: 1px solid #e8ddd6;
  border-radius: 30px;
  outline: none;
  font-size: 0.9rem;
  transition: all 0.2s;
}

.add-input:focus {
  border-color: #ea9679;
  box-shadow: 0 0 0 3px rgba(234,150,121,0.12);
}

.table-wrap {
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
  min-width: 600px;
}

.data-table th {
  background: #faf7f5;
  color: #888;
  font-weight: 600;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 12px 14px;
  text-align: left;
  border-bottom: 2px solid #f0e8e4;
}

.data-table td {
  padding: 12px 14px;
  border-bottom: 1px solid #f5efeb;
  font-size: 0.9rem;
  color: #555;
}

.data-table tbody tr {
  transition: background 0.15s;
}

.data-table tbody tr:hover {
  background: #fdf9f7;
}

.td-id {
  color: #bbb;
  font-size: 0.8rem;
  width: 50px;
}

.td-title {
  font-weight: 600;
  color: #2d2d2d;
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.td-date {
  font-size: 0.8rem;
  color: #aaa;
  white-space: nowrap;
}

.td-actions {
  white-space: nowrap;
}

.td-hint {
  color: #ccc;
}

.action-btn {
  border: none;
  padding: 5px 14px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.15s;
}

.action-btn.edit-btn {
  background: #fff0eb;
  color: #d4785c;
  margin-right: 6px;
}

.action-btn.edit-btn:hover {
  background: #ffe4d9;
}

.action-btn.delete-btn {
  background: #fff0f0;
  color: #d9534f;
}

.action-btn.delete-btn:hover {
  background: #ffe0e0;
}

.category-pill {
  display: inline-block;
  background: #fff5f2;
  color: #ea9679;
  padding: 2px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.role-pill {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 0.78rem;
  font-weight: 500;
}

.role-admin {
  background: #fff0eb;
  color: #ea9679;
}

.role-user {
  background: #f5f5f5;
  color: #888;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 0.7rem;
}

.manage-card {
  background: #faf7f5;
  border-radius: 12px;
  padding: 0 0.8rem 0 1rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.manage-card:hover {
  border-color: #ffe4d9;
  background: #fffbf9;
}

.manage-card-body {
  padding: 0.7rem 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.manage-card-name {
  font-size: 0.9rem;
  color: #4a4a4a;
  font-weight: 500;
}

.manage-card-delete {
  background: none;
  border: none;
  color: #ccc;
  cursor: pointer;
  padding: 6px;
  border-radius: 8px;
  transition: all 0.15s;
  display: flex;
  align-items: center;
}

.manage-card-delete:hover {
  color: #d9534f;
  background: #fff0f0;
}

.empty-state {
  text-align: center;
  padding: 2.5rem 1rem;
  color: #ccc;
}

.empty-state p {
  margin-top: 0.5rem;
  font-size: 0.9rem;
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
  backdrop-filter: blur(4px);
}

.modal-content {
  background: white;
  border-radius: 20px;
  width: 92%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 50px rgba(0,0,0,0.15);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem 1.5rem 0;
  position: sticky;
  top: 0;
  background: white;
  z-index: 1;
}

.modal-header h2 {
  margin: 0;
  font-size: 1.3rem;
  color: #2d2d2d;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.5rem;
  color: #bbb;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: all 0.15s;
}

.modal-close:hover {
  color: #555;
  background: #f5f5f5;
}

.modal-content form {
  padding: 1.2rem 1.5rem 1.5rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  font-weight: 600;
  margin-bottom: 0.35rem;
  color: #555;
  font-size: 0.88rem;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 0.65rem 0.9rem;
  border: 1px solid #e8ddd6;
  border-radius: 12px;
  outline: none;
  font-size: 0.9rem;
  font-family: inherit;
  transition: all 0.2s;
  resize: vertical;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #ea9679;
  box-shadow: 0 0 0 3px rgba(234,150,121,0.1);
}

.content-editor {
  font-family: 'Consolas', 'Fira Code', monospace;
  font-size: 0.85rem;
  line-height: 1.6;
}

.upload-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 0.75rem;
  color: #bbb;
}

.tag-select {
  width: 100%;
  min-height: 100px;
  border-radius: 12px;
  border: 1px solid #e8ddd6;
  padding: 8px;
  font-size: 0.9rem;
}

.form-actions {
  display: flex;
  gap: 0.8rem;
  justify-content: flex-end;
  margin-top: 1.5rem;
  padding-top: 1rem;
  border-top: 1px solid #f0e8e4;
}

.form-actions button {
  padding: 0.6rem 1.5rem;
  border-radius: 30px;
  border: none;
  font-weight: 600;
  font-size: 0.88rem;
  cursor: pointer;
  transition: all 0.2s;
}

.form-actions button[type="button"] {
  background: #f5f5f5;
  color: #666;
}

.form-actions button[type="button"]:hover {
  background: #e8e8e8;
}

.form-actions button[type="submit"] {
  background: linear-gradient(135deg, #ea9679, #d4785c);
  color: white;
}

.form-actions button[type="submit"]:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 14px rgba(234,150,121,0.3);
}

.form-actions button[type="submit"]:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

/* ==================== RESPONSIVE ==================== */

@media (max-width: 1024px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .card-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
}

@media (max-width: 768px) {
  .admin-container {
    margin: 60px auto 1rem;
    padding: 0 0.8rem;
    min-height: calc(100vh - 60px);
  }

  .admin-header h1 {
    font-size: 1.4rem;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 0.6rem;
  }

  .stat-card {
    padding: 0.8rem;
    gap: 0.5rem;
  }

  .stat-icon {
    width: 36px;
    height: 36px;
    border-radius: 10px;
  }

  .stat-icon svg {
    width: 18px;
    height: 18px;
  }

  .stat-value {
    font-size: 1.2rem;
  }

  .stat-label {
    font-size: 0.7rem;
  }

  .tab-nav {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
  }

  .tab-btn {
    flex: none;
    padding: 8px 14px;
    font-size: 0.82rem;
  }

  .tab-panel {
    padding: 1rem 0.8rem;
    border-radius: 12px;
  }

  .data-table th,
  .data-table td {
    padding: 8px 10px;
    font-size: 0.78rem;
  }

  .td-title {
    max-width: 120px;
  }

  .td-date {
    display: none;
  }

  .panel-header {
    flex-direction: column;
    align-items: stretch;
  }

  .panel-header .create-btn {
    width: 100%;
    text-align: center;
  }

  .add-row {
    flex-direction: column;
  }

  .add-btn {
    width: 100%;
    text-align: center;
  }

  .card-grid {
    grid-template-columns: 1fr;
  }

  .modal-content {
    border-radius: 16px;
    max-height: 95vh;
  }

  .modal-header {
    padding: 1rem 1rem 0;
  }

  .modal-content form {
    padding: 1rem;
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr 1fr;
    gap: 0.5rem;
  }

  .stat-card {
    padding: 0.7rem 0.7rem;
  }

  .stat-value {
    font-size: 1.1rem;
  }

  .data-table th:nth-child(1),
  .data-table td:nth-child(1) {
    display: none;
  }
}
</style>