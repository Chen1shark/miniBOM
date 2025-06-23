import { ref } from 'vue'
import { apiCateGet } from '@/api/CateGet'
import { ElMessage } from 'element-plus'

export function useCategoryTreeInPart() {
    const categoryTree = ref([])
    const loading = ref(false)
  
    const buildCategoryTree = (categories) => {
      const categoryMap = new Map()
      const rootCategories = []
      categories.forEach(category => {
        categoryMap.set(category.id, {
          id: category.id,
          name: category.name, // 注意这里用name，适配el-tree-select
          code: category.code,
          parentId: category.parentId,
          children: []
        })
      })
      categories.forEach(category => {
        const node = categoryMap.get(category.id)
        if (category.parentId && categoryMap.has(category.parentId)) {
          categoryMap.get(category.parentId).children.push(node)
        } else {
          rootCategories.push(node)
        }
      })
      return rootCategories
    }
  
    const fetchCategoryTreeData = async () => {
      loading.value = true
      try {
        const response = await apiCateGet(1, 1000, '', '', '')
        console.log('这是part中对分类树的response', response);
        if (response && response.code === 1 && response.data && response.data.records) {
          categoryTree.value = buildCategoryTree(response.data.records)
        } else {
          ElMessage.error('获取分类树数据失败')
        }
      } catch (e) {
        ElMessage.error('获取分类树数据失败')
      } finally {
        loading.value = false
      }
    }
  
    return { categoryTree, loading, fetchCategoryTreeData }



}