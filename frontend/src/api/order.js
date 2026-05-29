import request from './request'

export function createOrder(data) {
  return request.post('/order', data)
}

export function getBuyOrders(params) {
  return request.get('/order/buy', { params })
}

export function getSellOrders(params) {
  return request.get('/order/sell', { params })
}

export function updateOrderStatus(id, status) {
  return request.put(`/order/${id}/status`, null, { params: { status } })
}
