// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
interface UserInfoParam {
	name: string,
	psw: string
}

// 获取用户信息
export function apiLogin(param: UserInfoParam) {
    return httpRequest({
		url: '/user/login',
		method: 'post',
		data: param,
	})
}
