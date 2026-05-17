<template>
  <div class="weather-card">
    <div class="info-section">
      <div class="left-side">
        <div class="weather">
          <div class="weather-icon">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="currentColor" width="40" height="40">
              <path d="M6.5 5.5L12 1l5.5 4.5v12L12 22l-5.5-4.5v-12z"/>
            </svg>
          </div>
          <div class="temperature">{{ temperature }}°C</div>
        </div>
        <div>{{ weatherDesc }}</div>
      </div>
      <div class="right-side">
        <div class="hour">{{ currentTime }}</div>
        <div class="date">{{ currentDate }}</div>
      </div>
      <div class="background-design">
        <div class="circle"></div>
        <div class="circle"></div>
        <div class="circle"></div>
      </div>
    </div>
    <div class="days-section">
      <button v-for="day in weekForecast" :key="day.weekday">
        <div class="icon-weather-day">
          <svg viewBox="0 0 24 24" width="18" height="18" fill="currentColor">
            <path d="M12 3c-4.97 0-9 4.03-9 9s4.03 9 9 9 9-4.03 9-9-4.03-9-9-9z"/>
          </svg>
        </div>
        <div class="day">{{ day.weekday }}</div>
        <div class="temp">{{ day.temp }}°</div>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

// 创建独立的 axios 实例，不携带任何全局配置（避免 Authorization 头）
const weatherApi = axios.create()

const temperature = ref('--')
const weatherDesc = ref('加载中...')
const currentTime = ref('')
const currentDate = ref('')
const weekForecast = ref([])

let timer = null

const fetchWeather = async () => {
  try {
    // 使用独立实例请求，不会带上主应用的 Authorization 头
    const res = await weatherApi.get('https://wttr.in?format=j1')
    const data = res.data
    const current = data.current_condition[0]
    temperature.value = current.temp_C
    weatherDesc.value = current.weatherDesc[0].value
    // 获取未来三天预报（包含星期和温度）
    const days = data.weather.slice(0, 3)
    weekForecast.value = days.map(day => ({
      weekday: new Date(day.date).toLocaleDateString('zh-CN', { weekday: 'short' }),
      temp: day.avgtempC
    }))
  } catch (error) {
    console.error('获取天气失败', error)
    weatherDesc.value = '天气获取失败'
  }
}

const updateTime = () => {
  const now = new Date()
  currentTime.value = now.toLocaleTimeString('zh-CN', { hour12: false })
  currentDate.value = now.toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })
}

onMounted(() => {
  fetchWeather()
  updateTime()
  timer = setInterval(updateTime, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
/* 卡片容器：主色 #a6c8f1 透明度 60% + 毛玻璃 */
.weather-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 180px;
  width: 280px;
  border-radius: 25px;
  background: rgba(166, 200, 241, 0.6); /* #a6c8f1 60% */
  backdrop-filter: blur(8px);
  overflow: hidden;
  transition: 100ms ease;
  box-shadow: rgba(0, 0, 0, 0.15) 2px 3px 4px;
  font-family: system-ui, -apple-system, sans-serif;
}

/* ---------- Info section ---------- */
.info-section {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 75%;
  color: white;
}

.left-side {
  display: flex;
  flex-direction: column;
  justify-content: space-around;
  height: 100%;
  z-index: 1;
  padding-left: 18px;
}

.weather {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 5px;
}

.weather div:nth-child(1) {
  width: 40%;
  height: auto;
}

.temperature {
  font-size: 34pt;
  font-weight: 500;
  line-height: 1;
}

.right-side {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  justify-content: space-around;
  height: 100%;
  padding-right: 18px;
  z-index: 1;
}

.right-side > div {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.hour {
  font-size: 19pt;
  line-height: 1em;
}

.date {
  font-size: 15px;
}

/* ---------- Background ---------- */
.background-design {
  position: absolute;
  height: 100%;
  width: 100%;
  background-color: rgba(166, 200, 241, 0.4);
  overflow: hidden;
  top: 0;
  left: 0;
  z-index: -1;
}

.circle {
  background-color: rgba(166, 200, 241, 0.7);
  opacity: 0.8;
}

.circle:nth-child(1) {
  position: absolute;
  top: -80%;
  right: -50%;
  width: 300px;
  height: 300px;
  border-radius: 50%;
}

.circle:nth-child(2) {
  position: absolute;
  top: -70%;
  right: -30%;
  width: 210px;
  height: 210px;
  border-radius: 50%;
}

.circle:nth-child(3) {
  position: absolute;
  top: -35%;
  right: -8%;
  width: 100px;
  height: 100px;
  border-radius: 50%;
}

/* ---------- Days section ---------- */
.days-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 25%;
  background-color: rgba(166, 200, 241, 0.5);
  gap: 2px;
  box-shadow: inset 0px 2px 5px rgba(0,0,0,0.1);
}

.days-section button {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  background-color: rgba(166, 200, 241, 0.7);
  box-shadow: inset 0px 2px 5px rgba(0,0,0,0.1);
  cursor: pointer;
  transition: 100ms ease;
  gap: 2px;
  border: none;
  color: #2c3e50;
}

.days-section button:hover {
  scale: 0.9;
  border-radius: 10px;
}

.days-section .day {
  font-size: 10pt;
  font-weight: 500;
  opacity: 0.9;
}

.days-section .temp {
  font-size: 8pt;
  opacity: 0.7;
}

.icon-weather-day {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}
</style>