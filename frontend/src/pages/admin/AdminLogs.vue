<template>
  <div class="admin-page">
    <section class="panel">
      <div class="section-title">
        <div>
          <h2>问答日志</h2>
          <p>查看命中情况、相似度和用户提问原文，辅助分析知识库覆盖率</p>
        </div>
      </div>

      <div class="toolbar">
        <el-input v-model="keyword" placeholder="搜索用户问题或匹配问题" clearable />
        <el-select v-model="hitFilter" placeholder="全部结果" clearable>
          <el-option label="命中" :value="1" />
          <el-option label="未命中" :value="0" />
        </el-select>
      </div>

      <el-table :data="filteredRows" stripe>
        <el-table-column prop="userQuestion" label="用户问题" min-width="260" />
        <el-table-column prop="matchedQuestion" label="匹配问题" min-width="240" />
        <el-table-column label="命中" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isHit === 1 ? 'success' : 'warning'">
              {{ row.isHit === 1 ? '命中' : '未命中' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="相似度" width="110">
          <template #default="{ row }">
            {{ row.similarity ? Number(row.similarity).toFixed(4) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="提问时间" width="180" />
      </el-table>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import http from '../../api/http'

const rows = ref([])
const keyword = ref('')
const hitFilter = ref(null)

const filteredRows = computed(() =>
  [...rows.value]
    .sort((a, b) => `${b.createdAt || ''}`.localeCompare(`${a.createdAt || ''}`))
    .filter((row) => {
      const text = `${row.userQuestion || ''} ${row.matchedQuestion || ''}`.toLowerCase()
      const keywordMatched = !keyword.value || text.includes(keyword.value.trim().toLowerCase())
      const hitMatched = hitFilter.value === null || hitFilter.value === undefined || row.isHit === hitFilter.value
      return keywordMatched && hitMatched
    })
)

const loadData = async () => {
  const resp = await http.get('/api/admin/qa-logs')
  rows.value = resp.data.data || []
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
  grid-template-columns: minmax(0, 1fr) 180px;
  gap: 12px;
  margin-bottom: 16px;
}

@media (max-width: 768px) {
  .toolbar {
    grid-template-columns: 1fr;
  }
}
</style>
