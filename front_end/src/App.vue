<script setup>
import BasicDis from '@/pages/PartBom/BasicDis.vue'
import Home from './components/Home.vue';

</script>

<template>
  <router-view />
</template>

<style>
/* 全局样式重置 */
html,
body,
#app {
  width: 100%;
  height: 100%;
  margin: 0;
  padding: 0;
  /* 如果有 max-width, line-height, text-align 等限制，也一并移除或重置 */
  max-width: none;
}
</style>
