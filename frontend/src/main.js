import { createApp } from 'vue'
import VueFullPage from 'vue-fullpage.js'
import 'vue-fullpage.js/dist/style.css'
import 'fullpage.js/dist/fullpage.min.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'


axios.defaults.baseURL = ''   // 换成你的服务器公网IP

axios.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

const app = createApp(App)
app.use(router)
app.use(VueFullPage)
app.use(ElementPlus)   // 新增这一行
app.mount('#app')