import request from '@/utils/request'

export function getCertificateDetail(id) {
  return request({ url: `/certificate/${id}`, method: 'get' })
}

export function getCertificateByNo(certNo) {
  return request({ url: `/certificate/no/${certNo}`, method: 'get' })
}

export function getCertificatePage(params) {
  return request({ url: '/certificate/page', method: 'get', params })
}

export function invalidCertificate(data) {
  return request({ url: '/certificate/invalid', method: 'post', data })
}

export function getValidCountByWard(wardId) {
  return request({ url: `/certificate/count/${wardId}`, method: 'get' })
}

export function transferCompanion(data) {
  return request({ url: '/certificate/transfer', method: 'post', data })
}

export function temporaryLeave(data) {
  return request({ url: '/certificate/leave', method: 'post', data })
}

export function returnFromLeave(data) {
  return request({ url: '/certificate/return', method: 'post', data })
}

export function getTransferPage(params) {
  return request({ url: '/certificate/transfer/page', method: 'get', params })
}

export function getTransferDetail(id) {
  return request({ url: `/certificate/transfer/${id}`, method: 'get' })
}

export function getLeavePage(params) {
  return request({ url: '/certificate/leave/page', method: 'get', params })
}

export function getLeaveDetail(id) {
  return request({ url: `/certificate/leave/${id}`, method: 'get' })
}
