<template>
  <el-dialog
    v-model="dialogVisible"
    title="编辑信息"
    width="60%"
    :before-close="handleClose"
  >
    <!-- 页签区 -->
    <el-tabs v-model="activeTab">
      <!-- 基本属性 -->
      <el-tab-pane label="基本属性" name="basic">
        <el-form :model="formData" label-width="120px">
          <el-form-item label="产品">
          </el-form-item>
          <el-form-item label="部件名称">
            <el-input v-model="formData.partName" />
          </el-form-item>
          <el-form-item label="默认单位">
            <el-select v-model="formData.defaultUnit" placeholder="请选择单位">
              <el-option label="PCS" value="PCS" />
              <el-option label="SITE" value="SITE" />
              <el-option label="SET" value="SET" />
              <el-option label="M" value="M" />
              <el-option label="EACH" value="EACH" />
              <el-option label="HOP" value="HOP" />
              <el-option label="M^M" value="M^M" />
              <el-option label="TRP" value="TRP" />
              <el-option label="MON" value="MON" />
              <el-option label="KG" value="KG" />
            </el-select>
          </el-form-item>
          <el-form-item label="来源">
            <el-select v-model="formData.source" placeholder="请选择来源">
              <el-option label="制造" value="Make" />
              <el-option label="购买" value="Buy" />
              <el-option label="购买-单一供应源" value="Buy_SingleSource" />
            </el-select>
          </el-form-item>
          <el-form-item label="装配模式">
            <el-select v-model="formData.assemblyMode" placeholder="请选择模式">
              <el-option label="可分离" value="Separable"/>
              <el-option label="不可分离" value="Inseparable" />
              <el-option label="零件" value="Part" />
            </el-select>
          </el-form-item>
          <el-form-item label="分类">
            <el-tree-select
              v-model="formData.categoryCode"
              :data="categoryTree"
              placeholder="请选择分类"
              :props="{ label: 'name', children: 'children', value: 'id' }"
              check-strictly
              node-key="id"
              default-expand-all
              :loading="categoryTreeLoading"
            />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <!-- BOM 清单 -->
      <el-tab-pane label="BOM清单" name="bomList">
        <!-- 操作工具栏 -->
        <div class="bom-toolbar">
          <el-button type="primary" @click="showAddPartDialog">新增子项</el-button>
          <el-button @click="showBomDialog">查看BOM清单</el-button>
          <el-button @click="showParentDialog">查看父项</el-button>
        </div>

       <el-table :data="bomItems" border style="width: 100%" highlight-current-row>
  <!-- 编码列 -->
  <el-table-column label="编码" width="260">
    <template #default="{ row }">
      <span>{{ row.partId }}</span>  <!-- 显示 partId 作为编码 -->
    </template>
  </el-table-column>

  <!-- 名称列 -->
  <el-table-column prop="name" label="名称" />

  <!-- 数量列 -->
  <el-table-column label="数量" width="120">
    <template #default="{ row }">
      <span>{{ row.quantity }}</span>  <!-- 显示数量 -->
    </template>
  </el-table-column>

  <!-- 位号列 -->
  <el-table-column label="位号" width="160">
    <template #default="{ row }">
      <span>{{ row.referenceDesignator }}</span>  <!-- 显示 referenceDesignator 作为位号 -->
    </template>
  </el-table-column>

  <!-- 操作列 -->
  <el-table-column label="操作" width="200" align="center">
    <template #default="{ $index, row }">
      <el-button type="primary" @click="showEditBomDialog(row)">修改</el-button>
      <el-button type="danger" link @click="removeBomItem($index)">删除</el-button>
    </template>
  </el-table-column>
</el-table>



      </el-tab-pane>

      <!-- 版本管理 -->
      <el-tab-pane label="版本管理" name="version">
        <el-form :model="formData" label-width="120px">
          <el-form-item label="父级零件">
            <el-input v-model="formData.parentPart" />
          </el-form-item>
          <el-form-item label="数量">
            <el-input-number v-model="formData.quantity" :min="1" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input type="textarea" v-model="formData.notes" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>

    <!-- 主弹窗 footer -->
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </span>
    </template>

    <!-- 查看 BOM 清单弹窗（树形） -->
    <el-dialog v-model="bomDialogVisible" title="BOM清单" width="60%">
      <div class="tree-header">
        <span class="col-code">编码</span>
        <span class="col-name">名称</span>
        <span class="col-qty">数量</span>
        <span class="col-pos">位号</span>
      </div>
      <el-tree
        :data="bomTree"
        node-key="code"
        default-expand-all
        :props="{ children: 'children' }"
        class="bom-tree"
      >
        <template #default="{ data }">
          <span class="col-code">{{ data.code }}</span>
          <span class="col-name">{{ data.name }}</span>
          <span class="col-qty">{{ data.quantity }}</span>
          <span class="col-pos">{{ data.position }}</span>
        </template>
      </el-tree>
    </el-dialog>

    <!-- 查看父项弹窗 -->
    <el-dialog v-model="parentDialogVisible" title="父项" width="30%">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="父级零件">{{ formData.parentPart }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 修改 BOM 项弹窗 -->
<el-dialog v-model="editBomDialogVisible" title="修改 BOM 项" width="30%">
  <el-form :model="editBomData" label-width="120px">
    <el-form-item label="数量">
      <el-input-number v-model="editBomData.quantity" :min="1" />
    </el-form-item>
    <el-form-item label="位号">
      <el-input v-model="editBomData.position" />
    </el-form-item>
  </el-form>
  <template #footer>
    <el-button @click="editBomDialogVisible = false">取消</el-button>
    <el-button type="primary" @click="handleEditBomSave">保存</el-button>
  </template>
</el-dialog>



    <!-- 新增子项组件 -->
    <AddPartDialog
      :visible="addPartDialogVisible"
      :parentId="formData.masterId || props.rowData.partId"
      @update:visible="v => (addPartDialogVisible = v)"
      @add="addParts"
    />
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue'
import AddPartDialog from './AddPartDialog.vue'
import { useUpdatePart } from '@/hooks/usePartApi'
import { ElMessage } from 'element-plus'
import { apiBomChecklist, apiBomUpdate, apiBomDelete,apiBomCreate,apiParentPartQuery } from '@/api/BOM'
import { useCategoryTreeInPart } from '@/hooks/useCategoryTreeInPart'

// ---------- props & emit ----------
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  rowData: {
    type: Object,
    default: () => ({})
  }
})
const emit = defineEmits(['update:visible', 'save'])

// ---------- 对话框控制 ----------
const dialogVisible = ref(props.visible)
const activeTab = ref('basic')

// ---------- 基础表单 ----------
const formData = reactive({
  partMasterId: '',
  partNumber: '',
  partName: '',
  defaultUnit: 'PCS',
  source: '',
  assemblyMode: '',
  categoryCode: '',
  weight: '',
  height: '',
  width: '',
  length: '',
  description: '',
  material: '',
  specification: '',
  unit: '',
  parentPart: '',
  quantity: 1,
  notes: ''
})

const { categoryTree, fetchCategoryTreeData, loading: categoryTreeLoading } = useCategoryTreeInPart()

// ---------- 计算属性：树数据 ----------
const bomTree = computed(() => bomItems.value)

// ---------- 弹窗控制变量 ----------
const addPartDialogVisible = ref(false)
const bomDialogVisible = ref(false)
const parentDialogVisible = ref(false)


// ---------- 监听主弹窗显隐 ----------
watch(
  () => props.visible,
  (newVal) => {
    dialogVisible.value = newVal
    if (newVal) {
      fetchCategoryTreeData()
      // 先重置为默认值
      formData.partMasterId = ''
      formData.partNumber = ''
      formData.partName = props.rowData.partName
      formData.defaultUnit = 'PCS'
      formData.source = props.rowData.source
      formData.assemblyMode = props.rowData.assemblyMode
      formData.categoryCode = props.rowData.categoryCode
      formData.weight = props.rowData.weight
      formData.height = props.rowData.height
      formData.width = ''
      formData.length = ''
      formData.description = ''
      formData.material = ''
      formData.specification = ''
      formData.unit = ''
      formData.parentPart = ''
      formData.quantity = 1
      formData.notes = ''

       // 获取 BOM 子项并更新到表格
      fetchBomItems()  // 获取并更新 bomItems 数据

      // 再用rowData回填
      Object.keys(formData).forEach(key => {
        if (props.rowData && props.rowData[key] !== undefined) {
          formData[key] = props.rowData[key]
        }
      })
      
      bomItems.value = props.rowData?.bomItems ? [...props.rowData.bomItems] : []
    }
  }
)



// ---------- 工具栏按钮 ----------
const showAddPartDialog = () => {
  console.log('当前父部件ID:', props.rowData.partId)
  addPartDialogVisible.value = true
}

const showBomDialog = () => {
  bomDialogVisible.value = true
}
const showParentDialog = () => {
  parentDialogVisible.value = true
}


// ---------- API hook ----------
const { execute: updatePart, loading } = useUpdatePart()

// ---------- 主弹窗操作 ----------
const handleClose = () => {
  emit('update:visible', false)
}
const handleSave = async () => {
  try {
    // 校验partMasterId不能为空
    if (!formData.partMasterId) {
      ElMessage.error('主对象ID（partMasterId）不能为空')
      return
    }
    await updatePart({
      masterId: formData.partMasterId.toString(),
      name: formData.partName,
      number: formData.partNumber || '',
      source: formData.source,
      partType: formData.assemblyMode,
      categoryId: formData.categoryCode.toString(),
      clsAttrs: {
        height: formData.height,
        Brand: formData.width, // 可根据实际表单补充
        Weight: formData.weight,
        Size: '0', // 可根据实际表单补充
        Number: '0' // 可根据实际表单补充
      }
    })
    ElMessage.success('编辑成功')
    emit('save')
    handleClose()
  } catch (e) {
    ElMessage.error('编辑失败')
  }
}

// 响应式数据
const bomItems = ref([]);

const fetchBomItems = async () => {
  try {
    const parentPartId = props.rowData.partId  // 获取父部件的 partId
    const response = await apiParentPartQuery({ parentPartId })  // 调用接口获取子部件数据
    console.log(response.data);  // 打印返回的数据，检查是否包含编码、数量和位号
    bomItems.value = response.data || []  // 更新 bomItems，这样表格就会显示新数据
  } catch (error) {
    console.error('获取子部件数据失败:', error)
    ElMessage.error('获取子部件数据失败')
  }
}




// ---------- BOM 子项增/删 ----------
const addParts = (items) => {
  // 确保 bomItems 是一个数组
  if (!Array.isArray(bomItems.value)) {
    console.error("bomItems 应该是一个数组，但当前是:", typeof bomItems.value);
    bomItems.value = []; // 如果不是数组，重新初始化为数组
  }

  console.log("添加的子项：", items); // 打印接收到的子项

  // 使用 ... 扩展符添加多个项
  bomItems.value.push(...items);  // 确保将子项添加到 bomItems 数组中
};

const editBomDialogVisible = ref(false);
const editBomData = reactive({
  quantity: 1,
  position: ''
});

// 显示修改 BOM 弹窗
const showEditBomDialog = (row) => {
  // 将当前行的数据填充到弹窗表单中
  editBomData.bomLinkId = row.bomLinkId
  editBomData.quantity = row.quantity
  editBomData.position = row.referenceDesignator
  // 显示弹窗
  editBomDialogVisible.value = true
}

const handleEditBomSave = async () => {
  try {
    const param = {
      bomLinkId: editBomData.bomLinkId.toString(),  // 将 bomLinkId 转换为字符串
      quantity: editBomData.quantity,                // 获取修改后的数量
      referenceDesignator: editBomData.position,     // 获取修改后的位号
    }

    // 调用 apiBomUpdate 更新 BOM 项
    const response = await apiBomUpdate(param)

    // 如果更新成功，更新 bomItems 数据并关闭弹窗
    if (response.code === 1) {
      // 在 bomItems 中找到对应的 BOM 项并更新
      const index = bomItems.value.findIndex(item => item.bomLinkId === editBomData.bomLinkId)
      if (index !== -1) {
        bomItems.value[index].quantity = editBomData.quantity
        bomItems.value[index].referenceDesignator = editBomData.position
      }
      ElMessage.success('BOM项已更新')
      editBomDialogVisible.value = false  // 关闭弹窗
    } else {
      ElMessage.error('更新失败')
    }
  } catch (error) {
    console.error('更新BOM项失败:', error)
    ElMessage.error('更新BOM项失败')
  }
}


const removeBomItem = async (index) => {
  try {
    // 获取 BOM 项的 ID，并将其转换为字符串
    const bomLinkId = bomItems.value[index].bomLinkId.toString()  // 转换 BigInt 为 string

    // 调用删除接口
    const response = await apiBomDelete({ ids: [bomLinkId] })
    
    // 如果删除成功，移除 bomItems 中对应的项
    if (response.code === 1) {  // 根据接口返回的 code 判断是否成功
      bomItems.value.splice(index, 1)  // 从 bomItems 中移除对应项
      ElMessage.success('BOM项已删除')  // 提示删除成功
    } else {
      ElMessage.error('删除失败')  // 提示删除失败
    }
  } catch (error) {
    console.error('删除BOM项失败:', error)
    ElMessage.error('删除BOM项失败')
  }
}




</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
.el-form {
  padding: 20px;
}
.el-tabs {
  margin-bottom: 20px;
}
.bom-toolbar {
  margin-bottom: 12px;
  display: flex;
  gap: 10px;
}
.tree-header,
.bom-tree .el-tree-node__content {
  display: flex;
  align-items: center;
  line-height: 28px;
}
.col-code {
  width: 160px;
}
.col-name {
  flex: 1;
}
.col-qty {
  width: 80px;
  text-align: right;
}
.col-pos {
  width: 120px;
}
.tree-header {
  font-weight: 600;
  padding: 4px 0;
  border-bottom: 1px solid var(--el-border-color-light, #ebeef5);
}
</style>

