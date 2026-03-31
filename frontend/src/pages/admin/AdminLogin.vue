<template>
  <div class="page">
    <section class="login-card glass-panel">
      <div>
        <span class="hero-eyebrow">管理运营视角</span>
        <h1>登录后台，处理未命中问题并维护知识库</h1>
        <p>登录后可查看趋势图、补录 FAQ、配置第三方 AI 能力。</p>
      </div>

      <el-input v-model="username" placeholder="用户名" style="margin-bottom:12px" />
      <el-input v-model="password" type="password" placeholder="密码" style="margin-bottom:12px" />
      <el-button type="primary" size="large" @click="login">登录后台</el-button>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../api/http'

const username = ref('admin')
const password = ref('Admin@123')
const router = useRouter()

const login = async () => {
  const resp = await http.post('/api/admin/login', { username: username.value, password: password.value })
  localStorage.setItem('admin_token', resp.data.data.token)
  router.push('/admin/dashboard')
}
</script>

<style scoped>
.login-card {
  max-width: 520px;
  margin: 40px auto 0;
  padding: 28px;
}

.login-card h1 {
  margin: 12px 0 10px;
  font-size: 36px;
  line-height: 1.2;
}

.login-card p {
  margin: 0 0 22px;
  color: var(--muted);
  line-height: 1.7;
}
</style>
