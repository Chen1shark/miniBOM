<template>
  <el-dialog :title="mode === 'create' ? '新建属性' : '编辑属性'" :model-value="visible" width="500px" @close="$emit('close')">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px">
      <el-form-item label="属性中文名" prop="name" v-if="mode === 'create'">
        <el-input v-model="form.name" placeholder="请输入属性中文名" />
      </el-form-item>
      <el-form-item label="属性中文名" v-else>
        <el-input v-model="form.name" disabled />
      </el-form-item>
      <el-form-item label="属性英文名" prop="nameEn" v-if="mode === 'create'">
        <el-input v-model="form.nameEn" placeholder="请输入属性英文名" />
      </el-form-item>
      <el-form-item label="属性英文名" v-else>
        <el-input v-model="form.nameEn" disabled />
      </el-form-item>
      <el-form-item label="属性中文描述" prop="description">
        <el-input v-model="form.description" type="textarea" placeholder="请输入属性中文描述" />
      </el-form-item>
      <el-form-item label="属性英文描述" prop="descriptionEn">
        <el-input v-model="form.descriptionEn" type="textarea" placeholder="请输入属性英文描述" />
      </el-form-item>
      <el-form-item label="数据类型" prop="type" v-if="mode === 'create'">
        <el-select v-model="form.type" placeholder="请选择数据类型">
          <el-option label="字符串型" value="STRING" />
          <el-option label="整数型" value="INTEGER" />
          <el-option label="实数型" value="DECIMAL" />
        </el-select>
      </el-form-item>
      <el-form-item label="数据类型" v-else>
        <el-input v-model="form.type" disabled />
      </el-form-item>
      <el-form-item label="属性状态">
        <el-switch
          v-model="form.disableFlag"
          :active-value="true"
          :inactive-value="false"
          active-text="失效"
          inactive-text="有效"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="$emit('close')">取消</el-button>
      <el-button type="primary" @click="onSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch } from 'vue'
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
  id: null,
  name: '',
  nameEn: '',
  description: '',
  descriptionEn: '',
  type: '',
  disableFlag: false
})

const rules = {
  name: [{ required: true, message: '请输入属性中文名', trigger: 'blur' }],
  nameEn: [{ required: true, message: '请输入属性英文名', trigger: 'blur' }],
  description: [{ required: true, message: '请输入属性中文描述', trigger: 'blur' }],
  descriptionEn: [{ required: true, message: '请输入属性英文描述', trigger: 'blur' }],
  type: [{ required: true, message: '请选择数据类型', trigger: 'change' }]
}

watch(
  () => props.initData,
  (val) => {
    if (props.mode === 'edit' && val) {
      Object.assign(form, val)
    } else if (props.mode === 'create') {
      form.id = null
      form.name = ''
      form.nameEn = ''
      form.description = ''
      form.descriptionEn = ''
      form.type = ''
      form.disableFlag = false
    }
  },
  { immediate: true, deep: true }
)

function onSubmit() {
  formRef.value.validate((valid) => {
    if (valid) {
      if (props.mode === 'edit') {
        // 编辑模式只提交可修改的字段
        const submitData = {
          id: form.id,
          description: form.description,
          descriptionEn: form.descriptionEn,
          disableFlag: form.disableFlag
        }
        emit('submit', submitData)
      } else {
        // 新建模式提交所有字段
        const submitData = {
          name: form.name,
          nameEn: form.nameEn,
          description: form.description,
          descriptionEn: form.descriptionEn,
          type: form.type,
          disableFlag: form.disableFlag
        }
        emit('submit', submitData)
      }
    }
  })
}
</script> 