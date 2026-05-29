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
        <el-card style="max-width:600px;margin:0 auto">
          <template #header><h3 style="margin:0">发布商品</h3></template>
          <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入商品标题" />
            </el-form-item>
            <el-form-item label="分类" prop="category">
              <el-select v-model="form.category" placeholder="请选择分类" style="width:100%">
                <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
              </el-select>
            </el-form-item>
            <el-form-item label="描述" prop="description">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="描述商品成色、使用情况等" />
            </el-form-item>
            <el-form-item label="售价" prop="price">
              <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" placeholder="0.00" />
            </el-form-item>
            <el-form-item label="原价">
              <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:100%" placeholder="选填" />
            </el-form-item>
            <el-form-item label="图片">
              <el-input v-model="form.images" placeholder="图片URL，多个用逗号分隔（选填）" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handlePublish" :loading="loading">发布</el-button>
              <el-button @click="$router.back()">取消</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { publishGoods } from '../api/goods'
import { ElMessage } from 'element-plus'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const categories = ['电子产品', '书籍教材', '生活用品', '衣物鞋帽', '运动器材', '其他']
const form = reactive({
  title: '', category: '', description: '', price: null, originalPrice: null, images: ''
})
const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  description: [{ required: true, message: '请输入描述', trigger: 'blur' }],
  price: [{ required: true, message: '请输入售价', trigger: 'blur' }]
}

async function handlePublish() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await publishGoods(form)
    ElMessage.success('发布成功，等待管理员审核')
    router.push('/my-goods')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.header { display:flex; align-items:center; justify-content:space-between; background:#fff; box-shadow:0 1px 4px rgba(0,0,0,0.1); }
.main { padding:24px; }
</style>
