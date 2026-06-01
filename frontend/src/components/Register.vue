<template>
  <div class="auth-container">
    <!-- 动态粒子装饰 -->
    <div class="bg-particles">
      <div class="particle p-1"></div>
      <div class="particle p-2"></div>
      <div class="particle p-3"></div>
      <div class="particle p-4"></div>
      <div class="particle p-5"></div>
      <div class="particle p-6"></div>
      <div class="particle p-7"></div>
      <div class="particle p-8"></div>
    </div>

    <!-- 渐变光环 -->
    <div class="glow glow-1"></div>
    <div class="glow glow-2"></div>

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
      <button class="button-confirm" type="submit">
        <span>注册</span>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"/><path d="M12 8v8M8 12h8"/></svg>
      </button>
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
    const res = await axios.post('/api/auth/register', {
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
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, #1a0533, #2d1b69, #1e0a3c);
  background-size: 300% 300%;
  animation: gradientDrift 10s ease infinite;
}
@keyframes gradientDrift {
  0% { background-position: 0% 50%; }
  50% { background-position: 100% 100%; }
  100% { background-position: 0% 50%; }
}

/* 光环 */
.glow {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  opacity: 0.25;
  pointer-events: none;
  z-index: 0;
}
.glow-1 {
  width: 400px; height: 400px;
  background: #a855f7;
  top: -20%; left: -15%;
  animation: glowPulse 6s ease-in-out infinite;
}
.glow-2 {
  width: 350px; height: 350px;
  background: #ec4899;
  bottom: -15%; right: -10%;
  animation: glowPulse 8s ease-in-out infinite 1s;
}
@keyframes glowPulse {
  0%, 100% { opacity: 0.2; transform: scale(1); }
  50% { opacity: 0.35; transform: scale(1.15); }
}

/* 浮动粒子 */
.bg-particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}
.particle {
  position: absolute;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #c084fc;
  opacity: 0.5;
  animation: particleFloat 8s ease-in-out infinite;
}
.p-1 { top: 10%; left: 20%; animation-delay: 0s; background: #f9a8d4; }
.p-2 { top: 25%; left: 70%; animation-delay: 1s; background: #a78bfa; }
.p-3 { top: 45%; left: 15%; animation-delay: 2s; background: #c084fc; }
.p-4 { top: 60%; left: 80%; animation-delay: 0.5s; background: #f9a8d4; }
.p-5 { top: 75%; left: 40%; animation-delay: 1.5s; background: #a78bfa; }
.p-6 { top: 15%; left: 50%; animation-delay: 3s; background: #c084fc; width: 4px; height: 4px; }
.p-7 { top: 55%; left: 55%; animation-delay: 2.5s; background: #f9a8d4; width: 5px; height: 5px; }
.p-8 { top: 85%; left: 10%; animation-delay: 1.8s; background: #a78bfa; width: 4px; height: 4px; }
@keyframes particleFloat {
  0%, 100% { transform: translate(0, 0) scale(1); opacity: 0.3; }
  25% { transform: translate(15px, -20px) scale(1.5); opacity: 0.7; }
  50% { transform: translate(-10px, -35px) scale(1); opacity: 0.4; }
  75% { transform: translate(-20px, -10px) scale(1.3); opacity: 0.6; }
}

/* 表单卡片 */
.form {
  position: relative;
  z-index: 1;
  padding: 36px 32px;
  background: rgba(255, 255, 255, 0.04);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
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
  color: rgba(255, 255, 255, 0.5);
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
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.05);
  font-size: 15px;
  color: #fff;
  padding: 0 16px;
  outline: none;
  transition: all 0.3s ease;
}
.input::placeholder { color: rgba(255, 255, 255, 0.3); }
.input:focus {
  border-color: #c084fc;
  background: rgba(192, 132, 252, 0.08);
  box-shadow: 0 0 0 3px rgba(192, 132, 252, 0.12);
  transform: translateY(-1px);
}

.button-confirm {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(135deg, #a855f7, #ec4899);
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
  box-shadow: 0 8px 25px rgba(168, 85, 247, 0.4);
}
.button-confirm:active { transform: translateY(0); }

.register-link {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}
.register-link a {
  color: #c084fc;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.2s;
}
.register-link a:hover { color: #e9d5ff; }
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
