import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/components/Login.vue'
import Home from '@/components/Home.vue'
import AttrHome from '@/pages/Attribute/AttrHome.vue'
import CategoryDetail from '@/pages/Attribute/CategoryDetail.vue'
import CateHome from '@/pages/Category/CateHome.vue'

const routes = [
  { path: '/', component: Login },
  { path: '/Home', component: Home },
  { path: '/attribute/create', component: AttrHome },
  { path: '/attribute/edit', component: AttrHome },
  { path: '/attribute/delete', component: AttrHome },
  { path: '/attribute/query', component: AttrHome },
  { path: '/category/create', component: AttrHome },
  { path: '/category/edit', component: AttrHome },
  { path: '/category/generate', component: AttrHome },
  { path: '/category/query-one', component: AttrHome },
  { path: '/category/query-all', component: CateHome },
  { path: '/category/detail/:id', component: CategoryDetail },
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
