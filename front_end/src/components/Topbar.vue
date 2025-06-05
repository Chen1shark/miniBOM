<template>
  <div class="topbar">
    <div class="left">
      <span class="logo">miniBOM 系统</span>
    </div>
    <div class="right">
      <button @click="logout" class="logout-button">退出登录</button>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

function logout() {
  // 清除登录状态，可根据需要更改
  localStorage.removeItem('token')
  router.push('/')
}
</script>

<style scoped>
.topbar {
  width: 100%;
  height: 60px;
  background-color: #ffffff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 10;
}

.logo {
  font-size: 1.2rem;
  font-weight: bold;
  color: #333;
}

.logout-button {
  background-color: #f44336;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background-color 0.3s;
}

.logout-button:hover {
  background-color: #d32f2f;
}
</style>
