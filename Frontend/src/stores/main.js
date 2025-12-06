import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as jwtDecode from 'jwt-decode'
import { api } from '@/plugins/axios.js'

export const useMainStore = defineStore('main', () => {
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}')

  const role = ref(savedUser.role || '')
  const userName = ref(savedUser.name || '')
  const userEmail = ref(savedUser.email || '')
  const userPhone = ref(savedUser.phone || '')
  const userAddress = ref(savedUser.address || '')
  const hiredDate = ref(savedUser.hiredDate || '')
  const points = ref(savedUser.points || 0)

  const accessToken = ref(localStorage.getItem('accessToken') || '')
  const refreshToken = ref(localStorage.getItem('refreshToken') || '')

  const userAvatar = computed(() => {
    const seed = userEmail.value || userName.value || 'default'
    return `https://api.dicebear.com/7.x/avataaars/svg?seed=${seed.replace(/[^a-z0-9]+/gi, '-')}`
  })

  function saveToLocalStorage() {
    const userData = {
      role: role.value,
      name: userName.value,
      email: userEmail.value,
      phone: userPhone.value,
      address: userAddress.value,
      hiredDate: hiredDate.value,
      points: points.value,
    }
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('accessToken', accessToken.value)
    localStorage.setItem('refreshToken', refreshToken.value)
  }

  function setUser(data) {
    const user = data.user

    userName.value = `${user.first_name} ${user.last_name}`.trim()
    userEmail.value = user.email
    userPhone.value = user.phone
    userAddress.value = user.address
    hiredDate.value = user.hired_at
    points.value = user.loyalty_points || 0

    const usernameLower = userName.value.trim().toLowerCase()

    if (usernameLower === 'admin') {
      role.value = 'admin'
    } else if (usernameLower === 'manager') {
      role.value = 'manager'
    } else if (usernameLower === 'warehouse') {
      role.value = 'warehouse'
    } else if (usernameLower === 'sales') {
      role.value = 'sales'
    } else {
      role.value = (user.employee_type || 'customer').toLowerCase()
    }

    accessToken.value = data.accessToken
    refreshToken.value = data.refreshToken

    saveToLocalStorage()
  }

  async function login(identifier, password) {
    const username = identifier.trim().toLowerCase()

    const fakeUser = {
      first_name: identifier,
      last_name: '',
      email: `${username}@demo.com`,
      phone: '0123456789',
      address: 'Demo Address',
      hired_at: '2024-01-01',
      loyalty_points: 999,
      employee_type: null,
    }

    const fakeData = {
      user: fakeUser,
      accessToken: 'demo-access-token',
      refreshToken: 'demo-refresh-token',
    }

    setUser(fakeData)
    return true
  }


  function logout() {
    role.value = ''
    userName.value = ''
    userEmail.value = ''
    userPhone.value = ''
    userAddress.value = ''
    points.value = 0
    accessToken.value = ''
    refreshToken.value = ''

    localStorage.removeItem('user')
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
  }

  function isAccessTokenExpired() {
    if (!accessToken.value) return true
    try {
      const decoded = jwtDecode(accessToken.value)
      return Date.now() >= decoded.exp * 1000
    } catch {
      return true
    }
  }

  async function refreshAccessToken() {
    if (!refreshToken.value) throw new Error('No refresh token available')
    try {
      const res = await api.post('/auth/refresh', {
        refreshToken: refreshToken.value,
      })
      if (res.data?.success) {
        accessToken.value = res.data.data.accessToken
        refreshToken.value = res.data.data.refreshToken
        saveToLocalStorage()
        return true
      }
      return false
    } catch (err) {
      console.error('Refresh token failed', err)
      logout()
      return false
    }
  }

  return {
    role,
    userName,
    userEmail,
    userPhone,
    userAddress,
    hiredDate,
    points,
    userAvatar,
    setUser,
    login,
    logout,
    isAccessTokenExpired,
    refreshAccessToken,
  }
})
