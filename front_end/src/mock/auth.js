import Mock from 'mockjs'
import MockAdapter from 'axios-mock-adapter'
import axios from '@/utils/request' // 你自己封装的 axios 实例

// 创建 mock 适配器实例
const mock = new MockAdapter(axios, { delayResponse: 500 }) // 延迟 500ms 更真实

// 模拟 JWT 生成函数
function generateToken(username) {
  return `${btoa(username)}.mocktoken.${Date.now()}`
}

// 模拟登录接口
mock.onPost('/auth/login').reply(config => {
  const { username, password } = JSON.parse(config.data)
  console.log('[Mock] 收到登录请求', username, password)

  if (username === 'testuser2' && password === 'Test456$%^') {
    return [
      200,
      {
        code: 200,
        token: 'mocked.jwt.token'
      }
    ]
  } else {
    return [
      200,
      {
        code: 401,
        message: '用户名或密码错误'
      }
    ]
  }
})


console.log('✅ Mock 服务已启用')
