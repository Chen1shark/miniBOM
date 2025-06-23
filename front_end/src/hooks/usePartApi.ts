import { ref } from 'vue'
import { 
  apiGetAllpart, 
  apiCreateNewPart, 
  apiUpdate, 
  apideletePart 
} from '@/api/partApi'

// 类型定义
interface CreatePart {
  name: string
  number: string
  source: string
  partType: string
  categoryId: string
  clsAttrs: {
    height: string
    Brand: string
    Weight: string
    Size: string
    Number: string
  }
}

interface UpdatePart {
  masterId: BigInt
  name: string
  number: string
  source: string
  partType: string
  categoryId: string
  clsAttrs: {
    height: string
    Brand: string
    Weight: string
    Size: string
    Number: string
  }
}

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

// 通用的 API Hook 工厂函数
function createApiHook<T, R>(apiFunction: (data: T) => Promise<R>) {
  const loading = ref(false)
  const error = ref<unknown | null>(null)
  const success = ref(false)

  const execute = async (data: T) => {
    loading.value = true
    error.value = null
    success.value = false

    try {
      const response = await apiFunction(data)
      success.value = true
      return response
    } catch (err) {
      error.value = err
      throw err
    } finally {
      loading.value = false
    }
  }

  const reset = () => {
    loading.value = false
    error.value = null
    success.value = false
  }

  return {
    loading,
    error,
    success,
    execute,
    reset
  }
}

// 获取所有零件
export function useGetAllParts() {
  const parts = ref<PartData[]>([])
  const loading = ref(false)
  const error = ref<unknown | null>(null)

  const fetchParts = async () => {
    loading.value = true
    error.value = null
    try {
      const response: ApiResponse<PartListResponse> = await apiGetAllpart()
      console.log('API原始响应:', response)
      
      if (response && response.code === 1 && response.data && response.data.list) {
        parts.value = response.data.list
        console.log('解析后的parts:', parts.value)
        console.log('parts长度:', parts.value.length)
        
        // 验证ID是否正确处理为BigInt
        if (parts.value.length > 0) {
          const firstItem = parts.value[3]
          console.log('第二条数据的ID:')
          console.log('- partId:', firstItem.partId.toString(), '(类型:', typeof firstItem.partId, ')')
          console.log('- partMasterId:', firstItem.partMasterId.toString(), '(类型:', typeof firstItem.partMasterId, ')')
          console.log('- parBranchId:', firstItem.parBranchId.toString(), '(类型:', typeof firstItem.parBranchId, ')')
        }
      } else {
        console.error('API响应格式错误:', response)
        parts.value = []
      }
      
    } catch (err) {
      console.error('获取零件数据失败:', err)
      error.value = err
      parts.value = []
    } finally {
      loading.value = false
    }
  }

  return {
    parts,
    loading,
    error,
    fetchParts
  }
}

// 创建零件
export const useCreatePart = () => createApiHook(apiCreateNewPart)

// 更新零件
export const useUpdatePart = () => createApiHook(apiUpdate)

// 删除零件
export const useDeletePart = () => createApiHook(apideletePart)

// 综合管理 Hook
export function usePartManagement() {
  const { parts, loading: fetchLoading, error: fetchError, fetchParts } = useGetAllParts()
  const { loading: createLoading, error: createError, success: createSuccess, execute: createPart, reset: resetCreate } = useCreatePart()
  const { loading: updateLoading, error: updateError, success: updateSuccess, execute: updatePart, reset: resetUpdate } = useUpdatePart()
  const { loading: deleteLoading, error: deleteError, success: deleteSuccess, execute: deletePart, reset: resetDelete } = useDeletePart()

  // 创建零件后刷新列表
  const createPartAndRefresh = async (partData: CreatePart) => {
    await createPart(partData)
    if (createSuccess.value) {
      await fetchParts()
    }
  }

  // 更新零件后刷新列表
  const updatePartAndRefresh = async (partData: UpdatePart) => {
    await updatePart(partData)
    if (updateSuccess.value) {
      await fetchParts()
    }
  }

  // 删除零件后刷新列表
  const deletePartAndRefresh = async (masterId: BigInt) => {
    await deletePart(masterId)
    if (deleteSuccess.value) {
      await fetchParts()
    }
  }

  return {
    // 数据
    parts,
    
    // 加载状态
    fetchLoading,
    createLoading,
    updateLoading,
    deleteLoading,
    
    // 错误状态
    fetchError,
    createError,
    updateError,
    deleteError,
    
    // 成功状态
    createSuccess,
    updateSuccess,
    deleteSuccess,
    
    // 方法
    fetchParts,
    createPart,
    updatePart,
    deletePart,
    createPartAndRefresh,
    updatePartAndRefresh,
    deletePartAndRefresh,
    
    // 重置
    resetAll: () => {
      resetCreate()
      resetUpdate()
      resetDelete()
    }
  }
} 