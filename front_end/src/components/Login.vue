<template>
  <div class="layout">
    <header class="header">
      <div class="title-container">
        <h1 class="title-cn">miniBOM系统</h1>
        <h2 class="title-en">miniBOM System</h2>
      </div>
    </header>
    <div class="content">
      <aside class="aside">
        <img src="@\loginP.png" class="p1">
      </aside>
      <main class="main">

        <div class="login-page">
          <div class="login-container">
            <h2>账号登录</h2>
            <div class="form-group">
              <input v-model="loginForm.name" type="text" placeholder="请输入用户名" />
            </div>
            <div class="form-group">
              <input v-model="loginForm.psw" type="password" placeholder="请输入密码" />
            </div>
            <div class="error-msg" v-if="errorMsg">{{ errorMsg }}</div>
            <button @click="login">登录</button>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
// import axios from '@/utils/request'
// import request from '@/utils/request'
import { useRouter } from 'vue-router'
import { apiLogin } from '@/api/userLogin'

const router = useRouter()

const errorMsg = ref('')

const loginForm = reactive({
  name: '',
  psw: ''
})

// 校验函数
function isValidUsername(name) {
  const usernameRegex = /^[A-Za-z0-9]{6,32}$/
  return usernameRegex.test(name)
}

function isValidPassword(pwd) {
  const lengthOk = pwd.length >= 8 && pwd.length <= 32
  const hasLetter = /[A-Za-z]/.test(pwd)
  const hasNumber = /\d/.test(pwd)
  const hasSpecial = /[^A-Za-z0-9]/.test(pwd)
  return lengthOk && hasLetter && hasNumber && hasSpecial
}

const login = async () => {

  errorMsg.value = ''
  if (!isValidUsername(loginForm.name)) {
    errorMsg.value = '用户名需为6~32位的字母/数字组合'
    return
  }
  if (!isValidPassword(loginForm.psw)) {
    errorMsg.value = '密码需为8~32位，并包含字母、数字和特殊字符'
    return
  }

  try {
    const res = await apiLogin(loginForm)
    console.log('登录响应：', res)

    if (res.code === 1) {  // 成功状态码为1
      const userData = res.data
      localStorage.setItem('token', userData.token)
      localStorage.setItem('userId', userData.id)
      localStorage.setItem('userName', userData.name)

      alert('登录成功')
      router.push('/Home')
    } else {
      errorMsg.value = res.msg || '登录失败'
    }
  } catch (error) {
    console.error('登录出错：', error)
    errorMsg.value = '登录失败，请稍后重试'
  }

  // router.push('/Home')

}
</script>

<style scoped>
/* 基础和布局 */
.layout {
  display: flex;
  flex-direction: column;
  /* 改动：使用视口单位vh，让高度填满整个屏幕 */
  height: 100vh;
  /* 改动：使用百分比宽度，适应不同屏幕尺寸 */
  width: 100%;
  overflow: hidden;
  font-family: Arial, sans-serif;
  background-color: #FFFFFF;
}

/* 顶部 Header */
.header {
  height: 150px;
  display: flex;
  background-color: #99CCFF;
  align-items: center;
  padding: 0 40px;
  /* 增加了一些 padding */
  font-size: 18px;
  font-weight: bold;
  /* 改动：移除固定的 border-radius，或只保留顶部圆角 */
  border-top-left-radius: 10px;
  border-top-right-radius: 10px;
  flex-shrink: 0;
  /* 防止 Header 在内容过多时被压缩 */
}

.title-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  height: 100%;
  padding-left: 20px;
}

.title-cn {
  font-size: 32px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
  margin-bottom: 8px;
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.1);
}

.title-en {
  font-size: 20px;
  font-weight: 500;
  color: #34495e;
  margin: 0;
  font-family: 'Arial', 'Helvetica Neue', sans-serif;
  letter-spacing: 1px;
  text-transform: uppercase;
  opacity: 0.9;
}

/* 主体区域：左右结构 */
.content {
  display: flex;
  /* 改动：flex: 1 让此元素自动填充父容器的剩余空间 */
  flex: 1;
  /* 改动：移除不必要的固定 height 计算 */
  overflow-y: auto;
  /* 如果内容溢出，允许滚动 */
}

/* 左侧 Aside */
.aside {
  width: 50%;
  /* 可以根据喜好调整百分比 */
  padding: 20px;
  box-sizing: border-box;
  display: flex;
  /* 使用 flex 居中图片 */
  justify-content: center;
  align-items: center;
  overflow: hidden;
  /* 隐藏超出部分的图片 */
  flex-shrink: 0;
}

/* 右侧 Main 区域 */
.main {
  flex: 1;
  /* 自动填充剩余空间 */
  padding: 20px;
  box-sizing: border-box;
  /* 改动：使用 flex 布局来居中登录框 */
  display: flex;
  justify-content: center;
  align-items: center;
}

.p1 {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

/* 改动：不再需要 .login-page，因为 .main 已经承担了其布局功能 */

.login-container {
  width: 350px;
  /* 可以适当加宽登录框 */
  max-width: 90%;
  /* 在小屏幕上防止登录框过宽 */
  padding: 30px;
  border: 1px solid #ddd;
  border-radius: 8px;
  /* 增加圆角 */
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  /* 添加一些阴影增加质感 */
  /* 改动：移除固定的 margin，让 flex 布局自动处理居中 */
}

.form-group {
  margin-bottom: 20px;
  /* 增加间距 */
}

input {
  width: 100%;
  padding: 12px;
  /* 增加内边距 */
  box-sizing: border-box;
  border: 1px solid #ccc;
  border-radius: 4px;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #1976d2;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
  font-size: 16px;
  /* 增大字体 */
  transition: background-color 0.3s;
  /* 添加悬停过渡效果 */
}

button:hover {
  background-color: #1565c0;
}

.error-msg {
  color: red;
  font-size: 14px;
  margin-bottom: 15px;
}
</style>