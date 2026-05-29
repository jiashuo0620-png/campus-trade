<template>
  <div class="page">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2 @click="$router.push('/')" style="cursor:pointer;margin:0">校园二手交易</h2>
        </div>
        <div class="header-right">
          <el-button @click="$router.push('/publish')" type="primary" size="small">发布商品</el-button>
          <el-button @click="$router.push('/')" size="small">返回首页</el-button>
        </div>
      </el-header>
      <el-main class="main">
        <h3>我的商品</h3>
        <el-table :data="list" style="width:100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="150" />
          <el-table-column prop="category" label="分类" width="100" />
          <el-table-column prop="price" label="售价" width="100">
            <template #default="s">¥{{ s.row.price }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="s">
              <el-tag v-if="s.row.status === 0" type="warning">待审核</el-tag>
              <el-tag v-else-if="s.row.status === 1" type="success">在售</el-tag>
              <el-tag v-else-if="s.row.status === 2" type="danger">已售出</el-tag>
              <el-tag v-else type="info">已下架</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="发布时间" width="180" />
          <el-table-column label="操作" width="100">
            <template #default="s">
              <el-button size="small" type="danger" @click="handleDelete(s.row.id)"
                :disabled="s.row.status !== 0">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination" v-if="total > 0">
          <el-pagination v-model:current-page="page" :total="total" layout="prev, pager, next"
            @current-change="loadList" />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyGoods, deleteGoods } from '../api/goods'
import { ElMessage, ElMessageBox } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)

onMounted(() => { loadList() })

async function loadList() {
  loading.value = true
  try {
    const res = await getMyGoods({ page: page.value, size: 10 })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '确认', { type: 'warning' })
    await deleteGoods(id)
    ElMessage.success('删除成功')
    loadList()
  } catch { /* cancelled */ }
}
</script>

<style scoped>
.header { display:flex; align-items:center; justify-content:space-between; background:#fff; box-shadow:0 1px 4px rgba(0,0,0,0.1); }
.header-right { display:flex; gap:8px; }
.main { max-width:1000px; margin:24px auto; }
.pagination { display:flex; justify-content:center; margin-top:24px; }
</style>
