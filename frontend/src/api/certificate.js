import request from '@/utils/request'

export function getCertificateDetail(id) {
  return request({
    url: `/certificate/${id}`,
    method: 'get'
  })
}

export function getCertificateByNo(certNo) {
  return request({
    url: `/certificate/no/${certNo}`,
    method: 'get'
  })
}

export function getCertificatePage(params) {
  return request({
    url: '/certificate/page',
    method: 'get',
    params
  })
}

export function invalidCertificate(data) {
  return request({
    url: '/certificate/invalid',
    method: 'post',
    data
  })
}

export function getValidCountByWard(wardId) {
  return request({
    url: `/certificate/count/${wardId}`,
    method: 'get'
  })
}
