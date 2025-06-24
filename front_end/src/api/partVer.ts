import httpRequest from '@/request/index'

export interface VersionQueryParam {
  masterId: number
  someId: number  // 新增 someId 参数
  anotherId: number  // 新增 anotherId 参数
}

// 根据 masterId 查询版本
export function apiVersionQuery(param: VersionQueryParam) {
  return httpRequest({
    url: `/part/version/${param.someId}/${param.anotherId}/${param.masterId}`,  // 这里构造正确的 URL
    method: 'get',
    headers: {
      'token': 'your_token_here', // 这里替换为实际的 token
    },
  })
}

export interface DeletePartParam {
  masterId: string | number  // 将 masterId 转为字符串或数字类型
}

// 根据 masterId 删除部件
export function apiPartDelete(param: DeletePartParam) {
  return httpRequest({
    url: '/part/delete/new',  // 删除部件的接口 URL
    method: 'delete',
    headers: {
      'token': 'your_token_here',  // 替换为实际的 token
      'Content-Type': 'application/json',  // 请求头指定内容类型为 JSON
    },
    data: param.masterId.toString(),  // 直接传递 masterId，不包装成对象
  })
}
