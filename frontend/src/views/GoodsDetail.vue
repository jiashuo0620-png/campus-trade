<template>
  <div class="detail-page">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2 @click="$router.push('/')" style="cursor:pointer;margin:0">校园二手交易</h2>
        </div>
        <el-button @click="$router.push('/')" size="small">返回首页</el-button>
      </el-header>
      <el-main class="detail-main">
        <el-row :gutter="24" v-if="goods">
          <el-col :span="12">
            <div class="detail-img">
              <img v-if="goods.images" :src="goods.images.split(',')[0]" alt="" />
              <el-icon v-else :size="120" color="#ccc"><PictureFilled /></el-icon>
            </div>
          </el-col>
          <el-col :span="12">
            <h2>{{ goods.title }}</h2>
            <div class="detail-price">
              <span class="price">¥{{ goods.price }}</span>
              <span v-if="goods.originalPrice" class="original-price">¥{{ goods.originalPrice }}</span>
            </div>
            <el-descriptions :column="1" border style="margin:16px 0">
              <el-descriptions-item label="分类">{{ goods.category }}</el-descriptions-item>
              <el-descriptions-item label="发布时间">{{ goods.createTime }}</el-descriptions-item>
              <el-descriptions-item label="浏览量">{{ goods.viewCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="商品状态">
                <el-tag v-if="goods.status === 1" type="success">在售</el-tag>
                <el-tag v-else-if="goods.status === 2" type="danger">已售出</el-tag>
                <el-tag v-else type="info">已下架</el-tag>
              </el-descriptions-item>
            </el-descriptions>
            <div class="detail-desc" v-if="goods.description">
              <h4>商品描述</h4>
              <p>{{ goods.description }}</p>
            </div>
            <el-button type="primary" size="large" @click="handleBuy"
              :disabled="goods.status !== 1 || goods.userId === getUser()?.id"
              v-if="isLoggedIn()" style="margin-top:16px">
              {{ goods.userId === getUser()?.id ? '这是您的商品' : '立即购买' }}
            </el-button>
            <el-button v-else size="large" @click="$router.push('/login')" style="margin-top:16px">
              登录后购买
            </el-button>
          </el-col>
        </el-row>
        <div v-else-if="!loading" style="text-align:center;padding:80px">
          <el-empty description="商品不存在" />
        </div>
      </el-main>
    </el-container>

    <el-dialog v-model="dialogVisible" title="确认购买" width="400px">
      <p>确定要购买 <b>{{ goods?.title }}</b> 吗？</p>
      <p>价格：<span style="color:#e4393c;font-size:18px">¥{{ goods?.price }}</span></p>
      <el-input v-model="remark" placeholder="备注（选填）" style="margin-top:12px" />
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBuy" :loading="buying">确认购买</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getGoodsDetail } from '../api/goods'
import { createOrder } from '../api/order'
import { isLoggedIn, getUser, getToken } from '../utils/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const router = useRouter()
const goods = ref(null)
const loading = ref(false)
const dialogVisible = ref(false)
const remark = ref('')
const buying = ref(false)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getGoodsDetail(route.params.id)
    goods.value = res.data
  } finally {
    loading.value = false
  }
})

function handleBuy() {
  if (!isLoggedIn()) { router.push('/login'); return }
  dialogVisible.value = true
}

async function confirmBuy() {
  buying.value = true
  try {
    await createOrder({ goodsId: goods.value.id, remark: remark.value })
    ElMessage.success('购买成功')
    dialogVisible.value = false
    goods.value.status = 2
  } finally {
    buying.value = false
  }
}
</script>

<style scoped>
.header { display:flex; align-items:center; justify-content:space-between; background:#fff; box-shadow:0 1px 4px rgba(0,0,0,0.1); }
.detail-main { max-width:1000px; margin:24px auto; }
.detail-img { height:400px; display:flex; align-items:center; justify-content:center; background:#f5f5f5; border-radius:8px; overflow:hidden; }
.detail-img img { width:100%; height:100%; object-fit:cover; }
.price { color:#e4393c; font-size:28px; font-weight:bold; }
.original-price { color:#999; font-size:16px; text-decoration:line-through; margin-left:12px; }
.detail-desc { background:#f9f9f9; padding:16px; border-radius:8px; margin-top:16px; }
.detail-desc p { white-space:pre-wrap; color:#666; }
</style>
