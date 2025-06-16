// 导入axios实例
import httpRequest from '@/request/index'

// 定义更新分类的请求参数接口
interface CateUpdateParam {
    classificationId: bigint,
    sourceIds: bigint[]
}

// 更新分类的API
export function apiCateAddAttr(param: CateUpdateParam) {
    return httpRequest({
        url: '/category/batchCreateLinks',
        method: 'post',
        data: param
    })
} 