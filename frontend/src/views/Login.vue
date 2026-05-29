<template>
  <div class="auth-page">
    <el-card class="auth-card">
      <h2 style="text-align:center;margin-bottom:24px">校园二手交易</h2>
      <el-form :model="form" :rules="rules" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock"
            @keyup.enter="handleLogin" show-password />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width:100%" @click="handleLogin" :loading="loading">登录</el-button>
        </el-form-item>
      </el-form>
      <p style="text-align:center">还没有账号？<router-link to="/register">立即注册</router-link></p>
      <p style="text-align:center;margin-top:8px"><router-link to="/admin/login">管理员登录</router-link></p>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../api/user'
import { setToken, setUser } from '../utils/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await login(form)
    setToken(res.data.token)
    setUser(res.data.user)
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page { display:flex; justify-content:center; align-items:center; min-height:100vh; background:#f5f7fa; }
.auth-card { width:400px; }
</style>
