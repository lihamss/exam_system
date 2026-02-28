import request from '@/utils/request'

export function questionPage(params) {
  return request({ url: '/question/page', method: 'get', params })
}

export function getQuestionById(id) {
  return request({ url: `/question/${id}`, method: 'get' })
}

export function saveQuestion(data) {
  return request({ url: '/question', method: 'post', data })
}

export function deleteQuestion(id) {
  return request({ url: `/question/${id}`, method: 'delete' })
}
