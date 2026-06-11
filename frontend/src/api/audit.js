import request from '@/utils/request'

export function auditApply(data) {
  return request({
    url: '/audit/audit',
    method: 'post',
    data
  })
}

export function getPendingList(params) {
  return request({
    url: '/audit/pending',
    method: 'get',
    params
  })
}
