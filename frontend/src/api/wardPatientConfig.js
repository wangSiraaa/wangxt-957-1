import request from '@/utils/request'

export function adjustAccompanyCount(data) {
  return request({ url: '/ward-patient-config/adjust', method: 'post', data })
}

export function getConfigByPatientId(patientId) {
  return request({ url: `/ward-patient-config/patient/${patientId}`, method: 'get' })
}

export function getConfigPage(params) {
  return request({ url: '/ward-patient-config/page', method: 'get', params })
}
