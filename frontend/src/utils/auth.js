const TOKEN_KEY = 'campus_trade_token'
const USER_KEY = 'campus_trade_user'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getUser() {
  const raw = localStorage.getItem(USER_KEY)
  return raw ? JSON.parse(raw) : null
}

export function setUser(user) {
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function removeUser() {
  localStorage.removeItem(USER_KEY)
}

export function isLoggedIn() {
  return !!getToken()
}

export function isAdmin() {
  const user = getUser()
  return user && user.role === 'admin'
}

export function logout() {
  removeToken()
  removeUser()
}
