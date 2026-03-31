<template>
  <div class="shell">
    <header class="shell-header">
      <div class="shell-brand" @click="goHome">
        <span class="shell-brand-badge">GovQA</span>
        <div>
          <strong>政务智能问答工作台</strong>
          <p>一个入口，切换市民服务与运营管理视角</p>
        </div>
      </div>

      <div class="shell-switch">
        <button
          class="shell-switch-item"
          :class="{ active: !isAdminView }"
          @click="router.push('/')"
        >
          市民问答
        </button>
        <button
          class="shell-switch-item"
          :class="{ active: isAdminView }"
          @click="goAdmin"
        >
          管理运营
        </button>
      </div>

      <div class="shell-actions">
        <button v-if="isAdminView && hasAdminToken" class="shell-action ghost" @click="logout">
          退出登录
        </button>
        <button v-else class="shell-action" @click="goAdmin">
          {{ hasAdminToken ? '进入后台' : '管理员登录' }}
        </button>
      </div>
    </header>

    <main class="shell-main">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../api/http'

const route = useRoute()
const router = useRouter()

const isAdminView = computed(() => route.path.startsWith('/admin'))
const hasAdminToken = computed(() => {
  route.fullPath
  return Boolean(localStorage.getItem('admin_token'))
})

const goHome = () => {
  router.push('/')
}

const goAdmin = () => {
  router.push(hasAdminToken.value ? '/admin/dashboard' : '/admin/login')
}

const logout = async () => {
  try {
    await http.post('/api/admin/logout')
  } catch (_error) {
  }
  localStorage.removeItem('admin_token')
  router.push('/')
}
</script>
