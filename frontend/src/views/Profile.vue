<template>
  <div class="page">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2 @click="$router.push('/')" style="cursor:pointer;margin:0">校园二手交易</h2>
        </div>
        <el-button @click="$router.push('/')" size="small">返回首页</el-button>
      </el-header>
      <el-main class="main">
        <el-card style="max-width:500px;margin:0 auto">
          <template #header><h3 style="margin:0">个人信息</h3></template>
          <el-form :model="form" ref="formRef" label-width="80px" v-loading="loading">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item label="头像">
              <el-input v-model="form.avatar" placeholder="头像URL（选填）" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getProfile, updateProfile } from '../api/user'
import { setUser } from '../utils/auth'
import { ElMessage } from 'element-plus'

const form = reactive({ username: '', phone: '', avatar: '' })
const loading = ref(false)
const saving = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getProfile()
    form.username = res.data.username || ''
    form.phone = res.data.phone || ''
    form.avatar = res.data.avatar || ''
  } finally {
    loading.value = false
  }
})

async function handleSave() {
  saving.value = true
  try {
    const res = await updateProfile({ phone: form.phone, avatar: form.avatar })
    setUser(res.data)
    ElMessage.success('保存成功')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.header { display:flex; align-items:center; justify-content:space-between; background:#fff; box-shadow:0 1px 4px rgba(0,0,0,0.1); }
.main { padding:24px; }
</style>
