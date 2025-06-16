// 导入axios实例
import httpRequest from '@/request/index'

// 定义更新分类的请求参数接口
interface CateUpdateParam {
    id: bigint,
    name: string,
    nameEn: string,
    description?: string,
    descriptionEn?: string
}

// 更新分类的API
export function apiCateUpdate(param: CateUpdateParam) {
    return httpRequest({
        url: '/category/updateCategory',
        method: 'post',
        data: {
            ...param,
            id: param.id.toString() // 将 bigint 转换为字符串
        }
    })
} 