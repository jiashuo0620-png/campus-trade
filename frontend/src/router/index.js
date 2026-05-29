import { createRouter, createWebHistory } from 'vue-router'
import { isLoggedIn } from '../utils/auth'

const routes = [
  { path: '/login', name: 'Login', component: () => import('../views/Login.vue'), meta: { guest: true } },
  { path: '/register', name: 'Register', component: () => import('../views/Register.vue'), meta: { guest: true } },
  { path: '/', name: 'Home', component: () => import('../views/Home.vue') },
  { path: '/goods/:id', name: 'GoodsDetail', component: () => import('../views/GoodsDetail.vue') },
  { path: '/publish', name: 'Publish', component: () => import('../views/Publish.vue'), meta: { auth: true } },
  { path: '/my-goods', name: 'MyGoods', component: () => import('../views/MyGoods.vue'), meta: { auth: true } },
  { path: '/orders', name: 'Orders', component: () => import('../views/Orders.vue'), meta: { auth: true } },
  { path: '/profile', name: 'Profile', component: () => import('../views/Profile.vue'), meta: { auth: true } },
  { path: '/admin/login', name: 'AdminLogin', component: () => import('../views/AdminLogin.vue'), meta: { guest: true } },
  { path: '/admin', name: 'Admin', component: () => import('../views/Admin.vue'), meta: { auth: true } },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.auth && !isLoggedIn()) {
    next('/login')
  } else if (to.meta.guest && isLoggedIn()) {
    next('/')
  } else {
    next()
  }
})

export default router
