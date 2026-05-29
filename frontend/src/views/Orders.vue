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
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane label="我买到的" name="buy" />
          <el-tab-pane label="我卖出的" name="sell" />
        </el-tabs>
        <el-table :data="list" style="width:100%" v-loading="loading">
          <el-table-column prop="orderNo" label="订单编号" width="190" />
          <el-table-column prop="goodsId" label="商品ID" width="80" />
          <el-table-column prop="amount" label="金额" width="100">
            <template #default="s">¥{{ s.row.amount }}</template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="s">
              <el-tag v-if="s.row.status === 0" type="warning">待确认</el-tag>
              <el-tag v-else-if="s.row.status === 1" type="primary">已确认</el-tag>
              <el-tag v-else-if="s.row.status === 2" type="success">已完成</el-tag>
              <el-tag v-else type="info">已取消</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="120" />
          <el-table-column prop="createTime" label="时间" width="180" />
          <el-table-column label="操作" width="160">
            <template #default="s">
              <template v-if="activeTab === 'buy' && s.row.status === 0">
                <el-button size="small" type="success" @click="handleStatus(s.row.id, 2)">确认收货</el-button>
                <el-button size="small" type="danger" @click="handleStatus(s.row.id, 3)">取消</el-button>
              </template>
              <template v-if="activeTab === 'sell' && s.row.status === 0">
                <el-button size="small" type="primary" @click="handleStatus(s.row.id, 1)">确认订单</el-button>
              </template>
              <span v-if="s.row.status !== 0" style="color:#999">-</span>
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
import { getBuyOrders, getSellOrders, updateOrderStatus } from '../api/order'
import { ElMessage } from 'element-plus'

const list = ref([])
const page = ref(1)
const total = ref(0)
const loading = ref(false)
const activeTab = ref('buy')

onMounted(() => { loadList() })

function handleTabChange() {
  page.value = 1
  loadList()
}

async function loadList() {
  loading.value = true
  try {
    const fn = activeTab.value === 'buy' ? getBuyOrders : getSellOrders
    const res = await fn({ page: page.value, size: 10 })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function handleStatus(id, status) {
  try {
    await updateOrderStatus(id, status)
    ElMessage.success('操作成功')
    loadList()
  } catch { /* error handled by interceptor */ }
}
</script>

<style scoped>
.header { display:flex; align-items:center; justify-content:space-between; background:#fff; box-shadow:0 1px 4px rgba(0,0,0,0.1); }
.main { max-width:1000px; margin:24px auto; }
.pagination { display:flex; justify-content:center; margin-top:24px; }
</style>
