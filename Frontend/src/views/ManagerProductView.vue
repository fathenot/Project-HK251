<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { mdiMagnify, mdiCubeOutline } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios'

const searchQuery = ref('')
const products = ref([])
const loading = ref(false)

const modalActive = ref(false)
const selectedProduct = ref(null)
const loadingDetail = ref(false)

const addModalActive = ref(false)
const newProduct = ref({
  name: '',
  sku: '',
  barcode: '',
  price: 0,
  description: '',
})

const getTokenHeader = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
})

// Fetch sản phẩm theo search
const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await api.get('/products', {
      headers: getTokenHeader(),
      params: {
        name: searchQuery.value || undefined,
        sku: searchQuery.value || undefined,
        page: 0,
        size: 1000,
        sortBy: 'id',
        sortDirection: 'ASC',
      },
    })
    products.value = res.data.data.content.map(p => ({
      id: p.id,
      name: p.name,
      sku: p.sku,
      barcode: p.barcode,
      category: p.category || '',
      stock: p.available_quantity || 0,
      price: p.price || 0,
      description: p.description || '',
      lots: p.lots || [],
    }))
  } catch (err) {
    console.warn('Không thể load sản phẩm', err)
  } finally {
    loading.value = false
  }
}

// Chi tiết sản phẩm
const fetchProductDetail = async (id) => {
  loadingDetail.value = true
  selectedProduct.value = null
  try {
    const res = await api.get(`/products/${id}`, { headers: getTokenHeader() })
    selectedProduct.value = res.data.data
  } catch (err) {
    console.warn('Không thể load chi tiết sản phẩm', err)
  } finally {
    loadingDetail.value = false
  }
}

const viewProduct = (product) => {
  modalActive.value = true
  fetchProductDetail(product.id)
}

// Thêm sản phẩm
const openAddProductModal = () => {
  newProduct.value = { name: '', sku: '', barcode: '', price: 0, description: '' }
  addModalActive.value = true
}

const addProduct = async () => {
  try {
    const body = { ...newProduct.value }
    const res = await api.post('/products', body, { headers: getTokenHeader() })
    products.value.push(res.data.data)
    addModalActive.value = false
  } catch (err) {
    console.error('Không thể thêm sản phẩm', err)
    alert('Thêm sản phẩm lỗi!')
  }
}

const deleteProduct = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa sản phẩm này?')) return
  try {
    await api.delete(`/products/${id}`, { headers: getTokenHeader() })
    products.value = products.value.filter(p => p.id !== id)
  } catch (err) {
    console.error('Xóa sản phẩm lỗi', err)
    alert('Không thể xóa sản phẩm!')
  }
}

// Watch searchQuery để tự động fetch
watch(searchQuery, () => fetchProducts())

onMounted(fetchProducts)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiCubeOutline" title="Quản lý hàng hóa" />

      <!-- Search & Add -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm hàng hóa</h3>
            <p class="text-sm text-gray-600">Tìm kiếm theo SKU hoặc tên sản phẩm</p>
          </div>

          <button
            @click="openAddProductModal"
            class="rounded-lg bg-blue-600 px-4 py-2 text-white shadow hover:bg-blue-700"
          >
            + Thêm hàng hóa
          </button>

          <div class="w-full md:w-96">
            <div class="relative">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="currentColor">
                  <path :d="mdiMagnify" />
                </svg>
              </div>
              <FormControl v-model="searchQuery" placeholder="Nhập SKU hoặc tên sản phẩm..." class="pl-10" />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Product Table -->
      <CardBox class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách sản phẩm</h3>
          <p class="mt-1 text-sm text-gray-600">Tìm thấy {{ products.length }} sản phẩm</p>
        </div>
        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">SKU</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Tên sản phẩm</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Danh mục</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Tồn kho</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr v-for="p in products" :key="p.id" class="cursor-pointer hover:bg-blue-50">
                <td class="px-6 py-4">{{ p.sku }}</td>
                <td class="px-6 py-4">{{ p.name }}</td>
                <td class="px-6 py-4">{{ p.category }}</td>
                <td class="px-6 py-4 font-semibold text-green-600">{{ p.stock }}</td>
                <td class="px-6 py-4">
                  <button @click.stop="deleteProduct(p.id)" class="text-red-600 hover:underline">
                    Xóa
                  </button>
                </td>
              </tr>
              <tr v-if="products.length === 0">
                <td colspan="4" class="px-6 py-16 text-center text-gray-500">Không tìm thấy sản phẩm</td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Modal chi tiết sản phẩm -->
      <CardBoxModal v-model="modalActive" title="Chi tiết sản phẩm" button-label="Đóng" :has-button="true">
        <div v-if="loadingDetail" class="flex justify-center py-10">
          <div class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
        </div>

        <div v-else-if="selectedProduct" class="space-y-6">
          <div class="border-b pb-4">
            <h3 class="text-xl font-semibold">{{ selectedProduct.name }}</h3>
            <p class="text-sm text-gray-500">{{ selectedProduct.sku }} • {{ selectedProduct.category }}</p>
          </div>

          <div class="grid gap-4 sm:grid-cols-2 border-b pb-4">
            <div class="space-y-2">
              <div class="flex justify-between">
                <span class="font-medium text-gray-700">SKU:</span>
                <span class="text-gray-900">{{ selectedProduct.sku }}</span>
              </div>
              <div class="flex justify-between">
                <span class="font-medium text-gray-700">Danh mục:</span>
                <span class="text-gray-900">{{ selectedProduct.category }}</span>
              </div>
              <div class="flex justify-between">
                <span class="font-medium text-gray-700">Tồn kho:</span>
                <span class="font-semibold text-green-600">{{ selectedProduct.available_quantity }}</span>
              </div>
            </div>

            <div class="space-y-2">
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

          <div v-if="selectedProduct.lots?.length">
            <h4 class="text-sm font-semibold text-gray-600 uppercase mb-2">Lịch sử lô nhập</h4>
            <div class="space-y-3">
              <div v-for="lot in selectedProduct.lots" :key="lot.id" class="rounded-lg border p-4 bg-gray-50">
                <div class="flex justify-between"><strong>Mã lô:</strong> <span>{{ lot.id }}</span></div>
                <div class="flex justify-between"><span>Số lượng:</span> <span class="font-semibold text-green-600">{{ lot.quantity }}</span></div>
                <div class="flex justify-between"><span>Ngày nhập:</span> <span>{{ lot.date }}</span></div>
                <div class="flex justify-between"><span>Hạn sử dụng:</span> <span>{{ lot.expiry }}</span></div>
              </div>
            </div>
          </div>
        </div>
      </CardBoxModal>

      <!-- Modal thêm sản phẩm -->
      <CardBoxModal v-model="addModalActive" title="Thêm sản phẩm mới" button-label="Lưu" :has-button="true" @confirm="addProduct">
        <div class="space-y-4">
          <FormControl v-model="newProduct.name" placeholder="Tên sản phẩm" />
          <FormControl v-model="newProduct.sku" placeholder="SKU" />
          <FormControl v-model="newProduct.barcode" placeholder="Barcode" />
          <FormControl v-model="newProduct.price" type="number" placeholder="Giá bán" />
          <FormControl v-model="newProduct.description" placeholder="Mô tả" />
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
