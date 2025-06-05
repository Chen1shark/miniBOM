import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/components/Login.vue'
import Home from '@/components/Home.vue'

const routes = [
  { path: '/', component: Login },
  { path: '/Home', component: Home },
  { path: '/attribute/create', component: () => import('@/pages/Attribute/AttrHome.vue') },
  { path: '/attribute/edit', component: () => import('@/pages/Attribute/AttrHome.vue') },
  { path: '/attribute/delete', component: () => import('@/pages/Attribute/AttrHome.vue') },
  { path: '/attribute/query', component: () => import('@/pages/Attribute/AttrHome.vue') },
  { path: '/category/create', component: () => import('@/pages/Category/CateHome.vue') },
  { path: '/category/edit', component: () => import('@/pages/Category/CateHome.vue') },
  { path: '/category/generate', component: () => import('@/pages/Category/CateHome.vue') },
  { path: '/category/query-one', component: () => import('@/pages/Category/CateHome.vue') },
  { path: '/category/query-all', component: () => import('@/pages/Category/CateHome.vue') },
  { path: '/part/create', component: () => import('@/pages/PartBom/PartBomHome.vue') },
  { path: '/part/edit', component: () => import('@/pages/PartBom/PartBomHome.vue') },
  { path: '/part/query', component: () => import('@/pages/PartBom/PartBomHome.vue') },
  { path: '/part/version', component: () => import('@/pages/PartBom/PartBomHome.vue') },
  { path: '/part/parent-child', component: () => import('@/pages/PartBom/PartBomHome.vue') },
  { path: '/part/attribute', component: () => import('@/pages/PartBom/PartBomHome.vue') },

]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
