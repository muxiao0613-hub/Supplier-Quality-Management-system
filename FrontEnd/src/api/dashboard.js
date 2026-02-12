import request from '@/utils/request'

export function getDashboardStats() {
  return request({
    url: '/dashboard/stats',
    method: 'get'
  })
}

export function getScoreRanking() {
  return request({
    url: '/dashboard/score-ranking',
    method: 'get'
  })
}

export function getQualityTrend() {
  return request({
    url: '/dashboard/quality-trend',
    method: 'get'
  })
}

export function getSupplierComparison() {
  return request({
    url: '/dashboard/supplier-comparison',
    method: 'get'
  })
}
