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
        <img src="F:\miniBOMF\miniBOM\front_end\src\loginP.png" class="p1">
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
  }
  </script>
  
  <style scoped>
    .layout {
  display: flex;
  flex-direction: column;
  height: 100%;
  width: 1696px;
  overflow: hidden;
  font-family: Arial, sans-serif;
  border-radius: 10px; 
  background-color: #FFFFFF;
}

/* 顶部 Header */
.header {
  height: 150px;
  display: flex;
  background-color: #99CCFF;
  align-items: center;
  padding: 0 20px;
  font-size: 18px;
  font-weight: bold;
  border-radius: 10px;
  flex-shrink: 0;
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
  flex: 1;
  height: calc(100vh - 60px); /* 减去 header 高度 */
}

/* 左侧 Aside */
.aside {
  width: 40%;
  padding: 20px;
  box-sizing: border-box;
  flex-shrink: 0;
  overflow-y: auto;
}

/* 右侧 Main 区域 */
.main {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  box-sizing: border-box;
}

.p1 {
  width: 99%;
  height: 99%;
  object-fit: contain; /* 或 contain */}

  .login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  flex: 1;      /* 也可以加 flex:1 */
  background-color: #FFFFFF;
  }

  .login-container {
    width: 300px;
    margin: 100px auto;
    padding: 20px;
    border: 1px solid #ddd;
    border-radius: 4px;
    text-align: center;

    
  }
  .form-group {
    margin-bottom: 15px;
  }
  input {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  button {
    width: 100%;
    padding: 10px;
    background-color: #1976d2;
    color: white;
    border: none;
    cursor: pointer;
  }
  button:hover {
    background-color: #1565c0;
  }
  .error-msg {
    color: red;
    font-size: 14px;
    margin-bottom: 10px;
  }
  </style>
  