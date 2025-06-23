<template>
  <div class="category-query-all-container">
    <el-card class="query-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span class="title">分类树状图</span>
          <div class="header-actions">
            <el-button type="primary" @click="refreshTree" :loading="loading">
              <el-icon><Refresh /></el-icon>
              刷新
            </el-button>
          </div>
        </div>
      </template>

      <div class="tree-content">
        <category-tree-view :treeData="categoryTreeData"></category-tree-view>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import { Refresh } from '@element-plus/icons-vue';
import { apiCateGet } from '@/api/CateGet';
import CategoryTreeView from './CategoryTreeView.vue';

const categoryTreeData = ref([]);
const loading = ref(false);

// 构建分类树状结构
const buildCategoryTree = (categories) => {
  const categoryMap = new Map();
  const rootCategories = [];

  // 首先创建所有分类的映射
  categories.forEach(category => {
    categoryMap.set(category.id, {
      id: category.id,
      categoryCode: category.code,
      categoryNameZh: category.name,
      categoryNameEn: category.nameEn,
      children: []
    });
  });

  // 构建父子关系
  categories.forEach(category => {
    const node = categoryMap.get(category.id);
    if (category.parentId && categoryMap.has(category.parentId)) {
      // 有父分类，添加到父分类的children中
      const parent = categoryMap.get(category.parentId);
      parent.children.push(node);
    } else {
      // 没有父分类或父分类不存在，作为根节点
      rootCategories.push(node);
    }
  });

  return rootCategories;
};

const fetchCategoryTreeData = async () => {
  try {
    loading.value = true;
    console.log('Fetching category tree data');
    
    // 获取所有分类数据
    const response = await apiCateGet(1, 1000, '', '', '');
    console.log('response', response);
    
    if (response && response.code === 1 && response.data && response.data.records) {
      // 构建树状结构
      categoryTreeData.value = buildCategoryTree(response.data.records);
      console.log('分类树数据:', categoryTreeData.value);
      ElMessage.success('分类树加载成功');
    } else {
      ElMessage.error('获取分类树数据失败');
    }
  } catch (error) {
    console.error('获取分类树数据失败:', error);
    ElMessage.error('获取分类树数据失败');
  } finally {
    loading.value = false;
  }
};

// 刷新树状图
const refreshTree = () => {
  fetchCategoryTreeData();
};

onMounted(() => {
  fetchCategoryTreeData();
});
</script>

<style scoped>
.category-query-all-container {
  padding: 20px;
  height: 100%;
  display: flex;
  flex-direction: column;
}

.query-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}

.title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.tree-content {
  flex: 1;
  padding: 20px;
  overflow: auto;
  display: flex;
  justify-content: flex-start;
  align-items: flex-start;
}
</style>
  