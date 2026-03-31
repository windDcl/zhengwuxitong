<template>
  <div class="settings-page">
    <section class="panel">
      <div class="section-title">
        <div>
          <h2>系统设置</h2>
          <p>配置语义阈值与第三方 AI，决定未命中问题的处理策略</p>
        </div>
      </div>

      <el-form label-position="top">
        <div class="grid-two">
          <el-form-item label="语义命中阈值">
            <el-input v-model="form['semantic.threshold']" placeholder="例如 0.30" />
          </el-form-item>

          <el-form-item label="启用第三方 AI">
            <el-switch
              v-model="aiEnabled"
              inline-prompt
              active-text="开启"
              inactive-text="关闭"
            />
          </el-form-item>
        </div>

        <div class="grid-two">
          <el-form-item label="AI Base URL">
            <el-input v-model="form['ai.base-url']" placeholder="例如 https://api.openai.com/v1" />
          </el-form-item>
          <el-form-item label="AI 模型名称">
            <el-input v-model="form['ai.model']" placeholder="例如 gpt-4o-mini / kimi / qwen-plus" />
          </el-form-item>
        </div>

        <div class="grid-two">
          <el-form-item label="API Key">
            <el-input v-model="form['ai.api-key']" type="password" show-password />
          </el-form-item>
          <div class="settings-tip">
            <strong>推荐接入方式</strong>
            <p>当前实现采用 OpenAI 兼容 `chat/completions` 协议，便于接入 OpenAI、通义、Kimi、豆包等兼容网关。</p>
          </div>
        </div>

        <el-form-item label="系统提示词">
          <el-input v-model="form['ai.system-prompt']" type="textarea" :rows="5" />
        </el-form-item>

        <el-button type="primary" @click="saveSettings">保存设置</el-button>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const form = reactive({
  'semantic.threshold': '0.30',
  'ai.enabled': 'false',
  'ai.base-url': '',
  'ai.api-key': '',
  'ai.model': '',
  'ai.system-prompt': ''
})

const aiEnabled = computed({
  get: () => form['ai.enabled'] === 'true',
  set: (value) => {
    form['ai.enabled'] = value ? 'true' : 'false'
  }
})

const loadSettings = async () => {
  const resp = await http.get('/api/admin/settings')
  Object.assign(form, resp.data.data || {})
}

const saveSettings = async () => {
  await http.put('/api/admin/settings', {
    settings: form
  })
  ElMessage.success('系统设置已保存')
}

onMounted(loadSettings)
</script>

<style scoped>
.settings-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.settings-tip {
  padding: 18px;
  border-radius: 24px;
  background: linear-gradient(180deg, rgba(15, 118, 110, 0.08), rgba(14, 165, 233, 0.08));
}

.settings-tip p {
  margin: 8px 0 0;
  color: var(--muted);
  line-height: 1.7;
}
</style>
