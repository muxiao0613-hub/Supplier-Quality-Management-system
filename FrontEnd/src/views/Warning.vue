<template>
  <div class="warning-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>
            <el-icon class="warning-icon"><Warning /></el-icon>
            风险供应商列表
          </span>
          <el-tag type="danger" size="large">
            共 {{ tableData.length }} 家风险供应商
          </el-tag>
        </div>
      </template>

      <el-alert
        title="风险提示"
        type="error"
        description="以下供应商评分低于60分，存在质量风险，建议采取相应措施"
        :closable="false"
        show-icon
        style="margin-bottom: 20px"
      />

      <el-table :data="tableData" border stripe v-loading="loading" :row-class-name="tableRowClassName">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="supplierName" label="供应商" width="180" />
        <el-table-column prop="qualityRate" label="合格率(%)" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.qualityRate" :color="getRateColor(row.qualityRate)" />
          </template>
        </el-table-column>
        <el-table-column prop="ontimeRate" label="准时率(%)" width="120">
          <template #default="{ row }">
            <el-progress :percentage="row.ontimeRate" :color="getRateColor(row.ontimeRate)" />
          </template>
        </el-table-column>
        <el-table-column prop="serviceScore" label="服务评分" width="120">
          <template #default="{ row }">
            <el-rate v-model="row.serviceScore" disabled show-score />
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="100">
          <template #default="{ row }">
            <el-tag type="danger" size="large">{{ row.totalScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scoreDate" label="评分日期" width="120" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleView(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="供应商评分详情" width="600px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="供应商ID">{{ currentRow.id }}</el-descriptions-item>
        <el-descriptions-item label="供应商名称">{{ currentRow.supplierName }}</el-descriptions-item>
        <el-descriptions-item label="合格率">
          <el-tag :type="getRateTagType(currentRow.qualityRate)">
            {{ currentRow.qualityRate }}%
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="准时率">
          <el-tag :type="getRateTagType(currentRow.ontimeRate)">
            {{ currentRow.ontimeRate }}%
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="服务评分">
          <el-rate v-model="currentRow.serviceScore" disabled show-score />
        </el-descriptions-item>
        <el-descriptions-item label="总分">
          <el-tag type="danger" size="large">{{ currentRow.totalScore }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="风险状态">
          <el-tag type="danger">风险供应商</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="评分日期">{{ currentRow.scoreDate }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getRiskSuppliers } from '@/api/supplierScore'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const detailVisible = ref(false)
const currentRow = ref(null)
const tableData = ref([])
const suppliers = ref([])

const getRateColor = (rate) => {
  if (rate >= 90) return '#67c23a'
  if (rate >= 70) return '#e6a23c'
  return '#f56c6c'
}

const getRateTagType = (rate) => {
  if (rate >= 90) return 'success'
  if (rate >= 70) return 'warning'
  return 'danger'
}

const tableRowClassName = ({ row }) => {
  return 'risk-row'
}

const fetchSuppliers = async () => {
  try {
    const res = await getAllSuppliers()
    suppliers.value = res.data
  } catch (error) {
    ElMessage.error('获取供应商列表失败')
  }
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRiskSuppliers()
    tableData.value = res.data.map(item => {
      const supplier = suppliers.value.find(s => s.id === item.supplierId)
      return { ...item, supplierName: supplier?.supplierName || '' }
    })
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleView = (row) => {
  currentRow.value = row
  detailVisible.value = true
}

onMounted(async () => {
  await fetchSuppliers()
  fetchData()
})
</script>

<style scoped>
.warning-page {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.warning-icon {
  font-size: 24px;
  color: #f56c6c;
  margin-right: 10px;
  vertical-align: middle;
}

:deep(.risk-row) {
  background-color: #fef0f0;
}

:deep(.risk-row:hover) {
  background-color: #fde2e2 !important;
}
</style>
