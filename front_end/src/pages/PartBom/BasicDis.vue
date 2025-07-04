<template>
    <div class="tabs-container">
      <!-- 搜索区域 -->
      <div class="search-area">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>添加
        </el-button>
        
        <div class="search-inputs">
          <el-input
            v-model="searchForm.partCode"
            placeholder="部件编码"
            :disabled="isInputDisabled('partCode')"
            class="search-input"
            @focus="handleInputFocus('partCode')"
            @blur="handleInputBlur"
          />

          <el-input
            v-model="searchForm.partName"
            placeholder="部件名称"
            :disabled="isInputDisabled('partName')"
            class="search-input"
            @focus="handleInputFocus('partName')"
            @blur="handleInputBlur"
          />
        </div>

        <el-button type="primary" @click="handleSearch">
          <el-icon><Search /></el-icon>查询
        </el-button>
      </div>

      <!-- 表格区域 -->
      <el-table :data="paginatedData" stripe height="700">
        <!-- <el-table-column fixed prop="id" label=" "/> -->
        <el-table-column prop="partId" label="部件编码"/>
        <el-table-column prop="name" label="部件名"/>
        <el-table-column prop="version" label="版本号"/>
        <el-table-column prop="partType" label="装配模式"/>
        <el-table-column prop="businessCode" label="分类码"/>
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleEdit(scope.row)">
              <el-icon><Edit /></el-icon>
            </el-button>
            <el-button link type="primary" size="small" @click="handleDelete(scope.row)">
              <el-icon><DeleteFilled /></el-icon>
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页组件 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 30, 40]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>

      <!-- 编辑弹窗 -->
      <EditDialog
        v-model:visible="dialogVisible"
        :row-data="currentRow"
        @save="handleSave"
      />

      <!-- 添加弹窗 -->
      <AddDialog
        v-model:visible="addDialogVisible"
        @save="handleAddSave"
      />
    </div>
  </template>
  
  <script setup>
  import { onMounted, ref, reactive, computed } from 'vue'
  import { Edit, DeleteFilled, Plus, Search } from '@element-plus/icons-vue'
  import { useGetAllParts, useDeletePart } from '@/hooks/usePartApi'
  import EditDialog from './Editdialog.vue'
  import AddDialog from './AddDialog.vue'
  import { ElMessage, ElMessageBox } from 'element-plus'

  // 分页相关数据
  const currentPage = ref(1)
  const pageSize = ref(10)
  const total = ref(0)

  // 搜索表单数据
  const searchForm = reactive({
    partCode: '',
    partName: '',
    assemblyMode: '',
    categoryCode: ''
  })

  // 判断输入框是否应该被禁用
  const isInputDisabled = (field) => {
    // 如果当前输入框有内容，则另一个输入框禁用
    if (searchForm.partCode && field === 'partName') return true
    if (searchForm.partName && field === 'partCode') return true
    
    // 如果有激活的输入框，且不是当前输入框，则禁用
    if (searchForm.activeField && searchForm.activeField !== field) return true
    
    return false
  }

  // 处理输入框获得焦点事件
  const handleInputFocus = (field) => {
    searchForm.activeField = field
  }

  // 处理输入框失去焦点事件
  const handleInputBlur = () => {
    // 如果两个输入框都没有内容，则解除禁用状态
    if (!searchForm.partCode && !searchForm.partName && !searchForm.assemblyMode && !searchForm.categoryCode) {
      searchForm.activeField = null
    }
  }

  // API hooks
  const { parts, loading, error, fetchParts } = useGetAllParts()
  const { execute: deletePart, loading: deleteLoading } = useDeletePart()

  // 本地分页和搜索数据
  const tdForPart = ref([])
  const originalData = ref([])

  // 初始化数据
  const refreshList = async () => {
    await fetchParts()
    
    originalData.value = parts.value ? [...parts.value] : []
    tdForPart.value = parts.value ? [...parts.value] : []
    total.value = tdForPart.value.length
  }

  onMounted(async () => {
    await refreshList()
    console.log('有挂载')
  })

  // 计算当前页显示的数据
  const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value
    const end = start + pageSize.value
    return tdForPart.value.slice(start, end)
  })

  // 优化搜索方法
  const handleSearch = () => {
    const partCode = searchForm.partCode.trim()
    const partName = searchForm.partName.trim()

    // 如果所有搜索条件都为空，恢复原始数据
    if (!partCode && !partName) {
      tdForPart.value = [...originalData.value]
      total.value = originalData.value.length
      currentPage.value = 1
      return
    }

    // 根据搜索条件过滤数据
    const filteredData = originalData.value.filter(item => {
      // 处理BigInt类型的partId，转换为字符串进行搜索
      const itemPartId = item.partId ? item.partId.toString() : ''
      const itemName = item.name ? String(item.name) : ''
      
      const matchPartCode = !partCode || itemPartId.toLowerCase().includes(partCode.toLowerCase())
      const matchPartName = !partName || itemName.toLowerCase().includes(partName.toLowerCase())
      
      return matchPartCode && matchPartName
    })

    // 更新表格数据和总数
    tdForPart.value = filteredData
    total.value = filteredData.length
    currentPage.value = 1
  }

  // 处理页码变化
  const handleCurrentChange = (val) => {
    currentPage.value = val
  }

  // 处理每页条数变化
  const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
  }

  // 添加方法
  const handleAdd = () => {
    addDialogVisible.value = true
  }

  // 编辑方法
  const handleEdit = (row) => {
    // 实现编辑逻辑
    
    currentRow.value = {...row}
    dialogVisible.value = true
  }

  // 删除方法
  const handleDelete = (row) => {

    console.log('partMasterId:', row.partMasterId.toString(), typeof row.partMasterId);
    ElMessageBox.confirm('确定要删除该零件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      try {
        const partMasterId = row.partMasterId 
        
        if (!partMasterId) {
          ElMessage.error('无法获取零件ID')
          return
        }
        
        // 使用封装好的useDeletePart hook
        await deletePart(partMasterId)
        ElMessage.success('删除成功')
        await refreshList()
      } catch (e) {
        console.error('删除失败:', e)
        // 显示后端返回的具体错误信息
        const errorMessage = e.message || '删除失败'
        ElMessage.error(errorMessage)
      }
    })
  }

  const handleSave = async () => {
    dialogVisible.value = false
    await refreshList()
  }
  
  // 弹窗标签页相关数据
  const dialogVisible = ref(false)
  const addDialogVisible = ref(false)
  const currentRow = ref(null)

  const handleAddSave = async (data) => {
    addDialogVisible.value = false
    await refreshList()
  }
  
  </script>
  
  <style scoped>
  .tabs-container {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    font-family: Arial, sans-serif;
    gap: 20px; /* 添加间距 */
  }

  .search-area {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 20px;
    background-color: #f5f7fa;
    border-radius: 8px;
  }

  .search-inputs {
    display: flex;
    gap: 15px;
    flex: 1;
  }

  .search-input {
    width: 200px;
  }

  .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
  }
  </style>