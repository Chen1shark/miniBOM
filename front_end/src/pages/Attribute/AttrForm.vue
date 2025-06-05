<template>
  <el-dialog :title="mode === 'create' ? '新建属性' : '编辑属性'" :model-value="visible" width="500px" @close="$emit('close')">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
      <el-form-item label="中文名称" prop="nameZh">
        <el-input v-model="form.nameZh" placeholder="请输入中文名称" />
      </el-form-item>
      <el-form-item label="英文名称" prop="nameEn">
        <el-input v-model="form.nameEn" placeholder="请输入英文名称" />
      </el-form-item>
      <el-form-item label="中文描述" prop="descZh">
        <el-input v-model="form.descZh" placeholder="请输入中文描述" />
      </el-form-item>
      <el-form-item label="英文描述" prop="descEn">
        <el-input v-model="form.descEn" placeholder="请输入英文描述" />
      </el-form-item>
      <el-form-item label="数据类型" prop="dataType">
        <el-select v-model="form.dataType" placeholder="请选择数据类型">
          <el-option label="字符串型" value="string" />
          <el-option label="整数型" value="int" />
          <el-option label="实数型" value="float" />
        </el-select>
      </el-form-item>
      <el-form-item label="属性状态" prop="status">
        <el-select v-model="form.status" placeholder="请选择属性状态">
          <el-option label="有效" value="active" />
          <el-option label="失效" value="inactive" />
        </el-select>
      </el-form-item>
      <el-form-item label="属性类型" prop="type">
        <el-input v-model="form.type" placeholder="默认为扩展属性" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('close')">取消</el-button>
      <el-button type="primary" @click="onSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, toRefs } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  visible: Boolean,
  mode: {
    type: String,
    default: 'create',
  },
  initData: {
    type: Object,
    default: () => ({}),
  },
})
const emit = defineEmits(['close', 'submit'])

const formRef = ref()
const form = reactive({
  nameZh: '',
  nameEn: '',
  descZh: '',
  descEn: '',
  dataType: '',
  status: '',
  type: '扩展属性',
})

const rules = {
  nameZh: [{ required: true, message: '请输入中文名称', trigger: 'blur' }],
  nameEn: [{ required: true, message: '请输入英文名称', trigger: 'blur' }],
  dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }],
  status: [{ required: true, message: '请选择属性状态', trigger: 'change' }],
}

watch(
  () => props.initData,
  (val) => {
    if (props.mode === 'edit' && val) {
      Object.assign(form, val)
    } else if (props.mode === 'create') {
      form.nameZh = ''
      form.nameEn = ''
      form.descZh = ''
      form.descEn = ''
      form.dataType = ''
      form.status = ''
      form.type = '扩展属性'
    }
  },
  { immediate: true, deep: true }
)

function onSubmit() {
  formRef.value.validate((valid) => {
    if (valid) {
      emit('submit', { ...form })
    }
  })
}
</script> 