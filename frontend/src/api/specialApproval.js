import request from '@/utils/request'

export function submitSpecialApproval(data) {
  return request({ url: '/special-approval/submit', method: 'post', data })
}

export function auditSpecialApproval(data) {
  return request({ url: '/special-approval/audit', method: 'post', data })
}

export function getSpecialApprovalPage(params) {
  return request({ url: '/special-approval/page', method: 'get', params })
}

export function getSpecialApprovalDetail(id) {
  return request({ url: `/special-approval/${id}`, method: 'get' })
}
