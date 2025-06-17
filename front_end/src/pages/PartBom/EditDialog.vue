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
              <el-option label="制造" value="制造" />
              <el-option label="购买" value="购买" />
            </el-select>
          </el-form-item>
          <el-form-item label="装配模式">
            <el-select v-model="formData.assemblyMode" placeholder="请选择模式">
              <el-option label="装配part" value="装配part" />
              <el-option label="零件part" value="零件part" />
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

        <!-- 可编辑表格 -->
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
import { ref, reactive, watch, computed } from 'vue'
import AddPartDialog from './AddPartDialog.vue'

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


// ---------- BOM 清单数据 (模拟初始树结构) ----------
const bomItems = ref([
  {
    code: 'A-1000',
    name: '主机架',
    quantity: 1,
    position: '-',
    children: [
      {
        code: 'B-1100',
        name: '传动系统',
        quantity: 1,
        position: '-',
        children: [
          { code: 'C-1110', name: '齿轮', quantity: 4, position: 'G1' },
          { code: 'C-1120', name: '轴承', quantity: 2, position: 'B1-B2' }
        ]
      },
      {
        code: 'B-1200',
        name: '控制系统',
        quantity: 1,
        position: '-',
        children: [
          { code: 'C-1210', name: '电机', quantity: 1, position: 'M1' },
          { code: 'C-1220', name: '传感器', quantity: 3, position: 'S1-S3' }
        ]
      }
    ]
  }
])

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
      Object.assign(formData, props.rowData)
      bomItems.value = props.rowData?.bomItems ? [...props.rowData.bomItems] : []
    }
  }
)

// ---------- 表格行变化回调 ----------
const onBomChange = (row) => {
  console.log('BOM 行已更新', row)
}

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
const removeBomItem = (index) => {
  bomItems.value.splice(index, 1)
}

// ---------- 主弹窗操作 ----------
const handleClose = () => {
  emit('update:visible', false)
}
const handleSave = () => {
  emit('save', { ...formData, bomItems: bomItems.value })
  handleClose()
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

