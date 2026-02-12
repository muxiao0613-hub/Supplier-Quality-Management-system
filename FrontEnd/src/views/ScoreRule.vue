<template>
  <div class="rule-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>评分规则配置（低代码配置）</span>
          <el-alert
            title="提示：修改权重后，系统会自动按新权重重新计算供应商评分"
            type="info"
            :closable="false"
            show-icon
          />
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ruleName" label="规则名称" width="150" />
        <el-table-column prop="ruleCode" label="规则编码" width="150" />
        <el-table-column prop="weight" label="权重(%)" width="150">
          <template #default="{ row }">
            <el-input-number
              v-model="row.weight"
              :min="0"
              :max="100"
              :precision="2"
              :disabled="row.status === 0"
              @change="handleWeightChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" :disabled="row.status === 1">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="total-weight">
        <el-tag :type="totalWeight === 100 ? 'success' : 'warning'" size="large">
          当前总权重: {{ totalWeight }}%
        </el-tag>
        <span v-if="totalWeight !== 100" class="weight-warning">
          （建议总权重为100%）
        </span>
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="formData.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则编码" prop="ruleCode">
          <el-input v-model="formData.ruleCode" placeholder="请输入规则编码（英文）" />
        </el-form-item>
        <el-form-item label="权重(%)" prop="weight">
          <el-input-number v-model="formData.weight" :min="0" :max="100" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入规则描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getAllRules, createRule, updateRule, deleteRule, updateRuleWeight } from '@/api/scoreRule'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增规则'
const formRef = ref(null)

const tableData = ref([])
const formData = reactive({
  id: null,
  ruleName: '',
  ruleCode: '',
  weight: 0,
  description: '',
  status: 1
})

const formRules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  ruleCode: [{ required: true, message: '请输入规则编码', trigger: 'blur' }],
  weight: [{ required: true, message: '请输入权重', trigger: 'blur' }]
}

const totalWeight = computed(() => {
  return tableData.value
    .filter(item => item.status === 1)
    .reduce((sum, item) => sum + Number(item.weight), 0)
    .toFixed(2)
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getAllRules()
    tableData.value = res.data
  } catch (error) {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

const handleWeightChange = async (row) => {
  try {
    await updateRuleWeight(row.id, row.weight)
    ElMessage.success('权重更新成功')
  } catch (error) {
    ElMessage.error('权重更新失败')
    fetchData()
  }
}

const handleStatusChange = async (row) => {
  try {
    await updateRule(row.id, row)
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
    fetchData()
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增规则'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑规则'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除规则"${row.ruleName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRule(row.id)
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
          await updateRule(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createRule(formData)
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

const handleDialogClose = () => {
  resetForm()
}

const resetForm = () => {
  formData.id = null
  formData.ruleName = ''
  formData.ruleCode = ''
  formData.weight = 0
  formData.description = ''
  formData.status = 1
  formRef.value?.clearValidate()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.rule-page {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.total-weight {
  margin-top: 20px;
  padding: 20px;
  background: #f5f7fa;
  border-radius: 4px;
  text-align: center;
}

.weight-warning {
  margin-left: 10px;
  color: #e6a23c;
  font-size: 14px;
}
</style>
