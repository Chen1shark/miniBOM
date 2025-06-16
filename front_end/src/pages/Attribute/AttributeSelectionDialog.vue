<template>
  <el-dialog
    title="选择属性"
    :model-value="visible"
    width="800px"
    @close="handleClose"
  >
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="请输入属性名称搜索"
        @input="handleSearch"
        clearable
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
      </el-input>
    </div>

    <el-table
      :data="attributeList"
      style="width: 100%"
      @selection-change="handleSelectionChange"
      v-loading="loading"
    >
      <el-table-column type="selection" width="55" :selectable="isSelectable" />
      <el-table-column prop="nameZh" label="属性中文名称" />
      <el-table-column prop="nameEn" label="属性英文名称" />
      <el-table-column prop="dataType" label="数据类型" />
      <el-table-column prop="description" label="中文描述" show-overflow-tooltip />
      <el-table-column prop="descriptionEn" label="英文描述" show-overflow-tooltip />
      <el-table-column label="状态" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.selected" type="success">已选</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue';
import { Search } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';

const props = defineProps({
  visible: { type: Boolean, default: false },
  attributeList: { type: Array, default: () => [] }
});

const emit = defineEmits(['close', 'confirm', 'search']);

const searchKeyword = ref('');
const loading = ref(false);
const selectedAttributes = ref([]);

// 处理搜索
const handleSearch = () => {
  emit('search', searchKeyword.value);
};

// 判断属性是否可选
const isSelectable = (row) => {
  return !row.selected;
};

// 处理表格选择变化
const handleSelectionChange = (selection) => {
  selectedAttributes.value = selection;
};

// 处理关闭
const handleClose = () => {
  searchKeyword.value = '';
  selectedAttributes.value = [];
  emit('close');
};

// 处理确认
const handleConfirm = () => {
  if (selectedAttributes.value.length === 0) {
    ElMessage.warning('请至少选择一个属性');
    return;
  }
  emit('confirm', selectedAttributes.value);
  handleClose();
};

// 监听属性列表变化
watch(() => props.attributeList, (newVal) => {
  console.log('属性列表更新:', newVal); // 添加日志
  // 更新选中状态
  selectedAttributes.value = newVal.filter(attr => attr.selected);
}, { deep: true });

// 监听对话框显示状态
watch(() => props.visible, (newVal) => {
  if (newVal) {
    // 对话框打开时，重置搜索关键字
    searchKeyword.value = '';
  }
});
</script>

<style scoped>
.search-bar {
  margin-bottom: 20px;
}
</style> 