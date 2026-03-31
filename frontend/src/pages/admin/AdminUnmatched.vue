<template>
  <div class="unmatched-page">
    <section class="panel">
      <div class="section-title">
        <div>
          <h2>未命中问题运营台</h2>
          <p>查看市民检索但知识库尚未覆盖的问题，并补录为正式 FAQ</p>
        </div>
        <el-button @click="loadData">刷新数据</el-button>
      </div>

      <el-table :data="rows" stripe>
        <el-table-column prop="userQuestion" label="用户问题" min-width="260" />
        <el-table-column label="相似度" width="110">
          <template #default="{ row }">
            {{ row.similarity ? Number(row.similarity).toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="statusMap[row.status]?.type || 'info'">
              {{ statusMap[row.status]?.label || '待处理' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="产生时间" width="180" />
        <el-table-column label="操作" width="300" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button type="primary" plain @click="generateAi(row)">AI建议</el-button>
              <el-button type="success" plain @click="openResolve(row)">录入FAQ</el-button>
              <el-button plain @click="ignoreRow(row)">忽略</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" title="未命中问题转 FAQ" width="720px">
      <el-form label-position="top">
        <el-form-item label="所属分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width:100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="标准问题">
          <el-input v-model="form.standardQuestion" />
        </el-form-item>
        <el-form-item label="标准答案">
          <el-input v-model="form.standardAnswer" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="别名问题">
          <el-input
            v-model="aliasesText"
            type="textarea"
            :rows="4"
            placeholder="每行一个别名问题"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitResolve">确认录入</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import http from '../../api/http'

const rows = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const currentRow = ref(null)
const aliasesText = ref('')
const form = reactive({
  categoryId: null,
  standardQuestion: '',
  standardAnswer: ''
})

const statusMap = {
  0: { label: '待处理', type: 'warning' },
  1: { label: 'AI已建议', type: 'primary' },
  2: { label: '已转FAQ', type: 'success' },
  3: { label: '已忽略', type: 'info' }
}

const resetForm = () => {
  form.categoryId = null
  form.standardQuestion = ''
  form.standardAnswer = ''
  aliasesText.value = ''
}

const loadData = async () => {
  const [unmatchedResp, categoryResp] = await Promise.all([
    http.get('/api/admin/unmatched'),
    http.get('/api/admin/categories')
  ])
  rows.value = unmatchedResp.data.data || []
  categories.value = categoryResp.data.data || []
}

const tryApplySuggestedCategory = (categoryName) => {
  if (!categoryName) {
    return
  }
  const matched = categories.value.find((item) => item.name === categoryName)
  if (matched) {
    form.categoryId = matched.id
  }
}

const openResolve = (row) => {
  currentRow.value = row
  form.standardQuestion = row.aiSuggestedQuestion || row.userQuestion || ''
  form.standardAnswer = row.aiSuggestedAnswer || ''
  aliasesText.value = row.aiSuggestedAliases || ''
  form.categoryId = null
  tryApplySuggestedCategory(row.aiSuggestedCategory)
  dialogVisible.value = true
}

const generateAi = async (row) => {
  const resp = await http.post(`/api/admin/unmatched/${row.id}/ai-suggestion`)
  const data = resp.data.data || {}
  row.aiSuggestedQuestion = data.standardQuestion || ''
  row.aiSuggestedAnswer = data.standardAnswer || ''
  row.aiSuggestedAliases = Array.isArray(data.aliases) ? data.aliases.join('\n') : ''
  row.aiSuggestedCategory = data.categoryName || ''
  row.status = 1
  ElMessage.success('AI 建议已生成，可以继续补录 FAQ')
  openResolve(row)
}

const submitResolve = async () => {
  if (!currentRow.value) {
    return
  }
  await http.post(`/api/admin/unmatched/${currentRow.value.id}/resolve-to-faq`, {
    categoryId: form.categoryId,
    standardQuestion: form.standardQuestion,
    standardAnswer: form.standardAnswer,
    aliases: aliasesText.value
      .split('\n')
      .map((item) => item.trim())
      .filter(Boolean),
    status: 1
  })
  ElMessage.success('已转为 FAQ 并完成知识库补录')
  dialogVisible.value = false
  resetForm()
  await loadData()
}

const ignoreRow = async (row) => {
  await http.post(`/api/admin/unmatched/${row.id}/ignore`)
  ElMessage.success('该问题已标记为忽略')
  await loadData()
}

onMounted(async () => {
  await loadData()
})
</script>

<style scoped>
.unmatched-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
</style>
