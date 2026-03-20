<template>
  <div class="page" style="max-width:420px">
    <h1>管理员登录</h1>
    <el-input v-model="username" placeholder="用户名" style="margin-bottom:8px" />
    <el-input v-model="password" type="password" placeholder="密码" style="margin-bottom:8px" />
    <el-button type="primary" @click="login">登录</el-button>
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
