import request from '@/utils/request'

export function getPatientPage(params) {
  return request({
    url: '/patient/page',
    method: 'get',
    params
  })
}

export function getPatientDetail(id) {
  return request({
    url: `/patient/${id}`,
    method: 'get'
  })
}
