import request from '@/utils/request'

export function noticeLatest(limit = 5) {
  return request({ url: '/notice/latest', method: 'get', params: { limit } })
}

export function noticePage(params) {
  return request({ url: '/notice/page', method: 'get', params })
}

export function getNoticeById(id) {
  return request({ url: `/notice/${id}`, method: 'get' })
}

export function saveNotice(data) {
  return request({ url: '/notice', method: 'post', data })
}

export function deleteNotice(id) {
  return request({ url: `/notice/${id}`, method: 'delete' })
}
