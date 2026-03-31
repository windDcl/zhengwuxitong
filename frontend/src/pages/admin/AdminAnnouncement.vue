<template>
  <div class="admin-page">
    <section class="panel">
      <div class="section-title">
        <div>
          <h2>公告管理</h2>
          <p>维护前台公告内容、发布时间和置顶状态</p>
        </div>
        <el-button type="primary" @click="openCreate">新增公告</el-button>
      </div>

      <el-table :data="sortedRows" stripe>
        <el-table-column prop="title" label="公告标题" min-width="220" />
        <el-table-column label="置顶" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isTop === 1 ? 'danger' : 'info'">
              {{ row.isTop === 1 ? '置顶' : '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '发布' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-space wrap>
              <el-button type="primary" plain @click="openEdit(row)">编辑</el-button>
              <el-button plain @click="toggleStatus(row)">
                {{ row.status === 1 ? '停用' : '发布' }}
              </el-button>
              <el-button type="danger" plain @click="removeRow(row)">删除</el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑公告' : '新增公告'" width="720px">
      <el-form label-position="top">
        <el-form-item label="公告标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="公告内容">
          <el-input v-model="form.content" type="textarea" :rows="7" />
        </el-form-item>
        <div class="grid-two">
          <el-form-item label="发布时间">
            <el-date-picker
              v-model="form.publishTime"
              type="datetime"
              style="width:100%"
              value-format="YYYY-MM-DDTHH:mm:ss"
            />
          </el-form-item>
          <div class="grid-two">
            <el-form-item label="置顶">
              <el-switch v-model="topEnabled" inline-prompt active-text="置顶" inactive-text="普通" />
            </el-form-item>
            <el-form-item label="状态">
              <el-switch v-model="statusEnabled" inline-prompt active-text="发布" inactive-text="停用" />
            </el-form-item>
          </div>
        </div>
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
const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
  title: '',
  content: '',
  publishTime: '',
  isTop: 0,
  status: 1
})

const sortedRows = computed(() =>
  [...rows.value].sort((a, b) => {
    if ((b.isTop ?? 0) !== (a.isTop ?? 0)) {
      return (b.isTop ?? 0) - (a.isTop ?? 0)
    }
    return `${b.publishTime || ''}`.localeCompare(`${a.publishTime || ''}`)
  })
)

const topEnabled = computed({
  get: () => form.isTop === 1,
  set: (value) => {
    form.isTop = value ? 1 : 0
  }
})

const statusEnabled = computed({
  get: () => form.status === 1,
  set: (value) => {
    form.status = value ? 1 : 0
  }
})

const resetForm = () => {
  editingId.value = null
  form.title = ''
  form.content = ''
  form.publishTime = ''
  form.isTop = 0
  form.status = 1
}

const loadData = async () => {
  const resp = await http.get('/api/admin/announcements')
  rows.value = resp.data.data || []
}

const openCreate = () => {
  resetForm()
  form.publishTime = new Date().toISOString().slice(0, 19)
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.title = row.title
  form.content = row.content
  form.publishTime = row.publishTime
  form.isTop = row.isTop
  form.status = row.status
  dialogVisible.value = true
}

const submitForm = async () => {
  const payload = {
    title: form.title,
    content: form.content,
    publishTime: form.publishTime,
    isTop: form.isTop,
    status: form.status
  }
  if (editingId.value) {
    await http.put(`/api/admin/announcements/${editingId.value}`, payload)
    ElMessage.success('公告已更新')
  } else {
    await http.post('/api/admin/announcements', payload)
    ElMessage.success('公告已创建')
  }
  dialogVisible.value = false
  resetForm()
  await loadData()
}

const toggleStatus = async (row) => {
  await http.put(`/api/admin/announcements/${row.id}`, {
    title: row.title,
    content: row.content,
    publishTime: row.publishTime,
    isTop: row.isTop,
    status: row.status === 1 ? 0 : 1
  })
  ElMessage.success(`公告已${row.status === 1 ? '停用' : '发布'}`)
  await loadData()
}

const removeRow = async (row) => {
  await ElMessageBox.confirm(`确定删除公告“${row.title}”吗？`, '删除确认', {
    type: 'warning'
  })
  await http.delete(`/api/admin/announcements/${row.id}`)
  ElMessage.success('公告已删除')
  await loadData()
}

onMounted(loadData)
</script>

<style scoped>
.admin-page {
  display: flex;
  flex-direction: column;
  gap: 18px;
}
</style>
