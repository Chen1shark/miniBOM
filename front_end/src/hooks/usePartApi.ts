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
  masterId: string
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
  const parts = ref<any[]>([])
  const loading = ref(false)
  const error = ref<unknown | null>(null)

  const fetchParts = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await apiGetAllpart()
      console.log('API原始响应:', response)
      
      // 尝试不同的数据结构
      let dataArray = []
      
      if (response && response.data) {
        // 情况1: response.data.data.list
        if (response.data.data && response.data.data.list) {
          dataArray = response.data.data.list
          console.log('使用 response.data.data.list')
        }
        // 情况2: response.data.list
        else if (response.data.list) {
          dataArray = response.data.list
          console.log('使用 response.data.list')
        }
        // 情况3: response.data 直接是数组
        else if (Array.isArray(response.data)) {
          dataArray = response.data
          console.log('使用 response.data (数组)')
        }
        // 情况4: response.data 是对象，包含数组属性
        else if (typeof response.data === 'object') {
          // 查找包含数组的属性
          for (const key in response.data) {
            if (Array.isArray(response.data[key])) {
              dataArray = response.data[key]
              console.log(`使用 response.data.${key}`)
              break
            }
          }
        }
      }
      
      parts.value = dataArray
      console.log('最终解析的parts:', parts.value)
      console.log('parts长度:', parts.value.length)
      
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
  const deletePartAndRefresh = async (masterId: string) => {
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