<template>
  <el-dialog title="分类属性信息" :model-value="visible" width="800px" @close="$emit('close')">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="分类信息" name="category">
    <el-table :data="categoryList" style="width: 100%">
      <el-table-column prop="categoryCode" label="分类号" />
      <el-table-column prop="categoryNameZh" label="分类中文名" />
      <el-table-column prop="categoryNameEn" label="分类英文名" />
      <el-table-column prop="parentCategoryNameZh" label="分类中文描述" />
      <el-table-column prop="parentCategoryNameEn" label="分类英文描述" />
    </el-table>
      </el-tab-pane>
      <el-tab-pane label="属性信息" name="attributes">
        <el-table :data="attributeList" style="width: 100%" v-loading="loading">
          <el-table-column prop="name" label="属性中文名" />
          <el-table-column prop="nameEn" label="属性英文名" />
          <el-table-column prop="description" label="中文描述" show-overflow-tooltip />
          <el-table-column prop="descriptionEn" label="英文描述" show-overflow-tooltip />
          <el-table-column prop="type" label="数据类型" />
          <el-table-column label="约束信息" width="200">
            <template #default="scope">
              <div v-if="scope.row.constraint">
                <div>精度: {{ scope.row.constraint.precision }}</div>
                <div>长度: {{ scope.row.constraint.length }}</div>
                <div>必填: {{ scope.row.constraint.notnull ? '是' : '否' }}</div>
                <div>多值: {{ scope.row.constraint.multiValue ? '是' : '否' }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="enable" label="状态">
            <template #default="scope">
              {{ scope.row.enable ? '启用' : '禁用' }}
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
    <template #footer>
      <el-button @click="$emit('close')">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { apiCateQueryAttribute } from '@/api/CateQueryAttribute'

const props = defineProps({
  visible: Boolean,
  categoryList: {
    type: Array,
    default: () => [],
  },
})

const emit = defineEmits(['close'])

const activeTab = ref('category')
const attributeList = ref([])
const loading = ref(false)

// 监听分类列表变化，获取第一个分类的属性
watch(() => props.categoryList, async (newList) => {
  if (newList && newList.length > 0 && newList[0].id) {
    try {
      loading.value = true
      const res = await apiCateQueryAttribute(BigInt(newList[0].id).toString())
      if (res.code === 1) {
        attributeList.value = res.data
      } else {
        ElMessage.error(res.msg || '获取属性列表失败')
      }
    } catch (error) {
      console.error('获取属性列表失败:', error)
      ElMessage.error('获取属性列表失败')
    } finally {
      loading.value = false
    }
  } else {
    attributeList.value = [] // 如果没有分类或分类没有id，清空属性列表
  }
}, { immediate: true })
</script> 

<style scoped>
.el-tabs {
  margin-bottom: 20px;
}
</style> 