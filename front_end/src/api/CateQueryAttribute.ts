// 导入axios实例
import httpRequest from '@/request/index'

// 定义属性接口
export interface Attribute {
    id: string,
    name: string,
    nameEn: string,
    description: string,
    descriptionEn: string,
    type: string,
    enable: boolean,
    constraint: any
}

// 查询分类属性的API
export const apiCateQueryAttribute = (categoryId: bigint) => {
    return httpRequest({
        url: `/category/queryAttribute?linkId=${categoryId}`,
        method: 'get'
    });
} 