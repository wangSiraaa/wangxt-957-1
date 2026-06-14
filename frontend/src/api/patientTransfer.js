import request from '@/utils/request'

export function transferWard(data) {
  return request({ url: '/patient/transfer', method: 'post', data })
}

export function getTransferPage(params) {
  return request({ url: '/patient/transfer/page', method: 'get', params })
}

export function getTransferDetail(id) {
  return request({ url: `/patient/transfer/${id}`, method: 'get' })
}
