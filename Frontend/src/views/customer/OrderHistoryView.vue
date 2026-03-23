<script setup>
import { ref, computed, onMounted } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import CustomerNavTabs from '@/components/CustomerNavTabs.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { mdiChevronRight, mdiMedal, mdiCashRegister, mdiGift } from '@mdi/js'
import { useMainStore } from '@/stores/main'

const mainStore = useMainStore()

const orders = ref([
  {
    id: 1,
    orderCode: 'ORD-2025-001',
    date: '24/10/2025 14:30',
    items: [
      { name: 'Sữa tươi Vinamilk', quantity: 2 },
      { name: 'Gạo ST25 5kg', quantity: 1 },
      { name: 'Nước ngọt Coca Cola', quantity: 2 },
    ],
    totalItems: 5,
    total: 247000,
    status: 'delivered',
  },
  {
    id: 2,
    orderCode: 'ORD-2025-002',
    date: '23/10/2025 10:15',
    items: [
      { name: 'Nước ngọt Coca Cola', quantity: 6 },
      { name: 'Bánh mì sandwich', quantity: 3 },
    ],
    totalItems: 9,
    total: 105000,
    status: 'delivering',
  },
  {
    id: 3,
    orderCode: 'ORD-2025-003',
    date: '22/10/2025 16:45',
    items: [
      { name: 'Dầu ăn Simply 1L', quantity: 2 },
      { name: 'Mì gói Hảo Hảo', quantity: 10 },
    ],
    totalItems: 12,
    total: 125000,
    status: 'delivered',
  },
])

const viewDetails = (order) => {
  alert(`Chi tiết đơn hàng: ${order.orderCode}`)
}

onMounted(() => {
  // Fetch orders
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">
          Chào mừng, {{ mainStore.userName || 'Khách hàng' }}!
        </h1>
        <p class="text-gray-600 dark:text-gray-400">Xem lịch sử mua hàng và khuyến mãi của bạn</p>
      </div>

      <!-- Summary Cards -->
      <div class="grid grid-cols-1 gap-6 lg:grid-cols-3 mb-6">
        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Điểm Tích Lũy</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">2,450</h1>
              <p class="text-sm text-yellow-600 dark:text-yellow-400 mt-2">Hạng Vàng</p>
            </div>
            <BaseIcon :path="mdiMedal" size="48" class="text-yellow-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Tổng Đơn Hàng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">47</h1>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2">Lịch sử mua hàng</p>
            </div>
            <BaseIcon :path="mdiCashRegister" size="48" class="text-blue-500" />
          </div>
        </CardBox>

        <CardBox class="relative">
          <div class="flex justify-between items-start">
            <div>
              <h3 class="text-gray-500 dark:text-gray-400 text-sm mb-2">Voucher Khả Dụng</h3>
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">5</h1>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2">Ưu đãi khuyến mãi</p>
            </div>
            <BaseIcon :path="mdiGift" size="48" class="text-purple-500" />
          </div>
        </CardBox>
      </div>

      <CustomerNavTabs />

      <CardBox>
        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">Lịch Sử Đơn Hàng</h2>
          <p class="text-gray-600 dark:text-gray-400">Theo dõi các đơn hàng đã mua tại siêu thị</p>
        </div>

        <div class="space-y-4">
          <div
            v-for="order in orders"
            :key="order.id"
            class="p-6 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-slate-800"
          >
            <div class="mb-4">
              <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-1">
                {{ order.orderCode }}
              </h3>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ order.date }}</p>
            </div>

            <div class="mb-4">
              <div class="space-y-2">
                <div
                  v-for="(item, index) in order.items"
                  :key="index"
                  class="text-sm text-gray-700 dark:text-gray-300"
                >
                  • {{ item.name }} x{{ item.quantity }}
                </div>
              </div>
              <p class="text-sm text-gray-600 dark:text-gray-400 mt-2">
                {{ order.totalItems }} sản phẩm
              </p>
            </div>

            <div class="flex justify-between items-center pt-4 border-t border-gray-200 dark:border-gray-700">
              <span class="text-xl font-bold text-blue-600 dark:text-blue-400">
                {{ order.total.toLocaleString('vi-VN') }}₫
              </span>
              <BaseButton
                label="Chi tiết"
                color="lightDark"
                :icon="mdiChevronRight"
                @click="viewDetails(order)"
              />
            </div>
          </div>
        </div>

        <div v-if="orders.length === 0" class="text-center py-12">
          <p class="text-gray-500 dark:text-gray-400">Chưa có đơn hàng nào</p>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
