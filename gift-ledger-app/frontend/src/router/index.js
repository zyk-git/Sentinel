import { createRouter, createWebHistory } from 'vue-router'
import GuestView from '../views/GuestView.vue'
import AdminLoginView from '../views/AdminLoginView.vue'
import AdminDashboardView from '../views/AdminDashboardView.vue'

const routes = [
  { path: '/', component: GuestView },
  { path: '/admin', component: AdminLoginView },
  { path: '/admin/dashboard', component: AdminDashboardView }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
