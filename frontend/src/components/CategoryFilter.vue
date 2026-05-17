<template>
  <div class="category-filter">
    <button
      :class="['btn-category', { active: selectedId === null }]"
      @click="selectCategory(null)"
    >
      全部
    </button>
    <button
      v-for="cat in categories"
      :key="cat.id"
      :class="['btn-category', { active: selectedId === cat.id }]"
      @click="selectCategory(cat.id)"
    >
      {{ cat.name }}
    </button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const categories = ref([])
const selectedId = ref(null)
const emit = defineEmits(['select'])

const selectCategory = (id) => {
  selectedId.value = id
  emit('select', id)
}

const fetchCategories = async () => {
  try {
    const res = await axios.get('http://localhost:8080/api/categories')
    categories.value = res.data
  } catch (error) {
    console.error('获取分类失败', error)
  }
}

onMounted(() => {
  fetchCategories()
})
</script>

<style scoped>
.category-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  justify-content: center;
  margin-bottom: 2rem;
}

.btn-category {
  cursor: pointer;
  display: flex;
  gap: 0.5rem;
  border: none;
  transition: all 0.5s cubic-bezier(0.165, 0.84, 0.44, 1);
  border-radius: 100px;
  font-weight: 800;
  place-content: center;
  padding: 0.9rem 1.5rem;  /* 增加内边距，让按钮更大 */
  font-size: 1rem;        /* 增大字体 */
  line-height: 1.2rem;
  background-color: rgba(173, 143, 218, 0.4);
  box-shadow:
    inset 0 1px 0 0 rgba(255, 255, 255, 0.04),
    inset 0 0 0 1px rgba(255, 255, 255, 0.04);
  color: #fff;
}

.btn-category:hover {
  background-color: rgba(173, 143, 218, 0.6);
  box-shadow:
    inset 0 1px 0 0 rgba(255, 255, 255, 0.08),
    inset 0 0 0 1px rgba(252, 232, 3, 0.08);
  color: #fce803;
  transform: translate(0, -0.25rem);
}

.btn-category.active {
  background-color: rgba(173, 143, 218, 0.7);
  box-shadow:
    inset 0 1px 0 0 rgba(255, 255, 255, 0.08),
    inset 0 0 0 1px #fce803;
  color: #e7b1e3;  /* 选中字体颜色改为 #e7b1e3 */
}
</style>