<script setup>
import { ref } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CustomerNavTabs from '@/components/CustomerNavTabs.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { useMainStore } from '@/stores/main'
import {
  mdiMedal,
  mdiCashRegister,
  mdiGift,
  mdiTrendingUp,
  mdiLightningBolt,
  mdiStar,
} from '@mdi/js'

const mainStore = useMainStore()

const points = ref(2450)
const currentRank = ref('Silver')
const nextRank = ref('Gold')
const pointsToNextRank = ref(50)
const currentProgress = ref(90) // 90% progress

const benefits = ref([
  {
    id: 1,
    icon: mdiTrendingUp,
    iconColor: 'text-blue-500',
    title: 'Tích điểm tăng',
    value: '+15%',
  },
  {
    id: 2,
    icon: mdiGift,
    iconColor: 'text-purple-500',
    title: 'Voucher tháng',
    value: '3 voucher',
  },
  {
    id: 3,
    icon: mdiLightningBolt,
    iconColor: 'text-green-500',
    title: 'Ưu tiên hỗ trợ',
    value: '24/7',
  },
])

const getRankLabel = (rank) => {
  const ranks = {
    Gold: 'Hạng Vàng',
    Silver: 'Hạng Bạc',
    Bronze: 'Hạng Đồng',
  }
  return ranks[rank] || rank
}
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
              <h1 class="text-3xl font-bold text-gray-800 dark:text-white">
                {{ points.toLocaleString('vi-VN') }}
              </h1>
              <p class="text-sm text-yellow-600 dark:text-yellow-400 mt-2">
                {{ getRankLabel(currentRank) }}
              </p>
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

      <!-- Points Progress Section -->
      <CardBox class="mb-6 bg-gradient-to-r from-blue-500 to-purple-600 text-white">
        <div class="flex justify-between items-start mb-4">
          <div>
            <h3 class="text-white/80 text-sm mb-2">Điểm tích lũy</h3>
            <h1 class="text-4xl font-bold text-white mb-2">
              {{ points.toLocaleString('vi-VN') }} điểm
            </h1>
            <p class="text-white/90">Tiến độ lên {{ getRankLabel(nextRank) }}</p>
          </div>
          <div class="flex items-center space-x-2">
            <BaseIcon :path="mdiStar" size="24" class="text-white" />
            <span class="text-white font-medium">{{ getRankLabel(currentRank) }}</span>
          </div>
        </div>

        <div class="mt-4">
          <div class="flex justify-between items-center mb-2">
            <span class="text-white/90 text-sm">Tiến độ</span>
            <span class="text-white font-medium">{{ pointsToNextRank }} điểm nữa</span>
          </div>
          <div class="w-full bg-white/20 rounded-full h-3">
            <div
              class="bg-white rounded-full h-3 transition-all duration-300"
              :style="{ width: currentProgress + '%' }"
            ></div>
          </div>
        </div>
      </CardBox>

      <!-- Membership Benefits -->
      <CardBox>
        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">
            Quyền Lợi {{ getRankLabel(currentRank) }}
          </h2>
          <p class="text-gray-600 dark:text-gray-400">Ưu đãi dành riêng cho bạn</p>
        </div>

        <div class="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div
            v-for="benefit in benefits"
            :key="benefit.id"
            class="p-6 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-slate-800 text-center"
          >
            <div class="flex justify-center mb-4">
              <div class="p-3 rounded-full bg-gray-100 dark:bg-slate-700">
                <BaseIcon :path="benefit.icon" size="32" :class="benefit.iconColor" />
              </div>
            </div>
            <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-2">
              {{ benefit.title }}
            </h3>
            <p class="text-2xl font-bold text-blue-600 dark:text-blue-400">{{ benefit.value }}</p>
          </div>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>

