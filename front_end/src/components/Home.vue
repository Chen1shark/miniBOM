<template>
  <div class="layout">
    <div class="topbar">
      <Topbar />
    </div>
    <div class="body">
      <SidebarMenu />
      <div class="main-content">
        <div class="content-box">
          <h1>欢迎使用 miniBOM 系统</h1>
          <router-view />
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import SidebarMenu from '../components/SidebarMenu.vue'
import Topbar from '../components/Topbar.vue' 
</script>

<style scoped>
/* 顶层垂直布局 */
.layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  width: 97vw;
  background: linear-gradient(to bottom right, #f0f0f0, #d9e7f4);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

/* 顶部栏 */
.topbar {
  height: 60px;
  background-color: #ffffff;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

/* 主体横向布局：左侧栏 + 内容区 */
.body {
  display: flex;
  flex: 1;
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
  overflow-y: auto;
}

/* 内容盒子居中展示 */
.content-box {
  width: 100vw;
  height: 100%;
  padding: 60px 40px;
  box-sizing: border-box;
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
}

/* 标题样式 */
h1 {
  text-align: center;
  font-size: 2.5rem;
  font-weight: 600;
  color: #333333;
  margin-bottom: 40px;
  letter-spacing: 1px;
}


</style>