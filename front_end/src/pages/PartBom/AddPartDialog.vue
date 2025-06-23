<template>
  <el-dialog v-model="visible" title="添加部件" width="70%" @closed="emit('update:visible', false)">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchCode" placeholder="按编码搜索" clearable @input="fetchParts" />
      <el-input v-model="searchName" placeholder="按名称搜索" clearable @input="fetchParts" style="margin-left: 10px;" />
      <el-button type="primary" @click="fetchParts" style="margin-left: 10px;">搜索</el-button>
    </div>

    <!-- 选择列表（多选） -->
    <el-table
      :data="filteredParts"
      border
      style="width: 100%; margin-top: 12px;"
      @selection-change="onSelectionChange"
      :highlight-current-row="true"
      :current-row="currentPart"
      :row-key="row => row.partId"
      type="selection"
    >
      <el-table-column type="selection" width="50" label="#" />
      <el-table-column prop="partId" label="部件编号" width="180" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="version" label="版本" />
      <el-table-column prop="partType" label="部件类型" />
      <el-table-column prop="source" label="来源" />
    </el-table>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :disabled="!selectedParts.length" @click="openQuantityDialog">
          添加选定项
        </el-button>
      </span>
    </template>

    <!-- 填写数量 / 位号弹窗 -->
    <el-dialog v-model="quantityDialogVisible" title="填写数量和位号" width="30%">
      <div v-for="item in selectedParts" :key="item.partId" class="item-form" style="margin-bottom: 16px;">
        <p>{{ item.partId }} - {{ item.name }}</p>
        <el-form :model="item" :inline="true">
          <el-form-item label="数量">
            <el-input-number v-model="item.quantity" :min="1" />
          </el-form-item>
          <el-form-item label="位号">
            <el-input v-model="item.position" />
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="quantityDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submit">提交</el-button>
        </span>
      </template>
    </el-dialog>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { apiGetAllpart } from '@/api/partApi'
import { apiBomCreate } from '@/api/BOM'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: { type: Boolean, default: false },
  parentId: { type: String, required: true }
})
const emit = defineEmits(['update:visible', 'add'])

const visible = ref(props.visible)
watch(() => props.visible, val => {
  visible.value = val
  if (val) fetchParts()      // 弹窗打开时自动加载部件列表
})

const searchCode = ref('')
const searchName = ref('')
const parts = ref([])

const fetchParts = async () => {
  try {
    const response = await apiGetAllpart()
    parts.value = response.data.list || []
  } catch (error) {
    console.error('获取部件失败', error)
  }
}

const filteredParts = computed(() => {
  return parts.value.filter((p) => {
    return (
      (!searchCode.value || p.partId?.toString().includes(searchCode.value)) &&
      (!searchName.value || p.name?.includes(searchName.value))
    )
  })
})

// 选中的部件
const selectedParts = ref([])

const onSelectionChange = (selectedRows) => {
  selectedParts.value = selectedRows.map((row) => ({
    ...row,
    quantity: 1.00, // 设置初始数量为1
    position: ''    // 设置初始位号为空
  }))
}

// 填数量 & 位号弹窗
const quantityDialogVisible = ref(false)
const openQuantityDialog = () => {
  if (selectedParts.value.length === 0) {
    ElMessage.warning('请选择一个或多个部件')
    return
  }
  quantityDialogVisible.value = true
}

const submit = async () => {
  if (!selectedParts.value || selectedParts.value.length === 0) return
  try {
    const params = selectedParts.value.map((part) => {
      return {
        sourceId: String(props.parentId), // 父部件 ID，转换为字符串
        targetId: part.partMasterId,            // 子部件 ID
        quantity: part.quantity.toFixed(2), // 保证传递两位小数的字符串
        referenceDesignator: part.position // 位号，直接传递字符串
      }
    })

    console.log('发送的请求参数:', params) // 查看发送的参数
    const response = await apiBomCreate(params)
    if (response.code === 1) {
      ElMessage.success('添加成功')
    } else {
      ElMessage.error('添加失败')
    }
    quantityDialogVisible.value = false
    visible.value = false
    emit('add', selectedParts.value)
  } catch (error) {
    console.error('创建 BOM 关系失败:', error)
    ElMessage.error('创建 BOM 关系失败')
  }
}
</script>

<style scoped>
.search-bar {
  display: flex;
  align-items: center;
}
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}
</style>
