<template>
  <el-dialog v-model="visible" title="添加部件" width="70%" @closed="emit('update:visible', false)">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchCode" placeholder="按编码搜索" clearable @input="fetchParts" />
      <el-input v-model="searchName" placeholder="按名称搜索" clearable @input="fetchParts" style="margin-left: 10px;" />
      <el-button type="primary" @click="fetchParts" style="margin-left: 10px;">搜索</el-button>
    </div>

    <!-- 选择列表（单选 radio） -->
    <el-table
      :data="filteredParts"
      border
      style="width: 100%; margin-top: 12px;"
      @current-change="onCurrentChange"
      :highlight-current-row="true"
      :current-row="currentPart"
    >
      <el-table-column type="index" width="50" label="#" />
      <el-table-column prop="partId" label="部件编号" width="180" />
      <el-table-column prop="name" label="名称" />
      <el-table-column prop="version" label="版本" />
      <el-table-column prop="partType" label="部件类型" />
      <el-table-column prop="source" label="来源" />
    </el-table>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button type="primary" :disabled="!currentPart" @click="openQuantityDialog">
          添加选定项
        </el-button>
      </span>
    </template>

    <!-- 填写数量 / 位号弹窗 -->
    <el-dialog v-model="quantityDialogVisible" title="填写数量和位号" width="30%">
      <div v-if="currentPart" class="item-form" style="margin-bottom: 16px;">
        <p>{{ currentPart.partId }} - {{ currentPart.name }}</p>
        <el-form :model="currentPart" :inline="true">
          <el-form-item label="数量">
            <el-input-number v-model="currentPart.quantity" :min="1" />
          </el-form-item>
          <el-form-item label="位号">
            <el-input v-model="currentPart.position" />
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
  parentId: { type: [Number, String], required: true }
})
const emit = defineEmits(['update:visible', 'add'])

// 对话框显隐
const visible = ref(props.visible)
watch(() => props.visible, val => {
  visible.value = val
  if (val) fetchParts()      // 弹窗打开时自动加载部件列表
})

// 搜索条件
const searchCode = ref('')
const searchName = ref('')

// 数据源
const parts = ref([])

const fetchParts = async () => {
  try {
    const response = await apiGetAllpart()
    parts.value = response.data.list || []
  } catch (error) {
    console.error('获取部件失败', error)
  }
}

// 过滤后的展示数据
const filteredParts = computed(() => {
  return parts.value.filter((p) => {
    return (
      (!searchCode.value || p.partId?.toString().includes(searchCode.value)) &&
      (!searchName.value || p.name?.includes(searchName.value))
    )
  })
})

// 单选选中项
const currentPart = ref(null)
const onCurrentChange = (row) => {
  if (row) {
    currentPart.value = {
      ...row,
      quantity: 1,
      position: ''
    }
  }
}

// 填数量 & 位号弹窗
const quantityDialogVisible = ref(false)
const openQuantityDialog = () => {
  if (!currentPart.value) {
    ElMessage.warning('请选择一个部件')
    return
  }
  quantityDialogVisible.value = true
}

// 提交，只处理单个
const submit = async () => {
  if (!currentPart.value) return
  try {
    const param = {
  sourceId: Number(props.parentId),              // 父部件ID
  targetId: currentPart.value.partId,            // 子部件ID
  quantity: currentPart.value.quantity,
  referenceDesignator: currentPart.value.position
}

    console.log('发送的请求参数:', param)
    const response = await apiBomCreate(param)
    if (response.code === 1) {
      ElMessage.success(`${currentPart.value.name} 添加成功`)
    } else {
      ElMessage.error(`${currentPart.value.name} 添加失败`)
    }
    quantityDialogVisible.value = false
    visible.value = false
    emit('add', currentPart.value)
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
