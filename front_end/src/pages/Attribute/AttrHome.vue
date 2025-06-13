<template>
  <div class="attr-home-container">
    <el-card class="attr-card">
      <div class="attr-header">
        <div class="query-type-switch">
          <el-radio-group v-model="queryType">
            <el-radio-button label="category">分类信息查询</el-radio-button>
            <el-radio-button label="attribute">属性信息查询</el-radio-button>
          </el-radio-group>
        </div>
        <div class="search-input">
          <el-input v-model="search" :placeholder="searchPlaceholder" clearable @keyup.enter="fetchList">
            <template #prefix>
              <el-icon class="el-input__icon"><Search /></el-icon>
            </template>
          </el-input>
        </div>
        <el-button type="primary" @click="openCreate">{{ createButtonText }}</el-button>
      </div>
      <el-table v-if="queryType === 'attribute'" :data="attrList" style="width: 100%;" v-loading="loading">
        <el-table-column prop="name" label="属性中文名" width="120" />
        <el-table-column prop="nameEn" label="属性英文名" width="140" />
        <el-table-column prop="description" label="属性中文描述" show-overflow-tooltip width="150" />
        <el-table-column prop="descriptionEn" label="属性英文描述" show-overflow-tooltip width="150" />
        <el-table-column prop="type" label="数据类型" width="100" />
        <el-table-column prop="disableFlag" label="属性状态" width="80">
          <template #default="scope">
            {{ scope.row.disableFlag ? '失效' : '有效' }}
          </template>
        </el-table-column>
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

      <el-table v-else-if="queryType === 'category'" :data="categoryList" style="width: 100%;" v-loading="loading">
        <el-table-column prop="categoryCode" label="分类码" width="180">
           <template #default="scope">
            <el-link type="primary" @click="goToCategoryDetail(scope.row)">{{ scope.row.categoryCode }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="categoryNameZh" label="分类中文名称" width="200">
          <template #default="scope">
            <el-link type="primary" @click="goToCategoryDetail(scope.row)">{{ scope.row.categoryNameZh }}</el-link>
          </template>
        </el-table-column>
        <el-table-column prop="categoryNameEn" label="分类英文名称" width="200" />
        <el-table-column prop="parentCategoryNameZh" label="父分类中文名称" width="200" />
        <el-table-column prop="parentCategoryNameEn" label="父分类英文名称" width="200" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" @click="openCategoryEdit(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="openCategoryDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container" v-if="queryType === 'attribute'">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <attr-form
      v-if="showForm && queryType === 'attribute'"
      :visible="showForm"
      :mode="formMode"
      :initData="formData"
      @close="showForm = false"
      @submit="handleFormSubmit"
    />
    <category-form
      v-if="showCategoryForm && queryType === 'category'"
      :visible="showCategoryForm"
      :mode="categoryFormMode"
      :initData="categoryFormData"
      @close="showCategoryForm = false"
      @submit="handleCategoryFormSubmit"
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
    <el-dialog title="确认删除分类？" :model-value="showCategoryDelete" width="300px" @close="showCategoryDelete = false">
      <span>确定要删除该分类吗？请注意：仅可删除未使用的分类。</span>
      <template #footer>
        <el-button @click="showCategoryDelete = false">取消</el-button>
        <el-button type="danger" @click="handleCategoryDelete">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import AttrForm from './AttrForm.vue'
import AttrCategoryDialog from './AttrCategoryDialog.vue'
import CategoryForm from './CategoryForm.vue'
import { Search } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { apiAttributeGet } from '@/api/attributeGet'
import { apiAttributeModify } from '@/api/attributeModify'
import { apiAttributeCreate } from '@/api/attributeCreate'
import { apiAttributeDel } from '@/api/attributeDel'
import { ElMessage } from 'element-plus'

const props = defineProps({
  defaultType: {
    type: String,
    default: 'attribute'
  }
});

const router = useRouter()
const route = useRoute()
const loading = ref(false)
const attrList = ref([])
const categoryList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const showForm = ref(false)
const formMode = ref('create')
const currentAttr = ref(null)
const showCategory = ref(false)
const currentCategory = ref(null)
const queryType = ref(props.defaultType)
const search = ref('')

const formData = ref({
  id: null,
  name: '',
  nameEn: '',
  description: '',
  descriptionEn: '',
  type: '',
  disableFlag: false
})

const formRules = {
  name: [{ required: true, message: '请输入属性中文名', trigger: 'blur' }],
  nameEn: [{ required: true, message: '请输入属性英文名', trigger: 'blur' }],
  description: [{ required: true, message: '请输入属性中文描述', trigger: 'blur' }],
  descriptionEn: [{ required: true, message: '请输入属性英文描述', trigger: 'blur' }],
  type: [{ required: true, message: '请选择数据类型', trigger: 'change' }]
}

const formRef = ref(null)

// 先定义 fetchList 函数
const fetchList = async () => {
  loading.value = true
  try {
    const res = await apiAttributeGet(currentPage.value, pageSize.value, search.value || '')
    if (res.code === 1) {
      // 使用 BigInt 处理 ID
      attrList.value = res.data.list.map(item => ({
        ...item,
        id: BigInt(item.id)
      }))
      total.value = res.data.number
    } else {
      ElMessage.error(res.msg || '获取属性列表失败')
    }
  } catch (error) {
    console.error('获取属性列表失败:', error)
    ElMessage.error('获取属性列表失败')
  } finally {
    loading.value = false
  }
}

// 然后再定义其他方法
const handleCurrentChange = (val) => {
  currentPage.value = val
  fetchList()
}

const handleSizeChange = (val) => {
  pageSize.value = val
  currentPage.value = 1
  fetchList()
}

const handleSearch = () => {
  currentPage.value = 1
  fetchList()
}

// 最后再设置 watch
watch(
  () => route.path,
  () => {
    if (route.path.startsWith('/Home/category/') && route.path !== '/Home/category/query-all') {
      queryType.value = 'category'
    } else {
      queryType.value = 'attribute'
    }
    fetchList()
  },
  { immediate: true }
)

// 计算属性
const searchPlaceholder = computed(() => {
  return queryType.value === 'attribute' ? '属性中文名' : '分类编码或分类中文名';
});

const createButtonText = computed(() => {
  return queryType.value === 'attribute' ? '新建属性' : '新建分类';
});

// 其他状态变量
const showCategoryForm = ref(false)
const categoryFormMode = ref('create')
const categoryFormData = ref({})
const showDelete = ref(false)
const deleteRow = ref(null)
const showCategoryDelete = ref(false)
const deleteCategoryRow = ref(null)

// 方法
function openCreate() {
  if (queryType.value === 'attribute') {
    formMode.value = 'create'
    formData.value = {}
    showForm.value = true
  } else {
    categoryFormMode.value = 'create'
    categoryFormData.value = {}
    showCategoryForm.value = true
  }
}
function openEdit(row) {
  formMode.value = 'edit'
  formData.value = {
    id: row.id.toString(), // 将 BigInt 转换为字符串用于表单显示
    name: row.name,
    nameEn: row.nameEn,
    description: row.description,
    descriptionEn: row.descriptionEn,
    type: row.type,
    disableFlag: row.disableFlag
  }
  showForm.value = true
}
function openDelete(row) {
  deleteRow.value = row
  showDelete.value = true
}
async function handleDelete() {
  try {
    loading.value = true
    const res = await apiAttributeDel({
      ids: [deleteRow.value.id.toString()]
    })
    if (res.code === 1) {
      ElMessage.success('删除成功')
      showDelete.value = false
      fetchList()
    } else {
      ElMessage.error(res.msg || '删除失败')
    }
  } catch (error) {
    console.error('删除属性失败:', error)
    ElMessage.error('删除属性失败')
  } finally {
    loading.value = false
  }
}
async function handleFormSubmit(data) {
  try {
    loading.value = true
    if (formMode.value === 'edit') {
      // 确保提交的数据格式正确
      const submitData = {
        id: data.id, // 直接使用字符串形式的 ID
        description: data.description,
        descriptionEn: data.descriptionEn,
        disableFlag: data.disableFlag
      }
      const res = await apiAttributeModify(submitData)
      if (res.code === 1) {
        ElMessage.success('修改成功')
        showForm.value = false
        fetchList()
      } else {
        ElMessage.error(res.msg || '修改失败')
      }
    } else if (formMode.value === 'create') {
      // 新建属性
      const submitData = {
        name: data.name,
        nameEn: data.nameEn,
        description: data.description,
        descriptionEn: data.descriptionEn,
        type: data.type,
        disableFlag: data.disableFlag
      }
      const res = await apiAttributeCreate(submitData)
      if (res.code === 1) {
        ElMessage.success('新建成功')
        showForm.value = false
        fetchList()
      } else {
        ElMessage.error(res.msg || '新建失败')
      }
    }
  } catch (error) {
    console.error('表单提交失败:', error)
    ElMessage.error('表单提交失败')
  } finally {
    loading.value = false
  }
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
function openCategoryEdit(row) {
  categoryFormMode.value = 'edit'
  categoryFormData.value = { ...row }
  showCategoryForm.value = true
}
function openCategoryDelete(row) {
  deleteCategoryRow.value = row
  showCategoryDelete.value = true
}
function handleCategoryDelete() {
  // TODO: 调用删除分类接口
  console.log('删除分类:', deleteCategoryRow.value)
  showCategoryDelete.value = false
  // 模拟删除后刷新列表
  fetchList()
}
function handleCategoryFormSubmit(data) {
  console.log('分类表单提交:', data)
  // TODO: 调用新建/编辑分类接口
  showCategoryForm.value = false
  // 模拟提交后刷新列表
  fetchList()
}
const goToCategoryDetail = (row) => {
  router.push({
    path: `/Home/category/detail/${row.categoryCode}`,
    query: {
      categoryNameZh: row.categoryNameZh,
      categoryNameEn: row.categoryNameEn
    }
  })
}

// 生命周期钩子
onMounted(() => {
  // 初始化时根据当前路由设置查询类型
  if (route.path.startsWith('/Home/category/') && route.path !== '/Home/category/query-all') {
    queryType.value = 'category'
  } else if (route.path.startsWith('/Home/attribute/')) {
    queryType.value = 'attribute'
  }
  fetchList()
})
</script>

<style scoped>
.attr-home-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.attr-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.attr-header {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
  margin-bottom: 20px;
}

.query-type-switch {
  flex-shrink: 0;
}

.search-input {
  flex: 1;
  max-width: 300px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 表格容器样式 */
.el-table {
  flex: 1;
  margin-bottom: 20px;
}

/* 确保表格内容可以滚动 */
.el-table__body-wrapper {
  overflow-y: auto;
  max-height: calc(100vh - 300px);
}

/* 可以添加更多样式来优化表格、按钮等的显示 */
</style>
  