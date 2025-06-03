<template>
    <div class="login-container">
      <h2>账号登录</h2>
      <div class="form-group">
        <input v-model="username" type="text" placeholder="请输入用户名" />
      </div>
      <div class="form-group">
        <input v-model="password" type="password" placeholder="请输入密码" />
      </div>
      <div class="error-msg" v-if="errorMsg">{{ errorMsg }}</div>
      <button @click="login">登录</button>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import axios from '@/utils/request'
  import request from '@/utils/request'
  import { useRouter } from 'vue-router'

  const router = useRouter()
  
  const username = ref('testuser2')
  const password = ref('Test456$%^')
  const errorMsg = ref('')
  
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
    if (!isValidUsername(username.value)) {
      errorMsg.value = '用户名需为6~32位的字母/数字组合'
      return
    }
    if (!isValidPassword(password.value)) {
      errorMsg.value = '密码需为8~32位，并包含字母、数字和特殊字符'
      return
    }
  
    const res = await request.post('/auth/login', {
    username: username.value,
    password: password.value
    })

    console.log('登录响应内容：', res)


    if (res.data.code === 200) {
    localStorage.setItem('token', res.data.token)
    alert('登录成功')
    router.push('/Attr')
    } else {
      alert(res.data.message || '登录失败')
    }
  }
  </script>
  
  <style scoped>
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
  