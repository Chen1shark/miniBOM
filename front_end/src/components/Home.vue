<template>
  <div class="layout">
    <div class="topbar">
      <Topbar />
    </div>
    <div class="body">
      <SidebarMenu @showCategoryTree="drawerVisible = true" />
      <div class="main-content">
        <div class="content-box">
          <h1 v-if="!$route.matched.length">欢迎使用 miniBOM 系统</h1>
          <router-view />
        </div>
      </div>
      <!-- 分类树抽屉 -->
      <el-drawer
        v-model="drawerVisible"
        title="分类树"
        direction="ltr"
        size="400px"
        :with-header="true"
        :close-on-click-modal="true"
        @open="fetchCategoryTreeData"
      >
        <CategoryTreeView :treeData="categoryTreeData" />
      </el-drawer>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import SidebarMenu from '../components/SidebarMenu.vue'
import Topbar from '../components/Topbar.vue'
import CategoryTreeView from '@/pages/Category/CategoryTreeView.vue'
import { ElMessage } from 'element-plus'
import { apiCateGet } from '@/api/CateGet'

const drawerVisible = ref(false)
const categoryTreeData = ref([])

// 构建分类树状结构
const buildCategoryTree = (categories) => {
  const categoryMap = new Map()
  const rootCategories = []
  categories.forEach(category => {
    categoryMap.set(category.id, {
      id: category.id,
      categoryCode: category.code,
      categoryNameZh: category.name,
      categoryNameEn: category.nameEn,
      children: []
    })
  })
  categories.forEach(category => {
    const node = categoryMap.get(category.id)
    if (category.parentId && categoryMap.has(category.parentId)) {
      const parent = categoryMap.get(category.parentId)
      parent.children.push(node)
    } else {
      rootCategories.push(node)
    }
  })
  return rootCategories
}

const fetchCategoryTreeData = async () => {
  try {
    // 获取所有分类数据
    const response = await apiCateGet(1, 1000, '', '', '')
    if (response && response.code === 1 && response.data && response.data.records) {
      categoryTreeData.value = buildCategoryTree(response.data.records)
    } else {
      ElMessage.error('获取分类树数据失败')
    }
  } catch (error) {
    ElMessage.error('获取分类树数据失败')
  }
}

onMounted(() => {
  fetchCategoryTreeData()
})
</script>

<style scoped>
/* 顶层垂直布局 */
.layout {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  background: linear-gradient(to bottom right, #f0f0f0, #d9e7f4);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  overflow: hidden; /* 防止出现滚动条 */
}

/* 顶部栏 */
.topbar {
  height: 60px;
  background-color: #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  z-index: 10;
  flex-shrink: 0; /* 防止顶部栏被压缩 */
}

/* 主体横向布局：左侧栏 + 内容区 */
.body {
  display: flex;
  flex: 1;
  overflow: hidden; /* 防止出现滚动条 */
}

/* 修改 SidebarMenu 为组件选择器 */
SidebarMenu {
  width: 250px;
  background-color: #ffffff;
  position: sticky;
  top: 60px; /* 顶部栏高度 */
  left: 0;
  flex-shrink: 0;
  height: calc(100vh - 60px);
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  z-index: 5;
}

/* 主内容填满剩余空间 */
.main-content {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0;
  overflow: hidden; /* 防止出现滚动条 */
}

/* 内容盒子居中展示 */
.content-box {
  width: 100%;
  height: 100%;
  padding: 20px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  text-align: center;
  overflow: auto; /* 内容过多时允许滚动 */
}

/* 标题样式 */
h1 {
  text-align: center;
  font-size: 2.5rem;
  font-weight: 600;
  color: #333333;
  margin: 20px 0;
  letter-spacing: 1px;
}


</style>