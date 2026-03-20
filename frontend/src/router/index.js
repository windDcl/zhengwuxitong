import { createRouter, createWebHistory } from 'vue-router'
import PublicHome from '../pages/public/PublicHome.vue'
import PublicCategory from '../pages/public/PublicCategory.vue'
import PublicResult from '../pages/public/PublicResult.vue'
import AdminLogin from '../pages/admin/AdminLogin.vue'
import AdminLayout from '../layouts/AdminLayout.vue'
import AdminDashboard from '../pages/admin/AdminDashboard.vue'
import AdminCategory from '../pages/admin/AdminCategory.vue'
import AdminFaq from '../pages/admin/AdminFaq.vue'
import AdminAnnouncement from '../pages/admin/AdminAnnouncement.vue'
import AdminLogs from '../pages/admin/AdminLogs.vue'
import AdminUnmatched from '../pages/admin/AdminUnmatched.vue'
import AdminSettings from '../pages/admin/AdminSettings.vue'
import AdminImport from '../pages/admin/AdminImport.vue'

const routes = [
  { path: '/', component: PublicHome },
  { path: '/category/:id', component: PublicCategory },
  { path: '/result', component: PublicResult },
  { path: '/admin/login', component: AdminLogin },
  {
    path: '/admin',
    component: AdminLayout,
    children: [
      { path: 'dashboard', component: AdminDashboard },
      { path: 'categories', component: AdminCategory },
      { path: 'faq', component: AdminFaq },
      { path: 'announcements', component: AdminAnnouncement },
      { path: 'logs', component: AdminLogs },
      { path: 'unmatched', component: AdminUnmatched },
      { path: 'settings', component: AdminSettings },
      { path: 'import', component: AdminImport }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, _from, next) => {
  if (to.path.startsWith('/admin') && to.path !== '/admin/login') {
    const token = localStorage.getItem('admin_token')
    if (!token) {
      next('/admin/login')
      return
    }
  }
  next()
})

export default router
