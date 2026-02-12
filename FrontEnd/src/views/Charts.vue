<template>
  <div class="charts-page">
    <el-row :gutter="20">
      <el-col :span="24">
        <el-card class="chart-card">
          <template #header>
            <div class="card-header">
              <span>供应商评分排行榜</span>
              <el-button type="primary" size="small" @click="refreshData">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          <div ref="rankingChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>合格率趋势</span>
          </template>
          <div ref="trendChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card">
          <template #header>
            <span>各供应商质量对比</span>
          </template>
          <div ref="comparisonChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getScoreRanking, getQualityTrend, getSupplierComparison } from '@/api/dashboard'
import { ElMessage } from 'element-plus'

const rankingChartRef = ref(null)
const trendChartRef = ref(null)
const comparisonChartRef = ref(null)

let rankingChart = null
let trendChart = null
let comparisonChart = null

const initRankingChart = (data) => {
  if (!rankingChartRef.value) return
  
  rankingChart = echarts.init(rankingChartRef.value)
  
  const option = {
    title: {
      text: '供应商评分TOP10',
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
      data: data.map(item => item.supplierName),
      inverse: true
    },
    series: [
      {
        name: '总分',
        type: 'bar',
        data: data.map(item => item.totalScore),
        label: {
          show: true,
          position: 'right'
        },
        itemStyle: {
          color: function(params) {
            const colors = ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4', '#ea7ccc']
            return colors[params.dataIndex % colors.length]
          }
        }
      }
    ]
  }
  
  rankingChart.setOption(option)
}

const initTrendChart = (data) => {
  if (!trendChartRef.value) return
  
  trendChart = echarts.init(trendChartRef.value)
  
  const dates = data.map(item => item.date)
  const qualifiedRates = data.map(item => {
    const total = item.total
    const qualified = item.qualified
    return total > 0 ? ((qualified / total) * 100).toFixed(2) : 0
  })
  
  const option = {
    title: {
      text: '月度合格率趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['合格率'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '合格率(%)',
      max: 100
    },
    series: [
      {
        name: '合格率',
        type: 'line',
        data: qualifiedRates,
        smooth: true,
        areaStyle: {
          opacity: 0.3
        },
        itemStyle: {
          color: '#5470c6'
        }
      }
    ]
  }
  
  trendChart.setOption(option)
}

const initComparisonChart = (data) => {
  if (!comparisonChartRef.value) return
  
  comparisonChart = echarts.init(comparisonChartRef.value)
  
  const option = {
    title: {
      text: '供应商质量指标对比',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    legend: {
      data: ['合格率', '准时率', '服务评分'],
      bottom: 0
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: data.map(item => item.supplierName)
    },
    yAxis: {
      type: 'value',
      name: '分数',
      max: 100
    },
    series: [
      {
        name: '合格率',
        type: 'bar',
        data: data.map(item => item.qualityRate),
        itemStyle: { color: '#5470c6' }
      },
      {
        name: '准时率',
        type: 'bar',
        data: data.map(item => item.ontimeRate),
        itemStyle: { color: '#91cc75' }
      },
      {
        name: '服务评分',
        type: 'bar',
        data: data.map(item => item.serviceScore),
        itemStyle: { color: '#fac858' }
      }
    ]
  }
  
  comparisonChart.setOption(option)
}

const refreshData = async () => {
  await fetchData()
  ElMessage.success('数据已刷新')
}

const fetchData = async () => {
  try {
    const [rankingRes, trendRes, comparisonRes] = await Promise.all([
      getScoreRanking(),
      getQualityTrend(),
      getSupplierComparison()
    ])
    
    initRankingChart(rankingRes.data)
    initTrendChart(trendRes.data)
    initComparisonChart(comparisonRes.data)
  } catch (error) {
    ElMessage.error('获取数据失败')
  }
}

const handleResize = () => {
  rankingChart?.resize()
  trendChart?.resize()
  comparisonChart?.resize()
}

onMounted(async () => {
  await fetchData()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  rankingChart?.dispose()
  trendChart?.dispose()
  comparisonChart?.dispose()
  window.removeEventListener('resize', handleResize)
})
</script>

<style scoped>
.charts-page {
  height: 100%;
}

.chart-card {
  margin-bottom: 20px;
}

.chart {
  width: 100%;
  height: 400px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
