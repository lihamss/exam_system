import request from '@/utils/request'

export function loginApi(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: { username: data.username, password: data.password },
  })
}

export function registerApi(data) {
  return request({
    url: '/auth/register',
    method: 'post',
    data: { username: data.username, password: data.password },
  })
}
