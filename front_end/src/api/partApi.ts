import httpRequest from '@/request/index'

// 零件数据接口 - 使用BigInt类型处理大整数ID
interface PartData {
  partId: BigInt
  name: string
  version: string
  partType: string
  source: string
  businessCode: string
  partMasterId: BigInt
  parBranchId: BigInt
}

// API响应接口
interface ApiResponse<T> {
  code: number
  msg: string | null
  data: T
}

// 零件列表响应接口
interface PartListResponse {
  count: number
  list: PartData[]
}

interface createPart {
  name: string,
  number: string,
  source: string,
  partType: string,
  categoryId: string,
  clsAttrs: {
    // height: string,
    // Brand: string,
    // Weight: string,
    // Size: string,
    // Number: string
  }

}

interface updatePart {
  masterId: BigInt,
  name: string,
  number: string,
  source: string,
  partType: string,
  categoryId: string,
  clsAttrs: {
    // height: string,
    // Brand: string,
    // Weight: string,
    // Size: string,
    // Number: string,


  }
}

// 基础fetch实现获取所有part
//得到所有part
export function apiGetAllpart() {
  return httpRequest({
    url: '/part/20/1?isFilterOld=true' ,           
    method:'get',
  }).then((response: any) => {
    // 处理响应数据，将大整数ID转换为BigInt以避免精度丢失
    if (response && response.data && response.data.list) {

      response.data.list = response.data.list.map((item: any) => ({
        ...item,
        partId: BigInt(item.partId),
        partMasterId: BigInt(item.partMasterId),
        parBranchId: BigInt(item.parBranchId)
      }))
      response.data.list.forEach((item: any) => {
        item.partId = item.partId.toString()
        item.partMasterId = item.partMasterId.toString()
        item.parBranchId = item.parBranchId.toString()
      })
      console.log(response.data.list);
      
      
    }
    return response
  })
}

//创建part
export function apiCreateNewPart(data1: createPart) {
  console.log('创建Part时发送的数据:', data1)
  return httpRequest({
    url: '/part/create',
    method: 'post',
    data: data1
  }).then((response: any) => {
    console.log('创建Part后端反馈:', response)
    
    // 检查后端返回的状态码
    if (response && response.code === 0) {
      // 操作失败，抛出错误并包含后端返回的错误信息
      const error = new Error(response.msg || '创建失败')
      ;(error as any).response = response
      throw error
    }
    
    return response
  })
}


//根据masterId更新
export function apiUpdate(data2: updatePart) {
  console.log('更新Part时发送的数据:', data2)
  return httpRequest({
    url: '/part/update',
    method: 'post',
    data: data2,
  }).then((response: any) => {
    console.log('更新Part后端反馈:', response)
    
    // 检查后端返回的状态码
    if (response && response.code === 0) {
      // 操作失败，抛出错误并包含后端返回的错误信息
      const error = new Error(response.msg || '更新失败')
      ;(error as any).response = response
      throw error
    }
    
    return response
  })
}



//根据masterID删除
export function apideletePart(masterId: BigInt) {
  // 将BigInt转换为字符串，避免JSON序列化错误
  const requestData = masterId.toString()

  console.log('删除请求 - 原始masterId:', masterId)
  console.log('删除请求 - 转换后的requestData:', requestData)

  return httpRequest({
    url: '/part/delete/all',
    method: 'delete',
    data: requestData,
    headers: {
      'Content-Type': 'application/json'
    }
  }).then((response: any) => {
    console.log('删除Part后端反馈:', response)
    
    // 检查后端返回的状态码
    if (response && response.code === 0) {
      // 操作失败，抛出错误并包含后端返回的错误信息
      const error = new Error(response.msg || '删除失败')
      ;(error as any).response = response
      throw error
    }
    
    return response
  })
}








