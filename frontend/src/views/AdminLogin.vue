<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2 style="text-align:center;margin-bottom:24px">管理员登录</h2>
      <el-form :model="form" ref="formRef">
        <el-form-item>
          <el-input v-model="form.username" placeholder="管理员用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock"
            @keyup.enter="handleLogin" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <p style="text-align:center"><router-link to="/login">用户登录</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { adminLogin } from '../api/admin'
import { setToken, setUser } from '../utils/auth'

const router = useRouter()
const loading = ref(false)
const form = reactive({ username: '', password: '' })

async function handleLogin() {
  if (!form.username || !form.password) return
  loading.value = true
  try {
    const res = await adminLogin(form.username, form.password)
    setToken(res.data.token)
    setUser({ role: 'admin', username: form.username })
    router.push('/admin')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page { display:flex; justify-content:center; align-items:center; min-height:100vh; background:#f5f7fa; }
.auth-card { width:400px; }
</style>
