import { createApp } from 'vue'
import VueFullPage from 'vue-fullpage.js'
import 'vue-fullpage.js/dist/style.css'
import 'fullpage.js/dist/fullpage.min.css'
import App from './App.vue'
import router from './router'
import axios from 'axios'


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
app.mount('#app')
