<template>
  <div class="page">
    <h1>分类服务</h1>
    <el-input v-model="keyword" placeholder="分类内搜索" @keyup.enter="loadFaqs" style="margin-bottom:12px" />
    <el-button type="primary" @click="loadFaqs">搜索</el-button>

    <el-card v-for="faq in faqs" :key="faq.id" style="margin-top:12px">
      <div style="display:flex;justify-content:space-between;align-items:center;gap:12px">
        <div>
          <strong>{{ faq.standardQuestion }}</strong>
          <div style="color:#64748b; margin-top:6px">{{ faq.standardAnswer }}</div>
        </div>
        <el-button @click="viewAnswer(faq.standardQuestion)">查看答案</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../../api/http'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const faqs = ref([])

const loadFaqs = async () => {
  const resp = await http.get(`/api/public/categories/${route.params.id}/faqs`, {
    params: { keyword: keyword.value }
  })
  faqs.value = resp.data.data || []
}

const viewAnswer = (question) => {
  localStorage.setItem('last_question', question)
  router.push('/result')
}

onMounted(loadFaqs)
</script>
