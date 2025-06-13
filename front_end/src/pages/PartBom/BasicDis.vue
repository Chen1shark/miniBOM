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
        <el-table-column fixed prop="id" label=" "/>
        <el-table-column prop="partCode" label="部件编码"/>
        <el-table-column prop="partName" label="部件名"/>
        <el-table-column prop="version" label="版本号"/>
        <el-table-column prop="categoryCode" label="装配模式"/>
        <el-table-column prop="source" label="分类码"/>
        <el-table-column fixed="right" label="操作" width="120">
          <template #default="scope">
            <el-button link type="primary" size="small" @click="handleClick(scope.row)">
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
          :page-sizes="[20,30, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </template>
  
  <script setup>
  import { onMounted, ref, reactive, computed } from 'vue'
  import { Edit, DeleteFilled, Plus, Search } from '@element-plus/icons-vue'

  // 分页相关数据
  const currentPage = ref(1)
  const pageSize = ref(20)
  const total = ref(0)

  // 搜索表单数据
  const searchForm = reactive({
    partCode: '',
    partName: '',
    activeField: null  // 当前激活的输入框
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
    if (!searchForm.partCode && !searchForm.partName) {
      searchForm.activeField = null
    }
  }

  // 分页处理方法
  const handleSizeChange = (val) => {
    pageSize.value = val
    currentPage.value = 1
  }

  const handleCurrentChange = (val) => {
    currentPage.value = val
  }

  const tdForPart = ref([]);
  // 生成更多测试数据
  const generateTestData = () => {
    const data = [];
    const categories = ['EL', 'DP', 'BT', 'CS', 'HS', 'PI', 'CM', 'SP', 'BR', 'AN'];
    const sources = ['内部生产', '外购'];
    const assemblyModes = ['螺丝固定', '卡扣安装', '插槽安装', '卡扣固定', '导热胶粘贴', '焊接', '胶水固定'];

    for (let i = 1; i <= 400; i++) {
      const categoryIndex = Math.floor(Math.random() * categories.length);
      const sourceIndex = Math.floor(Math.random() * sources.length);
      const assemblyModeIndex = Math.floor(Math.random() * assemblyModes.length);
      
      data.push({
        id: i,
        partCode: `CP-${1000 + i}`,
        partName: `部件${i}`,
        version: `V${Math.floor(Math.random() * 3) + 1}.${Math.floor(Math.random() * 5)}.${Math.floor(Math.random() * 10)}`,
        assemblyMode: assemblyModes[assemblyModeIndex],
        categoryCode: `${categories[categoryIndex]}-${String(i).padStart(3, '0')}`,
        source: sources[sourceIndex],
        weight: (Math.random() * 2).toFixed(2),
        height: (Math.random() * 10).toFixed(1),
        width: (Math.random() * 20).toFixed(1),
        length: (Math.random() * 30).toFixed(1)
      });
    }
    return data;
  };

  // 计算当前页显示的数据
  const paginatedData = computed(() => {
    const start = (currentPage.value - 1) * pageSize.value;
    const end = start + pageSize.value;
    return tdForPart.value.slice(start, end);
  });

  // 初始化数据
  onMounted(() => {
    tdForPart.value = generateTestData();
    total.value = tdForPart.value.length;
  });

  // 搜索方法
  const handleSearch = () => {
    // 实现搜索逻辑
    const filteredData = tdForPart.value.filter(item => {
      if (searchForm.partCode && !item.partCode.includes(searchForm.partCode)) return false;
      if (searchForm.partName && !item.partName.includes(searchForm.partName)) return false;
      return true;
    });
    tdForPart.value = filteredData;
    total.value = filteredData.length;
    currentPage.value = 1;
  };

  // 添加方法
  const handleAdd = () => {
    // 实现添加逻辑
  };

  // 编辑方法
  const handleClick = (row) => {
    // 实现编辑逻辑
  };

  // 删除方法
  const handleDelete = (row) => {
    // 实现删除逻辑
  };
  
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