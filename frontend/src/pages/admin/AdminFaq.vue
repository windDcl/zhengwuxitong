<template>
  <div class="admin-page">
    <section class="panel">
      <div class="section-title">
        <div>
          <h2>FAQ 管理</h2>
          <p>维护标准问答和别名问题，并支持一键重建语义索引</p>
        </div>
        <el-space wrap>
          <el-button @click="loadData">刷新</el-button>
          <el-button type="primary" plain @click="reindexFaqs">重建索引</el-button>
          <el-button type="primary" @click="openCreate">新增 FAQ</el-button>
        </el-space>
      </div>

      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索标准问题或答案" clearable />
        <el-select v-model="categoryFilter" placeholder="全部分类" clearable>
          <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
        </el-select>
        <el-select v-model="statusFilter" placeholder="全部状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </div>

      <el-table :data="filteredRows" stripe>
        <el-table-column prop="standardQuestion" label="标准问题" min-width="260" />
        <el-table-column label="所属分类" width="140">
          <template #default="{ row }">
            {{ categoryMap[row.categoryId] || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180" />
        <el-table-column label="操作" width="290" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button type="primary" plain @click="openEdit(row)">编辑</el-button>
              <el-button plain @click="toggleStatus(row)">
                {{ row.status === 1 ? '停用' : '启用' }}
              </el-button>
              <el-button type="danger" plain @click="removeRow(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑 FAQ' : '新增 FAQ'" width="760px">
      <el-form label-position="top">
        <div class="grid-two">
          <el-form-item label="所属分类">
            <el-select v-model="form.categoryId" style="width:100%">
              <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-switch
              v-model="statusEnabled"
              inline-prompt
              active-text="启用"
              inactive-text="停用"
            />
          </el-form-item>
        </div>
        <el-form-item label="标准问题">
          <el-input v-model="form.standardQuestion" />
        </el-form-item>
        <el-form-item label="标准答案">
          <el-input v-model="form.standardAnswer" type="textarea" :rows="7" />
        </el-form-item>
        <el-form-item label="别名问题">
          <el-input
            v-model="aliasesText"
            type="textarea"
            :rows="5"
            placeholder="每行一个别名问题"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import http from '../../api/http'

const rows = ref([])
const categories = ref([])
const keyword = ref('')
const categoryFilter = ref(null)
const statusFilter = ref(null)
const dialogVisible = ref(false)
const editingId = ref(null)
const aliasesText = ref('')
const form = reactive({
  categoryId: null,
  standardQuestion: '',
  standardAnswer: '',
  status: 1
})

const categoryMap = computed(() =>
  Object.fromEntries(categories.value.map((item) => [item.id, item.name]))
)

const filteredRows = computed(() =>
  rows.value.filter((row) => {
    const text = `${row.standardQuestion || ''} ${row.standardAnswer || ''}`.toLowerCase()
    const keywordMatched = !keyword.value || text.includes(keyword.value.trim().toLowerCase())
    const categoryMatched = !categoryFilter.value || row.categoryId === categoryFilter.value
    const statusMatched = statusFilter.value === null || statusFilter.value === undefined || row.status === statusFilter.value
    return keywordMatched && categoryMatched && statusMatched
  })
)

const statusEnabled = computed({
  get: () => form.status === 1,
  set: (value) => {
    form.status = value ? 1 : 0
  }
})

const resetForm = () => {
  editingId.value = null
  form.categoryId = null
  form.standardQuestion = ''
  form.standardAnswer = ''
  form.status = 1
  aliasesText.value = ''
}

const loadData = async () => {
  const [faqResp, categoryResp] = await Promise.all([
    http.get('/api/admin/faqs'),
    http.get('/api/admin/categories')
  ])
  rows.value = faqResp.data.data || []
  categories.value = categoryResp.data.data || []
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = async (row) => {
  const resp = await http.get(`/api/admin/faqs/${row.id}`)
  const data = resp.data.data || {}
  const faq = data.faq || row
  editingId.value = row.id
  form.categoryId = faq.categoryId
  form.standardQuestion = faq.standardQuestion
  form.standardAnswer = faq.standardAnswer
  form.status = faq.status
  aliasesText.value = (data.aliases || []).map((item) => item.aliasQuestion).join('\n')
  dialogVisible.value = true
}

const submitForm = async () => {
  const payload = {
    categoryId: form.categoryId,
    standardQuestion: form.standardQuestion,
    standardAnswer: form.standardAnswer,
    status: form.status,
    aliases: aliasesText.value
      .split('\n')
      .map((item) => item.trim())
      .filter(Boolean)
  }
  if (editingId.value) {
    await http.put(`/api/admin/faqs/${editingId.value}`, payload)
    ElMessage.success('FAQ 已更新')
  } else {
    await http.post('/api/admin/faqs', payload)
    ElMessage.success('FAQ 已创建')
  }
  dialogVisible.value = false
  resetForm()
  await loadData()
}

const toggleStatus = async (row) => {
  await http.post(`/api/admin/faqs/${row.id}/status`, null, {
    params: { status: row.status === 1 ? 0 : 1 }
  })
  ElMessage.success(`FAQ 已${row.status === 1 ? '停用' : '启用'}`)
  await loadData()
}

const removeRow = async (row) => {
  await ElMessageBox.confirm(`确定删除 FAQ “${row.standardQuestion}”吗？`, '删除确认', {
    type: 'warning'
  })
  await http.delete(`/api/admin/faqs/${row.id}`)
  ElMessage.success('FAQ 已删除')
  await loadData()
}

const reindexFaqs = async () => {
  await http.post('/api/admin/faqs/reindex')
  ElMessage.success('语义索引已重建')
}

onMounted(loadData)
</script>

<style scoped>
.admin-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.toolbar {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 180px 160px;
  gap: 12px;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .toolbar {
    grid-template-columns: 1fr;
  }
}
</style>
