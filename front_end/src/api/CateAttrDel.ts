// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
interface AttrInfoParam {
    ids: bigint[]
}


export function apiCateAttrDel(attrInfo: AttrInfoParam) {
    return httpRequest({
		url: `/category/deleteAttribute`,
		method: 'delete',
		data: attrInfo,
	})
}
