<template>
  <div class="page home-page">
    <h1>政务智能问答</h1>
    <el-autocomplete
      v-model="question"
      :fetch-suggestions="querySearch"
      placeholder="请输入问题"
      @select="onSelectSuggestion"
      @keyup.enter="ask"
      style="width:100%"
    />
    <el-button style="margin-top:12px" type="primary" @click="ask">提问</el-button>

    <section class="block">
      <h2>分类入口</h2>
      <el-space wrap>
        <el-button v-for="c in categories" :key="c.id" @click="$router.push(`/category/${c.id}`)">{{ c.name }}</el-button>
      </el-space>
    </section>

    <section class="block">
      <h2>政务公告</h2>
      <el-card v-for="a in announcements" :key="a.id" class="card-gap">
        <strong>{{ a.title }}</strong>
        <div>{{ a.content }}</div>
      </el-card>
    </section>

    <section class="block">
      <h2>热门问题</h2>
      <el-card>
        <ol>
          <li
            v-for="(item, idx) in hotQuestions"
            :key="idx"
            style="cursor:pointer;margin-bottom:6px"
            @click="onSelectSuggestion({ value: item.question })"
          >
            {{ item.question }}（{{ item.cnt }}）
          </li>
        </ol>
      </el-card>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const question = ref('')
const categories = ref([])
const announcements = ref([])
const hotQuestions = ref([])
let timer = null

const ask = () => {
  if (!question.value.trim()) return
  localStorage.setItem('last_question', question.value)
  window.location.href = '/result'
}

const querySearch = (queryString, cb) => {
  if (!queryString.trim()) {
    cb([])
    return
  }
  if (timer) {
    clearTimeout(timer)
  }
  timer = setTimeout(async () => {
    const resp = await http.get('/api/public/suggestions', {
      params: { keyword: queryString, limit: 8 }
    })
    const items = (resp.data.data || []).map((item) => ({ value: item }))
    cb(items)
  }, 250)
}

const onSelectSuggestion = (item) => {
  question.value = item.value
  ask()
}

onMounted(async () => {
  const [c, a, h] = await Promise.all([
    http.get('/api/public/categories'),
    http.get('/api/public/announcements'),
    http.get('/api/public/hot-questions')
  ])
  categories.value = c.data.data || []
  announcements.value = a.data.data || []
  hotQuestions.value = h.data.data || []
})
</script>

<style scoped>
.home-page .block {
  margin-top: 18px;
}
.card-gap {
  margin-bottom: 8px;
}
@media (max-width: 768px) {
  .home-page {
    padding: 14px;
  }
  .home-page h1 {
    font-size: 24px;
  }
}
</style>
