import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login',
  },

  // Login
  {
    path: '/login',
    name: 'login',
    meta: { title: 'Login' },
    component: () => import('@/views/LoginView.vue'),
  },

  // // Register
  // {
  //   path: '/register',
  //   name: 'register',
  //   meta: { title: 'Register' },
  //   component: () => import('@/views/RegisterView.vue'),
  // },

  // // Manager dashboard
  // {
  //   path: '/manager-dashboard',
  //   name: 'manager-dashboard',
  //   meta: { title: 'Trang chính (Quản lý)', requiresAuth: true },
  //   component: () => import('@/views/ManagerView.vue'),
  // },

  // // Warehouse dashboard
  // {
  //   path: '/warehouse-dashboard',
  //   name: 'warehouse-dashboard',
  //   meta: { title: 'Trang chính (Kho)', requiresAuth: true },
  //   component: () => import('@/views/WarehouseView.vue'),
  // },

  // // Profile
  // {
  //   path: '/profile',
  //   name: 'profile',
  //   meta: { title: 'Profile', requiresAuth: true },
  //   component: () => import('@/views/ProfileView.vue'),
  // },

  // // Create receipt
  // {
  //   path: '/create-receipt',
  //   name: 'create-receipt',
  //   meta: { title: 'Tạo phiếu nhập/xuất', requiresAuth: true },
  //   component: () => import('@/views/CreateReceiptView.vue'),
  // },

  // // Search receipt
  // {
  //   path: '/search-receipt',
  //   name: 'search-receipt',
  //   meta: { title: 'Tra cứu phiếu', requiresAuth: true },
  //   component: () => import('@/views/ReceiptView.vue'),
  // },

  // // Search product
  // {
  //   path: '/search-product',
  //   name: 'search-product',
  //   meta: { title: 'Tra cứu lô hàng/Hàng hóa', requiresAuth: true },
  //   component: () => import('@/views/ProductView.vue'),
  // },

  // // Search employee
  // {
  //   path: '/search-employee',
  //   name: 'search-employee',
  //   meta: { title: 'Tra cứu nhân viên', requiresAuth: true },
  //   component: () => import('@/views/EmployeeView.vue'),
  // },

  // // Search customer
  // {
  //   path: '/search-customer',
  //   name: 'search-customer',
  //   meta: { title: 'Tra cứu khách hàng', requiresAuth: true },
  //   component: () => import('@/views/CustomerView.vue'),
  // },

  // // Transfer
  // {
  //   path: '/transfer',
  //   name: 'transfer',
  //   meta: { title: 'Chuyển hàng', requiresAuth: true },
  //   component: () => import('@/views/TransferView.vue'),
  // },

  // // Warehouses
  // {
  //   path: '/warehouses',
  //   name: 'warehouses',
  //   meta: { title: 'Quản lý kho', requiresAuth: true },
  //   component: () => import('@/views/WarehousesView.vue'),
  // },

  // // Store info
  // {
  //   path: '/store-info',
  //   name: 'store-info',
  //   meta: { title: 'Thông tin gian hàng', requiresAuth: true },
  //   component: () => import('@/views/StoreView.vue'),
  // },

  // // Discount management
  // {
  //   path: '/discount-management',
  //   name: 'discount-management',
  //   meta: { title: 'Quản lý ưu đãi giảm giá', requiresAuth: true },
  //   component: () => import('@/views/DiscountView.vue'),
  // },

  // // Text (test/demo)
  // {
  //   path: '/text',
  //   name: 'text',
  //   meta: { title: 'Text' },
  //   component: () => import('@/views/TextView.vue'),
  // },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    return savedPosition || { top: 0 }
  },
})

// Auth guard
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('role')

  if (to.meta.requiresAuth && !isLoggedIn) {
    next({ path: '/login' })
  } else {
    next()
  }
})

export default router
