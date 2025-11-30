<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiMagnify, mdiCubeOutline } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios'

const sampleLots = [
  {
    id: 'LO001',
    productId: 'SP001',
    product: 'Sản phẩm 1',
    warehouse: 'Kho A',
    quantity: 20,
    date: '2025-11-01',
  },
  {
    id: 'LO002',
    productId: 'SP002',
    product: 'Sản phẩm 2',
    warehouse: 'Kho B',
    quantity: 15,
    date: '2025-11-05',
  },
]

const searchQuery = ref('')
const lots = ref([])

const filteredLots = computed(() =>
  lots.value.filter(
    (l) =>
      l.id.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      l.productId.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      l.product.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      l.warehouse.toLowerCase().includes(searchQuery.value.toLowerCase()),
  ),
)

const modalActive = ref(false)
const selectedLot = ref(null)
const loadingDetail = ref(false)

const fetchLots = async () => {
  try {
    const res = await api.get('/warehouse/lots')
    lots.value = res.data
  } catch (err) {
    console.warn('Không thể load lô hàng từ API, dùng dữ liệu mẫu', err)
    lots.value = sampleLots
  }
}

const fetchLotDetail = async (id) => {
  loadingDetail.value = true
  selectedLot.value = null

  try {
    const res = await api.get(`/warehouse/lots/${id}`)
    selectedLot.value = res.data
  } catch (err) {
    console.warn('Không thể load chi tiết lô hàng từ API, dùng dữ liệu mẫu', err)
    const sampleLotDetails = {
      LO001: {
        id: 'LO001',
        productId: 'SP001',
        product: 'Sản phẩm 1',
        warehouse: 'Kho A',
        quantity: 20,
        manufacturer: 'NSX A',
        supplier: 'NCC X',
        price: 100000,
        expiry: '2026-05-01',
      },
      LO002: {
        id: 'LO002',
        productId: 'SP002',
        product: 'Sản phẩm 2',
        warehouse: 'Kho B',
        quantity: 15,
        manufacturer: 'NSX B',
        supplier: 'NCC Y',
        price: 150000,
        expiry: '2026-08-15',
      },
    }
    selectedLot.value = sampleLotDetails[id]
  } finally {
    loadingDetail.value = false
  }
}

const viewLot = (lot) => {
  modalActive.value = true
  fetchLotDetail(lot.id)
}

onMounted(fetchLots)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiCubeOutline" title="Tìm kiếm lô hàng/hàng hóa" />

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm lô hàng</h3>
            <p class="text-sm text-gray-600">
              Tìm kiếm theo mã lô, mã sản phẩm, tên sản phẩm hoặc kho
            </p>
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
                placeholder="Nhập mã lô, sản phẩm hoặc kho..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Results Card -->
      <CardBox class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <!-- Header -->
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between">
            <div>
              <h3 class="text-lg font-semibold text-gray-800">Kết quả tìm kiếm</h3>
              <p class="mt-1 text-sm text-gray-600">
                Tìm thấy {{ filteredLots.length }} lô hàng
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
                  Mã lô
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Mã hàng hóa
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Sản phẩm
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Kho
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Số lượng
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Ngày nhập
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="lot in filteredLots"
                :key="lot.id"
                class="cursor-pointer transition-colors duration-200 hover:bg-blue-50"
                @click="viewLot(lot)"
              >
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center">
                    <div class="flex h-10 w-10 items-center justify-center rounded-lg bg-blue-100">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-5 w-5 text-blue-600"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiCubeOutline" />
                      </svg>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">{{ lot.id }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    class="inline-flex items-center rounded-full bg-gray-100 px-3 py-1 text-xs font-medium text-gray-800"
                  >
                    {{ lot.productId }}
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm font-medium text-gray-900">{{ lot.product }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-gray-700">{{ lot.warehouse }}</div>
                </td>
                <td class="px-6 py-4">
                  <span
                    class="inline-flex items-center rounded-full bg-green-100 px-3 py-1 text-sm font-medium text-green-800"
                  >
                    {{ lot.quantity }}
                  </span>
                </td>
                <td class="px-6 py-4 text-sm whitespace-nowrap text-gray-700">
                  {{ lot.date }}
                </td>
              </tr>

              <!-- Empty state -->
              <tr v-if="filteredLots.length === 0">
                <td colspan="6" class="px-6 py-16 text-center">
                  <div class="flex flex-col items-center justify-center">
                    <div class="rounded-full bg-gray-100 p-4">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-8 w-8 text-gray-400"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiMagnify" />
                      </svg>
                    </div>
                    <h3 class="mt-4 text-lg font-medium text-gray-900">Không tìm thấy lô hàng</h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery"
                        >Không có lô hàng nào phù hợp với "{{ searchQuery }}"</span
                      >
                      <span v-else>Không có lô hàng nào trong hệ thống</span>
                    </p>
                  </div>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Detail Modal -->
      <CardBoxModal
        v-model="modalActive"
        title="Chi tiết lô hàng"
        button-label="Đóng"
        :has-button="true"
      >
        <!-- Loading state -->
        <div v-if="loadingDetail" class="flex flex-col items-center justify-center py-12">
          <div
            class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết lô hàng...</p>
        </div>

        <!-- Detail content -->
        <div v-else-if="selectedLot" class="space-y-6">
          <!-- Header -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">{{ selectedLot.product }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                {{ selectedLot.id }} • {{ selectedLot.productId }}
              </p>
            </div>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-blue-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiCubeOutline" />
              </svg>
            </div>
          </div>

          <!-- Grid layout -->
          <div class="grid gap-6 sm:grid-cols-2">
            <!-- Basic info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin cơ bản
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Kho lưu trữ</span>
                  <span class="text-sm text-gray-900">{{ selectedLot.warehouse }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Số lượng</span>
                  <span class="text-sm font-semibold text-green-600">{{
                    selectedLot.quantity
                  }}</span>
                </div>
              </div>
            </div>

            <!-- Date info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin thời gian
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Ngày nhập</span>
                  <span class="text-sm text-gray-900">{{ selectedLot.date }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Hạn sử dụng</span>
                  <span class="text-sm text-gray-900">{{ selectedLot.expiry }}</span>
                </div>
              </div>
            </div>

            <!-- Supplier info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin đối tác
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Nhà sản xuất</span>
                  <span class="text-sm text-gray-900">{{ selectedLot.manufacturer }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Nhà cung cấp</span>
                  <span class="text-sm text-gray-900">{{ selectedLot.supplier }}</span>
                </div>
              </div>
            </div>

            <!-- Financial info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin giá
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Giá nhập</span>
                  <span class="text-sm font-semibold text-blue-600">
                    {{ new Intl.NumberFormat('vi-VN').format(selectedLot.price) }} VND
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Tổng giá trị</span>
                  <span class="text-sm font-semibold text-green-600">
                    {{
                      new Intl.NumberFormat('vi-VN').format(
                        selectedLot.price * selectedLot.quantity,
                      )
                    }}
                    VND
                  </span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
