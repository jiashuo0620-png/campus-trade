<template>
  <div class="admin-page">
    <el-container>
      <el-header class="header">
        <h3 style="margin:0">管理后台</h3>
        <div>
          <span style="margin-right:16px">{{ getUser()?.username }}</span>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="用户管理" name="users">
            <el-table :data="users" v-loading="userLoading" style="width:100%">
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="username" label="用户名" width="120" />
              <el-table-column prop="phone" label="手机号" width="140" />
              <el-table-column prop="role" label="角色" width="80">
                <template #default="s">{{ s.row.role === 1 ? '管理员' : '用户' }}</template>
              </el-table-column>
              <el-table-column label="状态" width="100">
                <template #default="s">
                  <el-tag :type="s.row.status === 1 ? 'success' : 'danger'">
                    {{ s.row.status === 1 ? '正常' : '禁用' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="createTime" label="注册时间" width="180" />
              <el-table-column label="操作">
                <template #default="s">
                  <el-button v-if="s.row.status === 1" size="small" type="danger"
                    @click="handleUserStatus(s.row.id, 0)">禁用</el-button>
                  <el-button v-else size="small" type="success"
                    @click="handleUserStatus(s.row.id, 1)">启用</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination" v-if="userTotal > 0">
              <el-pagination v-model:current-page="userPage" :total="userTotal" layout="prev, pager, next"
                @current-change="loadUsers" />
            </div>
          </el-tab-pane>
          <el-tab-pane label="商品审核" name="audit">
            <el-table :data="pendingList" v-loading="goodsLoading" style="width:100%">
              <el-table-column prop="id" label="ID" width="60" />
              <el-table-column prop="title" label="标题" min-width="150" />
              <el-table-column prop="category" label="分类" width="100" />
              <el-table-column prop="price" label="售价" width="100">
                <template #default="s">¥{{ s.row.price }}</template>
              </el-table-column>
              <el-table-column prop="createTime" label="发布时间" width="180" />
              <el-table-column label="操作" width="160">
                <template #default="s">
                  <el-button size="small" type="success"
                    @click="handleAudit(s.row.id, 1)">通过</el-button>
                  <el-button size="small" type="danger"
                    @click="handleAudit(s.row.id, 3)">下架</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="pagination" v-if="goodsTotal > 0">
              <el-pagination v-model:current-page="goodsPage" :total="goodsTotal" layout="prev, pager, next"
                @current-change="loadPendingGoods" />
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getUserList, updateUserStatus, getPendingGoods, auditGoods } from '../api/admin'
import { getUser, logout } from '../utils/auth'
import { ElMessage } from 'element-plus'

const router = useRouter()
const activeTab = ref('users')

const users = ref([])
const userPage = ref(1)
const userTotal = ref(0)
const userLoading = ref(false)

const pendingList = ref([])
const goodsPage = ref(1)
const goodsTotal = ref(0)
const goodsLoading = ref(false)

onMounted(() => {
  loadUsers()
  loadPendingGoods()
})

async function loadUsers() {
  userLoading.value = true
  try {
    const res = await getUserList({ page: userPage.value, size: 10 })
    users.value = res.data.records
    userTotal.value = res.data.total
  } finally {
    userLoading.value = false
  }
}

async function loadPendingGoods() {
  goodsLoading.value = true
  try {
    const res = await getPendingGoods({ page: goodsPage.value, size: 10 })
    pendingList.value = res.data.records
    goodsTotal.value = res.data.total
  } finally {
    goodsLoading.value = false
  }
}

async function handleUserStatus(id, status) {
  try {
    await updateUserStatus(id, status)
    ElMessage.success('操作成功')
    loadUsers()
  } catch { /* handled */ }
}

async function handleAudit(id, status) {
  try {
    await auditGoods(id, status)
    ElMessage.success(status === 1 ? '已通过审核' : '已下架')
    loadPendingGoods()
  } catch { /* handled */ }
}

function handleLogout() {
  logout()
  router.push('/admin/login')
}
</script>

<style scoped>
.header { display:flex; align-items:center; justify-content:space-between; background:#fff; box-shadow:0 1px 4px rgba(0,0,0,0.1); }
.main { max-width:1100px; margin:24px auto; }
.pagination { display:flex; justify-content:center; margin-top:16px; }
</style>
