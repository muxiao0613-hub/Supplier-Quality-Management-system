import request from '@/utils/request'

export function getRecordList(params) {
  return request({
    url: '/purchase-records',
    method: 'get',
    params
  })
}

export function getRecordById(id) {
  return request({
    url: `/purchase-records/${id}`,
    method: 'get'
  })
}

export function createRecord(data) {
  return request({
    url: '/purchase-records',
    method: 'post',
    data
  })
}

export function updateRecord(id, data) {
  return request({
    url: `/purchase-records/${id}`,
    method: 'put',
    data
  })
}

export function deleteRecord(id) {
  return request({
    url: `/purchase-records/${id}`,
    method: 'delete'
  })
}
