<template>
  <div class="auth-container">
    <form class="form" @submit.prevent="handleRegister">
      <div class="title">
        注册
        <span>创建新账号</span>
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
      <input
        v-model="email"
        class="input"
        type="email"
        placeholder="邮箱 (可选)"
      />
      <button class="button-confirm" type="submit">注册</button>
      <p class="register-link">
        已有账号？<router-link to="/login">去登录</router-link>
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
const email = ref('')
const router = useRouter()

const handleRegister = async () => {
  try {
    const res = await axios.post('http://localhost:8080/api/auth/register', {
      username: username.value,
      password: password.value,
      email: email.value
    })
    if (res.data.success) {
      alert('注册成功，请登录')
      router.push('/login')
    } else {
      alert(res.data.message)
    }
  } catch (error) {
    console.error('注册失败', error)
    alert('注册失败')
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: url('/register-bg.jpg') center center / cover no-repeat fixed;
}

/* 星空主题表单样式 */
.form {
  --input-focus: #c084fc;       /* 淡紫色聚焦光晕 */
  --font-color: #ffffff;
  --font-color-sub: #e0d4ff;
  --bg-color: rgba(10, 10, 30, 0.7);  /* 深蓝紫色半透明背景 */
  --main-color: #8b5cf6;         /* 紫罗兰色边框 */
  padding: 24px;
  background: var(--bg-color);
  backdrop-filter: blur(6px);     /* 轻微毛玻璃增加星空朦胧感（可选） */
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 20px;
  border-radius: 24px;
  border: 1px solid rgba(139, 92, 246, 0.5);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3), 0 0 0 1px rgba(255, 255, 255, 0.05);
  width: 100%;
  max-width: 400px;
}

.title {
  color: var(--font-color);
  font-weight: 700;
  font-size: 26px;
  margin-bottom: 0;
  text-align: center;
  text-shadow: 0 2px 10px rgba(0,0,0,0.3);
}

.title span {
  color: var(--font-color-sub);
  font-weight: 400;
  font-size: 14px;
  display: block;
  margin-top: 4px;
}

.input {
  width: 100%;
  height: 44px;
  border-radius: 40px;
  border: 1px solid rgba(139, 92, 246, 0.6);
  background-color: rgba(255, 255, 255, 0.1);
  box-shadow: none;
  font-size: 15px;
  font-weight: 500;
  color: white;
  padding: 0 16px;
  outline: none;
  box-sizing: border-box;
  transition: all 0.2s;
}

.input::placeholder {
  color: rgba(255, 255, 255, 0.6);
  font-weight: 400;
}

.input:focus {
  border-color: var(--input-focus);
  background-color: rgba(255, 255, 255, 0.2);
  box-shadow: 0 0 0 3px rgba(192, 132, 252, 0.3);
}

.button-confirm {
  width: 140px;
  height: 44px;
  border-radius: 40px;
  border: none;
  background: linear-gradient(135deg, #8b5cf6, #c084fc);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  font-size: 16px;
  font-weight: 600;
  color: white;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 8px;
}

.button-confirm:hover {
  background: linear-gradient(135deg, #7c3aed, #a855f7);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(139, 92, 246, 0.4);
}

.button-confirm:active {
  transform: translateY(1px);
}

.register-link {
  margin-top: 8px;
  font-size: 13px;
  color: #e0d4ff;
}

.register-link a {
  color: #f3e8ff;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}

.register-link a:hover {
  color: #ffffff;
  text-decoration: underline;
}
</style>