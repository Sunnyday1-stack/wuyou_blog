<template>
  <div class="tag-cloud">
    <h3>热门标签</h3>
    <div class="tags">
      <span 
        v-for="tag in tags" 
        :key="tag.id" 
        class="tag"
        @click="selectTag(tag.id)"
      >
        {{ tag.name }}
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const emit = defineEmits(['select'])
const tags = ref([])

const fetchTags = async () => {
  try {
    const res = await axios.get('/api/tags')
    tags.value = res.data
  } catch (error) {
    console.error('获取标签失败', error)
  }
}

const selectTag = (tagId) => {
  emit('select', tagId)
}

onMounted(() => {
  fetchTags()
})
</script>

<style scoped>
.tag-cloud {
  margin-top: 2rem;
}
.tag-cloud h3 {
  margin-bottom: 0.5rem;
}
.tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}
.tag {
  background: #e2e8f0;
  padding: 0.2rem 0.8rem;
  border-radius: 20px;
  cursor: pointer;
  transition: background 0.2s;
}
.tag:hover {
  background: #cbd5e0;
}
</style>