import request from '@/utils/request'

export function getUserInfo() {
  return request({ url: '/user/info', method: 'get' })
}

export function updateUserInfo(data) {
  return request({ url: '/user/info', method: 'put', data })
}

export function updatePassword(oldPassword, newPassword) {
  return request({
    url: '/user/password',
    method: 'put',
    params: { oldPassword, newPassword },
  })
}

export function userPage(params) {
  return request({ url: '/user/page', method: 'get', params })
}

export function getUserById(id) {
  return request({ url: `/user/${id}`, method: 'get' })
}

export function saveUser(data) {
  return request({ url: '/user', method: 'post', data })
}

export function deleteUser(id) {
  return request({ url: `/user/${id}`, method: 'delete' })
}

export function resetUserPassword(id, password) {
  return request({ url: `/user/${id}/reset-password`, method: 'put', params: { password } })
}
