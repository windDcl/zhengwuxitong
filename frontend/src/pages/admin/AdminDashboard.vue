<template>
  <div>
    <h2>Dashboard</h2>
    <el-row :gutter="12" style="margin-bottom:12px">
      <el-col :span="6"><el-card>FAQ 总数：{{ overview.faqCount || 0 }}</el-card></el-col>
      <el-col :span="6"><el-card>分类总数：{{ overview.categoryCount || 0 }}</el-card></el-col>
      <el-col :span="6"><el-card>今日提问：{{ overview.todayQuestions || 0 }}</el-card></el-col>
      <el-col :span="6"><el-card>未命中：{{ overview.unmatchedCount || 0 }}</el-card></el-col>
    </el-row>

    <el-card style="margin-bottom:12px">
      <h3>提问趋势</h3>
      <pre>{{ trend }}</pre>
    </el-card>

    <el-card style="margin-bottom:12px">
      <h3>分类占比</h3>
      <pre>{{ categoryDistribution }}</pre>
    </el-card>

    <el-card>
      <h3>热门问题排行</h3>
      <pre>{{ hotQuestions }}</pre>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import http from '../../api/http'

const overview = ref({})
const trend = ref([])
const categoryDistribution = ref([])
const hotQuestions = ref([])

onMounted(async () => {
  const [o, t, c, h] = await Promise.all([
    http.get('/api/admin/dashboard/overview'),
    http.get('/api/admin/dashboard/trend'),
    http.get('/api/admin/dashboard/category-distribution'),
    http.get('/api/admin/dashboard/hot-questions')
  ])
  overview.value = o.data.data || {}
  trend.value = t.data.data || []
  categoryDistribution.value = c.data.data || []
  hotQuestions.value = h.data.data || []
})
</script>
