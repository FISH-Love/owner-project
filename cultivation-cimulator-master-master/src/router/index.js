import { createRouter, createWebHistory } from 'vue-router';
import LoginView from '../views/LoginAndRegister/LoginView.vue';
import RegisterView from '../views/LoginAndRegister/RegisterView.vue';
import HomeView from '../views/Home/HomeView.vue';
import CultivationView from '../views/CultivationAndAdventure/CultivationView.vue';
import AdventureView from '../views/CultivationAndAdventure/AdventureView.vue';


const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: LoginView
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterView
  },
  {
    path: '/home',
    name: 'Home',
    component: HomeView,
    meta: {
      requiresAuth: true // 需要登录才能访问
    }
  },
  {
    path:'/cultivation',
    name:'Cultivation',
    component:CultivationView //这是自我修炼的界面
  },
  {
    path:'/adventure',
    name:'Adventure',
    component:AdventureView  //这是出去冒险的界面
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

// 全局前置守卫 - 用于验证登录状态
router.beforeEach((to, from, next) => {
  // 从本地存储获取登录状态
  const isAuthenticated = localStorage.getItem('token');
  
  // 如果目标路由需要认证
  if (to.meta.requiresAuth) {
    // 已登录则继续访问
    if (isAuthenticated) {
      next();
    } else {
      // 未登录则重定向到登录页
      next({ 
        path: '/login', 
        query: { redirect: to.fullPath } 
      });
    }
  } else {
    // 不需要认证的路由直接访问
    next();
  }
});

export default router;