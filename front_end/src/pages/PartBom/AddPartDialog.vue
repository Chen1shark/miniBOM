<template>
  <el-dialog v-model="visible" title="添加部件" width="70%" @closed="emit('update:visible', false)">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-input v-model="searchCode" placeholder="按编码搜索" clearable @input="fetchParts" />
      <el-input v-model="searchName" placeholder="按名称搜索" clearable @input="fetchParts" style="margin-left: 10px;" />
      <el-button type="primary" @click="fetchParts" style="margin-left: 10px;">搜索</el-button>
    </div>

    <!-- 选择列表 -->
    <el-table
      :data="filteredParts"
      border
      style="width: 100%; margin-top: 12px;"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="55" />
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
import { ref, reactive, watch, computed } from 'vue'
import { apiGetAllpart } from '@/api/partApi';  // 引入接口方法

const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})
const emit = defineEmits(['update:visible', 'add'])

// 对话框显隐
const visible = ref(props.visible)
watch(
  () => props.visible,
  (val) => (visible.value = val)
)

// 搜索条件
const searchCode = ref('')
const searchName = ref('')

// 数据源（初始化为空，待接口请求后填充）
const parts = ref([])

const fetchParts = async () => {
  try {
    const response = await apiGetAllpart();
    console.log('获取的数据:', response); // 打印获取的原始数据

    // 从数据中提取list数组
    parts.value = response.data.list || [];
  } catch (error) {
    console.error('获取部件失败', error);
  }
}

// 过滤后的展示数据
const filteredParts = computed(() => {
  return parts.value.filter((p) => {
    return (
      (!searchCode.value || p.partId.toString().includes(searchCode.value)) &&
      (!searchName.value || p.name.includes(searchName.value))
    )
  })
})

// 选择的行
const selectedParts = ref([])
const onSelectionChange = (rows) => {
  selectedParts.value = rows.map((r) => ({ ...r, quantity: 1, position: '' }))
}

// 填数量 & 位号弹窗
const quantityDialogVisible = ref(false)
const openQuantityDialog = () => {
  quantityDialogVisible.value = true
}

const submit = () => {
  emit('add', selectedParts.value)
  quantityDialogVisible.value = false
  visible.value = false
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
