import request from '@/utils/request'

export function getAllRules() {
  return request({
    url: '/score-rules',
    method: 'get'
  })
}

export function getRuleById(id) {
  return request({
    url: `/score-rules/${id}`,
    method: 'get'
  })
}

export function createRule(data) {
  return request({
    url: '/score-rules',
    method: 'post',
    data
  })
}

export function updateRule(id, data) {
  return request({
    url: `/score-rules/${id}`,
    method: 'put',
    data
  })
}

export function updateRuleWeight(id, weight) {
  return request({
    url: `/score-rules/${id}/weight`,
    method: 'put',
    params: { weight }
  })
}

export function deleteRule(id) {
  return request({
    url: `/score-rules/${id}`,
    method: 'delete'
  })
}
