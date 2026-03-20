<template>
  <div class="page">
    <h1>问答结果</h1>
    <el-alert v-if="error" :title="error" type="error" show-icon style="margin-bottom:12px" />
    <el-skeleton v-if="loading" :rows="4" animated />
    <el-card v-if="result">
      <template v-if="result.hit">
        <p><b>匹配问题：</b>{{ result.candidates?.[0]?.matchedQuestion }}</p>
        <p><b>相似度：</b>{{ Number(result.similarity || 0).toFixed(4) }}</p>
        <p><b>答案：</b>{{ result.faq?.standardAnswer }}</p>
      </template>
      <template v-else>
        <p>未找到相关问题</p>
        <p>建议咨询政务服务大厅、拨打12345热线、查看政务公告。</p>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const result = ref(null)
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  const q = localStorage.getItem('last_question') || ''
  if (!q.trim()) {
    error.value = '没有获取到问题内容，请返回首页重新提问。'
    loading.value = false
    return
  }
  try {
    const resp = await http.post('/api/public/ask', { question: q, topN: 3 })
    result.value = resp.data.data
  } catch (e) {
    error.value = '请求结果失败，请确认后端服务已启动。'
  } finally {
    loading.value = false
  }
})
</script>
