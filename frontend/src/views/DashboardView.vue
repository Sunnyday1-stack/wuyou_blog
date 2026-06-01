<template>
  <div class="dashboard-container">
    <h1 class="page-title">仪表盘</h1>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-icon">👁️</div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.totalViews.toLocaleString() }}</div>
          <div class="stat-label">总阅读量</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📄</div>
        <div class="stat-body">
          <div class="stat-value">{{ stats.articleCount }}</div>
          <div class="stat-label">文章总数</div>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-body">
          <div class="stat-value">{{ avgViews }}</div>
          <div class="stat-label">篇均阅读</div>
        </div>
      </div>
    </div>

    <!-- 双栏布局：排行 + 分类占比 -->
    <div class="charts-row">
      <!-- 文章阅读量排行 -->
      <div class="chart-section">
        <h2 class="section-title">文章阅读量排行</h2>
        <div v-if="loading" class="loading-hint">加载中...</div>
        <div v-else-if="ranking.length === 0" class="empty-hint">暂无数据</div>
        <div v-else ref="barChartRef" class="chart-box"></div>
      </div>

      <!-- 分类占比圆环图 -->
      <div class="chart-section">
        <h2 class="section-title">分类占比</h2>
        <div v-if="loading" class="loading-hint">加载中...</div>
        <div v-else-if="categoryStats.length === 0" class="empty-hint">暂无数据</div>
        <div v-else ref="pieChartRef" class="chart-box"></div>
      </div>
    </div>

    <!-- 本周热门 TOP5 -->
    <div class="top5-section">
      <h2 class="section-title">本周热门阅读 TOP5</h2>
      <div v-if="loading" class="loading-hint">加载中...</div>
      <div v-else-if="ranking.length === 0" class="empty-hint">暂无数据</div>
      <div v-else class="top5-list">
        <div
          v-for="(item, idx) in ranking.slice(0, 5)"
          :key="item.id"
          class="top5-item"
        >
          <div class="top5-rank" :class="'rank-' + (idx + 1)">{{ idx + 1 }}</div>
          <div class="top5-body">
            <div class="top5-title">{{ item.title }}</div>
            <div class="top5-meta">
              <span class="top5-views">👁️ {{ item.viewCount }} 阅读</span>
            </div>
          </div>
          <div class="top5-bar-bg">
            <div
              class="top5-bar-fill"
              :style="{ width: getBarWidth(item.viewCount) + '%' }"
            ></div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'

const stats = ref({ totalViews: 0, articleCount: 0 })
const ranking = ref([])
const categoryStats = ref([])
const loading = ref(true)

const barChartRef = ref(null)
const pieChartRef = ref(null)
let barChartInstance = null
let pieChartInstance = null

const avgViews = computed(() => {
  if (stats.value.articleCount === 0) return '0'
  const avg = Math.round(stats.value.totalViews / stats.value.articleCount)
  return avg.toLocaleString()
})

const getBarWidth = (views) => {
  const max = ranking.value[0]?.viewCount || 1
  return Math.round((views / max) * 100)
}

const fetchData = async () => {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    const headers = { Authorization: `Bearer ${token}` }
    const [viewsRes, catRes] = await Promise.all([
      axios.get('/api/dashboard/views', { headers }),
      axios.get('/api/dashboard/category-stats', { headers })
    ])
    stats.value.totalViews = viewsRes.data.totalViews
    stats.value.articleCount = viewsRes.data.articleCount
    ranking.value = viewsRes.data.ranking || []
    categoryStats.value = catRes.data.stats || []
  } catch (err) {
    console.error('获取仪表盘数据失败', err)
  } finally {
    loading.value = false
    await nextTick()
    renderBarChart()
    renderPieChart()
  }
}

const renderBarChart = () => {
  if (!barChartRef.value || ranking.value.length === 0) return
  if (barChartInstance) barChartInstance.dispose()

  barChartInstance = echarts.init(barChartRef.value)

  const names = ranking.value.map(item => item.title)
  const values = ranking.value.map(item => item.viewCount)

  barChartInstance.setOption({
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      formatter: (params) => `${params[0].name}<br/>阅读量：<strong>${params[0].value}</strong>`
    },
    grid: { left: '3%', right: '8%', bottom: '3%', containLabel: true },
    xAxis: {
      type: 'value',
      axisLine: { show: false },
      axisTick: { show: false },
      splitLine: { lineStyle: { color: '#ffe4d9' } },
      axisLabel: { color: '#999' }
    },
    yAxis: {
      type: 'category',
      data: names,
      inverse: true,
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: {
        color: '#4a4a4a',
        fontSize: 13,
        formatter: (value) => value.length > 12 ? value.slice(0, 12) + '...' : value
      }
    },
    series: [{
      type: 'bar',
      data: values,
      barWidth: 18,
      itemStyle: {
        borderRadius: [0, 8, 8, 0],
        color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
          { offset: 0, color: '#f9a8d4' },
          { offset: 1, color: '#ea9679' }
        ])
      },
      label: { show: true, position: 'right', color: '#999', fontSize: 12 },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
            { offset: 0, color: '#f472b6' },
            { offset: 1, color: '#d4785c' }
          ])
        }
      }
    }]
  })
}

const renderPieChart = () => {
  if (!pieChartRef.value || categoryStats.value.length === 0) return
  if (pieChartInstance) pieChartInstance.dispose()

  pieChartInstance = echarts.init(pieChartRef.value)

  const colors = ['#ea9679', '#f9a8d4', '#f9d49a', '#a8d8ea', '#b8e6b8', '#d4b8e6', '#e6d4b8']

  pieChartInstance.setOption({
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} 篇 ({d}%)'
    },
    legend: {
      bottom: '0%',
      textStyle: { color: '#999', fontSize: 12 }
    },
    series: [{
      type: 'pie',
      radius: ['45%', '75%'],
      center: ['50%', '48%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 4,
        borderColor: '#fff',
        borderWidth: 3
      },
      label: {
        show: true,
        position: 'outside',
        formatter: '{b}\n{d}%',
        color: '#666',
        fontSize: 12
      },
      emphasis: {
        label: { fontSize: 16, fontWeight: 'bold' },
        scaleSize: 8
      },
      data: categoryStats.value.map((item, idx) => ({
        ...item,
        itemStyle: { color: colors[idx % colors.length] }
      }))
    }]
  })
}

const handleResize = () => {
  if (barChartInstance) barChartInstance.resize()
  if (pieChartInstance) pieChartInstance.resize()
}

onMounted(() => {
  fetchData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (barChartInstance) barChartInstance.dispose()
  if (pieChartInstance) pieChartInstance.dispose()
})
</script>

<style scoped>
.dashboard-container {
  max-width: 1200px;
  margin: 80px auto 0;
  padding: 2rem;
  min-height: calc(100vh - 80px);
  background: linear-gradient(135deg, #fff8f5 0%, #fff 100%);
}

.page-title {
  font-size: 2rem;
  font-weight: 700;
  color: #ea9679;
  margin-bottom: 1.8rem;
  letter-spacing: -0.01em;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  border-radius: 20px;
  padding: 1.5rem;
  display: flex;
  align-items: center;
  gap: 1rem;
  border: 1px solid #ffddd0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(234, 150, 121, 0.12);
}

.stat-icon {
  font-size: 2.2rem;
  width: 56px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff5f2;
  border-radius: 16px;
  flex-shrink: 0;
}

.stat-body { flex: 1; }

.stat-value {
  font-size: 1.8rem;
  font-weight: 700;
  color: #4a4a4a;
  line-height: 1.2;
}

.stat-label {
  font-size: 0.85rem;
  color: #bcbcbc;
  margin-top: 0.2rem;
}

/* 双栏布局 */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.chart-section {
  background: white;
  border-radius: 20px;
  padding: 1.5rem 1.8rem;
  border: 1px solid #ffddd0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.section-title {
  font-size: 1.2rem;
  font-weight: 700;
  color: #ea9679;
  margin-bottom: 1rem;
}

.chart-box {
  width: 100%;
  height: 380px;
}

.loading-hint,
.empty-hint {
  text-align: center;
  padding: 3rem;
  color: #bcbcbc;
  font-size: 0.95rem;
}

/* TOP5 列表 */
.top5-section {
  background: white;
  border-radius: 20px;
  padding: 1.5rem 1.8rem;
  border: 1px solid #ffddd0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.top5-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.top5-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.8rem 1rem;
  background: #fffaf8;
  border-radius: 14px;
  border: 1px solid #ffddd0;
  transition: transform 0.2s, box-shadow 0.2s;
}

.top5-item:hover {
  transform: translateX(6px);
  box-shadow: 0 4px 16px rgba(234, 150, 121, 0.15);
}

.top5-rank {
  width: 36px;
  height: 36px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 1rem;
  color: #fff;
  flex-shrink: 0;
  background: #ccc;
}

.top5-rank.rank-1 { background: linear-gradient(135deg, #f7971e, #ffd200); }
.top5-rank.rank-2 { background: linear-gradient(135deg, #bdc3c7, #a0aab5); }
.top5-rank.rank-3 { background: linear-gradient(135deg, #cd7f32, #d4a574); }
.top5-rank.rank-4,
.top5-rank.rank-5 { background: #ddd; }

.top5-body {
  flex: 1;
  min-width: 0;
}

.top5-title {
  font-size: 0.95rem;
  font-weight: 600;
  color: #4a4a4a;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 0.2rem;
}

.top5-meta {
  font-size: 0.8rem;
  color: #bcbcbc;
}

.top5-views {
  display: inline-flex;
  align-items: center;
  gap: 0.2rem;
}

.top5-bar-bg {
  width: 100px;
  height: 8px;
  background: #ffe4d9;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.top5-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #f9a8d4, #ea9679);
  border-radius: 4px;
  transition: width 0.8s ease;
}

@media (max-width: 1024px) {
  .charts-row {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    margin-top: 60px;
    min-height: calc(100vh - 60px);
    padding: 1rem;
  }
  .stats-row {
    grid-template-columns: 1fr;
  }
  .chart-box {
    height: 280px;
  }
  .page-title {
    font-size: 1.5rem;
  }
  .top5-bar-bg {
    display: none;
  }
}
</style>