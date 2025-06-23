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

// 4. 创建BOM接口参数类型
export interface BomCreateParam {
  sourceId: number
  targetId: number
  quantity: number
  referenceDesignator: string
}

// 创建BOM
export function apiBomCreate(param: BomCreateParam) {
  return httpRequest({
    url: '/bom/add',
    method: 'post',
    data: param,
  })
}


// 1. 根据子 partMasterId 查询父部件接口参数类型
export interface PartMasterQueryParam {
  partMasterId: number
}

// 根据子 partMasterId 查询父部件
export function apiPartMasterQuery(param: PartMasterQueryParam) {
  return httpRequest({
    url: `/bom/target/${param.partMasterId}`,
    method: 'get',
    headers: {
      'token': 'your_token_here', // 这里替换为实际的 token
    },
  })
}

// 2. 根据父 partId 查询子部件接口参数类型
export interface ParentPartQueryParam {
  parentPartId: number
}

// 根据父 partId 查询子部件
export function apiParentPartQuery(param: ParentPartQueryParam) {
  return httpRequest({
    url: `/bom/source/${param.parentPartId}`,
    method: 'get',
    headers: {
      'token': 'your_token_here', // 这里替换为实际的 token
    },
  })
}