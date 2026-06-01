<template>
  <div class="auth-container">
    <!-- 动态装饰元素 -->
    <div class="bg-shapes">
      <div class="shape shape-1"></div>
      <div class="shape shape-2"></div>
      <div class="shape shape-3"></div>
      <div class="shape shape-4"></div>
      <div class="shape shape-5"></div>
      <div class="shape shape-6"></div>
    </div>

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
      <button class="button-confirm" type="submit">
        <span>登录</span>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
      </button>
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
    const res = await axios.post('/api/auth/login', {
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
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #0f2027, #203a43, #2c5364);
  background-size: 400% 400%;
  animation: gradientShift 12s ease infinite;
}
@keyframes gradientShift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 50%; }
  100% { background-position: 0% 50%; }
}

/* 浮动装饰形状 */
.bg-shapes {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}
.shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.08;
}
.shape-1 {
  width: 300px; height: 300px;
  background: #2dd4bf;
  top: -10%; left: -5%;
  animation: float1 14s ease-in-out infinite;
}
.shape-2 {
  width: 200px; height: 200px;
  background: #60a5fa;
  bottom: -8%; right: -5%;
  animation: float2 18s ease-in-out infinite;
}
.shape-3 {
  width: 150px; height: 150px;
  background: #f472b6;
  top: 40%; right: 15%;
  animation: float3 11s ease-in-out infinite;
}
.shape-4 {
  width: 100px; height: 100px;
  background: #fbbf24;
  top: 15%; right: 30%;
  animation: float1 16s ease-in-out infinite 2s;
}
.shape-5 {
  width: 250px; height: 250px;
  background: #34d399;
  bottom: 20%; left: 10%;
  animation: float2 20s ease-in-out infinite 1s;
}
.shape-6 {
  width: 80px; height: 80px;
  background: #a78bfa;
  top: 60%; left: 30%;
  animation: float3 13s ease-in-out infinite 3s;
}
@keyframes float1 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(40px, -30px) scale(1.1); }
  66% { transform: translate(-20px, 20px) scale(0.9); }
}
@keyframes float2 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  33% { transform: translate(-50px, 30px) scale(1.15); }
  66% { transform: translate(30px, -40px) scale(0.85); }
}
@keyframes float3 {
  0%, 100% { transform: translate(0, 0) scale(1); }
  50% { transform: translate(20px, 50px) scale(1.2); }
}

/* 表单卡片 */
.form {
  position: relative;
  z-index: 1;
  padding: 36px 32px;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 22px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  width: 100%;
  max-width: 400px;
  animation: formIn 0.7s cubic-bezier(0.16, 1, 0.3, 1);
}
@keyframes formIn {
  from { opacity: 0; transform: translateY(30px) scale(0.96); }
  to { opacity: 1; transform: translateY(0) scale(1); }
}

.title {
  color: #fff;
  font-weight: 700;
  font-size: 26px;
  text-align: center;
  letter-spacing: 2px;
}
.title span {
  color: rgba(255, 255, 255, 0.55);
  font-weight: 400;
  font-size: 14px;
  display: block;
  margin-top: 6px;
  letter-spacing: 1px;
}

.input {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.15);
  background: rgba(255, 255, 255, 0.06);
  font-size: 15px;
  color: #fff;
  padding: 0 16px;
  outline: none;
  transition: all 0.3s ease;
}
.input::placeholder { color: rgba(255, 255, 255, 0.35); }
.input:focus {
  border-color: #2dd4bf;
  background: rgba(45, 212, 191, 0.08);
  box-shadow: 0 0 0 3px rgba(45, 212, 191, 0.15);
  transform: translateY(-1px);
}

.button-confirm {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #2dd4bf, #0d9488);
  font-size: 16px;
  font-weight: 600;
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s ease;
  margin-top: 4px;
}
.button-confirm:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(45, 212, 191, 0.35);
}
.button-confirm:active { transform: translateY(0); }
.button-confirm svg {
  transition: transform 0.3s ease;
}
.button-confirm:hover svg {
  transform: translateX(4px);
}

.register-link {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.55);
}
.register-link a {
  color: #2dd4bf;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.register-link a:hover { color: #5eeadb; }
@media (max-width: 480px) {
  .form {
    padding: 24px 20px;
    margin: 0 1rem;
    max-width: 100%;
  }
  .title { font-size: 22px; }
  .input { height: 44px; font-size: 14px; }
  .button-confirm { height: 44px; }
}
</style>
