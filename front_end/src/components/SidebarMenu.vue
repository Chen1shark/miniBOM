<template>
  <div class="sidebar">
    <div class="menu-group">
      <h3>属性管理</h3>
      
      <button @click="$router.push('/Home/attribute/create')">📌 创建属性</button>
      <button @click="$router.push('/Home/attribute/edit')">✏️ 修改属性</button>
      <button @click="$router.push('/Home/attribute/delete')">🗑️ 删除属性</button>
      <button @click="$router.push('/Home/attribute/query')">🔍 查询属性</button>
    </div>

    <div class="menu-group">
      <h3>分类管理</h3>
      <button @click="$router.push('/Home/category/create')">📌 创建分类模板</button>
      <button @click="$router.push('/Home/category/edit')">✏️ 修改分类模板</button>
      <button @click="$router.push('/Home/category/generate')">📎 作成分类模板</button>
      <button @click="$router.push('/Home/category/query-one')">🔍 查询单个分类</button>
      <button @click="$emit('showCategoryTree')">📚 查看分类树</button>
    </div>

    <div class="menu-group">
      <button @click="$router.push('/Home/part/basicShow')">Part & BOM管理</button>
    </div>
  </div>
</template>

<style scoped>
.sidebar {
  width: 250px;
  background: #f9f9f9;
  padding: 24px 16px;
  box-shadow: 2px 0 6px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  gap: 24px;
  font-family: 'Segoe UI', sans-serif;
  height: 100vh; /* 占据全屏高度 */
  overflow-y: auto; /* 允许滚动 */
  position: sticky;
  top: 0;
}


.menu-group {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.menu-group h3 {
  margin-bottom: 12px;
  font-size: 16px;
  color: #333;
  border-left: 4px solid #409eff;
  padding-left: 8px;
}

button {
  width: 100%;
  background: #ffffff;
  border: none;
  padding: 10px 12px;
  text-align: left;
  font-size: 14px;
  color: #444;
  border-radius: 6px;
  transition: all 0.2s ease-in-out;
  margin-bottom: 6px;
}

button:hover {
  background: #409eff;
  color: white;
  transform: translateX(4px);
  box-shadow: 0 2px 6px rgba(64, 158, 255, 0.3);
}
</style>
