import request from '@/utils/request'

export function examPage(params) {
  return request({ url: '/exam/page', method: 'get', params })
}

export function examAvailable() {
  return request({ url: '/exam/available', method: 'get' })
}

export function getExamById(id) {
  return request({ url: `/exam/${id}`, method: 'get' })
}

export function saveExam(data) {
  return request({ url: '/exam', method: 'post', data })
}

export function deleteExam(id) {
  return request({ url: `/exam/${id}`, method: 'delete' })
}
