<script setup>
import { ref, reactive, onMounted } from 'vue'
import { mdiStore, mdiChevronDown, mdiChevronUp, mdiPackageVariant, mdiMapMarker } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import { api } from '@/plugins/axios.js'

const sampleShops = [
  { id: 'S001', name: 'Gian hàng A', location: 'Tầng 1', status: 'active' },
  { id: 'S002', name: 'Gian hàng B', location: 'Tầng 2', status: 'maintenance' },
  { id: 'S003', name: 'Gian hàng C', location: 'Tầng 3', status: 'inactive' },
]

const sampleShopItems = {
  S001: [
    { id: 'SP001', name: 'Sản phẩm 1', quantity: 10, batch: 'LO001', expiry: '2026-05-01' },
    { id: 'SP002', name: 'Sản phẩm 2', quantity: 5, batch: 'LO002', expiry: '2026-08-15' },
  ],
  S002: [{ id: 'SP003', name: 'Sản phẩm 3', quantity: 2, batch: 'LO003', expiry: '2026-02-28' }],
  S003: [],
}

const shops = ref([])
const activeShopId = ref(null)
const shopItems = reactive({})
const shopLoading = reactive({})
const loadingShops = ref(false)

const toggleShop = async (shopId) => {
  if (activeShopId.value === shopId) {
    activeShopId.value = null
  } else {
    activeShopId.value = shopId
    await fetchShopItems(shopId)
  }
}

const fetchShopItems = async (shopId) => {
  if (shopLoading[shopId]) return
  shopLoading[shopId] = true
  try {
    const res = await api.get(`/shops/${shopId}/items`)
    shopItems[shopId] = res.data?.items || []
  } catch (error) {
    console.warn(`Không thể lấy dữ liệu gian hàng ${shopId}, dùng dữ liệu mẫu.`, error)
    shopItems[shopId] = sampleShopItems[shopId] || []
  } finally {
    shopLoading[shopId] = false
  }
}

const fetchShops = async () => {
  loadingShops.value = true
  try {
    const res = await api.get('/shops')
    shops.value = res.data?.shops || sampleShops
  } catch (error) {
    console.warn('Không thể lấy danh sách gian hàng, dùng dữ liệu mẫu.', error)
    shops.value = sampleShops
  } finally {
    loadingShops.value = false
  }
}

const getStatusColor = (status) => {
  const colors = {
    active: 'text-green-600 bg-green-100',
    maintenance: 'text-amber-600 bg-amber-100',
    inactive: 'text-red-600 bg-red-100',
  }
  return colors[status] || colors.inactive
}

const getStatusText = (status) => {
  const texts = {
    active: 'Đang hoạt động',
    maintenance: 'Bảo trì',
    inactive: 'Ngừng hoạt động',
  }
  return texts[status] || texts.inactive
}

onMounted(() => {
  fetchShops()
})
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiStore" title="Quản lý gian hàng" />

      <CardBox class="overflow-x-auto rounded-xl shadow-md">
        <table class="w-full border-collapse text-sm">
          <thead class="bg-white text-black">
            <tr>
              <th class="w-1/6 border-b border-blue-400 px-4 py-4 text-left font-semibold">
                ID Gian hàng
              </th>
              <th class="w-1/3 border-b border-blue-400 px-4 py-4 text-left font-semibold">
                Tên gian hàng
              </th>
              <th class="w-1/4 border-b border-blue-400 px-4 py-4 text-left font-semibold">
                Vị trí
              </th>
              <th class="w-1/6 border-b border-blue-400 px-4 py-4 text-left font-semibold">
                Trạng thái
              </th>
              <th class="w-1/12 border-b border-blue-400 px-4 py-4"></th>
            </tr>
          </thead>
          <tbody>
            <template v-for="shop in shops" :key="shop.id">
              <!-- Row gian hàng -->
              <tr
                :class="[
                  'cursor-pointer transition-all duration-300',
                  activeShopId === shop.id
                    ? 'bg-blue-50 ring-2 ring-blue-200'
                    : shop.id % 2 === 0
                      ? 'bg-white'
                      : 'bg-gray-50',
                  'hover:bg-blue-50',
                ]"
                @click="toggleShop(shop.id)"
              >
                <td
                  class="flex items-center border-b border-gray-200 px-4 py-4 text-left font-medium text-gray-800"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="mr-2 h-5 w-5 text-blue-500"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                  >
                    <path :d="mdiStore" />
                  </svg>
                  {{ shop.id }}
                </td>
                <td class="border-b border-gray-200 px-4 py-4 text-left font-medium text-gray-800">
                  {{ shop.name }}
                </td>
                <td
                  class="flex items-center border-b border-gray-200 px-4 py-4 text-left text-gray-600"
                >
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    class="mr-1 h-4 w-4 text-gray-500"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                  >
                    <path :d="mdiMapMarker" />
                  </svg>
                  {{ shop.location }}
                </td>
                <td class="border-b border-gray-200 px-4 py-4 text-left">
                  <span
                    :class="[
                      'inline-flex rounded-full px-3 py-1 text-xs font-semibold',
                      getStatusColor(shop.status),
                    ]"
                  >
                    {{ getStatusText(shop.status) }}
                  </span>
                </td>
                <td class="border-b border-gray-200 px-4 py-4 text-center">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    :class="[
                      'h-5 w-5 transition-transform duration-300',
                      activeShopId === shop.id ? 'rotate-180 text-blue-500' : 'text-gray-500',
                    ]"
                    viewBox="0 0 24 24"
                    fill="currentColor"
                  >
                    <path :d="activeShopId === shop.id ? mdiChevronUp : mdiChevronDown" />
                  </svg>
                </td>
              </tr>

              <!-- Row chi tiết -->
              <tr v-if="activeShopId === shop.id">
                <td colspan="5" class="border-b border-gray-200 p-0">
                  <div
                    class="bg-gradient-to-br from-gray-50 to-blue-50 p-4 transition-all duration-500 ease-in-out"
                  >
                    <div class="mb-3 flex items-center">
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="mr-2 h-5 w-5 text-blue-500"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiPackageVariant" />
                      </svg>
                      <h3 class="text-lg font-semibold text-gray-800">Mặt hàng trong gian</h3>
                      <span
                        class="ml-2 rounded-full bg-blue-100 px-2 py-1 text-xs font-medium text-blue-800"
                      >
                        {{ shopItems[shop.id] ? shopItems[shop.id].length : 0 }} sản phẩm
                      </span>
                    </div>

                    <div v-if="loadingItems" class="flex justify-center py-8">
                      <div
                        class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
                      ></div>
                    </div>

                    <div v-else>
                      <table class="w-full border-collapse overflow-hidden rounded-lg shadow-sm">
                        <thead class="bg-gradient-to-r from-gray-100 to-gray-200">
                          <tr>
                            <th
                              class="border border-gray-300 px-3 py-3 text-left text-xs font-semibold tracking-wider text-gray-700 uppercase"
                            >
                              Mã sản phẩm
                            </th>
                            <th
                              class="border border-gray-300 px-3 py-3 text-left text-xs font-semibold tracking-wider text-gray-700 uppercase"
                            >
                              Tên sản phẩm
                            </th>
                            <th
                              class="border border-gray-300 px-3 py-3 text-center text-xs font-semibold tracking-wider text-gray-700 uppercase"
                            >
                              Số lượng
                            </th>
                            <th
                              class="border border-gray-300 px-3 py-3 text-center text-xs font-semibold tracking-wider text-gray-700 uppercase"
                            >
                              Lô
                            </th>
                            <th
                              class="border border-gray-300 px-3 py-3 text-center text-xs font-semibold tracking-wider text-gray-700 uppercase"
                            >
                              Ngày hết hạn
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr
                            v-for="(item, index) in shopItems[shop.id]"
                            :key="item.id"
                            :class="index % 2 === 0 ? 'bg-white' : 'bg-gray-50'"
                            class="transition-colors duration-200 hover:bg-blue-50"
                          >
                            <td
                              class="border border-gray-200 px-3 py-3 text-sm font-medium text-blue-600"
                            >
                              {{ item.id }}
                            </td>
                            <td class="border border-gray-200 px-3 py-3 text-sm text-gray-700">
                              {{ item.name }}
                            </td>
                            <td class="border border-gray-200 px-3 py-3 text-center">
                              <span
                                :class="[
                                  'inline-flex rounded-full px-2 py-1 text-xs font-semibold',
                                  item.quantity > 8
                                    ? 'bg-green-100 text-green-800'
                                    : item.quantity > 3
                                      ? 'bg-amber-100 text-amber-800'
                                      : 'bg-red-100 text-red-800',
                                ]"
                              >
                                {{ item.quantity }}
                              </span>
                            </td>
                            <td
                              class="border border-gray-200 px-3 py-3 text-center text-sm text-gray-600"
                            >
                              {{ item.batch }}
                            </td>
                            <td
                              class="border border-gray-200 px-3 py-3 text-center text-sm text-gray-600"
                            >
                              {{ item.expiry }}
                            </td>
                          </tr>
                          <tr v-if="!shopItems[shop.id] || shopItems[shop.id].length === 0">
                            <td
                              colspan="5"
                              class="border border-gray-200 px-3 py-8 text-center text-gray-500"
                            >
                              <p class="text-lg font-medium">Không có mặt hàng</p>
                              <p class="mt-1 text-sm">Gian hàng này hiện chưa có sản phẩm nào</p>
                            </td>
                          </tr>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </CardBox>
    </SectionMain>
  </LayoutAuthenticated>
</template>
