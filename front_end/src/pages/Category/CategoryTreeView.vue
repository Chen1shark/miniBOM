<template>
  <div class="category-tree-view">
    <el-tree 
      :data="treeData" 
      :props="defaultProps" 
      @node-click="handleNodeClick"
      node-key="categoryCode"
      :expand-on-click-node="false"
      default-expand-all
    >
      <template #default="{ node, data }">
        <span class="custom-tree-node">
          <span class="category-name">
            {{ data.categoryNameZh }}
          </span>
          <span class="category-code">
            ({{ data.categoryCode }})
          </span>
        </span>
      </template>
    </el-tree>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const props = defineProps({
  treeData: { // 定义 treeData prop
    type: Array,
    default: () => [],
  },
});

// 声明 emits
const emit = defineEmits(['node-click']);

const defaultProps = {
  children: 'children',
  label: 'categoryNameZh', // 假设以中文名称作为节点标签
};

const handleNodeClick = (data) => {
  console.log('Tree node clicked:', data);
  // 触发 node-click 事件并传递节点数据
  emit('node-click', data);
};

</script>

<style scoped>
.category-tree-view {
  width: 100%;
  max-width: 600px;
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.category-tree-view .el-tree {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: transparent;
}

/* 自定义树节点样式 */
.el-tree-node__content {
  height: 36px;
  border-radius: 4px;
  margin: 1px 0;
  transition: background-color 0.2s ease;
}

.el-tree-node__content:hover {
  background-color: #f5f7fa;
}

/* 选中节点样式 */
.el-tree-node.is-current > .el-tree-node__content {
  background-color: #ecf5ff;
  color: #409eff;
}

.custom-tree-node {
  display: flex;
  align-items: center;
  width: 100%;
  padding: 2px 0;
}

.category-name {
  flex: 1;
  font-weight: 500;
  color: #303133;
}

.category-code {
  margin-left: 8px;
  color: #909399;
  font-size: 12px;
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 3px;
  font-family: 'Courier New', monospace;
}

/* 树节点展开/收起图标样式 */
.el-tree-node__expand-icon {
  color: #909399;
  font-size: 14px;
}

.el-tree-node__expand-icon.expanded {
  transform: rotate(90deg);
}

/* 空状态样式 */
.el-tree__empty-block {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  color: #909399;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-tree-view {
    max-width: 100%;
    border-radius: 8px;
  }
  
  .category-tree-view .el-tree {
    padding: 15px;
  }
  
  .el-tree-node__content {
    height: 36px;
  }
  
  .category-name {
    font-size: 14px;
  }
  
  .category-code {
    font-size: 11px;
    padding: 1px 4px;
  }
}

@media (max-width: 480px) {
  .category-tree-view .el-tree {
    padding: 10px;
  }
  
  .el-tree-node__content {
    height: 32px;
  }
  
  .category-name {
    font-size: 13px;
  }
  
  .category-code {
    font-size: 10px;
  }
}
</style> 