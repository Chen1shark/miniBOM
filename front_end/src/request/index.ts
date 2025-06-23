// @ts-ignore: no type definitions for json-bigint
import JSONbig from 'json-bigint'
import axios from 'axios'

// 创建一个 axios 实例
const service = axios.create({
	baseURL: 'http://localhost:8080', // 所有的请求地址前缀部分
	timeout: 60000, // 请求超时时间毫秒
	withCredentials: false, // 异步请求携带cookie
	headers: {
		
	},
	transformResponse: [function (data) {
		try {
			// 只对 JSON 字符串做解析，支持大整数
			return JSONbig({ useNativeBigInt: true }).parse(data)
		} catch (e) {
			return data
		}
	}]
})

// 添加请求拦截器
service.interceptors.request.use(
	function (config) {
		// 从 localStorage 获取 token
		const token = localStorage.getItem('token')
		console.log('这是token',token);
		// 如果 token 存在，则添加到请求头
		if (token) {
			config.headers['token'] = token
		}
		return config
	},
	function (error) {
		// 对请求错误做些什么
		console.log(error)
		return Promise.reject(error)
	}
)

// 添加响应拦截器
service.interceptors.response.use(
	function (response) {
		// 2xx 范围内的状态码都会触发该函数。
		// 对响应数据做点什么
		const dataAxios = response.data
		return dataAxios
	},
	function (error) {
		// 超出 2xx 范围的状态码都会触发该函数。
		// 对响应错误做点什么
		console.log('请求错误：', error)
		if (error.response) {
			switch (error.response.status) {
				case 401:
					// token 过期或无效，清除本地存储并跳转到登录页
					localStorage.removeItem('token')
					localStorage.removeItem('userId')
					localStorage.removeItem('userName')
					window.location.href = '/login'
					break
				default:
					break
			}
		}
		return Promise.reject(error)
	}
)

export default service

