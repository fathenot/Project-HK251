import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { api } from '@/plugins/axios.js'

export const useMainStore = defineStore('main', () => {
  const savedUser = JSON.parse(localStorage.getItem('user') || '{}')

  const role = ref(savedUser.role || '')
  const userName = ref(savedUser.name || '')
  const userEmail = ref(savedUser.email || '')
  const userPhone = ref(savedUser.phone || '')
  const userAddress = ref(savedUser.address || '')
  const hireDate = ref(savedUser.hireDate || '')
  const points = ref(savedUser.points || 0)

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
      hireDate: hireDate.value,
      points: points.value,
    }
    localStorage.setItem('user', JSON.stringify(userData))
    localStorage.setItem('role', userData.role)
  }

  function setUser(payload) {
    role.value = payload.role || ''
    userName.value = payload.name || ''

    if (['admin', 'manager', 'sales', 'warehouse'].includes(role.value)) {
      hireDate.value = payload.hireDate || ''
    } else {
      userEmail.value = payload.email || ''
      userPhone.value = payload.phone || ''
      userAddress.value = payload.address || ''
      points.value = payload.points || 0
    }

    saveToLocalStorage()
  }

  function loadTestUser() {
    const testUser = {
      role: 'warehouse',
      name: 'Lionel Messi',
      email: 'lionelmessi@example.com',
      phone: '0123456789',
      address: '123 Đường ABC, TP.HCM',
      points: 120,
      hireDate: '2022-07-01',
    }
    setUser(testUser)
  }

  async function login(username, password) {
    try {
      // const response = await api.post('/login', { username, password })
      // const data = response.data
      // setUser(data)

      throw new Error('Simulate no backend')
    } catch (error) {
      console.warn('Using test user due to backend not available:', error)

      const testUser = {
        role: username === 'warehouse' ? 'warehouse' :
              username === 'admin' ? 'admin' :
              username === 'manager' ? 'manager' :
              username === 'sales' ? 'sales' :
              username === 'customer' ? 'customer' : 'customer',
        name: username === 'admin' ? 'Nguyễn Văn Admin' :
              username === 'manager' ? 'Trần Thị Quản Lý' :
              username === 'sales' ? 'Trần Thị Bán Hàng' :
              username === 'warehouse' ? 'Phạm Văn Kho' :
              username === 'customer' ? 'Phạm Thị Khách' :
              username || 'Test User',
        email: username === 'sales' ? 'sales@supermarket.com' :
               username === 'customer' ? 'customer@example.com' :
               username + '@supermarket.com',
        phone: '0123456789',
        address: '123 Đường ABC, TP.HCM',
        points: username === 'customer' ? 2450 : 0,
        hireDate: '2023-01-15',
      }

      setUser(testUser)
      return true
    }
  }

  function logout() {
    role.value = ''
    userName.value = ''
    userEmail.value = ''
    userPhone.value = ''
    userAddress.value = ''
    hireDate.value = ''
    points.value = 0
    localStorage.removeItem('user')
  }

  return {
    role,
    userName,
    userEmail,
    userPhone,
    userAddress,
    hireDate,
    points,
    userAvatar,
    setUser,
    login,
    logout,
  }
})
