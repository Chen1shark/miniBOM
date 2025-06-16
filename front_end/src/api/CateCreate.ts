// 导入axios实例
import httpRequest from '@/request/index'

// 定义接口的传参
interface CateDetailParam {
    code: string,
    name: string,
    nameEn: string,
    status: boolean
}

interface CateCreateParam {
    code: string,
    name: string,
    nameEn: string,
    status: boolean,
    parentId: bigint | null,
    description?: string,
    descriptionEn?: string,
    attributes?: any[]
}

export function apiCateCreate(cateInfo: CateCreateParam) {
    return httpRequest({
        url: `/category/create`,
        method: 'post',
        data: cateInfo,
    })
}
