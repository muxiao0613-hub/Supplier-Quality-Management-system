<template>
  <div class="supplier-page">
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="供应商名称/编号/联系人" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="searchForm.category" placeholder="请选择" clearable>
            <el-option label="电子元器件" value="电子元器件" />
            <el-option label="机械配件" value="机械配件" />
            <el-option label="塑料制品" value="塑料制品" />
            <el-option label="化工原料" value="化工原料" />
            <el-option label="金属制品" value="金属制品" />
            <el-option label="包装材料" value="包装材料" />
            <el-option label="橡胶制品" value="橡胶制品" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="searchForm.grade" placeholder="请选择" clearable>
            <el-option label="A" value="A" />
            <el-option label="B" value="B" />
            <el-option label="C" value="C" />
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
          <span>供应商列表</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon>
            新增供应商
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="supplierCode" label="供应商编号" width="120" />
        <el-table-column prop="supplierName" label="供应商名称" width="180" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="contactPerson" label="联系人" width="100" />
        <el-table-column prop="contactPhone" label="联系电话" width="130" />
        <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="products" label="合作产品" width="150" show-overflow-tooltip />
        <el-table-column prop="grade" label="等级" width="80">
          <template #default="{ row }">
            <el-tag :type="getGradeType(row.grade)">{{ row.grade }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
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
        <el-form-item label="供应商编号" prop="supplierCode">
          <el-input v-model="formData.supplierCode" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="supplierName">
          <el-input v-model="formData.supplierName" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="formData.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="电子元器件" value="电子元器件" />
            <el-option label="机械配件" value="机械配件" />
            <el-option label="塑料制品" value="塑料制品" />
            <el-option label="化工原料" value="化工原料" />
            <el-option label="金属制品" value="金属制品" />
            <el-option label="包装材料" value="包装材料" />
            <el-option label="橡胶制品" value="橡胶制品" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="formData.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="formData.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="formData.address" type="textarea" :rows="2" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="合作产品" prop="products">
          <el-input v-model="formData.products" type="textarea" :rows="2" placeholder="请输入合作产品" />
        </el-form-item>
        <el-form-item label="等级" prop="grade">
          <el-radio-group v-model="formData.grade">
            <el-radio label="A">A</el-radio>
            <el-radio label="B">B</el-radio>
            <el-radio label="C">C</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="formData.status" :active-value="1" :inactive-value="0" />
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
import { getSupplierList, createSupplier, updateSupplier, deleteSupplier } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const dialogVisible = ref(false)
const dialogTitle = ref('新增供应商')
const formRef = ref(null)

const searchForm = reactive({
  keyword: '',
  category: '',
  grade: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const formData = reactive({
  id: null,
  supplierCode: '',
  supplierName: '',
  category: '',
  contactPerson: '',
  contactPhone: '',
  address: '',
  products: '',
  grade: 'C',
  status: 1,
  remark: ''
})

const formRules = {
  supplierCode: [{ required: true, message: '请输入供应商编号', trigger: 'blur' }],
  supplierName: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  grade: [{ required: true, message: '请选择等级', trigger: 'change' }]
}

const getGradeType = (grade) => {
  const typeMap = { A: 'success', B: 'warning', C: 'info' }
  return typeMap[grade] || 'info'
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getSupplierList({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
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
  searchForm.keyword = ''
  searchForm.category = ''
  searchForm.grade = ''
  pagination.current = 1
  fetchData()
}

const handleAdd = () => {
  dialogTitle.value = '新增供应商'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑供应商'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除供应商"${row.supplierName}"吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteSupplier(row.id)
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
          await updateSupplier(formData.id, formData)
          ElMessage.success('更新成功')
        } else {
          await createSupplier(formData)
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
  formData.supplierCode = ''
  formData.supplierName = ''
  formData.category = ''
  formData.contactPerson = ''
  formData.contactPhone = ''
  formData.address = ''
  formData.products = ''
  formData.grade = 'C'
  formData.status = 1
  formData.remark = ''
  formRef.value?.clearValidate()
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.supplier-page {
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
