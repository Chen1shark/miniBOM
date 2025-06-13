// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
interface AttrInfoParam {
    name: string,
    nameEn: string,
    description: string,
    descriptionEn: string,
    disableFlag: boolean
    type: string
}


export function apiAttributeCreate(attrInfo: AttrInfoParam) {
    return httpRequest({
		url: `/attribute/create`,
		method: 'post',
		data: attrInfo,
	})
}
