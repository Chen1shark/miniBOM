// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
// interface UserInfoParam {
// 	name: string,
// 	psw: string
// }


export function apiCateGet(curPage: number, pageSize: number, name?: string,code?:string,id?:bigint) {
    return httpRequest({
		url: `/category/query?curPage=${curPage}&pageSize=${pageSize}&name=${name}&code=${code}&id=${id}`,
		method: 'get',
		// data: param,
	})
}