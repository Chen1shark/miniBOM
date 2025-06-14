import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import './style.css'
import App from './App.vue'
import router from './router'
import { createPinia } from 'pinia'

if (import.meta.env.MODE === 'development') {
    import('./mock/auth')
  }

  
const app = createApp(App)  
app.use(router)             
app.use(ElementPlus)
app.use(createPinia())
app.mount('#app')           
  