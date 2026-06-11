import request from '@/utils/request'

export function getWardList() {
  return request({
    url: '/ward/list',
    method: 'get'
  })
}

export function getWardDetail(id) {
  return request({
    url: `/ward/${id}`,
    method: 'get'
  })
}
