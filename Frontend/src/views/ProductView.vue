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

const searchQuery = ref('')
const products = ref([])

// Modal xem chi tiết
const modalActive = ref(false)
const selectedProduct = ref(null)
const loadingDetail = ref(false)

// Lấy danh sách sản phẩm từ API
const fetchProducts = async () => {
  const accessToken = localStorage.getItem('accessToken') || ''
  try {
    const res = await api.get(`/products`, {
      headers: { Authorization: `Bearer ${accessToken}` }
    })
    products.value = res.data.data.content
  } catch (err) {
    console.warn('Không thể load sản phẩm từ API', err)
    products.value = []
  }
}

// Filter theo search
const filteredProducts = computed(() =>
  products.value.filter((p) =>
    [p.name, p.sku, p.barcode]
      .some(f => f && f.toLowerCase().includes(searchQuery.value.toLowerCase()))
  ),
)

// Xem chi tiết sản phẩm
const fetchProductDetail = async (id) => {
  const accessToken = localStorage.getItem('accessToken') || ''
  loadingDetail.value = true
  selectedProduct.value = null
  try {
    const res = await api.get(`/products/${id}`, {
      headers: { Authorization: `Bearer ${accessToken}` }
    })
    // Nếu có variants API thì hiển thị
    const resVariant = await api.get(`/products/${id}/variants`, {
      headers: { Authorization: `Bearer ${accessToken}` }
    })
    selectedProduct.value = {
      ...res.data.data,
      variants: resVariant.data.data || []
    }
  } catch (err) {
    console.warn('Không thể load chi tiết sản phẩm', err)
    selectedProduct.value = null
  } finally {
    loadingDetail.value = false
  }
}

const viewProduct = (product) => {
  modalActive.value = true
  fetchProductDetail(product.id)
}

onMounted(fetchProducts)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiCubeOutline" title="Danh sách hàng hóa" />

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="relative w-full md:w-96">
          <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="currentColor">
              <path :d="mdiMagnify" />
            </svg>
          </div>
          <FormControl
            v-model="searchQuery"
            placeholder="Nhập tên, SKU hoặc barcode..."
            class="pl-10"
          />
        </div>
      </CardBox>

      <!-- Products Table -->
      <CardBox class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Kết quả tìm kiếm</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredProducts.length }} sản phẩm
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Tên sản phẩm</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">SKU</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Barcode</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Giá</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Tồn kho</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="product in filteredProducts"
                :key="product.id"
                class="cursor-pointer hover:bg-blue-50 transition-colors"
                @click="viewProduct(product)"
              >
                <td class="px-6 py-4">{{ product.name }}</td>
                <td class="px-6 py-4">{{ product.sku }}</td>
                <td class="px-6 py-4">{{ product.barcode }}</td>
                <td class="px-6 py-4">{{ new Intl.NumberFormat('vi-VN').format(product.price) }} VND</td>
                <td class="px-6 py-4 font-semibold text-green-600">{{ product.available_quantity }}</td>
              </tr>

              <tr v-if="filteredProducts.length === 0">
                <td colspan="5" class="px-6 py-16 text-center text-gray-500">
                  Không tìm thấy sản phẩm phù hợp
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Modal xem chi tiết -->
      <CardBoxModal v-model="modalActive" title="Chi tiết sản phẩm" button-label="Đóng" :has-button="true">
        <div v-if="loadingDetail" class="flex justify-center py-10">
          <div class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
        </div>

        <div v-else-if="selectedProduct" class="space-y-6">
          <!-- Thông tin cơ bản -->
          <div class="border-b border-gray-200 pb-4">
            <h3 class="text-2xl font-bold text-gray-800">{{ selectedProduct.name }}</h3>
            <p class="text-sm text-gray-500 mt-1">SKU: {{ selectedProduct.sku }} • Barcode: {{ selectedProduct.barcode }}</p>
            <p class="text-sm text-gray-400 mt-1">ID sản phẩm: {{ selectedProduct.id }}</p>
          </div>

          <!-- Giá và Tồn kho -->
          <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div class="flex justify-between">
              <span class="font-medium text-gray-700">Giá:</span>
              <span class="text-gray-900">{{ new Intl.NumberFormat('vi-VN').format(selectedProduct.price) }} VND</span>
            </div>
            <div class="flex justify-between">
              <span class="font-medium text-gray-700">Tồn kho:</span>
              <span class="font-semibold text-green-600">{{ selectedProduct.available_quantity }}</span>
            </div>
          </div>

          <!-- Mô tả sản phẩm -->
          <div v-if="selectedProduct.description" class="mt-2">
            <span class="font-medium text-gray-700">Mô tả:</span>
            <p class="text-gray-900 mt-1">{{ selectedProduct.description }}</p>
          </div>

          <!-- Chủng loại / Variants -->
          <div v-if="selectedProduct.variants && selectedProduct.variants.length" class="mt-4">
            <h4 class="text-sm font-semibold text-gray-600 uppercase mb-2">Chủng loại (Variants)</h4>
            <div class="space-y-2">
              <div
                v-for="v in selectedProduct.variants"
                :key="v.id"
                class="border rounded-lg p-3 bg-gray-50 hover:bg-gray-100 transition-colors"
              >
                <div class="flex justify-between items-center mb-1">
                  <span class="text-gray-700 font-medium">ID: {{ v.id }}</span>
                </div>
                <div class="flex flex-wrap gap-3 text-sm text-gray-600">
                  <span v-for="(val, key) in v.variant_attributes" :key="key">
                    <strong>{{ key }}:</strong> {{ val }}
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
