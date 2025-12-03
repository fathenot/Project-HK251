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

// Dữ liệu mẫu khi API không có
const sampleEmployees = [
  {
    id: 'EMP001',
    role: 'manager',
    name: 'Lionel Messi',
    email: 'lionelmessi@example.com',
    phone: '0123456789',
    address: '123 Đường ABC, TP.HCM',
    hireDate: '2022-07-01',
  },
  {
    id: 'EMP002',
    role: 'staff',
    name: 'Cristiano Ronaldo',
    email: 'cr7@example.com',
    phone: '0987654321',
    address: '456 Đường XYZ, Hà Nội',
    hireDate: '2023-01-15',
  },
]

const searchQuery = ref('')
const employees = ref([])

const filteredEmployees = computed(() =>
  employees.value.filter((e) => {
    const q = searchQuery.value.toLowerCase()
    return (
      e.id.toLowerCase().includes(q) ||
      e.name.toLowerCase().includes(q) ||
      e.email.toLowerCase().includes(q) ||
      e.phone.includes(q)
    )
  }),
)

const modalActive = ref(false)
const selectedEmployee = ref(null)
const loadingDetail = ref(false)

const fetchEmployees = async () => {
  try {
    const res = await api.get('/employees')
    employees.value = res.data
  } catch (err) {
    console.warn('Không thể load nhân viên từ API, dùng dữ liệu mẫu', err)
    employees.value = sampleEmployees
  }
}

const fetchEmployeeDetail = async (id) => {
  loadingDetail.value = true
  selectedEmployee.value = null
  try {
    const res = await api.get(`/employees/${id}`)
    selectedEmployee.value = res.data
  } catch (err) {
    console.warn('Không thể load chi tiết, dùng dữ liệu mẫu', err)
    selectedEmployee.value = employees.value.find((e) => e.id === id)
  } finally {
    loadingDetail.value = false
  }
}

const viewEmployee = (employee) => {
  modalActive.value = true
  fetchEmployeeDetail(employee.id)
}

const addModalActive = ref(false)

const newEmployee = ref({
  id: '',
  name: '',
  email: '',
  phone: '',
  address: '',
  role: { id: 'sales', label: 'Nhân viên bán hàng' },
  hireDate: '',
})

const openAddModal = () => {
  newEmployee.value = {
    id: '',
    name: '',
    email: '',
    phone: '',
    address: '',
    role: { id: 'sales', label: 'Nhân viên bán hàng' },
    hireDate: '',
  }
  addModalActive.value = true
}

const addEmployee = async () => {
  try {
    const res = await api.post('/employees', newEmployee.value)

    employees.value.push(res.data)

    addModalActive.value = false
  } catch (err) {
    console.error('Lỗi thêm nhân viên:', err)
    alert('Không thể thêm nhân viên')
  }
}

onMounted(fetchEmployees)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLineWithoutButton :icon="mdiAccount" title="Tra cứu nhân viên" />

      <!-- Search Card -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm nhân viên</h3>
            <p class="text-sm text-gray-600">Tìm theo ID, tên, email hoặc số điện thoại</p>
          </div>

          <button
            @click="openAddModal"
            class="rounded-lg bg-blue-600 px-4 py-2 text-white shadow hover:bg-blue-700"
          >
            + Thêm nhân viên
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
          <h3 class="text-lg font-semibold text-gray-800">Danh sách nhân viên</h3>
          <p class="mt-1 text-sm text-gray-600">
            Tìm thấy {{ filteredEmployees.length }} nhân viên
            <span v-if="searchQuery" class="font-medium">cho "{{ searchQuery }}"</span>
          </p>
        </div>

        <div class="overflow-x-auto">
          <table class="w-full">
            <thead class="bg-gray-50">
              <tr>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">ID</th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Họ và tên
                </th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Email
                </th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Số điện thoại
                </th>
                <th class="px-6 py-4 text-left text-xs font-medium text-gray-500 uppercase">
                  Vai trò
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="emp in filteredEmployees"
                :key="emp.id"
                class="cursor-pointer transition-colors duration-200 hover:bg-blue-50"
                @click="viewEmployee(emp)"
              >
                <td class="px-6 py-4">{{ emp.id }}</td>
                <td class="px-6 py-4">{{ emp.name }}</td>
                <td class="px-6 py-4">{{ emp.email }}</td>
                <td class="px-6 py-4">{{ emp.phone }}</td>
                <td class="px-6 py-4 capitalize">{{ emp.role }}</td>
              </tr>

              <tr v-if="filteredEmployees.length === 0">
                <td colspan="5" class="px-6 py-16 text-center text-gray-500">
                  Không tìm thấy nhân viên phù hợp
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Employee Detail Modal -->
      <CardBoxModal
        v-model="modalActive"
        title="Chi tiết nhân viên"
        button-label="Đóng"
        :has-button="true"
      >
        <div v-if="loadingDetail" class="flex flex-col items-center justify-center py-12">
          <div
            class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết...</p>
        </div>

        <div v-else-if="selectedEmployee" class="space-y-6">
          <div class="flex items-center justify-between border-b border-gray-200 pb-4">
            <h3 class="text-xl font-semibold text-gray-900">{{ selectedEmployee.name }}</h3>
            <div class="flex h-12 w-12 items-center justify-center rounded-lg bg-blue-100">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                class="h-6 w-6 text-blue-600"
                viewBox="0 0 24 24"
                fill="currentColor"
              >
                <path :d="mdiAccount" />
              </svg>
            </div>
          </div>

          <div class="grid gap-6 sm:grid-cols-2">
            <div class="space-y-3">
              <p><strong>Email:</strong> {{ selectedEmployee.email }}</p>
              <p><strong>Số điện thoại:</strong> {{ selectedEmployee.phone }}</p>
              <p><strong>Địa chỉ:</strong> {{ selectedEmployee.address }}</p>
            </div>
            <div class="space-y-3">
              <p><strong>Vai trò:</strong> {{ selectedEmployee.role }}</p>
              <p v-if="selectedEmployee.points">
                <strong>Điểm:</strong> {{ selectedEmployee.points }}
              </p>
              <p v-if="selectedEmployee.hireDate">
                <strong>Ngày tuyển dụng:</strong> {{ selectedEmployee.hireDate }}
              </p>
            </div>
          </div>
        </div>
      </CardBoxModal>

      <CardBoxModal
        v-model="addModalActive"
        title="Thêm nhân viên mới"
        button-label="Lưu"
        :has-button="true"
        @confirm="addEmployee"
      >
        <div class="space-y-4">
          <FormControl v-model="newEmployee.id" placeholder="Mã nhân viên (ID)" />
          <FormControl v-model="newEmployee.name" placeholder="Họ và tên" />
          <FormControl v-model="newEmployee.email" placeholder="Email" />
          <FormControl v-model="newEmployee.phone" placeholder="Số điện thoại" />
          <FormControl v-model="newEmployee.address" placeholder="Địa chỉ" />

          <div class="grid grid-cols-2 gap-4">
            <FormField label="Vai trò">
              <FormControl
                v-model="newEmployee.role"
                :options="[
                  { id: 'warehouse', label: 'Nhân viên kho' },
                  { id: 'sales', label: 'Nhân viên bán hàng' },
                ]"
              />
            </FormField>

            <FormControl v-model="newEmployee.hireDate" type="date" placeholder="Ngày tuyển dụng" />
          </div>
        </div>
      </CardBoxModal>

    </SectionMain>
  </LayoutAuthenticated>
</template>
