<template>
  <div class="score-page">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="供应商">
          <el-select v-model="searchForm.supplierId" placeholder="请选择" clearable filterable>
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.supplierName"
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="风险状态">
          <el-select v-model="searchForm.isRisk" placeholder="请选择" clearable>
            <el-option label="风险供应商" :value="1" />
            <el-option label="正常供应商" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="handleReset">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>供应商评分列表</span>
          <el-button type="primary" @click="handleCalculate">
            <el-icon><TrendCharts /></el-icon>
            计算评分
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
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
            <el-tag :type="getScoreType(row.totalScore)" size="large">
              {{ row.totalScore }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isRisk" label="风险状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isRisk === 1 ? 'danger' : 'success'">
              {{ row.isRisk === 1 ? '风险' : '正常' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scoreDate" label="评分日期" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchData"
        @current-change="fetchData"
        class="pagination"
      />
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="formData.supplierId" placeholder="请选择供应商" style="width: 100%" filterable>
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.supplierName"
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="合格率(%)" prop="qualityRate">
          <el-input-number v-model="formData.qualityRate" :min="0" :max="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="准时率(%)" prop="ontimeRate">
          <el-input-number v-model="formData.ontimeRate" :min="0" :max="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="服务评分" prop="serviceScore">
          <el-input-number v-model="formData.serviceScore" :min="0" :max="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="评分日期" prop="scoreDate">
          <el-date-picker
            v-model="formData.scoreDate"
            type="date"
            placeholder="选择评分日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="calculateVisible" title="计算供应商评分" width="500px">
      <el-form :model="calculateForm" label-width="100px">
        <el-form-item label="供应商">
          <el-select v-model="calculateForm.supplierId" placeholder="请选择供应商" style="width: 100%" filterable>
            <el-option
              v-for="supplier in suppliers"
              :key="supplier.id"
              :label="supplier.supplierName"
              :value="supplier.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="评分日期">
          <el-date-picker
            v-model="calculateForm.scoreDate"
            type="date"
            placeholder="选择评分日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="calculateVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCalculateSubmit">计算</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getScoreList, createScore, updateScore, deleteScore, calculateScore } from '@/api/supplierScore'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const calculateVisible = ref(false)
const dialogTitle = ref('新增评分')
const formRef = ref(null)
const suppliers = ref([])

const searchForm = reactive({
  supplierId: null,
  isRisk: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const formData = reactive({
  id: null,
  supplierId: null,
  qualityRate: 0,
  ontimeRate: 0,
  serviceScore: 80,
  scoreDate: ''
})

const calculateForm = reactive({
  supplierId: null,
  scoreDate: new Date().toISOString().split('T')[0]
})

const formRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  qualityRate: [{ required: true, message: '请输入合格率', trigger: 'blur' }],
  ontimeRate: [{ required: true, message: '请输入准时率', trigger: 'blur' }],
  serviceScore: [{ required: true, message: '请输入服务评分', trigger: 'blur' }],
  scoreDate: [{ required: true, message: '请选择评分日期', trigger: 'change' }]
}

const getRateColor = (rate) => {
  if (rate >= 90) return '#67c23a'
  if (rate >= 70) return '#e6a23c'
  return '#f56c6c'
}

const getScoreType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 60) return 'warning'
  return 'danger'
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
    const res = await getScoreList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records.map(item => {
      const supplier = suppliers.value.find(s => s.id === item.supplierId)
      return { ...item, supplierName: supplier?.supplierName || '' }
    })
    pagination.total = res.data.total
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.supplierId = null
  searchForm.isRisk = null
  pagination.current = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增评分'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑评分'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除这条评分记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteScore(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (formData.id) {
          await updateScore(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createScore(formData)
          ElMessage.success('创建成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }
  })
}

const handleCalculate = () => {
  calculateVisible.value = true
}

const handleCalculateSubmit = async () => {
  if (!calculateForm.supplierId) {
    ElMessage.warning('请选择供应商')
    return
  }
  
  try {
    const res = await calculateScore(calculateForm.supplierId, calculateForm.scoreDate)
    ElMessage.success('计算成功')
    calculateVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('计算失败')
  }
}

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  formData.id = null
  formData.supplierId = null
  formData.qualityRate = 0
  formData.ontimeRate = 0
  formData.serviceScore = 80
  formData.scoreDate = ''
  formRef.value?.clearValidate()
}

onMounted(async () => {
  await fetchSuppliers()
  fetchData()
})
</script>

<style scoped>
.score-page {
  height: 100%;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  height: calc(100% - 100px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
