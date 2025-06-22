// src/api/bom.ts

import httpRequest from '@/request/index'

// 1. 修改BOM接口参数类型
export interface BomUpdateParam {
  bomLinkId: number
  quantity: number
  referenceDesignator: string
}

// 修改BOM
export function apiBomUpdate(param: BomUpdateParam) {
  return httpRequest({
    url: '/bom/update',
    method: 'post',
    data: param,
  })
}

// 2. 删除BOM接口参数类型
export interface BomDeleteParam {
  ids: number[]
}

// 删除BOM
export function apiBomDelete(param: BomDeleteParam) {
  return httpRequest({
    url: '/bom/delete',
    method: 'delete',
    data: param,
  })
}

// 3. 获取BOM清单
export function apiBomChecklist() {
  return httpRequest({
    url: '/bom/checklist',
    method: 'get',
  })
}
