<template>
  <div class="dashboard-page">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card risk-card">
          <div class="stat-content">
            <div class="stat-icon risk-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.riskCount }}</div>
              <div class="stat-label">风险供应商</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card supplier-card">
          <div class="stat-content">
            <div class="stat-icon supplier-icon">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalSuppliers }}</div>
              <div class="stat-label">供应商总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card record-card">
          <div class="stat-content">
            <div class="stat-icon record-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalRecords }}</div>
              <div class="stat-label">采购记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card qualified-card">
          <div class="stat-content">
            <div class="stat-icon qualified-icon">
              <el-icon><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.qualifiedCount }}</div>
              <div class="stat-label">合格记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>供应商评分TOP5</span>
              <el-button type="primary" size="small" @click="goToCharts">
                查看全部
              </el-button>
            </div>
          </template>
          <div ref="topChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="quick-card">
          <template #header>
            <span>快捷入口</span>
          </template>
          <div class="quick-actions">
            <div class="quick-item" @click="goToSuppliers">
              <el-icon class="quick-icon"><OfficeBuilding /></el-icon>
              <span>供应商管理</span>
            </div>
            <div class="quick-item" @click="goToPurchaseRecords">
              <el-icon class="quick-icon"><Document /></el-icon>
              <span>采购记录</span>
            </div>
            <div class="quick-item" @click="goToScores">
              <el-icon class="quick-icon"><TrendCharts /></el-icon>
              <span>供应商评分</span>
            </div>
            <div class="quick-item" @click="goToWarnings">
              <el-icon class="quick-icon warning"><Warning /></el-icon>
              <span>风险预警</span>
            </div>
            <div class="quick-item" @click="goToRules">
              <el-icon class="quick-icon"><Setting /></el-icon>
              <span>评分规则</span>
            </div>
            <div class="quick-item" @click="goToCharts">
              <el-icon class="quick-icon"><DataLine /></el-icon>
              <span>数据可视化</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>近期采购记录</span>
              <el-button type="primary" size="small" @click="goToPurchaseRecords">
                查看全部
              </el-button>
            </div>
          </template>
          <el-table :data="recentRecords" border stripe>
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="supplierName" label="供应商" width="150" />
            <el-table-column prop="productName" label="产品名称" width="150" />
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="inspectionResult" label="检验结果" width="100">
              <template #default="{ row }">
                <el-tag :type="row.inspectionResult === 1 ? 'success' : 'danger'">
                  {{ row.inspectionResult === 1 ? '合格' : '不合格' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="arrivalDate" label="到货日期" width="120" />
            <el-table-column prop="isDelayed" label="是否延迟" width="100">
              <template #default="{ row }">
                <el-tag :type="row.isDelayed === 1 ? 'danger' : 'success'">
                  {{ row.isDelayed === 1 ? '是' : '否' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { getDashboardStats, getScoreRanking } from '@/api/dashboard'
import { getRecordList } from '@/api/purchaseRecord'
import { getAllSuppliers } from '@/api/supplier'
import { ElMessage } from 'element-plus'

const router = useRouter()
const topChartRef = ref(null)
let topChart = null

const stats = reactive({
  riskCount: 0,
  totalSuppliers: 0,
  totalRecords: 0,
  qualifiedCount: 0
})

const recentRecords = ref([])

const initTopChart = (data) => {
  if (!topChartRef.value) return
  
  topChart = echarts.init(topChartRef.value)
  
  const top5 = data.slice(0, 5)
  
  const option = {
    title: {
      text: '供应商评分TOP5',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'value',
      name: '评分',
      max: 100
    },
    yAxis: {
      type: 'category',
      data: top5.map(item => item.supplierName),
      inverse: true
    },
    series: [
      {
        name: '总分',
        type: 'bar',
        data: top5.map(item => item.totalScore),
        label: {
          show: true,
          position: 'right'
        },
        itemStyle: {
          color: function(params) {
            const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de']
            return colors[params.dataIndex % colors.length]
          }
        }
      }
    ]
  }
  
  topChart.setOption(option)
}

const fetchStats = async () => {
  try {
    const res = await getDashboardStats()
    Object.assign(stats, res.data)
  } catch (error) {
    ElMessage.error('获取统计数据失败')
  }
}

const fetchRecentRecords = async () => {
  try {
    const suppliersRes = await getAllSuppliers()
    const recordsRes = await getRecordList({ current: 1, size: 5 })
    
    recentRecords.value = recordsRes.data.records.map(item => {
      const supplier = suppliersRes.data.find(s => s.id === item.supplierId)
      return { ...item, supplierName: supplier?.supplierName || '' }
    })
  } catch (error) {
    ElMessage.error('获取近期记录失败')
  }
}

const fetchData = async () => {
  await Promise.all([fetchStats(), fetchRecentRecords()])
  
  try {
    const rankingRes = await getScoreRanking()
    initTopChart(rankingRes.data)
  } catch (error) {
    console.error('获取评分排名失败', error)
  }
}

const goToSuppliers = () => router.push('/suppliers')
const goToPurchaseRecords = () => router.push('/purchase-records')
const goToScores = () => router.push('/supplier-scores')
const goToWarnings = () => router.push('/warnings')
const goToRules = () => router.push('/score-rules')
const goToCharts = () => router.push('/charts')

const handleResize = () => {
  topChart?.resize()
}

onMounted(async () => {
  await fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  topChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.dashboard-page {
  height: 100%;
}

.stat-card {
  cursor: pointer;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
}

.risk-icon {
  background: linear-gradient(135deg, #f56c6c 0%, #f78989 100%);
  color: white;
}

.supplier-icon {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  color: white;
}

.record-icon {
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
}

.qualified-icon {
  background: linear-gradient(135deg, #e6a23c 0%, #ebb563 100%);
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: 300px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.quick-card {
  height: 100%;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.quick-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-item:hover {
  background: #e6f7ff;
  transform: translateX(5px);
}

.quick-icon {
  font-size: 24px;
  color: #409eff;
}

.quick-icon.warning {
  color: #f56c6c;
}

.quick-item span {
  font-size: 14px;
  color: #606266;
}
</style>
