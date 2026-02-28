import request from '@/utils/request'

export function paperPage(params) {
  return request({ url: '/paper/page', method: 'get', params })
}

export function getPaperById(id) {
  return request({ url: `/paper/${id}`, method: 'get' })
}

export function getPaperQuestions(id) {
  return request({ url: `/paper/${id}/questions`, method: 'get' })
}

export function savePaper(data) {
  return request({ url: '/paper', method: 'post', data })
}

export function deletePaper(id) {
  return request({ url: `/paper/${id}`, method: 'delete' })
}
