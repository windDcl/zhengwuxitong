<template>
  <div class="page home-page">
    <section class="hero glass-panel">
      <div class="hero-copy">
        <span class="hero-eyebrow">市民问答视角</span>
        <h1>把复杂政务问题，变成一段清晰可执行的回答</h1>
        <p>
          支持分类检索、热门问题直达和智能语义匹配。知识库未命中时，也可以接入第三方 AI 做参考答复。
        </p>
        <div class="hero-search">
          <el-autocomplete
            v-model="question"
            :fetch-suggestions="querySearch"
            placeholder="例如：公积金如何提取？社保转移怎么办？"
            @select="onSelectSuggestion"
            @keyup.enter="ask"
          />
          <el-button type="primary" size="large" @click="ask">立即提问</el-button>
        </div>
        <div class="hero-tags">
          <button v-for="item in quickQuestions" :key="item" @click="onSelectSuggestion({ value: item })">
            {{ item }}
          </button>
        </div>
      </div>

      <div class="hero-side">
        <div class="hero-side-card">
          <span>热点追踪</span>
          <strong>{{ hotQuestions[0]?.question || '知识库已就绪' }}</strong>
          <p>当前高频咨询会优先展示，方便快速进入结果页。</p>
        </div>
        <div class="hero-side-grid">
          <div>
            <small>服务分类</small>
            <strong>{{ categories.length }}</strong>
          </div>
          <div>
            <small>最新公告</small>
            <strong>{{ announcements.length }}</strong>
          </div>
        </div>
      </div>
    </section>

    <section class="content-grid">
      <div class="panel">
        <div class="section-title">
          <div>
            <h2>分类入口</h2>
            <p>按主题快速找到常见办事场景</p>
          </div>
        </div>
        <div class="category-grid">
          <button
            v-for="c in categories"
            :key="c.id"
            class="category-card"
            @click="$router.push(`/category/${c.id}`)"
          >
            <span>{{ c.name }}</span>
            <strong>查看分类问答</strong>
          </button>
        </div>
      </div>

      <div class="panel">
        <div class="section-title">
          <div>
            <h2>热门问题</h2>
            <p>最近大家最关心的高频咨询</p>
          </div>
        </div>
        <div v-if="hotQuestions.length" class="hot-list">
          <button
            v-for="(item, idx) in hotQuestions"
            :key="idx"
            class="hot-item"
            @click="onSelectSuggestion({ value: item.question })"
          >
            <span>TOP {{ idx + 1 }}</span>
            <strong>{{ item.question }}</strong>
            <em>{{ item.cnt }} 次</em>
          </button>
        </div>
        <div v-else class="empty-tip">暂无热门问题数据</div>
      </div>
    </section>

    <section class="panel">
      <div class="section-title">
        <div>
          <h2>政务公告</h2>
          <p>展示近期政策提醒与服务通知</p>
        </div>
      </div>
      <div v-if="announcements.length" class="announcement-list">
        <article v-for="a in announcements" :key="a.id" class="announcement-card">
          <div class="badge-dot"></div>
          <div>
            <strong>{{ a.title }}</strong>
            <p>{{ a.content }}</p>
          </div>
        </article>
      </div>
      <div v-else class="empty-tip">暂无公告内容</div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import http from '../../api/http'

const question = ref('')
const categories = ref([])
const announcements = ref([])
const hotQuestions = ref([])
const router = useRouter()
let timer = null
const quickQuestions = [
  '公积金如何提取',
  '社保转移需要什么材料',
  '灵活就业人员如何参保'
]

const ask = () => {
  if (!question.value.trim()) return
  localStorage.setItem('last_question', question.value)
  router.push('/result')
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
.home-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) 360px;
  gap: 20px;
  padding: 28px;
}

.hero-copy h1 {
  margin: 14px 0 12px;
  font-size: 48px;
  line-height: 1.14;
}

.hero-copy p {
  margin: 0;
  max-width: 760px;
  color: var(--muted);
  font-size: 16px;
  line-height: 1.7;
}

.hero-eyebrow {
  color: var(--accent);
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}

.hero-search {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 148px;
  gap: 12px;
  margin-top: 22px;
}

:deep(.hero-search .el-autocomplete) {
  width: 100%;
}

:deep(.hero-search .el-input__wrapper) {
  min-height: 58px;
  border-radius: 18px;
}

.hero-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 16px;
}

.hero-tags button,
.category-card,
.hot-item {
  border: 0;
  cursor: pointer;
}

.hero-tags button {
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(15, 118, 110, 0.08);
  color: var(--accent-strong);
}

.hero-side {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero-side-card {
  padding: 22px;
  min-height: 220px;
  border-radius: 26px;
  background: linear-gradient(160deg, #0f766e, #0f4c5c 72%);
  color: #fff;
}

.hero-side-card span,
.hero-side-grid small {
  opacity: 0.72;
}

.hero-side-card strong {
  display: block;
  margin: 18px 0 10px;
  font-size: 30px;
  line-height: 1.25;
}

.hero-side-card p {
  margin: 0;
  color: rgba(255, 255, 255, 0.84);
  line-height: 1.7;
}

.hero-side-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.hero-side-grid div {
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.86);
  border: 1px solid var(--border);
}

.hero-side-grid strong {
  display: block;
  margin-top: 8px;
  font-size: 28px;
}

.content-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.1fr) 400px;
  gap: 18px;
}

.category-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.category-card {
  padding: 20px;
  border-radius: 22px;
  text-align: left;
  background: linear-gradient(180deg, #ffffff, #eff8f5);
  border: 1px solid rgba(15, 118, 110, 0.08);
}

.category-card span {
  display: block;
  color: var(--muted);
  font-size: 13px;
}

.category-card strong {
  display: block;
  margin-top: 8px;
  font-size: 22px;
}

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-item {
  display: grid;
  grid-template-columns: 84px minmax(0, 1fr) auto;
  align-items: center;
  gap: 16px;
  padding: 18px;
  border-radius: 20px;
  background: linear-gradient(180deg, #ffffff, #f8fbff);
  border: 1px solid rgba(14, 165, 233, 0.1);
  text-align: left;
}

.hot-item span {
  color: var(--accent);
  font-weight: 700;
}

.hot-item em {
  color: var(--muted);
  font-style: normal;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.announcement-card {
  display: grid;
  grid-template-columns: 14px minmax(0, 1fr);
  gap: 14px;
  padding: 18px;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.82);
}

.announcement-card p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

@media (max-width: 768px) {
  .hero,
  .content-grid,
  .category-grid,
  .hero-search,
  .hero-side-grid,
  .hot-item {
    grid-template-columns: 1fr;
  }

  .hero {
    padding: 20px;
  }

  .hero-copy h1 {
    font-size: 34px;
  }
}
</style>
