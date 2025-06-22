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
    <el-table-column prop="code" label="编码" width="160" />
    <el-table-column prop="name" label="名称" />
    <el-table-column label="数量" width="120">
      <template #default="{ row }">
        <el-input-number v-model="row.quantity" :min="1" @change="onBomChange(row)" />
      </template>
    </el-table-column>
    <el-table-column label="位号" width="160">
      <template #default="{ row }">
        <el-input v-model="row.position" @input="onBomChange(row)" />
      </template>
    </el-table-column>
    <el-table-column label="操作" width="100" align="center">
      <template #default="{ $index }">
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

    <!-- 新增子项组件 -->
    <AddPartDialog
      :visible="addPartDialogVisible"
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
import { apiBomChecklist, apiBomUpdate, apiBomDelete } from '@/api/BOM'

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
  masterId: '',
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

const categoryTree = [
  {
    id: '1',
    name: '机械类',
    children: [
      {
        id: '1-1',
        name: '传动系统',
        children: [
          { id: '1-1-1', name: '齿轮' },
          { id: '1-1-2', name: '轴承' },
          { id: '1-1-3', name: '轴' }
        ]
      },
      {
        id: '1-2',
        name: '液压系统',
        children: [
          { id: '1-2-1', name: '油缸' },
          { id: '1-2-2', name: '泵' },
          { id: '1-2-3', name: '阀' }
        ]
      }
    ]
  },
  {
    id: '2',
    name: '电气类',
    children: [
      {
        id: '2-1',
        name: '控制系统',
        children: [
          { id: '2-1-1', name: '控制器' },
          { id: '2-1-2', name: '传感器' },
          { id: '2-1-3', name: '执行器' }
        ]
      },
      {
        id: '2-2',
        name: '动力系统',
        children: [
          { id: '2-2-1', name: '电机' },
          { id: '2-2-2', name: '电池' },
          { id: '2-2-3', name: '电源' }
        ]
      }
    ]
  },
  {
    id: '3',
    name: '结构类',
    children: [
      {
        id: '3-1',
        name: '框架',
        children: [
          { id: '3-1-1', name: '主框架' },
          { id: '3-1-2', name: '支撑架' }
        ]
      },
      {
        id: '3-2',
        name: '外壳',
        children: [
          { id: '3-2-1', name: '防护罩' },
          { id: '3-2-2', name: '机箱' }
        ]
      }
    ]
  }
]



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
      // 先重置
      Object.keys(formData).forEach(key => {
        formData[key] = '' // 或初始值
      })
      formData.quantity = 1 // 例如初始值
      formData.defaultUnit = 'PCS'
      // 再填充
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
  addPartDialogVisible.value = true
}
const showBomDialog = () => {
  bomDialogVisible.value = true
}
const showParentDialog = () => {
  parentDialogVisible.value = true
}

// ---------- BOM 子项增/删 ----------
const addParts = (items) => {
  bomItems.value.push(...items)
}

// ---------- API hook ----------
const { execute: updatePart, loading } = useUpdatePart()

// ---------- 主弹窗操作 ----------
const handleClose = () => {
  emit('update:visible', false)
}
const handleSave = async () => {
  try {
    await updatePart({
      masterId: formData.masterId,
      name: formData.partName,
      number: formData.partNumber || '',
      source: formData.source,
      partType: formData.assemblyMode,
      categoryId: formData.categoryCode,
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

// 获取BOM清单
const fetchBomItems = async () => {
  try {
    const response = await apiBomChecklist();
    bomItems.value = response.data; // 假设接口返回的数据格式是 { data: [...] }
  } catch (error) {
    console.error('获取BOM清单失败:', error);
  }
};

// 更新BOM项
const onBomChange = async (row) => {
  try {
    const param = {
      bomLinkId: row.bomLinkId, // 假设每个 BOM 项都有 bomLinkId
      quantity: row.quantity,
      referenceDesignator: row.position,
    };
    await apiBomUpdate(param); // 调用更新接口
    console.log('BOM项已更新');
  } catch (error) {
    console.error('更新BOM项失败:', error);
  }
};

// 删除BOM项
const removeBomItem = async (index) => {
  try {
    const param = { ids: [bomItems.value[index].bomLinkId] }; // 获取bomLinkId
    await apiBomDelete(param); // 调用删除接口
    bomItems.value.splice(index, 1); // 从数据中删除
    console.log('BOM项已删除');
  } catch (error) {
    console.error('删除BOM项失败:', error);
  }
};

// 在组件挂载后获取BOM清单
onMounted(() => {
  fetchBomItems();
});

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

