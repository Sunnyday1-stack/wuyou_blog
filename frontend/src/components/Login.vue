<template>
  <div class="auth-container">
    <form class="form" @submit.prevent="handleLogin">
      <div class="title">
        登录
        <span>欢迎回来</span>
      </div>
      <input
        v-model="username"
        class="input"
        type="text"
        placeholder="用户名"
        required
      />
      <input
        v-model="password"
        class="input"
        type="password"
        placeholder="密码"
        required
      />
      <button class="button-confirm" type="submit">登录</button>
      <p class="register-link">
        没有账号？<router-link to="/register">立即注册</router-link>
      </p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const username = ref('')
const password = ref('')
const router = useRouter()

const handleLogin = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/auth/login', {
      username: username.value,
      password: password.value
    })
    if (res.data.success) {
      localStorage.setItem('token', res.data.token)
      localStorage.setItem('username', res.data.username)
      if (res.data.userId) localStorage.setItem('userId', res.data.userId)
      if (res.data.role) localStorage.setItem('role', res.data.role)
      alert('登录成功')
      router.push('/')
    } else {
      alert(res.data.message)
    }
  } catch (error) {
    console.error('登录失败', error)
    alert('登录失败，请检查后端服务')
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  
  /* 背景图片 + 深色柔和滤镜 + 模糊 */
  background: url('/login-bg.jpg') center center / cover no-repeat fixed;
  position: relative;
}

/* 背景遮罩层：让图片变暗、变柔，不影响表单 */
.auth-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.35); /* 变暗 */
  backdrop-filter: blur(6px);     /* 背景模糊 */
  z-index: 0;
}

/* 表单样式 */
.form {
  --input-focus: #2d8cf0;
  --font-color: #ffffff;
  --font-color-sub: #e0e0e0;
  --bg-color: rgba(255, 255, 255, 0.15); 
  --main-color: rgba(255, 255, 255, 0.3);
  padding: 28px;
  background: var(--bg-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  border-radius: 16px;
  border: 1.5px solid var(--main-color);
  width: 100%;
  max-width: 420px;
  backdrop-filter: blur(12px); /* 卡片更强毛玻璃 */
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.title {
  color: var(--font-color);
  font-weight: 900;
  font-size: 24px;
  margin-bottom: 5px;
  text-align: center;
}

.title span {
  color: var(--font-color-sub);
  font-weight: 500;
  font-size: 16px;
  display: block;
  margin-top: 4px;
}

.input {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  border: 1.5px solid var(--main-color);
  background: rgba(255, 255, 255, 0.2);
  font-size: 15px;
  font-weight: 500;
  color: #fff;
  padding: 0 14px;
  outline: none;
  box-sizing: border-box;
  backdrop-filter: blur(4px);
}

.input::placeholder {
  color: #d0d0d0;
}

.input:focus {
  border-color: #b1e7e5;
  background: rgba(255, 255, 255, 0.25);
}

.button-confirm {
  width: 100%;
  height: 46px;
  border-radius: 10px;
  border: none;
  background: rgba(177, 231, 229, 0.85);
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.button-confirm:hover {
  background: rgba(177, 231, 229, 1);
}

.register-link {
  margin-top: 10px;
  font-size: 14px;
  color: #fff;
}

.register-link a {
  color: #b1e7e5;
  text-decoration: none;
  font-weight: 500;
}
</style>