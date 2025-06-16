// 导入axios实例
import httpRequest from '@/request/index'

// 删除分类的API
export function apiCateDelete(id: bigint) {
    return httpRequest({
        url: `/category/${id}`,
        method: 'delete'
    })
} 