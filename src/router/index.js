import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/components/Login.vue'
import AttributeManager from '@/components/AttributeManager.vue'

const routes = [
  { path: '/', component: Login },
  { path: '/Attr', component: AttributeManager }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
