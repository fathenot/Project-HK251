<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiMagnify, mdiCubeOutline } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios'

// Dữ liệu mẫu
const sampleProducts = [
  {
    id: 'SP001',
    name: 'Sản phẩm 1',
    category: 'Thực phẩm',
    stock: 120,
    lots: [
      { id: 'LO001', quantity: 20, date: '2025-11-01', expiry: '2026-05-01' },
      { id: 'LO003', quantity: 30, date: '2025-12-20', expiry: '2026-06-05' },
    ],
  },
  {
    id: 'SP002',
    name: 'Sản phẩm 2',
    category: 'Đồ uống',
    stock: 75,
    lots: [
      { id: 'LO002', quantity: 15, date: '2025-11-05', expiry: '2026-08-15' },
    ],
  },
]

const searchQuery = ref('')
const products = ref([])

// Modal xem chi tiết
const modalActive = ref(false)
const selectedProduct = ref(null)
const loadingDetail = ref(false)

// Modal thêm sản phẩm mới
const addModalActive = ref(false)
const newProduct = ref({
  id: '',
  name: '',
  category: '',
  stock: 0,
})

// Fetch danh sách sản phẩm
const fetchProducts = async () => {
  try {
    const res = await api.get('/products')
    products.value = res.data
  } catch (err) {
    console.warn('Không thể load sản phẩm, dùng dữ liệu mẫu')
    products.value = sampleProducts
  }
}

// Filter theo search
const filteredProducts = computed(() =>
  products.value.filter((p) =>
    [p.id, p.name, p.category]
      .some((f) => f.toLowerCase().includes(searchQuery.value.toLowerCase())),
  ),
)

// Xem chi tiết sản phẩm
const fetchProductDetail = async (id) => {
  loadingDetail.value = true
  selectedProduct.value = null
  try {
    const res = await api.get(`/products/${id}`)
    selectedProduct.value = res.data
  } catch (err) {
    console.warn('Không thể load chi tiết, dùng mẫu')
    selectedProduct.value = products.value.find((p) => p.id === id)
  } finally {
    loadingDetail.value = false
  }
}

const viewProduct = (product) => {
  modalActive.value = true
  fetchProductDetail(product.id)
}

// Mở modal thêm sản phẩm
const openAddProductModal = () => {
  newProduct.value = { id: '', name: '', category: '', stock: 0 }
  addModalActive.value = true
}

// Thêm sản phẩm mới
const addProduct = async () => {
  try {
    const res = await api.post('/products', newProduct.value)
    products.value.push(res.data)
    addModalActive.value = false
  } catch (err) {
    console.warn('Không thể thêm sản phẩm, thêm vào mẫu')
    products.value.push({ ...newProduct.value, lots: [] })
    addModalActive.value = false
  }
}

onMounted(fetchProducts)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiCubeOutline" title="Tìm kiếm hàng hóa" />

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm hàng hóa</h3>
            <p class="text-sm text-gray-600">
              Tìm kiếm theo mã sản phẩm, tên sản phẩm hoặc danh mục
            </p>
          </div>

          <!-- Nút thêm sản phẩm mới -->
          <button
            @click="openAddProductModal"
            class="rounded-lg bg-blue-600 px-4 py-2 text-white shadow hover:bg-blue-700"
          >
            + Thêm hàng hóa
          </button>

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
                placeholder="Nhập mã sản phẩm, tên hoặc danh mục..."
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
                Tìm thấy {{ filteredProducts.length }} sản phẩm
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
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Mã SP</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Tên sản phẩm</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Danh mục</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Tồn kho</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="product in filteredProducts"
                :key="product.id"
                class="cursor-pointer transition-colors duration-200 hover:bg-blue-50"
                @click="viewProduct(product)"
              >
                <td class="px-6 py-4">{{ product.id }}</td>
                <td class="px-6 py-4">{{ product.name }}</td>
                <td class="px-6 py-4">{{ product.category }}</td>
                <td class="px-6 py-4 font-semibold text-green-600">{{ product.stock }}</td>
              </tr>

              <!-- Empty state -->
              <tr v-if="filteredProducts.length === 0">
                <td colspan="4" class="px-6 py-16 text-center text-gray-500">
                  Không tìm thấy sản phẩm phù hợp
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Modal xem chi tiết sản phẩm -->
      <CardBoxModal
        v-model="modalActive"
        title="Chi tiết hàng hóa"
        button-label="Đóng"
        :has-button="true"
      >
        <div v-if="loadingDetail" class="flex justify-center py-10">
          <div class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
        </div>

        <div v-else-if="selectedProduct" class="space-y-6">
          <!-- Thông tin cơ bản -->
          <div class="border-b pb-4">
            <h3 class="text-xl font-semibold">{{ selectedProduct.name }}</h3>
            <p class="text-sm text-gray-500">{{ selectedProduct.id }} • {{ selectedProduct.category }}</p>
          </div>

          <!-- Thông tin chi tiết bổ sung -->
          <div class="grid gap-4 sm:grid-cols-2 border-b pb-4">
            <div class="space-y-2">
              <div class="flex justify-between">
                <span class="font-medium text-gray-700">Mã sản phẩm:</span>
                <span class="text-gray-900">{{ selectedProduct.id }}</span>
              </div>
              <div class="flex justify-between">
                <span class="font-medium text-gray-700">Danh mục:</span>
                <span class="text-gray-900">{{ selectedProduct.category }}</span>
              </div>
              <div class="flex justify-between">
                <span class="font-medium text-gray-700">Tồn kho:</span>
                <span class="font-semibold text-green-600">{{ selectedProduct.stock }}</span>
              </div>
            </div>

            <div class="space-y-2">
              <!-- Nếu có thêm trường như giá, mô tả, có thể hiển thị ở đây -->
              <div v-if="selectedProduct.price" class="flex justify-between">
                <span class="font-medium text-gray-700">Giá:</span>
                <span class="text-gray-900">{{ new Intl.NumberFormat('vi-VN').format(selectedProduct.price) }} VND</span>
              </div>
              <div v-if="selectedProduct.description" class="flex justify-between">
                <span class="font-medium text-gray-700">Mô tả:</span>
                <span class="text-gray-900">{{ selectedProduct.description }}</span>
              </div>
            </div>
          </div>

          <!-- Lịch sử lô nhập -->
          <div>
            <h4 class="text-sm font-semibold text-gray-600 uppercase mb-2">Lịch sử lô nhập</h4>

            <div class="space-y-3">
              <div
                v-for="lot in selectedProduct.lots"
                :key="lot.id"
                class="rounded-lg border p-4 bg-gray-50"
              >
                <div class="flex justify-between">
                  <strong>Mã lô:</strong>
                  <span>{{ lot.id }}</span>
                </div>
                <div class="flex justify-between">
                  <span>Số lượng:</span>
                  <span class="font-semibold text-green-600">{{ lot.quantity }}</span>
                </div>
                <div class="flex justify-between">
                  <span>Ngày nhập:</span>
                  <span>{{ lot.date }}</span>
                </div>
                <div class="flex justify-between">
                  <span>Hạn sử dụng:</span>
                  <span>{{ lot.expiry }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </CardBoxModal>


      <!-- Modal thêm sản phẩm -->
      <CardBoxModal
        v-model="addModalActive"
        title="Thêm hàng hóa mới"
        button-label="Lưu"
        :has-button="true"
        @confirm="addProduct"
      >
        <div class="space-y-4">
          <FormControl v-model="newProduct.id" placeholder="Mã sản phẩm" />
          <FormControl v-model="newProduct.name" placeholder="Tên sản phẩm" />
          <FormControl v-model="newProduct.category" placeholder="Danh mục" />
          <FormControl v-model="newProduct.stock" type="number" placeholder="Tồn kho ban đầu" />
        </div>
      </CardBoxModal>

    </SectionMain>
  </LayoutAuthenticated>
</template>
