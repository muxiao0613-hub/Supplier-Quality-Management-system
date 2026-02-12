<template>
  <div class="purchase-page">
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
        <el-form-item label="检验结果">
          <el-select v-model="searchForm.inspectionResult" placeholder="请选择" clearable>
            <el-option label="合格" :value="1" />
            <el-option label="不合格" :value="0" />
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
          <span>采购记录列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增记录
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="supplierName" label="供应商" width="150" />
        <el-table-column prop="productName" label="产品名称" width="150" />
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="inspectionResult" label="检验结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.inspectionResult === 1 ? 'success' : 'danger'">
              {{ row.inspectionResult === 1 ? '合格' : '不合格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="unqualifiedReason" label="不合格原因" width="150" show-overflow-tooltip />
        <el-table-column prop="arrivalDate" label="到货日期" width="120" />
        <el-table-column prop="expectedDate" label="预期日期" width="120" />
        <el-table-column prop="isDelayed" label="是否延迟" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isDelayed === 1 ? 'danger' : 'success'">
              {{ row.isDelayed === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="paymentStatus" label="付款状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.paymentStatus === 1 ? 'success' : 'warning'">
              {{ row.paymentStatus === 1 ? '已付款' : '未付款' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
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
      width="700px"
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
        <el-form-item label="产品名称" prop="productName">
          <el-input v-model="formData.productName" placeholder="请输入产品名称" />
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="formData.quantity" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="formData.unit" placeholder="请输入单位" />
        </el-form-item>
        <el-form-item label="检验结果" prop="inspectionResult">
          <el-radio-group v-model="formData.inspectionResult">
            <el-radio :label="1">合格</el-radio>
            <el-radio :label="0">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="不合格原因" prop="unqualifiedReason" v-if="formData.inspectionResult === 0">
          <el-input v-model="formData.unqualifiedReason" type="textarea" :rows="2" placeholder="请输入不合格原因" />
        </el-form-item>
        <el-form-item label="到货日期" prop="arrivalDate">
          <el-date-picker
            v-model="formData.arrivalDate"
            type="date"
            placeholder="选择到货日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="预期日期" prop="expectedDate">
          <el-date-picker
            v-model="formData.expectedDate"
            type="date"
            placeholder="选择预期日期"
            style="width: 100%"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item label="付款状态" prop="paymentStatus">
          <el-radio-group v-model="formData.paymentStatus">
            <el-radio :label="0">未付款</el-radio>
            <el-radio :label="1">已付款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { ref, reactive, onMounted } from 'vue'
import { getRecordList, createRecord, updateRecord, deleteRecord } from '@/api/purchaseRecord'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增记录')
const formRef = ref(null)
const suppliers = ref([])

const searchForm = reactive({
  supplierId: null,
  inspectionResult: null
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
  productName: '',
  quantity: 1,
  unit: '件',
  inspectionResult: 1,
  unqualifiedReason: '',
  arrivalDate: '',
  expectedDate: '',
  paymentStatus: 0,
  remark: ''
})

const formRules = {
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  productName: [{ required: true, message: '请输入产品名称', trigger: 'blur' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  inspectionResult: [{ required: true, message: '请选择检验结果', trigger: 'change' }],
  arrivalDate: [{ required: true, message: '请选择到货日期', trigger: 'change' }]
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
    const res = await getRecordList({
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
  searchForm.inspectionResult = null
  pagination.current = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增记录'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑记录'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除这条记录吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRecord(row.id)
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
          await updateRecord(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createRecord(formData)
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
  formData.supplierId = null
  formData.productName = ''
  formData.quantity = 1
  formData.unit = '件'
  formData.inspectionResult = 1
  formData.unqualifiedReason = ''
  formData.arrivalDate = ''
  formData.expectedDate = ''
  formData.paymentStatus = 0
  formData.remark = ''
  formRef.value?.clearValidate()
}

onMounted(async () => {
  await fetchSuppliers()
  fetchData()
})
</script>

<style scoped>
.purchase-page {
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
