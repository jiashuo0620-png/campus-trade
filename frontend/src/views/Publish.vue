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
              <el-upload
                :action="uploadUrl"
                :headers="uploadHeaders"
                list-type="picture-card"
                :on-success="handleUploadSuccess"
                :on-remove="handleRemove"
                :before-upload="beforeUpload"
                :file-list="fileList"
                multiple
              >
                <el-icon><Plus /></el-icon>
              </el-upload>
              <div style="color:#999;font-size:12px;margin-top:4px">支持 jpg/png/gif/webp，每张不超过 5MB，最多 9 张</div>
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
import { getToken } from '../utils/auth'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)
const fileList = ref([])
const uploadedUrls = ref([])

const uploadUrl = (import.meta.env.VITE_API_BASE || '/api') + '/upload'
const uploadHeaders = { Authorization: 'Bearer ' + getToken() }

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

function beforeUpload(file) {
  const allowedTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.type)) {
    ElMessage.error('仅支持 jpg、png、gif、webp 格式')
    return false
  }
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

function handleUploadSuccess(response) {
  if (response.code === 200 && response.data && response.data.url) {
    uploadedUrls.value.push(response.data.url)
  } else {
    ElMessage.error(response.message || '上传失败')
  }
}

function handleRemove(file) {
  const url = file.response?.data?.url || file.url
  if (url) {
    const idx = uploadedUrls.value.indexOf(url)
    if (idx > -1) uploadedUrls.value.splice(idx, 1)
  }
}

async function handlePublish() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  form.images = uploadedUrls.value.join(',')
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
