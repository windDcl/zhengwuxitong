<template>
  <div class="admin-page">
    <section class="panel">
      <div class="section-title">
        <div>
          <h2>分类管理</h2>
          <p>维护分类名称、排序和启停状态，决定市民端的分类入口展示</p>
        </div>
        <el-button type="primary" @click="openCreate">新增分类</el-button>
      </div>

      <el-table :data="sortedRows" stripe>
        <el-table-column prop="name" label="分类名称" min-width="220" />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
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

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑分类' : '新增分类'" width="560px">
      <el-form label-position="top">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="statusEnabled"
            inline-prompt
            active-text="启用"
            inactive-text="停用"
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
const dialogVisible = ref(false)
const editingId = ref(null)
const form = reactive({
  name: '',
  sortOrder: 0,
  status: 1
})

const sortedRows = computed(() =>
  [...rows.value].sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
)

const statusEnabled = computed({
  get: () => form.status === 1,
  set: (value) => {
    form.status = value ? 1 : 0
  }
})

const resetForm = () => {
  editingId.value = null
  form.name = ''
  form.sortOrder = 0
  form.status = 1
}

const loadData = async () => {
  const resp = await http.get('/api/admin/categories')
  rows.value = resp.data.data || []
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = (row) => {
  editingId.value = row.id
  form.name = row.name
  form.sortOrder = row.sortOrder
  form.status = row.status
  dialogVisible.value = true
}

const submitForm = async () => {
  const payload = {
    name: form.name,
    sortOrder: form.sortOrder,
    status: form.status
  }
  if (editingId.value) {
    await http.put(`/api/admin/categories/${editingId.value}`, payload)
    ElMessage.success('分类已更新')
  } else {
    await http.post('/api/admin/categories', payload)
    ElMessage.success('分类已创建')
  }
  dialogVisible.value = false
  resetForm()
  await loadData()
}

const toggleStatus = async (row) => {
  await http.put(`/api/admin/categories/${row.id}`, {
    name: row.name,
    sortOrder: row.sortOrder,
    status: row.status === 1 ? 0 : 1
  })
  ElMessage.success(`分类已${row.status === 1 ? '停用' : '启用'}`)
  await loadData()
}

const removeRow = async (row) => {
  await ElMessageBox.confirm(`确定删除分类“${row.name}”吗？`, '删除确认', {
    type: 'warning'
  })
  await http.delete(`/api/admin/categories/${row.id}`)
  ElMessage.success('分类已删除')
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
