import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/components/Login.vue'
import Home from '@/components/Home.vue'
import AttrHome from '@/pages/Attribute/AttrHome.vue'
import CategoryDetail from '@/pages/Attribute/CategoryDetail.vue'
import CateHome from '@/pages/Category/CateHome.vue'
import BasicDis from '@/pages/PartBom/BasicDis.vue'

const routes = [
  { path: '/', component: Login },



  // { path: '/Home', component: Home },
  { 
    path: '/Home',
    component: Home,
    children: [
      {
        path: 'part/basicShow',
        component: BasicDis
      },
      {
        path: 'attribute/create',
        component: AttrHome,
        props: { defaultType: 'attribute' }
      },
      {
        path: 'attribute/edit',
        component: AttrHome,
        props: { defaultType: 'attribute' }
      },
      {
        path: 'attribute/delete',
        component: AttrHome,
        props: { defaultType: 'attribute' }
      },
      {
        path: 'attribute/query',
        component: AttrHome,
        props: { defaultType: 'attribute' }
      },
      {
        path: 'category/create',
        component: AttrHome,
        props: { defaultType: 'category' }
      },
      {
        path: 'category/edit',
        component: AttrHome,
        props: { defaultType: 'category' }
      },
      {
        path: 'category/generate',
        component: AttrHome,
        props: { defaultType: 'category' }
      },
      {
        path: 'category/query-one',
        component: AttrHome,
        props: { defaultType: 'category' }
      },
      {
        path: 'category/query-all',
        component: CateHome
      },
      {
        path: 'category/detail/:id',
        component: CategoryDetail
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
