<script setup>
import { ref, computed } from 'vue'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import FormField from '@/components/FormField.vue'
import FormControl from '@/components/FormControl.vue'
import SalesNavTabs from '@/components/SalesNavTabs.vue'
import BaseIcon from '@/components/BaseIcon.vue'
import { mdiMagnify, mdiAlert } from '@mdi/js'

const searchQuery = ref('')

const products = ref([
  {
    id: 1,
    name: 'Sữa tươi Vinamilk',
    code: 'P001',
    category: 'Thực phẩm',
    price: 32000,
    stock: 245,
    barcode: '8936036011234',
    location: 'Khu A-12',
    stockStatus: 'good',
  },
  {
    id: 2,
    name: 'Gạo ST25 5kg',
    code: 'P002',
    category: 'Lương thực',
    price: 125000,
    stock: 89,
    barcode: '8936036022341',
    location: 'Khu B-05',
    stockStatus: 'low',
  },
  {
    id: 3,
    name: 'Nước ngọt Coca Cola',
    code: 'P003',
    category: 'Đồ uống',
    price: 10000,
    stock: 543,
    barcode: '8936036033452',
    location: 'Khu C-08',
    stockStatus: 'good',
  },
  {
    id: 4,
    name: 'Bánh mì sandwich',
    code: 'P004',
    category: 'Thực phẩm',
    price: 25000,
    stock: 156,
    barcode: '8936036044563',
    location: 'Khu A-15',
    stockStatus: 'good',
  },
  {
    id: 5,
    name: 'Dầu ăn Simply 1L',
    code: 'P005',
    category: 'Gia vị',
    price: 45000,
    stock: 78,
    barcode: '8936036055674',
    location: 'Khu B-20',
    stockStatus: 'low',
  },
])

const filteredProducts = computed(() => {
  if (!searchQuery.value) return products.value
  const query = searchQuery.value.toLowerCase()
  return products.value.filter(
    (p) =>
      p.name.toLowerCase().includes(query) ||
      p.code.toLowerCase().includes(query) ||
      p.barcode.includes(query),
  )
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <div class="mb-6">
        <h1 class="text-3xl font-bold text-gray-800 dark:text-white mb-2">Dashboard Bán Hàng</h1>
        <p class="text-gray-600 dark:text-gray-400">Thực hiện giao dịch và tra cứu sản phẩm</p>
      </div>

      <SalesNavTabs />

      <CardBox>
        <div class="mb-6">
          <h2 class="text-2xl font-bold text-gray-800 dark:text-white mb-2">Tra Cứu Sản Phẩm</h2>
          <p class="text-gray-600 dark:text-gray-400">Tìm kiếm thông tin sản phẩm, giá, và tồn kho</p>
        </div>

        <FormField class="mb-6">
          <FormControl
            v-model="searchQuery"
            :icon="mdiMagnify"
            placeholder="Tìm kiếm theo tên, mã sản phẩm, hoặc mã vạch..."
          />
        </FormField>

        <div class="space-y-4">
          <div
            v-for="product in filteredProducts"
            :key="product.id"
            class="p-4 rounded-lg border border-gray-200 dark:border-gray-700 bg-white dark:bg-slate-800"
          >
            <div class="flex justify-between items-start mb-3">
              <div class="flex-1">
                <h3 class="text-lg font-semibold text-gray-800 dark:text-white mb-2">
                  {{ product.name }}
                </h3>
                <div class="flex items-center space-x-2 mb-2">
                  <span
                    class="px-2 py-1 text-xs rounded-full bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200"
                  >
                    {{ product.category }}
                  </span>
                  <span class="text-sm text-gray-600 dark:text-gray-400">Mã: {{ product.code }}</span>
                </div>
              </div>
              <div class="text-right">
                <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">$ {{ product.price.toLocaleString('vi-VN') }} VNĐ</p>
                <p class="text-lg font-bold text-blue-600 dark:text-blue-400">
                  {{ product.price.toLocaleString('vi-VN') }}₫
                </p>
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4 mt-4">
              <div>
                <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">Tồn kho:</p>
                <span
                  class="px-2 py-1 text-xs rounded-full"
                  :class="{
                    'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-200':
                      product.stockStatus === 'good',
                    'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-200':
                      product.stockStatus === 'low',
                  }"
                >
                  {{ product.stock }} sản phẩm
                  <BaseIcon
                    v-if="product.stockStatus === 'low'"
                    :path="mdiAlert"
                    size="16"
                    class="inline ml-1"
                  />
                </span>
                <p
                  v-if="product.stockStatus === 'low'"
                  class="text-xs text-yellow-600 dark:text-yellow-400 mt-1"
                >
                  Tồn kho thấp
                </p>
              </div>
              <div>
                <p class="text-sm text-gray-600 dark:text-gray-400 mb-1">Mã vạch:</p>
                <p class="text-sm font-mono text-gray-800 dark:text-white">
                  IIIII {{ product.barcode }}
                </p>
              </div>
            </div>

            <div class="mt-3 pt-3 border-t border-gray-200 dark:border-gray-700">
              <p class="text-sm text-gray-600 dark:text-gray-400">
                <span class="font-medium">Vị trí:</span> {{ product.location }}
              </p>
            </div>
          </div>
        </div>

        <div v-if="filteredProducts.length === 0" class="text-center py-12">
          <p class="text-gray-500 dark:text-gray-400">Không tìm thấy sản phẩm nào</p>
        </div>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>

