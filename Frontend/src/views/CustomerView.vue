<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiAccount, mdiMagnify } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLineWithoutButton from '@/components/SectionTitleLineWithoutButton.vue'
import FormControl from '@/components/FormControl.vue'
import { api } from '@/plugins/axios'

// Đổi sampleEmployees -> sampleCustomers
const sampleCustomers = [
  {
    id: 'CUS001',
    role: 'Khách hàng',
    name: 'Lionel Messi',
    email: 'lionelmessi@example.com',
    phone: '0123456789',
    address: '123 Đường ABC, TP.HCM',
    points: 120,
  },
  {
    id: 'CUS002',
    role: 'Khách hàng',
    name: 'Cristiano Ronaldo',
    email: 'cr7@example.com',
    phone: '0987654321',
    address: '456 Đường XYZ, Hà Nội',
    points: 80,
  },
]

// employees -> customers
const searchQuery = ref('')
const customers = ref([])

const filteredCustomers = computed(() =>
  customers.value.filter((c) => {
    const q = searchQuery.value.toLowerCase()
    return (
      c.id.toLowerCase().includes(q) ||
      c.name.toLowerCase().includes(q) ||
      c.email.toLowerCase().includes(q) ||
      c.phone.includes(q)
    )
  }),
)

// modal & selection
const modalActive = ref(false)
const selectedCustomer = ref(null)
const loadingDetail = ref(false)

// fetch employees -> fetch customers
const fetchCustomers = async () => {
  try {
    const res = await api.get('/customers')
    customers.value = res.data
  } catch (err) {
    console.warn('Không thể load khách hàng từ API, dùng dữ liệu mẫu', err)
    customers.value = sampleCustomers
  }
}

const fetchCustomerDetail = async (id) => {
  loadingDetail.value = true
  selectedCustomer.value = null
  try {
    const res = await api.get(`/customers/${id}`)
    selectedCustomer.value = res.data
  } catch (err) {
    console.warn('Không thể load chi tiết, dùng dữ liệu mẫu', err)
    selectedCustomer.value = customers.value.find((c) => c.id === id)
  } finally {
    loadingDetail.value = false
  }
}

const viewCustomer = (customer) => {
  modalActive.value = true
  fetchCustomerDetail(customer.id)
}

onMounted(fetchCustomers)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiAccount" title="Tra cứu khách hàng" />

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm khách hàng</h3>
            <p class="text-sm text-gray-600">Tìm theo ID, tên, email hoặc số điện thoại</p>
          </div>
          <div class="w-full md:w-96">
            <div class="relative">
              <div class="pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3">
                <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-gray-400" viewBox="0 0 24 24" fill="currentColor">
                  <path :d="mdiMagnify" />
                </svg>
              </div>
              <FormControl
                v-model="searchQuery"
                placeholder="Nhập ID, tên, email hoặc điện thoại..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Results Card -->
      <CardBox class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="border-b border-gray-200 bg-gray-50 px-6 py-4">
          <h3 class="text-lg font-semibold text-gray-800">Danh sách khách hàng</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredCustomers.length }} khách hàng
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Họ và tên</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Email</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Số điện thoại</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">Vai trò</th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="c in filteredCustomers"
                :key="c.id"
                class="cursor-pointer transition-colors duration-200 hover:bg-blue-50"
                @click="viewCustomer(c)"
              >
                <td class="px-6 py-4">{{ c.id }}</td>
                <td class="px-6 py-4">{{ c.name }}</td>
                <td class="px-6 py-4">{{ c.email }}</td>
                <td class="px-6 py-4">{{ c.phone }}</td>
                <td class="px-6 py-4 capitalize">{{ c.role }}</td>
              </tr>

              <tr v-if="filteredCustomers.length === 0">
                <td colspan="5" class="px-6 py-16 text-center text-gray-500">
                  Không tìm thấy khách hàng phù hợp
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Customer Detail Modal -->
      <CardBoxModal
        v-model="modalActive"
        title="Chi tiết khách hàng"
        button-label="Đóng"
        :has-button="true"
      >
        <div v-if="loadingDetail" class="flex flex-col items-center justify-center py-12">
          <div class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết...</p>
        </div>

        <div v-else-if="selectedCustomer" class="space-y-6">
          <div class="flex items-center justify-between border-b border-gray-200 pb-4">
            <h3 class="text-xl font-semibold text-gray-900">{{ selectedCustomer.name }}</h3>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg xmlns="http://www.w3.org/2000/svg" class="h-6 w-6 text-blue-600" viewBox="0 0 24 24" fill="currentColor">
                <path :d="mdiAccount" />
              </svg>
            </div>
          </div>

          <div class="grid gap-6 sm:grid-cols-2">
            <div class="space-y-3">
              <p><strong>Email:</strong> {{ selectedCustomer.email }}</p>
              <p><strong>Số điện thoại:</strong> {{ selectedCustomer.phone }}</p>
              <p><strong>Địa chỉ:</strong> {{ selectedCustomer.address }}</p>
            </div>
            <div class="space-y-3">
              <p><strong>Vai trò:</strong> {{ selectedCustomer.role }}</p>
              <p v-if="selectedCustomer.points">
                <strong>Điểm:</strong> {{ selectedCustomer.points }}
              </p>
              <p v-if="selectedCustomer.hireDate">
                <strong>Ngày tuyển dụng:</strong> {{ selectedCustomer.hireDate }}
              </p>
            </div>
          </div>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
