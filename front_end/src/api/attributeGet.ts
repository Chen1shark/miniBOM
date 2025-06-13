// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
// interface UserInfoParam {
// 	name: string,
// 	psw: string
// }


export function apiAttributeGet(page: number, pageSize: number, attributeName: string) {
    return httpRequest({
		url: `/attribute/${pageSize}/${page}/${attributeName}`,
		method: 'get',
		// data: param,
	})
}
