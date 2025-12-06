<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiTableBorder, mdiMagnify } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios.js'

const searchQuery = ref('')
const receipts = ref([])
const filteredReceipts = computed(() =>
  receipts.value.filter(
    (r) =>
      r.id.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      r.employee.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      r.warehouse.toLowerCase().includes(searchQuery.value.toLowerCase())
  )
)

const modalActive = ref(false)
const selectedReceipt = ref(null)
const loadingDetail = ref(false)

const fetchReceipts = async () => {
  try {
    const accessToken = localStorage.getItem('accessToken') || ''
    const res = await api.get('/batches', {
      params: { page: 0, size: 50 },
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })

    receipts.value = res.data.data.content.map(b => ({
      id: `BATCH-${b.id}`,
      type: 'Nhập',
      warehouse: b.warehouse_name,
      employee: 'Không rõ',
      date: b.create_date,
      batchDetail: b
    }))
  } catch (error) {
    console.error('Lỗi khi lấy danh sách batch:', error)
    receipts.value = []
  }
}

const fetchReceiptDetail = async (id) => {
  loadingDetail.value = true
  selectedReceipt.value = null
  try {
    const accessToken = localStorage.getItem('accessToken') || ''
    const batchId = id.replace('BATCH-', '')
    const res = await api.get(`/batches/${batchId}`, {
      headers: {
        Authorization: `Bearer ${accessToken}`
      }
    })
    const b = res.data.data
    selectedReceipt.value = {
      id: `BATCH-${b.id}`,
      type: 'Nhập',
      warehouse: b.warehouse_name,
      employee: 'Không rõ',
      date: b.create_date,
      items: [
        {
          id: b.product_id,
          product: b.product_name,
          quantity: b.quantity_total,
          manufacturer: b.manufacture,
          supplier: b.supplier,
          price: b.unitPrice || 0,
          expiry: b.expiry_date
        }
      ]
    }
  } catch (error) {
    console.error(`Lỗi khi lấy chi tiết batch ${id}:`, error)
    selectedReceipt.value = null
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
      <SectionTitleLineWithoutButton :icon="mdiTableBorder" title="Tìm kiếm phiếu nhập" />

      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm phiếu</h3>
            <p class="text-sm text-gray-600">Tìm kiếm theo mã phiếu, nhân viên hoặc kho</p>
          </div>
          <div class="w-full md:w-96">
            <div class="relative">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="currentColor">
                  <path :d="mdiMagnify" />
                </svg>
              </div>
              <FormControl v-model="searchQuery" placeholder="Nhập mã phiếu, nhân viên hoặc kho..." class="pl-10" />
            </div>
          </div>
        </div>
      </CardBox>

      <CardBox class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
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

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Mã phiếu</th>
                <th class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Loại phiếu</th>
                <th class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Kho</th>
                <th class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Nhân viên</th>
                <th class="px-6 py-4 text-left text-xs font-medium tracking-wider text-gray-500 uppercase">Ngày tạo</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr v-for="receipt in filteredReceipts" :key="receipt.id" class="cursor-pointer transition-colors duration-200 hover:bg-blue-50" @click="viewReceipt(receipt)">
                <td class="px-6 py-4 whitespace-nowrap">{{ receipt.id }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ receipt.type }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ receipt.warehouse }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ receipt.employee }}</td>
                <td class="px-6 py-4 whitespace-nowrap">{{ receipt.date }}</td>
              </tr>
              <tr v-if="filteredReceipts.length === 0">
                <td colspan="5" class="px-6 py-16 text-center text-gray-500">Không có phiếu nào trong hệ thống</td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <CardBoxModal v-model="modalActive" title="Chi tiết phiếu" button-label="Đóng" :has-button="true">
        <div v-if="loadingDetail" class="flex flex-col items-center justify-center py-12">
          <div class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết phiếu...</p>
        </div>

        <div v-else-if="selectedReceipt" class="space-y-6">
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">Phiếu {{ selectedReceipt.type }}</h3>
              <p class="mt-1 text-sm text-gray-500">{{ selectedReceipt.id }} • {{ selectedReceipt.date }}</p>
            </div>
          </div>

          <div class="grid gap-6 sm:grid-cols-2">
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">Thông tin cơ bản</h4>
              <div class="space-y-3">
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Loại phiếu</span>
                  <span class="text-xs font-medium inline-flex items-center rounded-full px-3 py-1 bg-green-100 text-green-800">{{ selectedReceipt.type }}</span>
                </div>
                <div class="flex items-center justify-between">
                  <span class="text-sm font-medium text-gray-700">Kho</span>
                  <span class="text-sm text-gray-900">{{ selectedReceipt.warehouse }}</span>
                </div>
              </div>
            </div>

            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">Thông tin nhân viên</h4>
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

          <div class="space-y-4">
            <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">Danh sách sản phẩm ({{ selectedReceipt.items?.length || 0 }})</h4>
            <div class="space-y-3">
              <div v-for="(item, idx) in selectedReceipt.items" :key="idx" class="rounded-lg border border-gray-200 bg-gray-50 p-4">
                <div class="grid grid-cols-2 gap-4 text-sm">
                  <div>
                    <p class="font-medium text-gray-900">{{ item.product }}</p>
                    <p class="text-gray-500">ID: {{ item.id }}</p>
                  </div>
                  <div class="text-right">
                    <span class="font-semibold text-green-600">{{ item.quantity }} cái</span>
                    <p class="text-gray-500">{{ new Intl.NumberFormat('vi-VN').format(item.price) }} VND</p>
                  </div>
                </div>
                <div class="mt-2 grid grid-cols-2 gap-2 text-xs text-gray-600">
                  <div>NSX: {{ item.manufacturer }}</div>
                  <div>NCC: {{ item.supplier }}</div>
                  <div class="col-span-2">HSD: {{ item.expiry }}</div>
                </div>
              </div>
              <div v-if="!selectedReceipt.items || selectedReceipt.items.length === 0" class="py-4 text-center text-gray-500">
                Không có sản phẩm trong phiếu này
              </div>
            </div>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
