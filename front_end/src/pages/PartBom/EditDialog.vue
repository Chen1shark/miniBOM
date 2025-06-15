<template>
  <el-dialog
    v-model="dialogVisible"
    title="编辑信息"
    width="60%"
    :before-close="handleClose"
  >  
     <!--下面具体内容待修改-->
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="basic">
        <el-form :model="formData" label-width="120px">
          <el-form-item label="零件编号">
            <el-input v-model="formData.partNumber" />
          </el-form-item>
          <el-form-item label="零件名称">
            <el-input v-model="formData.partName" />
          </el-form-item>
          <el-form-item label="描述">
            <el-input type="textarea" v-model="formData.description" />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="属性信息" name="attributes">
        <el-form :model="formData" label-width="120px">
          <el-form-item label="材料">
            <el-input v-model="formData.material" />
          </el-form-item>
          <el-form-item label="规格">
            <el-input v-model="formData.specification" />
          </el-form-item>
          <el-form-item label="单位">
            <el-input v-model="formData.unit" />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="BOM信息" name="bom">
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

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'

// 定义props接收父组件传递的数据
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

// 定义emit事件
const emit = defineEmits(['update:visible'])

// 控制弹窗显示
const dialogVisible = ref(props.visible)

// 当前激活的标签页
const activeTab = ref('basic')

// 表单数据
const formData = reactive({
  partNumber: '',
  partName: '',
  description: '',
  material: '',
  specification: '',
  unit: '',
  parentPart: '',
  quantity: 1,
  notes: ''
})

// 监听visible属性变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
  if (newVal) {
    // 当弹窗打开时，将行数据填充到表单
    Object.assign(formData, props.rowData)
  }
})

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// 保存数据
const handleSave = () => {
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
</style> 