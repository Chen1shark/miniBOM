<template>
  <el-dialog
    v-model="dialogVisible"
    title="添加部件"
    width="60%"
    :before-close="handleClose"
  >  
    <el-tabs v-model="activeTab">
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
              :props="{
                label: 'name',
                children: 'children',
                value: 'id'
              }"
              check-strictly
              node-key="id"
              default-expand-all
            />
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="扩展属性" name="extended">
        <el-form :model="formData.extendedAttrs" label-width="120px">
          <template v-for="attr in extendedAttributes" :key="attr.id">
            <el-form-item :label="attr.name">
              <el-input v-model="formData.extendedAttrs[attr.nameEn]" :placeholder="attr.description || attr.name" />
            </el-form-item>
          </template>
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
import { useCreatePart } from '@/hooks/usePartApi'
import { ElMessage } from 'element-plus'
import { useCategoryTreeInPart } from '@/hooks/useCategoryTreeInPart'
const { categoryTree, fetchCategoryTreeData, loading: categoryTreeLoading, fetchCategoryAttribute } = useCategoryTreeInPart()

// 定义props接收父组件传递的数据
const props = defineProps({
  visible: {
    type: Boolean,
    default: false
  }
})

// 定义emit事件
const emit = defineEmits(['update:visible', 'save'])

// 控制弹窗显示
const dialogVisible = ref(props.visible)

// 当前激活的标签页
const activeTab = ref('basic')

// 表单数据
const formData = reactive({
  partName: '',
  defaultUnit: '',
  assemblyMode: '',
  categoryCode: '',
  source: '',
  extendedAttrs: {} // 用于存储扩展属性的值
})

const extendedAttributes = ref([])

// 监听visible属性变化
watch(() => props.visible, (newVal) => {
  dialogVisible.value = newVal
  if (newVal) {
    fetchCategoryTreeData()
    Object.keys(formData).forEach(key => {
      formData[key] = ''
    })
    // 弹窗打开时主动触发一次扩展属性获取
    if (formData.categoryCode) {
      fetchCategoryAttribute(formData.categoryCode).then(attrs => {
        extendedAttributes.value = attrs || []
        formData.extendedAttrs = {}
        extendedAttributes.value.forEach(attr => {
          formData.extendedAttrs[attr.nameEn] = ''
        })
      })
    } else {
      extendedAttributes.value = []
      formData.extendedAttrs = {}
    }
  }
})

// 监听分类选择变化，动态获取扩展属性
watch(() => formData.categoryCode, async (newVal) => {
  if (newVal) {
    const attrs = await fetchCategoryAttribute(newVal)
    extendedAttributes.value = attrs || []
    // 初始化扩展属性对象
    formData.extendedAttrs = {}
    extendedAttributes.value.forEach(attr => {
      formData.extendedAttrs[attr.nameEn] = ''
    })
  } else {
    extendedAttributes.value = []
    formData.extendedAttrs = {}
  }
})

// 关闭弹窗
const handleClose = () => {
  emit('update:visible', false)
}

// API hook
const { execute: createPart, loading } = useCreatePart()

// 保存数据
const handleSave = async () => {
  try {
    await createPart({
      name: formData.partName,
      number: '', // 可根据实际表单补充
      source: formData.source,
      partType: formData.assemblyMode,
      categoryId: formData.categoryCode.toString(),
      clsAttrs: formData.extendedAttrs // 提交扩展属性
    })
    ElMessage.success('添加成功')
    emit('save')
    handleClose()
  } catch (e) {
    // 显示后端返回的具体错误信息
    const errorMessage = e.message || '添加失败'
    ElMessage.error(errorMessage)
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

.el-select {
  width: 100%;
}
</style> 