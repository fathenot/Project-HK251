<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiTableBorder, mdiMagnify } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios.js'

const sampleList = [
  { id: 'PN001', type: 'Nhập', warehouse: 'Kho A', employee: 'Nguyễn Văn A', date: '2025-11-20' },
  { id: 'PX002', type: 'Xuất', warehouse: 'Kho B', employee: 'Trần Thị B', date: '2025-11-19' },
]

const searchQuery = ref('')
const receipts = ref([])
const filteredReceipts = computed(() =>
  receipts.value.filter(
    (r) =>
      r.id.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      r.employee.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      r.warehouse.toLowerCase().includes(searchQuery.value.toLowerCase()),
  ),
)

const modalActive = ref(false)
const selectedReceipt = ref(null)
const loadingDetail = ref(false)

const fetchReceipts = async () => {
  try {
    const res = await api.get('/receipts')
    receipts.value = res.data
  } catch (error) {
    console.error('Lỗi khi lấy danh sách phiếu, sử dụng dữ liệu mẫu:', error)
    receipts.value = sampleList
  }
}

const fetchReceiptDetail = async (id) => {
  loadingDetail.value = true
  selectedReceipt.value = null
  try {
    const res = await api.get(`/receipts/${id}`)
    selectedReceipt.value = res.data
  } catch (error) {
    console.error(`Lỗi khi lấy chi tiết phiếu ${id}, sử dụng dữ liệu mẫu:`, error)
    const detail = sampleList.find((r) => r.id === id)
    if (detail) {
      const sampleDetails = {
        PN001: {
          items: [
            {
              id: 'SP001',
              product: 'Sản phẩm 1',
              quantity: 10,
              manufacturer: 'NSX A',
              supplier: 'NCC X',
              price: 100000,
              expiry: '2026-05-01',
            },
            {
              id: 'SP002',
              product: 'Sản phẩm 2',
              quantity: 5,
              manufacturer: 'NSX B',
              supplier: 'NCC Y',
              price: 150000,
              expiry: '2026-08-15',
            },
          ],
        },
        PX002: {
          items: [
            {
              id: 'SP003',
              product: 'Sản phẩm 3',
              quantity: 2,
              manufacturer: 'NSX C',
              supplier: 'NCC Z',
              price: 200000,
              expiry: '2026-02-28',
            },
          ],
        },
      }
      selectedReceipt.value = { ...detail, items: sampleDetails[id]?.items || [] }
    }
  } finally {
    loadingDetail.value = false
  }
}

const viewReceipt = (receipt) => {
  modalActive.value = true
  fetchReceiptDetail(receipt.id)
}

onMounted(fetchReceipts)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiTableBorder" title="Tìm kiếm phiếu nhập/xuất" />

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm phiếu</h3>
            <p class="text-sm text-gray-600">Tìm kiếm theo mã phiếu, nhân viên hoặc kho</p>
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
                placeholder="Nhập mã phiếu, nhân viên hoặc kho..."
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
                Tìm thấy {{ filteredReceipts.length }} phiếu
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
                  Mã phiếu
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Loại phiếu
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Kho
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Nhân viên
                </th>
                <th
                  class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase"
                >
                  Ngày tạo
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="receipt in filteredReceipts"
                :key="receipt.id"
                class="cursor-pointer transition-colors duration-200 hover:bg-blue-50"
                @click="viewReceipt(receipt)"
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
                        <path :d="mdiTableBorder" />
                      </svg>
                    </div>
                    <div class="ml-4">
                      <div class="text-sm font-medium text-gray-900">{{ receipt.id }}</div>
                    </div>
                  </div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <span
                    :class="[
                      'inline-flex items-center rounded-full px-3 py-1 text-xs font-medium',
                      receipt.type === 'Nhập'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-orange-100 text-orange-800',
                    ]"
                  >
                    {{ receipt.type }}
                  </span>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm text-gray-700">{{ receipt.warehouse }}</div>
                </td>
                <td class="px-6 py-4">
                  <div class="text-sm font-medium text-gray-900">{{ receipt.employee }}</div>
                </td>
                <td class="px-6 py-4 text-sm whitespace-nowrap text-gray-700">
                  {{ receipt.date }}
                </td>
              </tr>

              <!-- Empty state -->
              <tr v-if="filteredReceipts.length === 0">
                <td colspan="5" class="px-6 py-16 text-center">
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
                    <h3 class="mt-4 text-lg font-medium text-gray-900">Không tìm thấy phiếu</h3>
                    <p class="mt-2 text-sm text-gray-500">
                      <span v-if="searchQuery"
                        >Không có phiếu nào phù hợp với "{{ searchQuery }}"</span
                      >
                      <span v-else>Không có phiếu nào trong hệ thống</span>
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
        title="Chi tiết phiếu"
        button-label="Đóng"
        :has-button="true"
      >
        <!-- Loading state -->
        <div v-if="loadingDetail" class="flex flex-col items-center justify-center py-12">
          <div
            class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết phiếu...</p>
        </div>

        <!-- Detail content -->
        <div v-else-if="selectedReceipt" class="space-y-6">
          <!-- Header -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">Phiếu {{ selectedReceipt.type }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                {{ selectedReceipt.id }} • {{ selectedReceipt.date }}
              </p>
            </div>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-blue-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiTableBorder" />
              </svg>
            </div>
          </div>

          <!-- Basic info -->
          <div class="grid gap-6 sm:grid-cols-2">
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin cơ bản
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Loại phiếu</span>
                  <span
                    :class="[
                      'inline-flex items-center rounded-full px-3 py-1 text-xs font-medium',
                      selectedReceipt.type === 'Nhập'
                        ? 'bg-green-100 text-green-800'
                        : 'bg-orange-100 text-orange-800',
                    ]"
                  >
                    {{ selectedReceipt.type }}
                  </span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Kho</span>
                  <span class="text-sm text-gray-900">{{ selectedReceipt.warehouse }}</span>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin nhân viên
              </h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Nhân viên</span>
                  <span class="text-sm text-gray-900">{{ selectedReceipt.employee }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Ngày tạo</span>
                  <span class="text-sm text-gray-900">{{ selectedReceipt.date }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Products list -->
          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
              Danh sách sản phẩm ({{ selectedReceipt.items?.length || 0 }})
            </h4>
            <div class="space-y-3">
              <div
                v-for="(item, idx) in selectedReceipt.items"
                :key="idx"
                class="rounded-lg border border-gray-200 bg-gray-50 p-4"
              >
                <div class="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <p class="font-medium text-gray-900">{{ item.product }}</p>
                    <p class="text-gray-500">ID: {{ item.id }}</p>
                  </div>
                  <div class="text-right">
                    <span class="font-semibold text-green-600">{{ item.quantity }} cái</span>
                    <p class="text-gray-500">
                      {{ new Intl.NumberFormat('vi-VN').format(item.price) }} VND
                    </p>
                  </div>
                </div>
                <div class="mt-2 grid grid-cols-2 gap-2 text-xs text-gray-600">
                  <div>NSX: {{ item.manufacturer }}</div>
                  <div>NCC: {{ item.supplier }}</div>
                  <div class="col-span-2">HSD: {{ item.expiry }}</div>
                </div>
              </div>

              <div
                v-if="!selectedReceipt.items || selectedReceipt.items.length === 0"
                class="py-4 text-center text-gray-500"
              >
                Không có sản phẩm trong phiếu này
              </div>
            </div>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
