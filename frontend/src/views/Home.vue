<template>
  <div class="home">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2 @click="$router.push('/')" style="cursor:pointer;margin:0">校园二手交易</h2>
        </div>
        <div class="header-right">
          <template v-if="isLoggedIn()">
            <el-button @click="$router.push('/publish')" type="primary" size="small">发布商品</el-button>
            <el-button @click="$router.push('/my-goods')" size="small">我的商品</el-button>
            <el-button @click="$router.push('/orders')" size="small">我的订单</el-button>
            <el-dropdown @command="handleCommand" style="margin-left:8px">
              <span class="user-name">{{ getUser()?.username }}</span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                  <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <el-button @click="$router.push('/login')" size="small">登录</el-button>
            <el-button @click="$router.push('/register')" size="small" type="primary">注册</el-button>
          </template>
        </div>
      </el-header>
      <el-main>
        <div class="search-bar">
          <el-input v-model="keyword" placeholder="搜索商品..." clearable style="max-width:500px"
            @keyup.enter="handleSearch">
            <template #append><el-button @click="handleSearch" icon="Search" /></template>
          </el-input>
        </div>
        <div class="category-bar">
          <el-radio-group v-model="category" @change="handleSearch" size="small">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="电子产品">电子产品</el-radio-button>
            <el-radio-button value="书籍教材">书籍教材</el-radio-button>
            <el-radio-button value="生活用品">生活用品</el-radio-button>
            <el-radio-button value="衣物鞋帽">衣物鞋帽</el-radio-button>
            <el-radio-button value="运动器材">运动器材</el-radio-button>
            <el-radio-button value="其他">其他</el-radio-button>
          </el-radio-group>
        </div>
        <div v-if="goodsList.length === 0 && !loading" class="empty">
          <el-empty description="暂无商品" />
        </div>
        <div class="goods-grid">
          <el-card v-for="item in goodsList" :key="item.id" class="goods-card"
            shadow="hover" @click="$router.push(`/goods/${item.id}`)">
            <div class="goods-img">
              <img v-if="item.images" :src="item.images.split(',')[0]" alt="" />
              <el-icon v-else :size="80" color="#ccc"><PictureFilled /></el-icon>
            </div>
            <div class="goods-info">
              <h4 class="goods-title">{{ item.title }}</h4>
              <div class="goods-price">
                <span class="price">¥{{ item.price }}</span>
                <span v-if="item.originalPrice" class="original-price">¥{{ item.originalPrice }}</span>
              </div>
              <div class="goods-meta">
                <el-tag size="small" type="info">{{ item.category }}</el-tag>
                <span class="views">{{ item.viewCount || 0 }}次浏览</span>
              </div>
            </div>
          </el-card>
        </div>
        <div class="pagination" v-if="total > 0">
          <el-pagination v-model:current-page="page" :page-size="size" :total="total"
            layout="prev, pager, next" @current-change="loadGoods" />
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getGoodsList, searchGoods } from '../api/goods'
import { isLoggedIn, getUser, logout } from '../utils/auth'

const router = useRouter()
const goodsList = ref([])
const page = ref(1)
const size = ref(12)
const total = ref(0)
const loading = ref(false)
const keyword = ref('')
const category = ref('')

onMounted(() => { loadGoods() })

async function loadGoods() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (category.value) params.category = category.value
    let res
    if (keyword.value) {
      params.keyword = keyword.value
      res = await searchGoods(params)
    } else {
      res = await getGoodsList(params)
    }
    goodsList.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  page.value = 1
  loadGoods()
}

function handleCommand(cmd) {
  if (cmd === 'profile') router.push('/profile')
  if (cmd === 'logout') { logout(); router.go(0) }
}
</script>

<style scoped>
.header { display:flex; align-items:center; justify-content:space-between; background:#fff; box-shadow:0 1px 4px rgba(0,0,0,0.1); position:sticky; top:0; z-index:100; }
.header-right { display:flex; align-items:center; gap:8px; }
.user-name { cursor:pointer; color:#409eff; }
.search-bar { display:flex; justify-content:center; margin:24px 0; }
.category-bar { display:flex; justify-content:center; margin-bottom:24px; }
.goods-grid { display:grid; grid-template-columns:repeat(auto-fill, minmax(220px, 1fr)); gap:16px; }
.goods-card { cursor:pointer; }
.goods-img { height:180px; display:flex; align-items:center; justify-content:center; background:#f5f5f5; border-radius:4px; overflow:hidden; }
.goods-img img { width:100%; height:100%; object-fit:cover; }
.goods-info { padding-top:12px; }
.goods-title { margin:0 0 8px; overflow:hidden; text-overflow:ellipsis; white-space:nowrap; }
.price { color:#e4393c; font-size:18px; font-weight:bold; }
.original-price { color:#999; font-size:13px; text-decoration:line-through; margin-left:8px; }
.goods-meta { display:flex; justify-content:space-between; align-items:center; margin-top:8px; }
.views { color:#999; font-size:12px; }
.pagination { display:flex; justify-content:center; margin-top:24px; }
.empty { display:flex; justify-content:center; padding:80px 0; }
</style>
