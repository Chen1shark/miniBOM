// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
interface AttrInfoParam {
    id: string,
    description: string,
    descriptionEn: string,
    disableFlag: boolean
}

export function apiAttributeModify(attrInfo: AttrInfoParam) {
    return httpRequest({
        url: `/attribute`,
        method: 'post',
        data: attrInfo,
    })
}
