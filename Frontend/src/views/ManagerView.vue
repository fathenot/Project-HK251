<script setup>
import { ref, onMounted } from 'vue'
import { api } from '@/plugins/axios'

import {
  mdiChartTimelineVariant,
  mdiReload,
  mdiChartPie,
  mdiCash,
  mdiClipboardList,
  mdiCube,
  mdiAlertCircle,
} from '@mdi/js'

import LineChart from '@/components/Charts/LineChart.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBoxWidget from '@/components/CardBoxWidget.vue'
import CardBox from '@/components/CardBox.vue'
import BaseButton from '@/components/BaseButton.vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'

// Dashboard data
const chartData = ref(null)

const dashboardData = ref({
  doanhThuHomNay: 0,
  donHangHomNay: 0,
  tongTonKho: 0,
  canhBao: 0,
})

// Mock chart data
const getTestChartData = () => ({
  labels: ['T2', 'T3', 'T4', 'T5', 'T6', 'T7', 'CN'],
  datasets: [
    {
      label: 'Doanh thu',
      data: [1200, 1500, 1800, 2000, 1700, 1900, 2200],
      borderColor: '#4ade80',
      fill: false,
      tension: 0.4,
    },
    {
      label: 'Đơn hàng',
      data: [80, 95, 130, 160, 140, 150, 170],
      borderColor: '#60a5fa',
      fill: false,
      tension: 0.4,
    },
  ],
})

// Fetch dashboard API
const fetchDashboard = async () => {
  try {
    const res = await api.get('/manager/warehouse-summary')
    dashboardData.value = res.data
  } catch (error) {
    console.error('Lỗi load dashboard:', error)
    // fallback dữ liệu mẫu
    dashboardData.value = {
      doanhThuHomNay: 3500000,
      donHangHomNay: 45,
      tongTonKho: 540,
      canhBao: 5,
    }
  }
}

// Fetch chart API
const fetchChart = async () => {
  try {
    const res = await api.get('/manager/warehouse-chart')
    chartData.value = res.data
  } catch (e) {
    console.error('Lỗi load chart:', e)
    chartData.value = getTestChartData()
  }
}

onMounted(() => {
  fetchDashboard()
  fetchChart()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiChartTimelineVariant" title="Tổng quan" main />

      <div class="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <CardBoxWidget
          :number="dashboardData.doanhThuHomNay"
          label="Doanh thu hôm nay"
          :icon="mdiCash"
          color="text-green-500"
          :formatNumber="true"
        />

        <CardBoxWidget
          :number="dashboardData.donHangHomNay"
          label="Đơn hàng hôm nay"
          :icon="mdiClipboardList"
          color="text-blue-500"
        />

        <CardBoxWidget
          :number="dashboardData.tongTonKho"
          label="Tổng tồn kho"
          :icon="mdiCube"
          color="text-gray-500"
        />

        <CardBoxWidget
          :number="dashboardData.canhBao"
          label="Cảnh báo tồn kho"
          :icon="mdiAlertCircle"
          color="text-red-500"
        />
      </div>

      <SectionTitleLineWithButton :icon="mdiChartPie" title="Biểu đồ tuần" main>
        <BaseButton :icon="mdiReload" color="whiteDark" @click="fetchChart" />
      </SectionTitleLineWithButton>

      <CardBox class="mb-6">
        <div v-if="chartData">
          <LineChart :data="chartData" class="h-96" />
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
