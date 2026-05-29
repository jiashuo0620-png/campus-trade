import request from './request'

export function getGoodsList(params) {
  return request.get('/goods', { params })
}

export function getGoodsDetail(id) {
  return request.get(`/goods/${id}`)
}

export function publishGoods(data) {
  return request.post('/goods', data)
}

export function deleteGoods(id) {
  return request.delete(`/goods/${id}`)
}

export function getMyGoods(params) {
  return request.get('/goods/my', { params })
}

export function searchGoods(params) {
  return request.get('/goods/search', { params })
}
