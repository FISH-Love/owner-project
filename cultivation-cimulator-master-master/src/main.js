import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import axios from 'axios'
import echarts from 'echarts'
import NavBar from './components/NavBar/NavBar.vue'
import RoleSelectModal from './components/RoleSelectModal/RoleSelectModal.vue'
// 创建应用实例
const app = createApp(App)

// 使用ElementPlus插件
app.use(ElementPlus)

//全局引入navbar
app.component('nav-bar',NavBar)
app.component('role-select-modal',RoleSelectModal)
// 使用路由
app.use(router)

// 配置axios - 不要使用use()
app.config.globalProperties.$axios = axios
// 配置默认baseURL
axios.defaults.baseURL = process.env.VUE_APP_BASE_API || '/api'

// 配置echarts - 不要使用use()
app.config.globalProperties.$echarts = echarts

// 设置全局变量，判断是否为开发环境
app.config.globalProperties.$isDev = process.env.NODE_ENV === 'development'

// 全局错误处理
app.config.errorHandler = (err, instance, info) => {
  console.error('全局错误捕获:', err, info)
  // 这里可以添加统一的错误处理逻辑
}

// 最后挂载应用
app.mount('#app')