// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
// interface UserInfoParam {
// 	name: string,
// 	psw: string
// }


export function apiAttrGetCate(id: bigint) {
    return httpRequest({
		url: `/attribute/category/${id}`,
		method: 'get',
		// data: param,
	})
}
