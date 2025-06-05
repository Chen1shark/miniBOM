<template>
  <el-dialog title="属性分类信息" :model-value="visible" width="600px" @close="$emit('close')">
    <el-table :data="categoryList" style="width: 100%">
      <el-table-column prop="categoryCode" label="分类号" />
      <el-table-column prop="categoryNameZh" label="分类中文名" />
      <el-table-column prop="categoryNameEn" label="分类英文名" />
      <el-table-column prop="parentCategoryNameZh" label="分类中文父类名" />
      <el-table-column prop="parentCategoryNameEn" label="分类英文父类名" />
    </el-table>
    <template #footer>
      <el-button @click="$emit('close')">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
const props = defineProps({
  visible: Boolean,
  categoryList: {
    type: Array,
    default: () => [],
  },
})
const emit = defineEmits(['close'])
</script> 