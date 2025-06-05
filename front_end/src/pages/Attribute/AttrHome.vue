<template>
  <div class="attr-home-container">
    <el-card class="attr-card">
      <div class="attr-header">
        <div class="search-input">
          <el-input v-model="search" placeholder="属性中文名/英文名" clearable @keyup.enter="fetchList">
            <template #prefix>
              <el-icon class="el-input__icon"><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <el-button type="primary" @click="openCreate">新建属性</el-button>
      </div>
      <el-table :data="attrList" style="width: 100%;" v-loading="loading">
        <el-table-column prop="nameZh" label="属性中文名" width="120" />
        <el-table-column prop="nameEn" label="属性英文名" width="140" />
        <el-table-column prop="descZh" label="属性中文描述" show-overflow-tooltip width="150" />
        <el-table-column prop="descEn" label="属性英文描述" show-overflow-tooltip width="150" />
        <el-table-column prop="dataType" label="数据类型" width="100" />
        <el-table-column prop="status" label="属性状态" width="80" />
        <el-table-column prop="type" label="属性类型" width="80" />
        <el-table-column label="查看属性所在分类" width="140" fixed="right">
           <template #default="scope">
            <el-button size="small" @click="openCategory(scope.row)">查看</el-button>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="openDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next, sizes, total"
          :total="total"
          :page-size="pageSize"
          :current-page="page"
          @current-change="onPageChange"
          @size-change="onSizeChange"
          :page-sizes="[15, 30, 50]"
        />
      </div>
    </el-card>

    <attr-form
      v-if="showForm"
      :visible="showForm"
      :mode="formMode"
      :initData="formData"
      @close="showForm = false"
      @submit="handleFormSubmit"
    />
    <attr-category-dialog
      v-if="showCategory"
      :visible="showCategory"
      :categoryList="categoryList"
      @close="showCategory = false"
    />
    <el-dialog title="确认删除属性？" :model-value="showDelete" width="300px" @close="showDelete = false">
      <span>确定要删除该属性吗？</span>
      <template #footer>
        <el-button @click="showDelete = false">取消</el-button>
        <el-button type="danger" @click="handleDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import AttrForm from './AttrForm.vue'
import AttrCategoryDialog from './AttrCategoryDialog.vue'
import { Search } from '@element-plus/icons-vue'

// 状态变量
const search = ref('')
const attrList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(15)
const showForm = ref(false)
const formMode = ref('create')
const formData = ref({})
const showCategory = ref(false)
const categoryList = ref([])
const showDelete = ref(false)
const deleteRow = ref(null)
const loading = ref(false)

// 方法
function fetchList() {
  loading.value = true
  // TODO: 替换为真实接口
  // 模拟数据
  setTimeout(() => {
    attrList.value = [
      {
        id: 1,
        nameZh: '产品型号',
        nameEn: 'Product_Model',
        descZh: '这是一个产品型号的描述信息，可能会比较长，看看是否显示Tooltip',
        descEn: 'Description of Product Model, potentially long to test Tooltip',
        dataType: '字符串型',
        status: '有效',
        type: '扩展属性',
      },
      {
        id: 2,
        nameZh: '产品重量',
        nameEn: 'Product_Weight',
        descZh: '产品的重量信息',
        descEn: 'Information about the product weight',
        dataType: '实数型',
        status: '有效',
        type: '扩展属性',
      },
       {
        id: 3,
        nameZh: '产品尺寸',
        nameEn: 'Product_Size',
        descZh: '产品的尺寸信息',
        descEn: 'Information about the product size',
        dataType: '字符串型',
        status: '失效',
        type: '扩展属性',
      },
    ]
    total.value = attrList.value.length // 模拟总数
    loading.value = false
  }, 500)
}

function openCreate() {
  formMode.value = 'create'
  formData.value = {}
  showForm.value = true
}
function openEdit(row) {
  formMode.value = 'edit'
  formData.value = { ...row }
  showForm.value = true
}
function openDelete(row) {
  deleteRow.value = row
  showDelete.value = true
}
function handleDelete() {
  // TODO: 调用删除接口
  console.log('删除属性:', deleteRow.value)
  showDelete.value = false
  // 模拟删除后刷新列表
  fetchList()
}
function handleFormSubmit(data) {
  // TODO: 新建/编辑接口
  console.log('表单提交:', data)
  showForm.value = false
  // 模拟提交后刷新列表
  fetchList()
}
function openCategory(row) {
  // TODO: 查询分类信息接口
  console.log('查看分类:', row)
  categoryList.value = [
    {
      categoryCode: '0231B',
      categoryNameZh: '发射功率',
      categoryNameEn: 'Transmit_Power',
      parentCategoryNameZh: '射频模块',
      parentCategoryNameEn: 'RF Module',
    },
     {
      categoryCode: '01A01',
      categoryNameZh: '通用器件',
      categoryNameEn: 'General Components',
      parentCategoryNameZh: '无',
      parentCategoryNameEn: 'None',
    },
  ]
  showCategory.value = true
}
function onPageChange(val) {
  page.value = val
  fetchList()
}
function onSizeChange(val) {
  pageSize.value = val
  fetchList()
}

// 生命周期钩子
onMounted(() => {
  fetchList()
})
</script>

<style scoped>
.attr-home-container {
  padding: 20px;
}

.attr-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.attr-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-input {
  width: 240px;
}

.pagination-container {
  margin-top: 20px;
  text-align: right;
}

/* 可以添加更多样式来优化表格、按钮等的显示 */
</style>
  