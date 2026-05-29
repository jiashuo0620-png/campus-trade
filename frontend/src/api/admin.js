import request from './request'

export function adminLogin(username, password) {
  return request.post('/admin/login', null, { params: { username, password } })
}

export function getUserList(params) {
  return request.get('/admin/users', { params })
}

export function updateUserStatus(id, status) {
  return request.put(`/admin/users/${id}/status`, null, { params: { status } })
}

export function auditGoods(id, status) {
  return request.put(`/admin/goods/${id}/audit`, null, { params: { status } })
}

export function getPendingGoods(params) {
  return request.get('/admin/goods/pending', { params })
}
