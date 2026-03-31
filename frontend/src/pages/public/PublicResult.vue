<template>
  <div class="page result-page">
    <el-alert v-if="error" :title="error" type="error" show-icon style="margin-bottom:12px" />
    <el-skeleton v-if="loading" :rows="6" animated />
    <section v-if="result" class="result-layout">
      <div class="panel result-main">
        <div class="section-title">
          <div>
            <h2>{{ result.hit ? '已为你匹配到最相关答案' : '知识库暂未命中' }}</h2>
            <p>{{ askedQuestion }}</p>
          </div>
        </div>

        <template v-if="result.hit">
          <div class="result-hit-top">
            <div>
              <span>匹配问题</span>
              <strong>{{ result.candidates?.[0]?.matchedQuestion }}</strong>
            </div>
            <div>
              <span>相似度</span>
              <strong>{{ Number(result.similarity || 0).toFixed(4) }}</strong>
            </div>
          </div>
          <article class="answer-card">
            <h3>标准答案</h3>
            <p>{{ result.faq?.standardAnswer }}</p>
          </article>
        </template>

        <template v-else>
          <article class="answer-card warn">
            <h3>处理结果</h3>
            <p>{{ result.message }}</p>
          </article>
          <article v-if="result.aiAnswer" class="answer-card ai">
            <h3>第三方 AI 参考答复</h3>
            <p>{{ result.aiAnswer }}</p>
          </article>
        </template>
      </div>

      <aside class="panel result-side">
        <div class="section-title">
          <div>
            <h3>下一步建议</h3>
            <p>继续提问，或从分类页进入更稳定的知识卡片</p>
          </div>
        </div>
        <button class="result-action primary" @click="$router.push('/')">返回首页继续提问</button>
        <button class="result-action" @click="$router.push('/category/1')">查看分类服务</button>
        <div class="side-tip">
          <strong>温馨提示</strong>
          <p>如涉及本地政策、材料清单和办理时效，建议以当地政务服务大厅、12345 或官方公告为准。</p>
        </div>
      </aside>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const result = ref(null)
const loading = ref(true)
const error = ref('')
const askedQuestion = ref('')

onMounted(async () => {
  const q = localStorage.getItem('last_question') || ''
  askedQuestion.value = q
  if (!q.trim()) {
    error.value = '没有获取到问题内容，请返回首页重新提问。'
    loading.value = false
    return
  }
  try {
    const resp = await http.post('/api/public/ask', { question: q, topN: 3 })
    result.value = resp.data.data
  } catch (e) {
    if (e.code === 'ECONNABORTED') {
      error.value = '请求超时：知识库未命中时第三方 AI 响应较慢，请稍后重试或暂时关闭 AI。'
    } else if (e.response?.data?.message) {
      error.value = `请求失败：${e.response.data.message}`
    } else {
      error.value = '请求结果失败，请检查后端服务和第三方 AI 配置是否正常。'
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.result-layout {
  display: grid;
  grid-template-columns: minmax(0, 1.35fr) 360px;
  gap: 18px;
}

.result-hit-top {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-bottom: 16px;
}

.result-hit-top div {
  padding: 18px;
  border-radius: 22px;
  background: linear-gradient(180deg, #ffffff, #eef8f6);
}

.result-hit-top span {
  color: var(--muted);
  font-size: 13px;
}

.result-hit-top strong {
  display: block;
  margin-top: 8px;
  font-size: 24px;
}

.answer-card {
  padding: 22px;
  border-radius: 24px;
  background: #fff;
  border: 1px solid var(--border);
}

.answer-card + .answer-card {
  margin-top: 14px;
}

.answer-card h3 {
  margin: 0 0 12px;
}

.answer-card p {
  margin: 0;
  line-height: 1.8;
  white-space: pre-wrap;
}

.answer-card.warn {
  background: linear-gradient(180deg, #fffef8, #fff7ed);
}

.answer-card.ai {
  background: linear-gradient(180deg, #f8fdff, #eff8ff);
}

.result-side {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.result-action {
  padding: 14px 18px;
  border: 1px solid var(--border);
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  text-align: left;
  font-weight: 700;
}

.result-action.primary {
  border: 0;
  background: linear-gradient(135deg, #0f766e, #0ea5e9);
  color: #fff;
}

.side-tip {
  padding: 18px;
  border-radius: 20px;
  background: rgba(15, 118, 110, 0.08);
}

.side-tip p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.7;
}

@media (max-width: 768px) {
  .result-layout,
  .result-hit-top {
    grid-template-columns: 1fr;
  }
}
</style>
