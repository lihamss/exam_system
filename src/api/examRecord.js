import request from '@/utils/request'

export function startExam(examId) {
  return request({ url: '/exam-record/start', method: 'post', params: { examId } })
}

export function submitExam(recordId, answers) {
  return request({
    url: `/exam-record/${recordId}/submit`,
    method: 'post',
    data: answers || {},
  })
}

export function gradeRecord(recordId, scores) {
  return request({
    url: `/exam-record/${recordId}/grade`,
    method: 'put',
    data: scores || {},
  })
}

export function myRecords() {
  return request({ url: '/exam-record/my', method: 'get' })
}

export function recordPageByExam(examId, params) {
  return request({ url: `/exam-record/exam/${examId}`, method: 'get', params })
}

export function getRecordById(id) {
  return request({ url: `/exam-record/${id}`, method: 'get' })
}

export function getRecordAnswers(id) {
  return request({ url: `/exam-record/${id}/answers`, method: 'get' })
}
