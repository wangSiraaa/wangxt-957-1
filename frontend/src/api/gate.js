import request from '@/utils/request'

export function checkCertificate(certNoOrIdCard) {
  return request({
    url: `/gate/check/${certNoOrIdCard}`,
    method: 'get'
  })
}

export function gateCheck(data) {
  return request({
    url: '/gate/check',
    method: 'post',
    data
  })
}

export function getGateRecords(params) {
  return request({
    url: '/gate/records',
    method: 'get',
    params
  })
}
