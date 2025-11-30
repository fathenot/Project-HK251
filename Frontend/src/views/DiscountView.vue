<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  mdiSale,
  mdiPlus,
  mdiMagnify,
  mdiCalendar,
  mdiCash,
  mdiPercent,
  mdiAccountGroup,
  mdiPencil,
  mdiDelete,
  mdiHistory,
} from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import CardBoxModalNew from '@/components/CardBoxModalNew.vue'
import SectionTitleLineWithButton from '@/components/SectionTitleLineWithButton.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import BaseButton from '@/components/BaseButton.vue'
import { api } from '@/plugins/axios'

const sampleDiscounts = [
  {
    id: 'DC001',
    name: 'Giảm giá mùa hè',
    type: 'percentage',
    value: 20,
    minOrder: 500000,
    maxDiscount: 200000,
    startDate: '2024-06-01',
    endDate: '2024-08-31',
    status: 'active',
    usedCount: 45,
    totalOrders: 120,
    createdAt: '2024-05-15',
  },
  {
    id: 'DC002',
    name: 'Khuyến mãi đặc biệt',
    type: 'fixed',
    value: 50000,
    minOrder: 300000,
    maxDiscount: 50000,
    startDate: '2024-07-01',
    endDate: '2024-07-31',
    status: 'active',
    usedCount: 23,
    totalOrders: 89,
    createdAt: '2024-06-20',
  },
  {
    id: 'DC003',
    name: 'Giảm giá cuối năm',
    type: 'percentage',
    value: 15,
    minOrder: 1000000,
    maxDiscount: 300000,
    startDate: '2023-12-01',
    endDate: '2023-12-31',
    status: 'expired',
    usedCount: 78,
    totalOrders: 156,
    createdAt: '2023-11-25',
  },
  {
    id: 'DC004',
    name: 'Ưu đãi thành viên',
    type: 'fixed',
    value: 100000,
    minOrder: 800000,
    maxDiscount: 100000,
    startDate: '2024-09-01',
    endDate: '2024-09-30',
    status: 'upcoming',
    usedCount: 0,
    totalOrders: 0,
    createdAt: '2024-08-15',
  },
]

const sampleHistory = [
  {
    id: 'H001',
    discountId: 'DC001',
    discountName: 'Giảm giá mùa hè',
    orderId: 'ORD001',
    customer: 'Nguyễn Văn A',
    amount: 750000,
    discountAmount: 150000,
    date: '2024-06-15 14:30',
  },
  {
    id: 'H002',
    discountId: 'DC001',
    discountName: 'Giảm giá mùa hè',
    orderId: 'ORD002',
    customer: 'Trần Thị B',
    amount: 600000,
    discountAmount: 120000,
    date: '2024-06-16 09:15',
  },
  {
    id: 'H003',
    discountId: 'DC002',
    discountName: 'Khuyến mãi đặc biệt',
    orderId: 'ORD003',
    customer: 'Lê Văn C',
    amount: 450000,
    discountAmount: 50000,
    date: '2024-07-05 16:45',
  },
]

const searchQuery = ref('')
const discounts = ref([])
const history = ref([])
const modalActive = ref(false)
const historyModalActive = ref(false)
const addModalActive = ref(false)
const selectedDiscount = ref(null)
const loading = ref(false)

const newDiscount = ref({
  name: '',
  type: 'percentage',
  value: 0,
  minOrder: 0,
  maxDiscount: 0,
  startDate: '',
  endDate: '',
})
window.debugDiscount = newDiscount
const filteredDiscounts = computed(() =>
  discounts.value.filter(
    (d) =>
      d.id.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      d.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      d.status.toLowerCase().includes(searchQuery.value.toLowerCase()),
  ),
)

const fetchDiscounts = async () => {
  loading.value = true

  try {
    const res = await api.get('/manager/discounts')
    discounts.value = res.data?.discounts || []
    history.value = res.data?.history || []
  } catch (error) {
    console.error('Lỗi tải ưu đãi:', error)

    // fallback dữ liệu mẫu
    discounts.value = sampleDiscounts
    history.value = sampleHistory
  }

  loading.value = false
}

const getStatusColor = (status) => {
  const colors = {
    active: 'bg-green-100 text-green-800 border-green-200',
    expired: 'bg-red-100 text-red-800 border-red-200',
    upcoming: 'bg-blue-100 text-blue-800 border-blue-200',
    inactive: 'bg-gray-100 text-gray-800 border-gray-200',
  }
  return colors[status] || colors.inactive
}

const getStatusText = (status) => {
  const texts = {
    active: 'Đang hoạt động',
    expired: 'Đã hết hạn',
    upcoming: 'Sắp diễn ra',
    inactive: 'Ngừng hoạt động',
  }
  return texts[status] || texts.inactive
}

const formatCurrency = (amount) => {
  return new Intl.NumberFormat('vi-VN', {
    style: 'currency',
    currency: 'VND',
  }).format(amount)
}

const formatDiscountValue = (discount) => {
  if (discount.type === 'percentage') {
    return `${discount.value}%`
  }
  return formatCurrency(discount.value)
}

const viewDiscount = (discount) => {
  selectedDiscount.value = discount
  modalActive.value = true
}

const viewHistory = (discount) => {
  selectedDiscount.value = discount
  historyModalActive.value = true
}

const resetForm = () => {
  newDiscount.value = {
    name: '',
    type: 'percentage',
    value: 0,
    minOrder: 0,
    maxDiscount: 0,
    startDate: '',
    endDate: '',
  }
}

const addDiscount = () => {
  resetForm()
  addModalActive.value = true
}

const submitDiscount = async () => {
  try {
    newDiscount.value.type = newDiscount.value.type.id
    //console.log(newDiscount.value)
    const res = await api.post('/manager/discounts', newDiscount.value)
    const created = res.data?.discount

    if (created) {
      discounts.value.unshift(created)
    } else {
      // fallback tự tạo ID nếu API không trả về
      const newId = `DC${String(discounts.value.length + 1).padStart(3, '0')}`
      const discount = {
        id: newId,
        ...newDiscount.value,
        status: 'upcoming',
        usedCount: 0,
        totalOrders: 0,
        createdAt: new Date().toISOString().split('T')[0],
      }
      discounts.value.unshift(discount)
    }

    addModalActive.value = false
  } catch (error) {
    console.error('Lỗi thêm ưu đãi:', error)

    const newId = `DC${String(discounts.value.length + 1).padStart(3, '0')}`
    const discount = {
      id: newId,
      ...newDiscount.value,
      status: 'upcoming',
      usedCount: 0,
      totalOrders: 0,
      createdAt: new Date().toISOString().split('T')[0],
    }
    discounts.value.unshift(discount)

    addModalActive.value = false
  }
}

const deleteDiscount = async (discount) => {
  if (!confirm(`Bạn có chắc muốn xóa ưu đãi "${discount.name}"?`)) return

  try {
    await api.post(`/manager/delete-discount/${discount.id}`)

    discounts.value = discounts.value.filter((d) => d.id !== discount.id)
  } catch (error) {
    console.error('Lỗi xóa ưu đãi:', error)

    discounts.value = discounts.value.filter((d) => d.id !== discount.id)
  }
}

onMounted(fetchDiscounts)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithButton :icon="mdiSale" title="Quản lý ưu đãi giảm giá" main>
        <button
          @click="addDiscount"
          class="flex items-center gap-2 rounded-lg bg-blue-600 px-4 py-2 text-sm font-medium text-white shadow-sm transition-all duration-200 hover:bg-blue-700 hover:shadow-md"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            class="h-5 w-5"
            viewBox="0 0 24 24"
            fill="currentColor"
          >
            <path :d="mdiPlus" />
          </svg>
          Thêm ưu đãi
        </button>
      </SectionTitleLineWithButton>

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm ưu đãi</h3>
            <p class="text-sm text-gray-600">Tìm kiếm theo mã ưu đãi, tên ưu đãi hoặc trạng thái</p>
          </div>
          <div class="w-full md:w-96">
            <div class="relative">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <svg
                  xmlns="http://www.w3.org/2000/svg"
                  class="h-5 w-5 text-gray-400"
                  viewBox="0 0 24 24"
                  fill="currentColor"
                >
                  <path :d="mdiMagnify" />
                </svg>
              </div>
              <FormControl
                v-model="searchQuery"
                placeholder="Nhập mã, tên ưu đãi hoặc trạng thái..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Discounts Card -->
      <CardBox class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <!-- Header -->
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 class="text-lg font-semibold text-gray-800">Danh sách ưu đãi</h3>
              <p class="mt-1 text-sm text-gray-600">
                Tìm thấy {{ filteredDiscounts.length }} ưu đãi
                <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
              </p>
            </div>
          </div>
        </div>

        <!-- Table -->
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Mã ưu đãi
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Tên ưu đãi
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Giá trị
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Thời gian
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Sử dụng
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Trạng thái
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="discount in filteredDiscounts"
                :key="discount.id"
                class="transition-colors duration-200 hover:bg-gray-50"
              >
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div
                      class="flex h-10 w-10 items-center justify-center rounded-lg bg-orange-100"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-orange-600"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiSale" />
                      </svg>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">{{ discount.id }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm font-medium text-gray-900">{{ discount.name }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="flex items-center gap-2">
                    <span class="text-lg font-bold text-green-600">
                      {{ formatDiscountValue(discount) }}
                    </span>
                    <span class="text-xs text-gray-500">
                      (Đơn tối thiểu: {{ formatCurrency(discount.minOrder) }})
                    </span>
                  </div>
                </td>
                <td class="px-6 py-4 text-sm whitespace-nowrap text-gray-700">
                  <div class="flex items-center">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="mr-2 h-4 w-4 text-gray-500"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path :d="mdiCalendar" />
                    </svg>
                    {{ discount.startDate }} - {{ discount.endDate }}
                  </div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-gray-900">
                    {{ discount.usedCount }}/{{ discount.totalOrders }} đơn
                  </div>
                  <div class="mt-1 h-2 w-full rounded-full bg-gray-200">
                    <div
                      class="h-2 rounded-full bg-blue-600"
                      :style="{
                        width:
                          discount.totalOrders > 0
                            ? `${(discount.usedCount / discount.totalOrders) * 100}%`
                            : '0%',
                      }"
                    ></div>
                  </div>
                </td>
                <td class="px-6 py-4">
                  <span
                    :class="[
                      'inline-flex items-center rounded-full border px-3 py-1 text-xs font-medium',
                      getStatusColor(discount.status),
                    ]"
                  >
                    {{ getStatusText(discount.status) }}
                  </span>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center gap-2">
                    <button
                      @click="viewDiscount(discount)"
                      class="rounded-lg bg-blue-100 p-2 text-blue-600 transition-colors hover:bg-blue-200"
                      title="Xem chi tiết"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiPencil" />
                      </svg>
                    </button>
                    <button
                      @click="viewHistory(discount)"
                      class="rounded-lg bg-green-100 p-2 text-green-600 transition-colors hover:bg-green-200"
                      title="Lịch sử sử dụng"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiHistory" />
                      </svg>
                    </button>
                    <button
                      @click="deleteDiscount(discount)"
                      class="rounded-lg bg-red-100 p-2 text-red-600 transition-colors hover:bg-red-200"
                      title="Xóa ưu đãi"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiDelete" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>

              <!-- Empty state -->
              <tr v-if="filteredDiscounts.length === 0 && !loading">
                <td colspan="7" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <div class="rounded-full bg-gray-100 p-4">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-8 w-8 text-gray-400"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiSale" />
                      </svg>
                    </div>
                    <h3 class="mt-4 text-lg font-medium text-gray-900">Không tìm thấy ưu đãi</h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery"
                        >Không có ưu đãi nào phù hợp với "{{ searchQuery }}"</span
                      >
                      <span v-else>Không có ưu đãi nào trong hệ thống</span>
                    </p>
                  </div>
                </td>
              </tr>

              <!-- Loading state -->
              <tr v-if="loading">
                <td colspan="7" class="px-6 py-12 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <div
                      class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
                    ></div>
                    <p class="mt-4 text-sm text-gray-600">Đang tải danh sách ưu đãi...</p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Discount Detail Modal -->
      <CardBoxModal
        v-model="modalActive"
        title="Chi tiết ưu đãi"
        button-label="Đóng"
        :has-button="true"
      >
        <div v-if="selectedDiscount" class="space-y-6">
          <!-- Header -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">{{ selectedDiscount.name }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                {{ selectedDiscount.id }} • Tạo ngày {{ selectedDiscount.createdAt }}
              </p>
            </div>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-orange-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-orange-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiSale" />
              </svg>
            </div>
          </div>

          <!-- Grid layout -->
          <div class="grid gap-6 sm:grid-cols-2">
            <!-- Discount info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin ưu đãi
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Loại ưu đãi</span>
                  <span class="flex items-center text-sm text-gray-900">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="mr-1 h-4 w-4 text-gray-500"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path :d="selectedDiscount.type === 'percentage' ? mdiPercent : mdiCash" />
                    </svg>
                    {{ selectedDiscount.type === 'percentage' ? 'Phần trăm' : 'Số tiền cố định' }}
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Giá trị</span>
                  <span class="text-lg font-bold text-green-600">
                    {{ formatDiscountValue(selectedDiscount) }}
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Giảm tối đa</span>
                  <span class="text-sm font-semibold text-blue-600">
                    {{ formatCurrency(selectedDiscount.maxDiscount) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Requirements -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Điều kiện áp dụng
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Đơn hàng tối thiểu</span>
                  <span class="text-sm text-gray-900">
                    {{ formatCurrency(selectedDiscount.minOrder) }}
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Trạng thái</span>
                  <span
                    :class="[
                      'inline-flex items-center rounded-full border px-2 py-1 text-xs font-medium',
                      getStatusColor(selectedDiscount.status),
                    ]"
                  >
                    {{ getStatusText(selectedDiscount.status) }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Time info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thời gian áp dụng
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Ngày bắt đầu</span>
                  <span class="flex items-center text-sm text-gray-900">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="mr-1 h-4 w-4 text-gray-500"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path :d="mdiCalendar" />
                    </svg>
                    {{ selectedDiscount.startDate }}
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Ngày kết thúc</span>
                  <span class="flex items-center text-sm text-gray-900">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="mr-1 h-4 w-4 text-gray-500"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path :d="mdiCalendar" />
                    </svg>
                    {{ selectedDiscount.endDate }}
                  </span>
                </div>
              </div>
            </div>

            <!-- Usage stats -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thống kê sử dụng
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Đã sử dụng</span>
                  <span class="flex items-center text-sm text-gray-900">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      class="mr-1 h-4 w-4 text-gray-500"
                      viewBox="0 0 24 24"
                      fill="currentColor"
                    >
                      <path :d="mdiAccountGroup" />
                    </svg>
                    {{ selectedDiscount.usedCount }} lượt
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Tổng đơn hàng</span>
                  <span class="text-sm text-gray-900">{{ selectedDiscount.totalOrders }} đơn</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Tỷ lệ sử dụng</span>
                  <span class="text-sm font-semibold text-green-600">
                    {{
                      selectedDiscount.totalOrders > 0
                        ? Math.round(
                            (selectedDiscount.usedCount / selectedDiscount.totalOrders) * 100,
                          )
                        : 0
                    }}%
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </CardBoxModal>

      <!-- History Modal -->
      <CardBoxModal
        v-model="historyModalActive"
        :title="`Lịch sử sử dụng - ${selectedDiscount?.name || ''}`"
        button-label="Đóng"
        :has-button="true"
      >
        <div class="space-y-4">
          <div v-if="selectedDiscount" class="overflow-x-auto">
            <table class="w-full">
              <thead class="bg-gray-50">
                <tr>
                  <th
                    class="px-4 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                  >
                    Mã đơn hàng
                  </th>
                  <th
                    class="px-4 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                  >
                    Khách hàng
                  </th>
                  <th
                    class="px-4 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                  >
                    Tổng tiền
                  </th>
                  <th
                    class="px-4 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                  >
                    Giảm giá
                  </th>
                  <th
                    class="px-4 py-3 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                  >
                    Thời gian
                  </th>
                </tr>
              </thead>
              <tbody class="divide-y divide-gray-200 bg-white">
                <tr
                  v-for="item in history.filter((h) => h.discountId === selectedDiscount.id)"
                  :key="item.id"
                  class="transition-colors hover:bg-gray-50"
                >
                  <td class="px-4 py-3 text-sm font-medium whitespace-nowrap text-gray-900">
                    {{ item.orderId }}
                  </td>
                  <td class="px-4 py-3 text-sm text-gray-700">{{ item.customer }}</td>
                  <td class="px-4 py-3 text-sm text-gray-700">{{ formatCurrency(item.amount) }}</td>
                  <td class="px-4 py-3 text-sm font-semibold text-green-600">
                    {{ formatCurrency(item.discountAmount) }}
                  </td>
                  <td class="px-4 py-3 text-sm whitespace-nowrap text-gray-700">{{ item.date }}</td>
                </tr>
                <tr v-if="history.filter((h) => h.discountId === selectedDiscount.id).length === 0">
                  <td colspan="5" class="px-4 py-8 text-center text-sm text-gray-500">
                    Chưa có lịch sử sử dụng cho ưu đãi này
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </CardBoxModal>

      <!-- Add Discount Modal -->
      <CardBoxModalNew v-model="addModalActive" title="Thêm ưu đãi mới">
        <div class="space-y-4">
          <FormField label="Tên ưu đãi">
            <FormControl v-model="newDiscount.name" placeholder="Nhập tên ưu đãi..." />
          </FormField>

          <div class="grid grid-cols-2 gap-4">
            <FormField label="Loại ưu đãi">
              <FormControl
                v-model="newDiscount.type"
                :options="[
                  { id: 'percentage', label: 'Phần trăm (%)' },
                  { id: 'fixed', label: 'Số tiền cố định' },
                ]"
              />
            </FormField>

            <FormField
              :label="newDiscount.type.id === 'percentage' ? 'Giá trị (%)' : 'Giá trị (VND)'"
            >
              <FormControl
                v-model="newDiscount.value"
                type="number"
                :placeholder="
                  newDiscount.type === 'percentage' ? 'Nhập phần trăm...' : 'Nhập số tiền...'
                "
              />
            </FormField>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <FormField label="Đơn hàng tối thiểu (VND)">
              <FormControl v-model="newDiscount.minOrder" type="number" placeholder="0" />
            </FormField>

            <FormField label="Giảm tối đa (VND)">
              <FormControl v-model="newDiscount.maxDiscount" type="number" placeholder="0" />
            </FormField>
          </div>

          <div class="grid grid-cols-2 gap-4">
            <FormField label="Ngày bắt đầu">
              <FormControl v-model="newDiscount.startDate" type="date" />
            </FormField>

            <FormField label="Ngày kết thúc">
              <FormControl v-model="newDiscount.endDate" type="date" />
            </FormField>
          </div>

          <!-- Custom buttons -->
          <div class="flex justify-end gap-3 pt-4">
            <BaseButton label="Hủy" color="whiteDark" @click="addModalActive = false" />
            <BaseButton label="Thêm ưu đãi" color="info" @click="submitDiscount" />
          </div>
        </div>
      </CardBoxModalNew>
    </SectionMain>
  </LayoutAuthenticated>
</template>
