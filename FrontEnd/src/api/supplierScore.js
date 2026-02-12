import request from '@/utils/request'

export function getScoreList(params) {
  return request({
    url: '/supplier-scores',
    method: 'get',
    params
  })
}

export function getRiskSuppliers() {
  return request({
    url: '/supplier-scores/risk',
    method: 'get'
  })
}

export function getRiskCount() {
  return request({
    url: '/supplier-scores/risk-count',
    method: 'get'
  })
}

export function calculateScore(supplierId, scoreDate) {
  return request({
    url: '/supplier-scores/calculate',
    method: 'post',
    params: { supplierId, scoreDate }
  })
}

export function createScore(data) {
  return request({
    url: '/supplier-scores',
    method: 'post',
    data
  })
}

export function updateScore(id, data) {
  return request({
    url: `/supplier-scores/${id}`,
    method: 'put',
    data
  })
}

export function deleteScore(id) {
  return request({
    url: `/supplier-scores/${id}`,
    method: 'delete'
  })
}
