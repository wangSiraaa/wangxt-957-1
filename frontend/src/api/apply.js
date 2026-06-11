import request from '@/utils/request'

export function submitApply(data) {
  return request({
    url: '/apply/submit',
    method: 'post',
    data
  })
}

export function getApplyDetail(id) {
  return request({
    url: `/apply/${id}`,
    method: 'get'
  })
}

export function getApplyPage(params) {
  return request({
    url: '/apply/page',
    method: 'get',
    params
  })
}
