<script setup>
import { ref, computed, onMounted } from 'vue'
import { mdiAccount, mdiMagnify, mdiPencil, mdiDelete } from '@mdi/js'
import LayoutAuthenticated from '@/layouts/LayoutAuthenticated.vue'
import SectionMain from '@/components/SectionMain.vue'
import CardBox from '@/components/CardBox.vue'
import CardBoxModal from '@/components/CardBoxModal.vue'
import SectionTitleLine from '@/components/SectionTitleLine.vue'
import FormControl from '@/components/FormControl.vue'
import FormField from '@/components/FormField.vue'
import { api } from '@/plugins/axios'

// Map role sang tiếng Việt
const roleMap = {
  admin: 'Quản trị viên',
  manager: 'Quản lý',
  sales: 'Nhân viên bán hàng',
  warehouse: 'Nhân viên kho',
}

const searchQuery = ref('')
const employees = ref([])

const filteredEmployees = computed(() =>
  employees.value.filter((e) => {
    const q = searchQuery.value.toLowerCase()
    return (
      e.id.toLowerCase().includes(q) ||
      e.username.toLowerCase().includes(q) ||
      e.name.toLowerCase().includes(q)
    )
  }),
)

// Chi tiết modal
const modalActive = ref(false)
const selectedEmployee = ref(null)
const loadingDetail = ref(false)

// Thêm mới modal
const addModalActive = ref(false)
const newEmployee = ref({
  username: '',
  firstName: '',
  lastName: '',
  role: 'sales',
})

// AccessToken
const getTokenHeader = () => ({ Authorization: `Bearer ${localStorage.getItem('accessToken')}` })

// Fetch employees
const fetchEmployees = async () => {
  try {
    const res = await api.get('/employees', {
      headers: getTokenHeader(),
      params: { page: 0, size: 1000 },
    })
    employees.value = res.data.data.content.map((e) => ({
      id: `${e.id}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type.toLowerCase(),
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    }))
  } catch (err) {
    console.warn('Không thể load nhân viên, dùng dữ liệu mẫu', err)
  }
}

// Chi tiết employee
const fetchEmployeeDetail = async (id) => {
  loadingDetail.value = true
  selectedEmployee.value = null
  try {
    const res = await api.get(`/employees/${id}`, { headers: getTokenHeader() })
    const e = res.data.data
    selectedEmployee.value = {
      id: `${e.id}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type,
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    }
  } catch (err) {
    console.warn('Không thể load chi tiết, dùng dữ liệu mẫu', err)
    selectedEmployee.value = employees.value.find((e) => e.id === id)
  } finally {
    loadingDetail.value = false
  }
}

const viewEmployee = (emp) => {
  modalActive.value = true
  fetchEmployeeDetail(emp.id)
}

// Thêm employee
const openAddModal = () => {
  newEmployee.value = { username: '', firstName: '', lastName: '', role: 'sales' }
  addModalActive.value = true
}

const addEmployee = async () => {
  try {
    const body = {
      employee: {
        username: newEmployee.value.username,
        password: '123456',
        firstName: newEmployee.value.firstName,
        lastName: newEmployee.value.lastName,
        managerId: 0,
      },
    }
    let res
    if (newEmployee.value.role === 'warehouse') {
      body.warehouseId = 1
      res = await api.post('/employees/warehouse', body, { headers: getTokenHeader() })
    } else {
      body.storeId = 0
      res = await api.post('/employees/sales', body, { headers: getTokenHeader() })
    }
    const e = res.data.data
    employees.value.push({
      id: `${e.id}`,
      username: e.username,
      name: `${e.first_name} ${e.last_name}`,
      role: e.employee_type,
      hireDate: new Date(e.hired_at).toLocaleDateString(),
    })
    addModalActive.value = false
  } catch (err) {
    console.error('Thêm nhân viên lỗi:', err)
    alert('Không thể thêm nhân viên')
  }
}

// Xóa employee
const deleteEmployee = async (id) => {
  if (!confirm('Bạn có chắc muốn xóa nhân viên này?')) return
  try {
    const numericId = parseInt(id.replace('EMP', ''))
    await api.delete(`/employees/${numericId}`, { headers: getTokenHeader() })
    employees.value = employees.value.filter((e) => e.id !== id)
  } catch (err) {
    console.error('Xóa nhân viên lỗi:', err)
    alert('Không thể xóa nhân viên')
  }
}

onMounted(fetchEmployees)
</script>

<template>
  <LayoutAuthenticated>
    <SectionMain>
      <SectionTitleLine :icon="mdiAccount" title="Quản lý nhân viên" main>
        <button
          @click="openAddModal"
          class="rounded-lg bg-blue-600 px-4 py-2 text-white shadow hover:bg-blue-700"
        >
          + Thêm nhân viên
        </button>
      </SectionTitleLine>

      <!-- Search + Add -->
      <CardBox class="mb-6 rounded-xl border border-gray-200 bg-white shadow-sm">
        <div class="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
          <div class="flex-1">
            <h3 class="mb-2 text-lg font-semibold text-gray-800">Tìm kiếm nhân viên</h3>
            <p class="text-sm text-gray-600">Tìm theo mã số, tên hoặc tên đăng nhập</p>
          </div>
          <div class="flex gap-2 md:items-center md:gap-4">
            <div class="relative w-full md:w-96">
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
                placeholder="Nhập ID, tên hoặc username..."
                class="pl-10"
              />
            </div>
          </div>
        </div>
      </CardBox>

      <!-- Employee Table -->
      <CardBox
        class="overflow-hidden rounded-xl border border-gray-200 bg-white shadow-sm"
        has-table
      >
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
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Mã số
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Họ và tên
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Tên đăng nhập
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Vai trò
                </th>
                <th class="px-6 py-4 text-center text-xs font-medium text-gray-500 uppercase">
                  Thao tác
                </th>
              </tr>
            </thead>
            <tbody class="divide-y divide-gray-200 bg-white">
              <tr
                v-for="emp in filteredEmployees"
                :key="emp.id"
                class="transition-colors duration-200 hover:bg-blue-50"
              >
                <td class="px-6 py-4 text-center">{{ emp.id }}</td>
                <td class="px-6 py-4">{{ emp.name }}</td>
                <td class="px-6 py-4">{{ emp.username }}</td>
                <td class="px-6 py-4">
                  {{ roleMap[emp.role] || emp.role }}
                </td>
                <td class="px-6 py-4 whitespace-nowrap">
                  <div class="flex items-center justify-center gap-2">
                    <button
                      @click="viewEmployee(emp)"
                      class="cursor-pointer rounded-lg bg-blue-100 p-2 text-blue-600 transition-colors hover:bg-blue-200"
                      title="Xem chi tiết"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiPencil" />
                      </svg>
                    </button>
                    <button
                      @click="deleteEmployee(emp.id)"
                      class="cursor-pointer rounded-lg bg-red-100 p-2 text-red-600 transition-colors hover:bg-red-200"
                      title="Xóa nhân viên"
                    >
                      <svg
                        xmlns="http://www.w3.org/2000/svg"
                        class="h-4 w-4"
                        viewBox="0 0 24 24"
                        fill="currentColor"
                      >
                        <path :d="mdiDelete" />
                      </svg>
                    </button>
                  </div>
                </td>
              </tr>
              <tr v-if="filteredEmployees.length === 0">
                <td colspan="5" class="px-6 py-16 text-center text-gray-500">
                  Không tìm thấy nhân viên
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </CardBox>

      <!-- Employee Detail Modal -->
      <!-- Employee Detail Modal -->
      <CardBoxModal v-model="modalActive" title="Chi tiết nhân viên" button-label="Đóng">
        <div v-if="selectedEmployee" class="space-y-6">
          <!-- Header -->
          <div class="flex items-start justify-between border-b border-gray-200 pb-4">
            <div>
              <h3 class="text-xl font-semibold text-gray-900">{{ selectedEmployee.name }}</h3>
              <p class="mt-1 text-sm text-gray-500">
                Mã số: {{ selectedEmployee.id }} • Ngày tuyển dụng: {{ selectedEmployee.hireDate }}
              </p>
            </div>
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

          <!-- Grid layout -->
          <div class="grid gap-6 sm:grid-cols-2">
            <!-- Basic info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin cơ bản
              </h4>
              <div class="space-y-3">
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Username</span>
                  <span class="text-sm text-gray-900">{{ selectedEmployee.username }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Họ & tên</span>
                  <span class="text-sm text-gray-900">{{ selectedEmployee.name }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Vai trò</span>
                  <span class="text-sm text-gray-900">{{
                    roleMap[selectedEmployee.role] || selectedEmployee.role
                  }}</span>
                </div>
                <div class="flex justify-between">
                  <span class="text-sm font-medium text-gray-700">Ngày tuyển dụng</span>
                  <span class="text-sm text-gray-900">{{ selectedEmployee.hireDate }}</span>
                </div>
              </div>
            </div>

            <!-- Optional info -->
            <div class="space-y-4">
              <h4 class="text-sm font-semibold tracking-wide text-gray-500 uppercase">
                Thông tin khác
              </h4>
              <div class="space-y-3">
                <!-- Thêm các thông tin khác nếu cần -->
                <!-- Ví dụ: bộ phận, kho quản lý, email, điện thoại -->
              </div>
            </div>
          </div>
        </div>

        <!-- Loading -->
        <div v-else class="flex flex-col items-center justify-center py-12">
          <div
            class="h-8 w-8 animate-spin rounded-full border-4 border-blue-500 border-t-transparent"
          ></div>
          <p class="mt-4 text-sm text-gray-600">Đang tải chi tiết...</p>
        </div>
      </CardBoxModal>

      <!-- Add Employee Modal -->
      <CardBoxModal
        v-model="addModalActive"
        title="Thêm nhân viên mới"
        button-label="Thêm nhân viên"
        @confirm="addEmployee"
        has-cancel
      >
        <div class="space-y-4">
          <FormField label="Username">
            <FormControl v-model="newEmployee.username" />
          </FormField>

          <FormField label="Họ">
            <FormControl v-model="newEmployee.firstName" />
          </FormField>

          <FormField label="Tên">
            <FormControl v-model="newEmployee.lastName" />
          </FormField>

          <FormField label="Vai trò">
            <FormControl
              v-model="newEmployee.role"
              :options="[
                { id: 'admin', label: 'Quản trị viên' },
                { id: 'manager', label: 'Quản lý' },
                { id: 'sales', label: 'Nhân viên bán hàng' },
                { id: 'warehouse', label: 'Nhân viên kho' },
              ]"
            />
          </FormField>
        </div>
      </CardBoxModal>
    </SectionMain>
  </LayoutAuthenticated>
</template>
