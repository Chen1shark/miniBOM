// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
interface AttrInfoParam {
    ids: bigint[]
}


export function apiAttributeDel(attrInfo: AttrInfoParam) {
    return httpRequest({
		url: `/attribute`,
		method: 'delete',
		data: attrInfo,
	})
}
